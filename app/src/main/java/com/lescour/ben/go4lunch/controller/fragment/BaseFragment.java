package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;

import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

/**
 * Created by benja on 16/04/2019.
 */
public abstract class BaseFragment extends Fragment {

    static final String ARG_PARCELABLE_RESTAURANTDETAILS = "PARCELABLE_RESTAURANTDETAILS";
    static final String ARG_USERSLIST = "USERSLIST";

    OnListFragmentInteractionListener mListener;
    ParcelableRestaurantDetails mParcelableRestaurantDetails;
    ArrayList<User> usersList;

    //CUSTOM\\
    protected abstract void notifyFragment();

    public void newDataForFragment(ArrayList<User> usersList) {
        this.usersList.clear();
        this.usersList.addAll(usersList);
        notifyFragment();
    }

    //OVERRIDE\\
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
