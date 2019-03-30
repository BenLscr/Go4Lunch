package com.lescour.ben.go4lunch.controller.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.ProcessRestaurantDetails;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment.OnListFragmentInteractionListener;
import com.lescour.ben.go4lunch.model.details.DetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder> {

    private List<Result> nearbyResults;
    private List<DetailsResponse> detailsResponses;
    private final OnListFragmentInteractionListener mListener;
    private RequestManager glide;

    public RestaurantRecyclerViewAdapter(List<Result> nearbyResults, List<DetailsResponse> detailsResponses, OnListFragmentInteractionListener listener, RequestManager glide) {
        this.nearbyResults = nearbyResults;
        this.detailsResponses = detailsResponses;
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
        holder.detailsResponse = this.detailsResponses.get(position);

        ProcessRestaurantDetails restaurantDetails = new ProcessRestaurantDetails(holder.nearbyResult, holder.detailsResponse);
        holder.restaurantName.setText(restaurantDetails.getRestaurantName());
        holder.restaurantAddress.setText(restaurantDetails.getRestaurantAddress());
        holder.restaurantOpenHours.setText(restaurantDetails.getRestaurantOpenHours());
        if (holder.nearbyResult.getRating() != null) {
            holder.restaurantRate1.setVisibility(restaurantDetails.getRestaurantRate1());
            holder.restaurantRate2.setVisibility(restaurantDetails.getRestaurantRate2());
            holder.restaurantRate3.setVisibility(restaurantDetails.getRestaurantRate3());
        }
        if (holder.nearbyResult.getPhotos() != null) {
            glide.load(restaurantDetails.getRestaurantImage()).into(holder.restaurantImage);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.detailsResponse);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailsResponses.size();
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
        public DetailsResponse detailsResponse;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

}
