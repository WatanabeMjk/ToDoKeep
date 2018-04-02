package com.watanabemjk.todokeep;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    private static final String STATE_POSITION = "selectedPosition";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private int mCurrentPosition;
    private Toolbar mToolbar;
    public MainActivity activity;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        initDrawer();

        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(this);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new PlanListFragment()).commit();
            mNavigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void initDrawer(){
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar, R.string.plan_name, R.string.plan_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, mCurrentPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInsaneState){
        super.onRestoreInstanceState(savedInsaneState);
        mCurrentPosition = savedInsaneState.getInt(STATE_POSITION,0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentPosition).setChecked(true);
    }

    @Override
    protected void onPostCreate(Bundle saveInstanceState){
        super.onPostCreate(saveInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        menuItem.setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (menuItem.getItemId()){
            case R.id.plan:
                fragmentManager.beginTransaction().replace(R.id.container,new PlanListFragment()).commit();
                mTitle = getString(R.string.plan_name);
                mCurrentPosition = 0;
                break;
            case R.id.archive:
                fragmentManager.beginTransaction().replace(R.id.container,new ArchiveListFragment()).commit();
                mTitle = getString(R.string.archive_name);
                mCurrentPosition = 1;
                break;
            case R.id.trash:
                fragmentManager.beginTransaction().replace(R.id.container,new TrashListFragment()).commit();
                mTitle = getString(R.string.trash_name);
                mCurrentPosition = 2;
                break;
            case R.id.menu_app_description:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.about_this_app)
                        .setMessage(R.string.thank_you_dl)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                break;

        }
        mToolbar.setTitle(mTitle);
        mDrawerLayout.closeDrawer(mNavigationView);
        return true;
    }
    @Override
    public void onClick(View v){mDrawerLayout.openDrawer(GravityCompat.START);}

    public boolean doubleBackToExitPressedOnce;

    @Override
    public void onBackPressed(){
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            this.mDrawerLayout.closeDrawers();
            return;
        }
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if(backStackCount > 0 || this.doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,R.string.press_again,Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }
}
