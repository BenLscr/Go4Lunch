package com.lescour.ben.go4lunch.controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.lescour.ben.go4lunch.controller.HomeActivity.INTENT_EXTRA_PLACEDETAILSRESPONSE;
import static com.lescour.ben.go4lunch.controller.HomeActivity.INTENT_EXTRA_RESULT;

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
            mProcessRestaurantDetails = new ProcessRestaurantDetails(mResult, mPlaceDetailsResponse);
        }
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

    @OnClick(R.id.restaurant_activity_button_call)
    public void callThisRestaurant() {

    }

    @OnClick(R.id.restaurant_activity_button_like)
    public void likeThisRestaurant() {

    }

    @OnClick(R.id.restaurant_activity_button_website)
    public void openWebsiteOfThisRestaurant() {

    }
}
