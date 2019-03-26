package com.lescour.ben.go4lunch.controller.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment.OnListFragmentInteractionListener;
import com.lescour.ben.go4lunch.model.Result;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder> {

    private List<Result> results;
    private final OnListFragmentInteractionListener mListener;

    public RestaurantRecyclerViewAdapter(List<Result> results, OnListFragmentInteractionListener listener) {
        this.results = results;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.result = this.results.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.result);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.restaurant_name) TextView restaurantName;
        @BindView(R.id.restaurant_address) TextView restaurantAddress;
        @BindView(R.id.restaurant_open_hours) TextView restaurantOpenHours;
        @BindView(R.id.restaurant_distance) TextView restaurantDistance;
        @BindView(R.id.restaurant_number_of_person) TextView restaurantNumberOfPerson;
        @BindView(R.id.restaurant_rate) ImageView restaurantRate;
        @BindView(R.id.restaurant_image) ImageView restaurantImage;
        public Result result;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

}
