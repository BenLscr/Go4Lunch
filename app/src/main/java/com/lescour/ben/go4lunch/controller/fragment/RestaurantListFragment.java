package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.view.RestaurantRecyclerViewAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RestaurantListFragment extends BaseFragment {

    private RecyclerView.Adapter mRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantListFragment() {
    }

    public static RestaurantListFragment newInstance(ParcelableRestaurantDetails mParcelableRestaurantDetails,
                                                     ArrayList<User> usersList) {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARCELABLE_RESTAURANTDETAILS, mParcelableRestaurantDetails);
        args.putParcelableArrayList(ARG_USERSLIST, usersList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.mRecyclerViewAdapter = new RestaurantRecyclerViewAdapter(this.mParcelableRestaurantDetails,
                    this.usersList, context, mListener);
            recyclerView.setAdapter(this.mRecyclerViewAdapter);
        }
        return view;
    }

    /**
     * Notify fragment that the data has changed.
     */
    public void notifyFragment() {
        this.mRecyclerViewAdapter.notifyDataSetChanged();
    }

}
