package com.example.wwang.movie.activity;

import android.content.Context;
import android.net.Uri;
import android.app.FragmentManager;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wwang.movie.utils.ClearEditText;
import com.example.wwang.movie.utils.Common;
import com.example.wwang.movie.utils.StringUtil;
import com.example.wwang.movie.fragment.DetailsFragment;
import com.example.wwang.movie.fragment.ItemFragment;
import com.example.wwang.movie.fragment.ListFragment;
import com.example.wwang.movie.R;
import com.example.wwang.movie.model.MovieDetails;
import com.example.wwang.movie.model.MovieItem;
import com.example.wwang.movie.request.ControllerContentTask;
import com.example.wwang.movie.request.Enums;
import com.example.wwang.movie.request.IControllerContentCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ItemFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener {

    private FragmentManager manager;

    private FragmentTransaction transaction;

    private List<MovieItem> movieItemList = new ArrayList<MovieItem>();

    private MovieDetails movieDetails = new MovieDetails();

    public static MainActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        Common.context = this;


        manager = getFragmentManager();

        ItemFragment itemFragment = (ItemFragment) manager.findFragmentById(R.id.fragment_list);
        ClearEditText et = (ClearEditText) itemFragment.getView().findViewById(R.id.search_ed);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String keyWords = editable.toString().toLowerCase(Locale.getDefault());
                if (keyWords.length() < 2) {
                    movieItemList.clear();
                    ItemFragment itemFragment = (ItemFragment) manager.findFragmentById(R.id.fragment_list);
                    itemFragment.mAdapter.setList(movieItemList);

                    return;
                }
                String url = "http://www.omdbapi.com/?s=" + keyWords;
//                String url ="http://www.omdbapi.com/?s=requiem";
                getMovieList(url);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        return;

    }

    @Override
    public void onFragmentInteraction(String id) {

        getMovieDetails(id);
        return;
    }

    private void getMovieList(final String url){

        IControllerContentCallback icc = new IControllerContentCallback() {
            public void handleSuccess(String content){

                JSONObject des;
                movieItemList.clear();

                try {
                    des = new JSONObject(content);
                    if(des.has("Error")){
                        StringUtil.showAlertDialog("MovieList", des.optString("Error"), instance);

                    }else if(des.has("Search")){

                        JSONArray array = des.getJSONArray("Search");
                        int len = array.length();
                        for (int i = 0; i < len; i++){
                            MovieItem item = new MovieItem();
                            item.setmId(i);
                            item.setmTitle(array.getJSONObject(i).optString("Title"));
                            item.setmImdbId(array.getJSONObject(i).optString("imdbID"));

                            movieItemList.add(item);

                        }
                    }


                    ItemFragment itemFragment = (ItemFragment) manager.findFragmentById(R.id.fragment_list);
                    itemFragment.mAdapter.setList(movieItemList);

                    if(!movieItemList.isEmpty())
                        getMovieDetails(movieItemList.get(0).getmImdbId());

 //                   itemFragment.getView().findViewById(R.id.fragment_list).setSelected(true);

                    InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(itemFragment.getView().getWindowToken(), 0);
                    }


                } catch (JSONException e) {
//                    StringUtil.showAlertDialog("Trips", "Data error !", context);
                    e.printStackTrace();
                }

            }

            public void handleError(Exception e){

//                StringUtil.showAlertDialog("Trips", "Data error !", context);
                return;

            }
        };


        ControllerContentTask cct = new ControllerContentTask(
                url, icc,
                Enums.ConnMethod.GET,false);
        String ss = null;
        cct.execute(ss);

    }

    private void getMovieDetails(final String id){
        IControllerContentCallback icc = new IControllerContentCallback() {
            public void handleSuccess(String content){

                JSONObject mds;
                try {
                    mds = new JSONObject(content);
                    movieDetails.setmTitle(mds.optString("Title"));
                    movieDetails.setmPlot(mds.optString("Plot"));
                    movieDetails.setmPoster(mds.optString("Poster"));
                    DetailsFragment detailsFragment = (DetailsFragment) manager.findFragmentById(R.id.fragment_detail);
                    ImageView poster = (ImageView) detailsFragment.getView().findViewById(R.id.details_poster);

                    if(movieDetails.getmPoster().equals("N/A") ) {
                        poster.setImageResource(R.mipmap.poster);
                    }else{
                        Picasso.with(instance).load(movieDetails.getmPoster()).resize(400, 400).centerCrop().into(poster);

                    }

                    TextView title = (TextView) detailsFragment.getView().findViewById(R.id.details_title);
                    title.setText(movieDetails.getmTitle());

                    TextView plot = (TextView) detailsFragment.getView().findViewById(R.id.details_plot);
                    plot.setText(movieDetails.getmPlot());

                } catch (JSONException e) {
//                    StringUtil.showAlertDialog("Trips", "Data error !", context);
                    e.printStackTrace();
                }

            }

            public void handleError(Exception e){

//                StringUtil.showAlertDialog("Trips", "Data error !", context);
                return;

            }
        };


        ControllerContentTask cct = new ControllerContentTask(
                "http://www.omdbapi.com/?i="+ id +"&plot=full&r=json", icc,
                Enums.ConnMethod.GET,false);
        String ss = null;
        cct.execute(ss);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.context = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Common.context = null;
    }


}
