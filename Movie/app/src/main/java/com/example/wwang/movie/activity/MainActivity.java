package com.example.wwang.movie.activity;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wwang.movie.fragment.DetailsFragment;
import com.example.wwang.movie.fragment.ItemFragment;
import com.example.wwang.movie.fragment.ListFragment;
import com.example.wwang.movie.R;


public class MainActivity extends ActionBarActivity implements ItemFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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