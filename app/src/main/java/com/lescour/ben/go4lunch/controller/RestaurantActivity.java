package com.lescour.ben.go4lunch.controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.WorkmatesListRestaurantFragment;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.model.nearby.Result;
import com.lescour.ben.go4lunch.utils.UserHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.CALL_PHONE;
import static com.lescour.ben.go4lunch.controller.HomeActivity.INTENT_EXTRA_PLACEDETAILSRESPONSE;
import static com.lescour.ben.go4lunch.controller.HomeActivity.INTENT_EXTRA_RESULT;
import static com.lescour.ben.go4lunch.controller.fragment.MapsFragment.INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS;
import static com.lescour.ben.go4lunch.controller.fragment.MapsFragment.INTENT_EXTRAS_RESULT_MAPS;

public class RestaurantActivity extends BaseActivity {

    @BindView(R.id.restaurant_activity_image) ImageView restaurantImage;
    @BindView(R.id.restaurant_activity_name) TextView restaurantName;
    @BindView(R.id.restaurant_activity_address) TextView restaurantAddress;
    @BindView(R.id.restaurant_activity_rate_1) ImageView restaurantRate1;
    @BindView(R.id.restaurant_activity_rate_2) ImageView restaurantRate2;
    @BindView(R.id.restaurant_activity_rate_3) ImageView restaurantRate3;
    @BindView(R.id.restaurant_activity_button_choice) ImageView restaurantChoice;
    @BindView(R.id.restaurant_activity_button_call) Button restaurantCall;
    @BindView(R.id.restaurant_activity_button_like) Button restaurantLike;
    @BindView(R.id.restaurant_activity_button_website) Button restaurantWebsite;
    @BindView(R.id.activity_restaurant_progress_bar) ProgressBar mProgressBar;

    private WorkmatesListRestaurantFragment workmatesListRestaurantFragment;
    private Result mResult;
    private PlaceDetailsResponse mPlaceDetailsResponse;
    private ProcessRestaurantDetails mProcessRestaurantDetails;
    private List<String> userLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        this.retrievesIntent();
        this.createUi();
        this.setFirestoreListener();
    }

    private void retrievesIntent() {
        if (getIntent().hasExtra(INTENT_EXTRA_RESULT) && getIntent().hasExtra(INTENT_EXTRA_PLACEDETAILSRESPONSE)) {
            mResult = getIntent().getParcelableExtra(INTENT_EXTRA_RESULT);
            mPlaceDetailsResponse = getIntent().getParcelableExtra(INTENT_EXTRA_PLACEDETAILSRESPONSE);
        } else if (getIntent().hasExtra(INTENT_EXTRAS_RESULT_MAPS) && getIntent().hasExtra(INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS)) {
            mResult = getIntent().getParcelableExtra(INTENT_EXTRAS_RESULT_MAPS);
            mPlaceDetailsResponse = getIntent().getParcelableExtra(INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS);
        }
        mProcessRestaurantDetails = new ProcessRestaurantDetails(mResult, mPlaceDetailsResponse, getApplicationContext());
    }

    private void createUi() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (mPlaceDetailsResponse.getBitmap() != null) {
            restaurantImage.setImageBitmap(mProcessRestaurantDetails.getRestaurantImage());
        }
        restaurantName.setText(mProcessRestaurantDetails.getRestaurantName());
        restaurantAddress.setText(mProcessRestaurantDetails.getRestaurantAddress());
        if (mResult.getRating() != null) {
            restaurantRate1.setVisibility(mProcessRestaurantDetails.getRestaurantRate1());
            restaurantRate2.setVisibility(mProcessRestaurantDetails.getRestaurantRate2());
            restaurantRate3.setVisibility(mProcessRestaurantDetails.getRestaurantRate3());
        }
        if (user.getUserChoicePlaceId().equals(mResult.getPlaceId())) {
            restaurantChoice.setColorFilter(getResources().getColor(R.color.mainThemeColorValid));
        }
        userLike = new ArrayList<>();
        if (user.getUserLike() != null) {
            userLike.addAll(user.getUserLike());
            if (userLike.contains(mResult.getPlaceId())) {
                restaurantLike.setTextColor(getResources().getColor(R.color.mainThemeColorValid));
                restaurantLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.baseline_star_rate_green_24, 0, 0);
            }
        }
    }

    private void setFirestoreListener() {
        UserHelper.listenerUsersWhoHaveSameChoice(mResult.getPlaceId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> listOfWorkmatesWithSameChoice = new ArrayList<>(queryDocumentSnapshots.getDocuments());
                    ArrayList<User> listOfUserWithSameChoice = new ArrayList<>();
                    if (listOfWorkmatesWithSameChoice.size() != 0) {
                        int i = 0;
                        do {
                            listOfUserWithSameChoice.add(listOfWorkmatesWithSameChoice.get(i).toObject(User.class));
                            i++;
                        } while (listOfUserWithSameChoice.size() != listOfWorkmatesWithSameChoice.size());
                    }
                    if (workmatesListRestaurantFragment != null) {
                        workmatesListRestaurantFragment.notifyRecyclerView(listOfUserWithSameChoice);
                    } else {
                        addFragment(listOfUserWithSameChoice);
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void addFragment(ArrayList<User> listOfUserWithSameChoice) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        workmatesListRestaurantFragment = WorkmatesListRestaurantFragment.newInstance(listOfUserWithSameChoice);
        fragmentTransaction.add(R.id.container_restaurant_activity, workmatesListRestaurantFragment).commit();
    }

    //CHOICE BUTTON\\
    public void setChoice(View view) {
        mProgressBar.setVisibility(View.VISIBLE);
        if (user.getUserChoicePlaceId().equals("")) {
            user.setUserChoicePlaceId(mResult.getPlaceId());
            user.setUserChoiceRestaurantName(mResult.getName());
            String address = mPlaceDetailsResponse.getAddress();
            String addressCut = address.substring(0, address.indexOf(","));
            user.setUserChoiceRestaurantAddress(addressCut);
            restaurantChoice.setColorFilter(getResources().getColor(R.color.mainThemeColorValid));
        } else if (!user.getUserChoicePlaceId().equals("") && !user.getUserChoicePlaceId().equals(mResult.getPlaceId())) {
            user.setUserChoicePlaceId(mResult.getPlaceId());
            user.setUserChoiceRestaurantName(mResult.getName());
            String address = mPlaceDetailsResponse.getAddress();
            String addressCut = address.substring(0, address.indexOf(","));
            user.setUserChoiceRestaurantAddress(addressCut);
            restaurantChoice.setColorFilter(getResources().getColor(R.color.mainThemeColorValid));
        } else {
            user.setUserChoicePlaceId("");
            user.setUserChoiceRestaurantName("");
            user.setUserChoiceRestaurantAddress("");
            restaurantChoice.setColorFilter(getResources().getColor(R.color.quantum_white_100));
        }
        this.updateUserChoice();
    }

    private void updateUserChoice() {
        UserHelper.updateChoice(user.getUid(), user.getUserChoicePlaceId(), user.getUserChoiceRestaurantName(), user.getUserChoiceRestaurantAddress())
                .addOnFailureListener(this.onFailureListener());
    }

    //CALL BUTTON\\
    private static final int requestCodeCall = 123;

    @OnClick(R.id.restaurant_activity_button_call)
    public void callThisRestaurant() {
        if (mPlaceDetailsResponse.getPhoneNumber() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, requestCodeCall);
                }
            } else {
                this.makeCall();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_phone_number), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case requestCodeCall: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.makeCall();
                } else {
                    Toast.makeText(this, getString(R.string.not_allowed_to_call), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void makeCall() {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPlaceDetailsResponse.getPhoneNumber())));
    }

    //LIKE BUTTON\\
    @OnClick(R.id.restaurant_activity_button_like)
    public void likeThisRestaurant() {
        if (userLike != null && userLike.contains(mResult.getPlaceId())) {
            restaurantLike.setTextColor(getResources().getColor(R.color.mainThemeColorPrimary));
            restaurantLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.baseline_star_rate_orange_24, 0, 0);
            userLike.remove(mResult.getPlaceId());
            user.setUserLike(userLike);
        } else {
            restaurantLike.setTextColor(getResources().getColor(R.color.mainThemeColorValid));
            restaurantLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.baseline_star_rate_green_24, 0, 0);
            userLike.add(mResult.getPlaceId());
            user.setUserLike(userLike);
        }
        this.updateUserLike();
    }

    private void updateUserLike() {
        UserHelper.updateLike(user.getUid(), user.getUserLike()).addOnFailureListener(this.onFailureListener());
    }

    //WEBSITE BUTTON\\
    public static final String BUNDLE_EXTRA_URL = "BUNDLE_EXTRA_URL";

    @OnClick(R.id.restaurant_activity_button_website)
    public void openWebsiteOfThisRestaurant() {
        if (mPlaceDetailsResponse.getWebsiteUri() != null) {
            Intent webViewActivity = new Intent(this, WebViewActivity.class);
            webViewActivity.putExtra(BUNDLE_EXTRA_URL, mPlaceDetailsResponse.getWebsiteUri().toString());
            this.startActivity(webViewActivity);
        }
        else {
            Toast.makeText(this, getString(R.string.no_website), Toast.LENGTH_LONG).show();
        }
    }

}
