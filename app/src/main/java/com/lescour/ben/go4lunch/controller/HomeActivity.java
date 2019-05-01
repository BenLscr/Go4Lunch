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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.lescour.ben.go4lunch.BuildConfig;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.BaseFragment;
import com.lescour.ben.go4lunch.controller.fragment.MapsFragment;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment;
import com.lescour.ben.go4lunch.controller.fragment.WorkmatesListFragment;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.model.nearby.NearbyResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;
import com.lescour.ben.go4lunch.utils.GoogleStreams;
import com.lescour.ben.go4lunch.utils.UserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
        BaseFragment.OnListFragmentInteractionListener {

    @BindView(R.id.activity_home_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_home_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_nav_view) NavigationView navigationView;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    @BindView(R.id.activity_main_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.card_view) CardView mCardView;
    @BindView(R.id.edittext_autocomplete) EditText mEditText;

    private BaseFragment fragment;
    private ProgressDialog mProgress;

    private ArrayList<User> usersList;

    private Disposable disposable;

    private ParcelableRestaurantDetails mParcelableRestaurantDetails;
    private ParcelableRestaurantDetails saveParcelableRestaurantDetails;
    private long MIN_TIME_FOR_UPDATES = 9999999;
    private long MIN_DISTANCE_FOR_UPDATES = 200;

    private PlacesClient placesClient;

    private String stringLocation;
    private int radius = 1000;
    private String type = "restaurant";
    private String apiKey = BuildConfig.PLACES_API_KEY;

    private List<PlaceDetailsResponse> mPlaceDetailsResponses;

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
        this.initializePlacesApiClient();
        this.getMyCurrentLocation();
    }

    /**
     * Close the NavigationDrawer or the Autocomplete bar with the button back
     */
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (mCardView.getVisibility() == View.VISIBLE) {
            mCardView.setVisibility(View.GONE);
            revertParcelableRestaurantDetails();
            fragment.newRestaurantsForFragment(mParcelableRestaurantDetails);
        } else {
            finish();
        }
    }


    //TOOLBAR\\
    private void configureToolbar() {
        setSupportActionBar(toolbar);
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
                if (mCardView.getVisibility() == View.GONE) {
                    this.buttonSearch();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void buttonSearch() {
        mCardView.setVisibility(View.VISIBLE);
        saveParcelableRestaurantDetails();
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) { searchRestaurant(mEditText.getText().toString()); }
        });
    }

    private void searchRestaurant(String query) {
        if (query.equals("")) {
            revertParcelableRestaurantDetails();
            fragment.newRestaurantsForFragment(mParcelableRestaurantDetails);
        } else {
            double distanceFromCenterToCorner = radius * Math.sqrt(2.0);
            LatLng center = new LatLng(mParcelableRestaurantDetails.getCurrentLat(), mParcelableRestaurantDetails.getCurrentLng());
            LatLng southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
            LatLng northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            // Create a RectangularBounds object.
            RectangularBounds bounds = RectangularBounds.newInstance(southwestCorner, northeastCorner);
            // Use the builder to create a FindAutocompletePredictionsRequest.
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationRestriction(bounds)
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setSessionToken(token)
                    .setQuery(query)
                    .build();


            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                ArrayList<Result> results = new ArrayList<>();
                ArrayList<PlaceDetailsResponse> placeDetailsResponses = new ArrayList<>();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    int z = 0;
                    do {
                        if (saveParcelableRestaurantDetails.getNearbyResults().get(z).getPlaceId().equals(prediction.getPlaceId())) {
                            results.add(saveParcelableRestaurantDetails.getNearbyResults().get(z));
                            placeDetailsResponses.add(saveParcelableRestaurantDetails.getPlaceDetailsResponses().get(z));
                        }
                        z++;
                    } while (saveParcelableRestaurantDetails.getNearbyResults().size() != z);
                }
                mParcelableRestaurantDetails.setNearbyResults(results);
                mParcelableRestaurantDetails.setPlaceDetailsResponses(placeDetailsResponses);
                fragment.newRestaurantsForFragment(mParcelableRestaurantDetails);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                }
            });
        }
    }

    private void saveParcelableRestaurantDetails() {
        saveParcelableRestaurantDetails = new ParcelableRestaurantDetails();
        saveParcelableRestaurantDetails.setNearbyResults(mParcelableRestaurantDetails.getNearbyResults());
        saveParcelableRestaurantDetails.setPlaceDetailsResponses(mParcelableRestaurantDetails.getPlaceDetailsResponses());
    }

    private void revertParcelableRestaurantDetails() {
        mParcelableRestaurantDetails.setNearbyResults(saveParcelableRestaurantDetails.getNearbyResults());
        mParcelableRestaurantDetails.setPlaceDetailsResponses(saveParcelableRestaurantDetails.getPlaceDetailsResponses());
    }

    //BOTTOM TOOLBAR\\
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        switch (item.getItemId()) {
            case R.id.navigation_map:
                fragment = MapsFragment.newInstance(mParcelableRestaurantDetails, usersList);
                addFragment();
                return true;
            case R.id.navigation_list_restaurant:
                fragment = RestaurantListFragment.newInstance(mParcelableRestaurantDetails, usersList);
                addFragment();
                return true;
            case R.id.navigation_workmates:
                fragment = WorkmatesListFragment.newInstance(mParcelableRestaurantDetails, usersList);
                addFragment();
                return true;
        }
        return false;
    };

    //FRAGMENT\\
    private void initFirstFragment() {
        fragment = MapsFragment.newInstance(mParcelableRestaurantDetails, usersList);
        addFragment();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onListFragmentInteraction(Result result, PlaceDetailsResponse placeDetailsResponse) {
        this.launchRestaurantActivity(result, placeDetailsResponse);
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
                this.retrievesTheRestaurant();
                break;
            case R.id.activity_home_drawer_settings:
                this.launchSettingsActivity();
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

    private void retrievesTheRestaurant() {
        if (user.getUserChoicePlaceId().equals("")) {
            Toast.makeText(this, getString(R.string.no_restaurant_selected), Toast.LENGTH_LONG).show();
        } else {
            Result result;
            PlaceDetailsResponse placeDetailsResponse;
            int l = 0;
            do {
                result = mParcelableRestaurantDetails.getNearbyResults().get(l);
                placeDetailsResponse = mParcelableRestaurantDetails.getPlaceDetailsResponses().get(l);
                l++;
            } while (!result.getPlaceId().equals(user.getUserChoicePlaceId()));
            this.launchRestaurantActivity(result, placeDetailsResponse);
        }
    }

    private void launchRestaurantActivity(Result result, PlaceDetailsResponse placeDetailsResponse) {
        Intent intent = new Intent(HomeActivity.this, RestaurantActivity.class);
        intent.putExtra(INTENT_EXTRA_RESULT, result);
        intent.putExtra(INTENT_EXTRA_PLACEDETAILSRESPONSE, placeDetailsResponse);
        startActivity(intent);
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void signOutUserFromFirebase() {
        mProgress.show();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, getString(R.string.fetch_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initProgressDialog() {
        mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.your_account_will_be_disconnected));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    //FIRESTORE\\
    private void setFirestoreListener() {
        UserHelper.listenerUsersCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> documentSnapshotList = new ArrayList<>(queryDocumentSnapshots.getDocuments());
                    usersList = new ArrayList<>();
                    if (documentSnapshotList.size() != 0) {
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            usersList.add(documentSnapshot.toObject(User.class));
                        }
                    }
                    if (fragment != null) {
                        fragment.newUsersForFragment(usersList);
                    } else {
                        initFirstFragment();
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }
        });
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
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //this.checkConnexion(locationManager, this);
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
                    public void onStatusChanged(String provider, int status, Bundle extras) { }

                    @Override
                    public void onProviderEnabled(String provider) { }

                    @Override
                    public void onProviderDisabled(String provider) { }
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 0);
            }
         }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                getMyCurrentLocation();
            }
        }
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
                    public void onError(Throwable e) { Log.e("TAG","On Error"+Log.getStackTraceString(e)); }

                    @Override
                    public void onComplete() {
                        Log.e("TAG","On Complete !!");
                    }
                });
    }

    private void updateList(NearbyResponse nearbyResponse) {
        mParcelableRestaurantDetails.setNearbyResults(nearbyResponse.getResults());
        for (Result result : mParcelableRestaurantDetails.getNearbyResults()) {
            PlaceDetailsResponse placeDetailsResponse = new PlaceDetailsResponse();
            this.getPlaceDetails(result.getPlaceId(), placeDetailsResponse);
            this.getPlacePhotos(result.getPlaceId(), placeDetailsResponse);
        }
    }

    private void getPlaceDetails(String placeId, PlaceDetailsResponse placeDetailsResponse) {
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
            //this.getPlacePhotos(placeId, placeDetailsResponse);
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
                    if (mParcelableRestaurantDetails.getNearbyResults().size() == mPlaceDetailsResponses.size()) {
                        mParcelableRestaurantDetails.setPlaceDetailsResponses(mPlaceDetailsResponses);
                        this.setFirestoreListener();
                    }
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

}
