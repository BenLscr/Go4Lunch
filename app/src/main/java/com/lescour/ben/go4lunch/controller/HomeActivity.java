package com.lescour.ben.go4lunch.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.lescour.ben.go4lunch.BuildConfig;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.MapsFragment;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment;
import com.lescour.ben.go4lunch.controller.fragment.WorkmatesListFragment;
import com.lescour.ben.go4lunch.controller.fragment.dummy.DummyContent;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.NearbyResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;
import com.lescour.ben.go4lunch.utils.GoogleStreams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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
    @BindView(R.id.activity_main_progress_bar) ProgressBar mProgressBar;

    private Fragment fragment;
    private ProgressDialog mProgress;
    private Bundle bundle;

    private Disposable disposable;

    private ParcelableRestaurantDetails mParcelableRestaurantDetails;
    private long MIN_TIME_FOR_UPDATES = 3000;
    private long MIN_DISTANCE_FOR_UPDATES = 200;

    private PlacesClient placesClient;

    private String stringLocation;
    private int radius = 1500;
    private String type = "restaurant";
    private String apiKey = BuildConfig.PLACES_API_KEY;

    private int i = 0;

    private List<PlaceDetailsResponse> mPlaceDetailsResponses;

    public static final String BUNDLE_EXTRA_PARCELABLERESTAURANTDETAILS = "BUNDLE_EXTRA_PARCELABLERESTAURANTDETAILS";
    public static final String INTENT_EXTRA_RESULT = "INTENT_EXTRA_RESULT";
    public static final String INTENT_EXTRA_PLACEDETAILSRESPONSE = "INTENT_EXTRA_PLACEDETAILSRESPONSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        this.configureToolbar();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.configureDrawerLayout();
        this.configureNavigationView();

        this.initProgressDialog();

        mProgressBar.setVisibility(View.VISIBLE);
        mParcelableRestaurantDetails = new ParcelableRestaurantDetails();
        this.initializePlacesApiClient();
        this.getMyCurrentLocation();
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initBundle() {
        bundle = new Bundle();
        bundle.putParcelable(BUNDLE_EXTRA_PARCELABLERESTAURANTDETAILS, mParcelableRestaurantDetails);
    }

    //BOTTOM TOOLBAR\\
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        switch (item.getItemId()) {
            case R.id.navigation_map:
                fragment = MapsFragment.newInstance("param1", "param2");
                fragment.setArguments(bundle);
                addFragment();
                return true;
            case R.id.navigation_list_restaurant:
                fragment = RestaurantListFragment.newInstance(mParcelableRestaurantDetails);
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
        fragment.setArguments(bundle);
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
    public void onListFragmentInteraction(Result result, PlaceDetailsResponse placeDetailsResponse) {
        Intent intent = new Intent(HomeActivity.this, RestaurantActivity.class);
        intent.putExtra(INTENT_EXTRA_RESULT, result);
        intent.putExtra(INTENT_EXTRA_PLACEDETAILSRESPONSE, placeDetailsResponse);
        startActivity(intent);
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

    private void initProgressDialog() {
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Your account will be disconnected...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    //HTTP REQUEST\\
    private void initializePlacesApiClient() {
        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        placesClient = Places.createClient(this);

    }

    private void getMyCurrentLocation() {
        mParcelableRestaurantDetails = new ParcelableRestaurantDetails();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 0);
            }
         }
        this.checkConnexion(locationManager, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mParcelableRestaurantDetails.setNearbyResults(new ArrayList<>());
                mPlaceDetailsResponses = new ArrayList<>();
                mParcelableRestaurantDetails.setCurrentLat(location.getLatitude());
                mParcelableRestaurantDetails.setCurrentLng(location.getLongitude());
                stringLocation = location.getLatitude() + "," + location.getLongitude();
                executeHttpRequestWithRetrofit_NearbySearch();
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

    private void checkConnexion(LocationManager locationManager, Context context) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(context)
                    .setTitle("GPS not found")  // GPS not found
                    .setMessage("Want to enable ?") // Want to enable?
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void executeHttpRequestWithRetrofit_NearbySearch(){
        this.disposable = GoogleStreams.streamFetchNearbySearch(stringLocation, radius, type, apiKey)
                .subscribeWith(new DisposableObserver<NearbyResponse>() {
                    @Override
                    public void onNext(NearbyResponse nearbyResponse) {
                        Log.e("TAG","On Next");
                        updateList(nearbyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG","On Error"+Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG","On Complete !!");
                    }
                });
    }

    private void updateList(NearbyResponse nearbyResponse) {
        mParcelableRestaurantDetails.setNearbyResults(nearbyResponse.getResults());
        this.everyRestaurantDetails_isRequired();
    }

    private void getPlaceDetails(String placeId) {
        PlaceDetailsResponse placeDetailsResponse = new PlaceDetailsResponse();
        // Specify the fields to return (in this example all fields are returned).
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeDetailsResponse.setName(place.getName());
            placeDetailsResponse.setOpeningHours(place.getOpeningHours());
            placeDetailsResponse.setAddress(place.getAddress());
            placeDetailsResponse.setPhoneNumber(place.getPhoneNumber());
            placeDetailsResponse.setWebsiteUri(place.getWebsiteUri());
            this.getPlacePhotos(placeId, placeDetailsResponse);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("PlaceDetails", "Place not found: " + exception.getMessage());
            }
        });
    }

    private void getPlacePhotos(String placeId, PlaceDetailsResponse placeDetailsResponse) {
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // Get the photo metadata.
            if (place.getPhotoMetadatas() != null) {
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                // Create a FetchPhotoRequest.
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(120) // Optional.
                        .setMaxHeight(120) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    placeDetailsResponse.setBitmap(bitmap);
                    mPlaceDetailsResponses.add(placeDetailsResponse);
                    everyRestaurantDetails_isRequired();
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e("PlacePhotos", "Place not found: " + exception.getMessage());
                    }
                });
            }
        });
    }

    private void everyRestaurantDetails_isRequired() {
        if (mParcelableRestaurantDetails.getNearbyResults().size() == mPlaceDetailsResponses.size()) {
            mParcelableRestaurantDetails.setPlaceDetailsResponses(mPlaceDetailsResponses);
            mProgressBar.setVisibility(View.GONE);
            this.initBundle();
            this.initFirstFragment();
        } else {
            getPlaceDetails(mParcelableRestaurantDetails.getNearbyResults().get(i).getPlaceId());
            i++;
        }
    }

}
