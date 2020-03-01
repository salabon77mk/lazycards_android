package com.salabon.lazycards.Cards;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.salabon.lazycards.Database.CardDbManager;
import com.salabon.lazycards.NetworkScanner.NetworkScannerActivity;
import com.salabon.lazycards.R;

public abstract class ActivityWithNavBar
        extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private enum Current_Fragment {
        HOME, QUEUE
    }

    private Current_Fragment mCurrent_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // init db
        CardDbManager cardDb = CardDbManager.getInstance(this);

        setContentView(R.layout.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.nav_open_drawer, R.string.nav_close_drawer);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState(); // must be called so that the icon is synchronized with state of drawer

        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = new MainFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
            mCurrent_fragment = Current_Fragment.HOME;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        switch (id){
            case R.id.nav_home_fragment:
                fragment = new MainFragment();
                mCurrent_fragment = Current_Fragment.HOME;
                break;
            case R.id.nav_queued_cards:
                fragment = new QueuedCardsFragment();
                mCurrent_fragment = Current_Fragment.QUEUE;
                break;
            case R.id.nav_network_scanner:
                intent = NetworkScannerActivity.newIntent(this);
                break;
        }
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
        else {
            startActivity(intent);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else if(mCurrent_fragment == Current_Fragment.QUEUE){
            Fragment fragment = new MainFragment();
            mCurrent_fragment = Current_Fragment.HOME;

            if(fragment != null){
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }

            mNavigationView.setCheckedItem(R.id.nav_home_fragment);
        }
        else{
            super.onBackPressed();
        }
    }
}
