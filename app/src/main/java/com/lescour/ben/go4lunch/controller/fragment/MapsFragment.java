package com.lescour.ben.go4lunch.controller.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private GoogleMap mMap;

    private List<Result> nearbyResults;
    private List<PlaceDetailsResponse> placeDetailsResponses;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setParcelableLocation();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        return v;
    }

    private void setParcelableLocation() {
        this.nearbyResults = new ArrayList<>();
        this.placeDetailsResponses = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("HomeToFragment")) {
            ParcelableRestaurantDetails mParcelableRestaurantDetails = bundle.getParcelable("HomeToFragment");
            if (mParcelableRestaurantDetails != null) {
                nearbyResults = mParcelableRestaurantDetails.getNearbyResults();
                placeDetailsResponses = mParcelableRestaurantDetails.getPlaceDetailsResponses();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng dieppe = new LatLng(49.9228, 1.07768);
        mMap.addMarker(new MarkerOptions().position(dieppe).title("Marker in Dieppe"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dieppe, 15));

        for (int i = 0; nearbyResults.size() > i; i++) {
            LatLng restaurant = new LatLng(nearbyResults.get(i).getGeometry().getLocation().getLat(), nearbyResults.get(i).getGeometry().getLocation().getLng());
            mMap.addMarker(new MarkerOptions().position(restaurant).title(nearbyResults.get(i).getName()));
        }
    }
}
