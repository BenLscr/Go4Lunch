package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lescour.ben.go4lunch.BuildConfig;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.ParcelableLocation;
import com.lescour.ben.go4lunch.model.details.DetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.NearbyResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;
import com.lescour.ben.go4lunch.utils.GoogleStreams;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RestaurantListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private RecyclerView.Adapter mRecyclerViewAdapter;
    private List<Result> nearbyResults;
    private List<DetailsResponse> detailsResponses;
    private Disposable disposable;

    private ParcelableLocation mParcelableLocation;
    private String stringLocation;
    private int radius = 3500;
    private String type = "restaurant";
    private String apiKey = BuildConfig.PLACES_API_KEY;

    private int i = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantListFragment() {
    }

    public static RestaurantListFragment newInstance(int columnCount) {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        this.nearbyResults = new ArrayList<>();
        this.detailsResponses = new ArrayList<>();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.mRecyclerViewAdapter = new RestaurantRecyclerViewAdapter(this.nearbyResults, this.detailsResponses, mListener, Glide.with(this));
            recyclerView.setAdapter(this.mRecyclerViewAdapter);
        }
        this.setParcelableLocation();
        this.buildStringLocation();
        this.executeHttpRequestWithRetrofit_NearbySearch();
        return view;
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
        void onListFragmentInteraction(DetailsResponse detailsResponse);
    }

    private void setParcelableLocation() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("HomeToFragment")) {
            mParcelableLocation = bundle.getParcelable("HomeToFragment");
        }
    }

    private void buildStringLocation() {
        stringLocation = mParcelableLocation.getLatitude() + "," + mParcelableLocation.getLongitude();
    }

    //HTTP REQUEST\\
    private void executeHttpRequestWithRetrofit_NearbySearch(){
        this.disposable = GoogleStreams.streamFetchNearbySearch(stringLocation, radius, type, apiKey)
                .subscribeWith(new DisposableObserver<NearbyResponse>() {
                    @Override
                    public void onNext(NearbyResponse nearbyResponse) {
                        Log.e("TAG","On Next");
                        updateUI(nearbyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG","On Error"+Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG","On Complete !!");
                    }
                });
    }

    private void updateUI(NearbyResponse nearbyResponse) {
        nearbyResults.addAll(nearbyResponse.getResults());
        executeHttpRequestWithRetrofit_DetailsSearch(nearbyResults.get(i).getPlaceId());
    }

    private void executeHttpRequestWithRetrofit_DetailsSearch(String placeId){
        this.disposable = GoogleStreams.streamFetchDetailsSearch(placeId, apiKey)
                .subscribeWith(new DisposableObserver<DetailsResponse>() {
                    @Override
                    public void onNext(DetailsResponse detailsResponse) {
                        Log.e("TAG","On Next");
                        detailsResponses.add(detailsResponse);
                        notifyRecyclerViewAdapter_WhenEveryRequestAreDone();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG","On Error"+Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG","On Complete !!");
                    }
                });
    }

    private void notifyRecyclerViewAdapter_WhenEveryRequestAreDone() {
        if (nearbyResults.size() == detailsResponses.size()) {
            mRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            i++;
            executeHttpRequestWithRetrofit_DetailsSearch(nearbyResults.get(i).getPlaceId());
        }
    }
}
