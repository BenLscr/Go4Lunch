package com.lescour.ben.go4lunch.controller.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.RestaurantActivity;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private List<Result> nearbyResults;
    private List<PlaceDetailsResponse> placeDetailsResponses;

    public static final String INTENT_EXTRAS_RESULT_MAPS = "INTENT_EXTRAS_RESULT_MAPS";
    public static final String INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS = "INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MapsFragment() {
    }

    public static MapsFragment newInstance(ParcelableRestaurantDetails mParcelableRestaurantDetails, ArrayList<User> usersList) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARCELABLE_RESTAURANTDETAILS, mParcelableRestaurantDetails);
        args.putParcelableArrayList(ARG_USERSLIST, usersList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.nearbyResults = new ArrayList<>();
        this.placeDetailsResponses = new ArrayList<>();
        if (getArguments() != null) {
            mParcelableRestaurantDetails = getArguments().getParcelable(ARG_PARCELABLE_RESTAURANTDETAILS);
            if (mParcelableRestaurantDetails != null) {
                nearbyResults = mParcelableRestaurantDetails.getNearbyResults();
                placeDetailsResponses = mParcelableRestaurantDetails.getPlaceDetailsResponses();
            }
            this.usersList = new ArrayList<>();
            this.usersList = getArguments().getParcelableArrayList(ARG_USERSLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLocation = new LatLng(mParcelableRestaurantDetails.getCurrentLat(), mParcelableRestaurantDetails.getCurrentLng());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("My position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        for (int i = 0; nearbyResults.size() > i; i++) {
            this.setMarker(mMap, i);
        }
        mMap.setOnMarkerClickListener(this);
    }

    private void setMarker(GoogleMap mMap, int position) {
        LatLng restaurant = new LatLng(nearbyResults.get(position).getGeometry().getLocation().getLat(), nearbyResults.get(position).getGeometry().getLocation().getLng());
        mMap.addMarker(new MarkerOptions().position(restaurant)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_orange_24)))
                .setTag(position);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int position = (int) marker.getTag();
        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
        intent.putExtra(INTENT_EXTRAS_RESULT_MAPS, nearbyResults.get(position));
        intent.putExtra(INTENT_EXTRAS_PLACEDETAILSRESPONSE_MAPS, placeDetailsResponses.get(position));
        startActivity(intent);
        return false;
    }

    public void notifyFragment() {
        //this.mRecyclerViewAdapter.notifyDataSetChanged();
    }

}
