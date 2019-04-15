package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.view.WorkmateRestaurantRecyclerViewAdapter;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class WorkmatesListRestaurantFragment extends Fragment {

    private ArrayList<User> listOfUserWithSameChoice;
    private static final String ARG_LISTOF_USERS = "LISTOF_USERS";
    private RecyclerView.Adapter mRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkmatesListRestaurantFragment() {
    }

    public static WorkmatesListRestaurantFragment newInstance(ArrayList<User> listOfUserWithSameChoice) {
        WorkmatesListRestaurantFragment fragment = new WorkmatesListRestaurantFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LISTOF_USERS, listOfUserWithSameChoice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.listOfUserWithSameChoice = new ArrayList<>();
            this.listOfUserWithSameChoice = getArguments().getParcelableArrayList(ARG_LISTOF_USERS);
        }
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
            this.mRecyclerViewAdapter = new WorkmateRestaurantRecyclerViewAdapter(this.listOfUserWithSameChoice, Glide.with(this), getContext());
            recyclerView.setAdapter(this.mRecyclerViewAdapter);
        }

        return view;
    }

    public void notifyRecyclerView(ArrayList<User> listOfUserWithSameChoice) {
        this.listOfUserWithSameChoice.clear();
        this.listOfUserWithSameChoice.addAll(listOfUserWithSameChoice);
        this.mRecyclerViewAdapter.notifyDataSetChanged();
    }
}
