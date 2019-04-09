package com.lescour.ben.go4lunch.controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.CALL_PHONE;
import static com.lescour.ben.go4lunch.controller.HomeActivity.INTENT_EXTRA_PLACEDETAILSRESPONSE;
import static com.lescour.ben.go4lunch.controller.HomeActivity.INTENT_EXTRA_RESULT;
import static com.lescour.ben.go4lunch.controller.fragment.MapsFragment.INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS;
import static com.lescour.ben.go4lunch.controller.fragment.MapsFragment.INTENT_EXTRAS_RESULT_MAPS;

public class RestaurantActivity extends AppCompatActivity {

    @BindView(R.id.restaurant_activity_image) ImageView restaurantImage;
    @BindView(R.id.restaurant_activity_name) TextView restaurantName;
    @BindView(R.id.restaurant_activity_address) TextView restaurantAddress;
    @BindView(R.id.restaurant_activity_rate_1) ImageView restaurantRate1;
    @BindView(R.id.restaurant_activity_rate_2) ImageView restaurantRate2;
    @BindView(R.id.restaurant_activity_rate_3) ImageView restaurantRate3;
    @BindView(R.id.restaurant_activity_button_choice) ImageButton restaurantChoice;
    @BindView(R.id.restaurant_activity_button_call) Button restaurantCall;
    @BindView(R.id.restaurant_activity_button_like) Button restaurantLike;
    @BindView(R.id.restaurant_activity_button_website) Button restaurantWebsite;

    private Result mResult;
    private PlaceDetailsResponse mPlaceDetailsResponse;
    private ProcessRestaurantDetails mProcessRestaurantDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        this.searchIntent();

        this.updateUi();
    }

    private void searchIntent() {
        if (getIntent().hasExtra(INTENT_EXTRA_RESULT) && getIntent().hasExtra(INTENT_EXTRA_PLACEDETAILSRESPONSE)) {
            mResult = getIntent().getParcelableExtra(INTENT_EXTRA_RESULT);
            mPlaceDetailsResponse = getIntent().getParcelableExtra(INTENT_EXTRA_PLACEDETAILSRESPONSE);
        } else if (getIntent().hasExtra(INTENT_EXTRAS_RESULT_MAPS) && getIntent().hasExtra(INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS)) {
            mResult = getIntent().getParcelableExtra(INTENT_EXTRAS_RESULT_MAPS);
            mPlaceDetailsResponse = getIntent().getParcelableExtra(INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS);
        }
        mProcessRestaurantDetails = new ProcessRestaurantDetails(mResult, mPlaceDetailsResponse);
    }

    private void updateUi() {
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
    }

    @OnClick(R.id.restaurant_activity_button_choice)
    public void setChoice() {

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
                makeCall();
            }
        } else {
            Toast.makeText(this, "No number available for this restaurant...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case requestCodeCall: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else {
                    Toast.makeText(this, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "No website available for this restaurant...", Toast.LENGTH_LONG).show();
        }

    }
}
