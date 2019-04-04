package com.lescour.ben.go4lunch.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.ProcessRestaurantDetails;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment.OnListFragmentInteractionListener;
import com.lescour.ben.go4lunch.model.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder> {

    private List<Result> nearbyResults;
    private List<PlaceDetailsResponse> placeDetailsResponses;
    private final OnListFragmentInteractionListener mListener;
    private RequestManager glide;

    public RestaurantRecyclerViewAdapter(List<Result> nearbyResults, List<PlaceDetailsResponse> placeDetailsResponses, OnListFragmentInteractionListener listener, RequestManager glide) {
        this.nearbyResults = nearbyResults;
        this.placeDetailsResponses = placeDetailsResponses;
        mListener = listener;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nearbyResult = this.nearbyResults.get(position);
        holder.placeDetailsResponse = this.placeDetailsResponses.get(position);

        ProcessRestaurantDetails restaurantDetails = new ProcessRestaurantDetails(holder.nearbyResult, holder.placeDetailsResponse);
        holder.restaurantName.setText(restaurantDetails.getRestaurantName());
        holder.restaurantAddress.setText(restaurantDetails.getRestaurantAddress());
        holder.restaurantOpenHours.setText(restaurantDetails.getRestaurantOpenHours());
        if (holder.nearbyResult.getRating() != null) {
            holder.restaurantRate1.setVisibility(restaurantDetails.getRestaurantRate1());
            holder.restaurantRate2.setVisibility(restaurantDetails.getRestaurantRate2());
            holder.restaurantRate3.setVisibility(restaurantDetails.getRestaurantRate3());
        }
        if (holder.placeDetailsResponse.getBitmap() != null) {
            holder.restaurantImage.setImageBitmap(restaurantDetails.getRestaurantImage());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.nearbyResult, holder.placeDetailsResponse);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearbyResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.restaurant_name) TextView restaurantName;
        @BindView(R.id.restaurant_address) TextView restaurantAddress;
        @BindView(R.id.restaurant_open_hours) TextView restaurantOpenHours;
        @BindView(R.id.restaurant_distance) TextView restaurantDistance;
        @BindView(R.id.restaurant_number_of_person) TextView restaurantNumberOfPerson;
        @BindView(R.id.restaurant_rate_1) ImageView restaurantRate1;
        @BindView(R.id.restaurant_rate_2) ImageView restaurantRate2;
        @BindView(R.id.restaurant_rate_3) ImageView restaurantRate3;
        @BindView(R.id.restaurant_image) ImageView restaurantImage;
        public Result nearbyResult;
        public PlaceDetailsResponse placeDetailsResponse;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

}
