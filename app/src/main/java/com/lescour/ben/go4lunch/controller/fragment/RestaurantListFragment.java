package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.model.nearby.Result;
import com.lescour.ben.go4lunch.view.RestaurantRecyclerViewAdapter;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RestaurantListFragment extends BaseFragment {

    private static final String ARG_PARCELABLE_RESTAURANTDETAILS = "parcelable_restaurantdetails";
    private static final String ARG_USERSLIST = "USERSLIST";
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private OnListFragmentInteractionListener mListener;

    private ParcelableRestaurantDetails mParcelableRestaurantDetails;
    private ArrayList<User> usersList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantListFragment() {
    }

    public static RestaurantListFragment newInstance(ParcelableRestaurantDetails mParcelableRestaurantDetails, ArrayList<User> usersList) {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARCELABLE_RESTAURANTDETAILS, mParcelableRestaurantDetails);
        args.putParcelableArrayList(ARG_USERSLIST, usersList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParcelableRestaurantDetails = getArguments().getParcelable(ARG_PARCELABLE_RESTAURANTDETAILS);
            this.usersList = new ArrayList<>();
            this.usersList = getArguments().getParcelableArrayList(ARG_USERSLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.mRecyclerViewAdapter = new RestaurantRecyclerViewAdapter(this.mParcelableRestaurantDetails, this.usersList, mListener);
            recyclerView.setAdapter(this.mRecyclerViewAdapter);
        }

        return view;
    }

    public void notifyRecyclerView(ArrayList<User> usersList) {
        this.usersList.clear();
        this.usersList.addAll(usersList);
        this.mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Result result, PlaceDetailsResponse placeDetailsResponse);
    }

}
