package com.lescour.ben.go4lunch.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.MapsFragment;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment;
import com.lescour.ben.go4lunch.controller.fragment.WorkmatesListFragment;
import com.lescour.ben.go4lunch.controller.fragment.dummy.DummyContent;
import com.lescour.ben.go4lunch.model.ParcelableLocation;
import com.lescour.ben.go4lunch.model.details.DetailsResponse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by benja on 15/03/2019.
 */
public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        RestaurantListFragment.OnListFragmentInteractionListener,
        WorkmatesListFragment.OnListFragmentInteractionListener {

    @BindView(R.id.activity_home_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_home_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_nav_view) NavigationView navigationView;
    @BindView(R.id.navigation) BottomNavigationView navigation;

    private Fragment fragment;
    private ProgressDialog mProgress;


    private ParcelableLocation mParcelableLocation;
    private long MIN_TIME_FOR_UPDATES = 300;
    private long MIN_DISTANCE_FOR_UPDATES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Log.e("HomeActivity", " onCreate");

        this.configureToolbar();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.configureDrawerLayout();
        this.configureNavigationView();

        this.initFirstFragment();

        this.initProgressDialog();

        this.getMyCurrentLocation();
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    private void getMyCurrentLocation() {
        mParcelableLocation = new ParcelableLocation();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mParcelableLocation.setLatitude(location.getLatitude());
                mParcelableLocation.setLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    //BOTTOM TOOLBAR\\
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Bundle bundle = new Bundle();
        bundle.putParcelable("HomeToFragment", mParcelableLocation);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        switch (item.getItemId()) {
            case R.id.navigation_map:
                fragment = MapsFragment.newInstance("param1", "param2");
                addFragment();
                return true;
            case R.id.navigation_list_restaurant:
                fragment = RestaurantListFragment.newInstance(0);
                fragment.setArguments(bundle);
                addFragment();
                return true;
            case R.id.navigation_workmates:
                fragment = WorkmatesListFragment.newInstance(0);
                //currentPlace();
                //placeDetails();
                addFragment();
                return true;
        }
        return false;
    };

    //FRAGMENT\\
    private void initFirstFragment() {
        fragment = MapsFragment.newInstance("param1", "param2");
        addFragment();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onListFragmentInteraction(DetailsResponse detailsResponse) {

    }

    //MENU TOOLBAR\\

    /**
     * Inflate the menu and add it to the Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Handle actions on menu items.
     * @param item Item selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_home_search:
                Toast.makeText(this, "Button not available", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //MAIN MENU\\
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(this, header);
        headerViewHolder.updateMainMenuWithUserInfo();
    }

    /**
     * When an item is selected. Display the associate fragment in the ViewPager.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.activity_home_drawer_your_lunch:
                break;
            case R.id.activity_home_drawer_settings:
                break;
            case R.id.activity_home_drawer_logout:
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Close the NavigationDrawer with the button back
     */
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOutUserFromFirebase() {
        mProgress.show();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, "Fetch Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Home", "Resume");
    }

    private void initProgressDialog() {
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Your account will be disconnected...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

    }

}
