package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.view.WorkmatesRecyclerViewAdapter;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WorkmatesListFragment extends BaseFragment {

    private RecyclerView.Adapter mRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkmatesListFragment() {
    }

    public static WorkmatesListFragment newInstance(ParcelableRestaurantDetails mParcelableRestaurantDetails, ArrayList<User> usersList) {
        WorkmatesListFragment fragment = new WorkmatesListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARCELABLE_RESTAURANTDETAILS, mParcelableRestaurantDetails);
        args.putParcelableArrayList(ARG_USERSLIST, usersList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.mRecyclerViewAdapter = new WorkmatesRecyclerViewAdapter(mParcelableRestaurantDetails, usersList, mListener, Glide.with(this), context);
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
