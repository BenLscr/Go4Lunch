package com.lescour.ben.go4lunch.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.firestore.User;

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
public class WorkmatesListRestaurantFragment extends Fragment {

    private ArrayList<User> listOfUserWithSameChoice;
    private static final String ARG_LISTOF_USERS = "LISTOF_USERS";

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
            listOfUserWithSameChoice = new ArrayList<>();
            listOfUserWithSameChoice = getArguments().getParcelableArrayList(ARG_LISTOF_USERS);
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
            recyclerView.setAdapter(new WorkmateRestaurantRecyclerViewAdapter(listOfUserWithSameChoice, Glide.with(this)));
        }
        return view;
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
        void onListFragmentInteraction(User user);
    }
}
