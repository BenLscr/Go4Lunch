package com.lescour.ben.go4lunch.controller;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.close_autocomplete) ImageButton mCloseAutocomplete;

    private BaseFragment fragment;
    private ProgressDialog mProgress;
    private ActionBarDrawerToggle toggle;

    private Disposable disposable;

    private Boolean allDataAreAvailable = false ;
    private Double currentLat;
    private Double currentLng;
    private List<PlaceDetailsResponse> mPlaceDetailsResponses;
    private List<Bitmap> mBitmapList;
    private ArrayList<User> usersList;
    private ParcelableRestaurantDetails mParcelableRestaurantDetails;
    private ParcelableRestaurantDetails saveParcelableRestaurantDetails;
    private long MIN_TIME_FOR_UPDATES = 9999999;
    private long MIN_DISTANCE_FOR_UPDATES = 200;

    private PlacesClient placesClient;

    private String stringLocation;
    private int radius = 1000;
    private String type = "restaurant";
    private String apiKey = BuildConfig.PLACES_API_KEY;

    private int i = 0;
    private int k = 0;

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

        this.initMapsFragment();

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
        if (item.getItemId() == R.id.menu_activity_home_search) {
            if (mCardView.getVisibility() == View.GONE) {
                this.buttonSearch();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void buttonSearch() {
        mCardView.setVisibility(View.VISIBLE);
        toggle.setDrawerIndicatorEnabled(false);
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
            LatLng center = new LatLng(currentLat, currentLng);
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
                ArrayList<Bitmap> bitmapList = new ArrayList<>();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    int z = 0;
                    do {
                        if (saveParcelableRestaurantDetails.getNearbyResults().get(z).getPlaceId().equals(prediction.getPlaceId())) {
                            results.add(saveParcelableRestaurantDetails.getNearbyResults().get(z));
                            placeDetailsResponses.add(saveParcelableRestaurantDetails.getPlaceDetailsResponses().get(z));
                            bitmapList.add(saveParcelableRestaurantDetails.getBitmapList().get(z));
                        }
                        z++;
                    } while (saveParcelableRestaurantDetails.getNearbyResults().size() != z);
                }
                mParcelableRestaurantDetails.setNearbyResults(results);
                mParcelableRestaurantDetails.setPlaceDetailsResponses(placeDetailsResponses);
                mParcelableRestaurantDetails.setBitmapList(bitmapList);
                fragment.newRestaurantsForFragment(mParcelableRestaurantDetails);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                }
            });
        }
    }

    /**
     * Call when the user click on the cross of the autocomplete bar.
     * Hide the autocomplete bar and notify the fragment to be update.
     */
    @OnClick(R.id.close_autocomplete)
    public void closeAutocomplete() {
        if (mCardView.getVisibility() == View.VISIBLE) {
            mCardView.setVisibility(View.GONE);
            toggle.setDrawerIndicatorEnabled(true);
            revertParcelableRestaurantDetails();
            fragment.newRestaurantsForFragment(mParcelableRestaurantDetails);
        }
    }

    private void saveParcelableRestaurantDetails() {
        saveParcelableRestaurantDetails = new ParcelableRestaurantDetails();
        saveParcelableRestaurantDetails.setNearbyResults(mParcelableRestaurantDetails.getNearbyResults());
        saveParcelableRestaurantDetails.setPlaceDetailsResponses(mParcelableRestaurantDetails.getPlaceDetailsResponses());
        saveParcelableRestaurantDetails.setBitmapList(mParcelableRestaurantDetails.getBitmapList());
    }

    private void revertParcelableRestaurantDetails() {
        mParcelableRestaurantDetails.setNearbyResults(saveParcelableRestaurantDetails.getNearbyResults());
        mParcelableRestaurantDetails.setPlaceDetailsResponses(saveParcelableRestaurantDetails.getPlaceDetailsResponses());
        mParcelableRestaurantDetails.setBitmapList(saveParcelableRestaurantDetails.getBitmapList());
    }

    //BOTTOM TOOLBAR\\
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        switch (item.getItemId()) {
            case R.id.navigation_map:
                initMapsFragment();
                return true;
            case R.id.navigation_list_restaurant:
                fragment = RestaurantListFragment.newInstance();
                addFragment();
                return true;
            case R.id.navigation_workmates:
                fragment = WorkmatesListFragment.newInstance();
                addFragment();
                return true;
        }
        return false;
    };

    //FRAGMENT\\
    private void initMapsFragment() {
        fragment = MapsFragment.newInstance();
        addFragment();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    public void recoversData() {
        if (allDataAreAvailable ) {
            fragment.shareDataToFragment(currentLat, currentLng, mParcelableRestaurantDetails, usersList);
        }
    }

    /**
     * Call when the user click on a restaurant or a workmate. Launch a new activity to see
     * restaurant details.
     */
    @Override
    public void onListFragmentInteraction(Result result, PlaceDetailsResponse placeDetailsResponse) {
        this.launchRestaurantActivity(result, placeDetailsResponse);
    }

    //MAIN MENU\\
    private void configureDrawerLayout() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
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
     * Call when an item is selected. Display the associate activity.
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
        } else if (!allDataAreAvailable){
            Toast.makeText(this, getString(R.string.restaurants_not_load), Toast.LENGTH_LONG).show();
        } else {
            Result result;
            PlaceDetailsResponse placeDetailsResponse;
            int l = 0;
            do {
                result = mParcelableRestaurantDetails.getNearbyResults().get(l);
                placeDetailsResponse = mParcelableRestaurantDetails.getPlaceDetailsResponses().get(l);
                l++;
            } while (l != mParcelableRestaurantDetails.getNearbyResults().size() && !result.getPlaceId().equals(user.getUserChoicePlaceId()));
            if (result.getPlaceId().equals(user.getUserChoicePlaceId())) {
                this.launchRestaurantActivity(result, placeDetailsResponse);
            } else {
                Toast.makeText(this, "You have took a restaurant in an other area.", Toast.LENGTH_LONG).show();
            }
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
        UserHelper.listenerUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                List<DocumentSnapshot> documentSnapshotList = new ArrayList<>(queryDocumentSnapshots.getDocuments());
                usersList = new ArrayList<>();
                if (documentSnapshotList.size() != 0) {
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        usersList.add(documentSnapshot.toObject(User.class));
                    }
                }
                //fragment.newUsersForFragment(usersList);
                allDataAreAvailable = true;
                recoversData();
            }
            mProgressBar.setVisibility(View.GONE);
        });
    }

    //REQUEST\\
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
                this.checkConnexion(locationManager, this);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES,
                        MIN_DISTANCE_FOR_UPDATES, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mParcelableRestaurantDetails.setNearbyResults(new ArrayList<>());
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        fragment.setPosition(currentLat, currentLng);
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
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 159);
            }
         }
    }

    /**
     * Call when the user answer for permission. The activity receive a requestCode.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 159: {
                this.getMyCurrentLocation();
            }
        }
    }

    private void checkConnexion(LocationManager locationManager, Context context) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(context)
                    .setTitle(getString(R.string.gps_not_found))
                    .setMessage(getString(R.string.want_to_enable))
                    .setPositiveButton(getString(R.string.popup_message_choice_yes), (dialogInterface, i) ->
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton(getString(R.string.popup_message_choice_no), null)
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
        mPlaceDetailsResponses = new ArrayList<>();
        this.getPlaceDetails(mParcelableRestaurantDetails.getNearbyResults().get(i).getPlaceId());
    }

    private void getPlaceDetails(String placeId) {
        PlaceDetailsResponse placeDetailsResponse = new PlaceDetailsResponse();
        // Specify the fields to return (in this example all fields are returned).
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.OPENING_HOURS,
                Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeDetailsResponse.setName(place.getName());
            placeDetailsResponse.setOpeningHours(place.getOpeningHours());
            placeDetailsResponse.setAddress(place.getAddress());
            placeDetailsResponse.setPhoneNumber(place.getPhoneNumber());
            placeDetailsResponse.setWebsiteUri(place.getWebsiteUri());
            mPlaceDetailsResponses.add(placeDetailsResponse);
            this.everyPlaceDetailsResponsesAreReceived();
        }).addOnFailureListener(this.onFailureListener());
    }

    private void everyPlaceDetailsResponsesAreReceived() {
        if (mParcelableRestaurantDetails.getNearbyResults().size() == mPlaceDetailsResponses.size()) {
            mParcelableRestaurantDetails.setPlaceDetailsResponses(mPlaceDetailsResponses);
            mBitmapList = new ArrayList<>();
            this.getPlacePhotos(mParcelableRestaurantDetails.getNearbyResults().get(k).getPlaceId());
        } else {
            i++;
            this.getPlaceDetails(mParcelableRestaurantDetails.getNearbyResults().get(i).getPlaceId());
        }
    }

    private void getPlacePhotos(String placeId) {
        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            if (place.getPhotoMetadatas() != null) {
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(120)
                        .setMaxHeight(120)
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    mBitmapList.add(bitmap);
                    this.everyPlacePhotosResponsesAreReceived();
                }).addOnFailureListener(this.onFailureListener());
            } else {
                mBitmapList.add(null);
                this.everyPlacePhotosResponsesAreReceived();
            }
        });
    }

    private void everyPlacePhotosResponsesAreReceived() {
        if (mParcelableRestaurantDetails.getNearbyResults().size() == mBitmapList.size()) {
            mParcelableRestaurantDetails.setBitmapList(mBitmapList);
            this.setFirestoreListener();
        } else {
            k++;
            this.getPlacePhotos(mParcelableRestaurantDetails.getNearbyResults().get(k).getPlaceId());
        }
    }

}
