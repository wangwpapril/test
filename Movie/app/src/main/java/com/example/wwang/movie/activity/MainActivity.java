package com.example.wwang.movie.activity;

import android.net.Uri;
import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wwang.movie.ClearEditText;
import com.example.wwang.movie.fragment.DetailsFragment;
import com.example.wwang.movie.fragment.ItemFragment;
import com.example.wwang.movie.fragment.ListFragment;
import com.example.wwang.movie.R;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ItemFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener {

    private FragmentManager manager;

    private FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

                String text = editable.toString().toLowerCase(Locale.getDefault());
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
        Uri u = uri;
        return;

    }

    @Override
    public void onFragmentInteraction(String id) {

        String tt = id;
        return;
    }
}
