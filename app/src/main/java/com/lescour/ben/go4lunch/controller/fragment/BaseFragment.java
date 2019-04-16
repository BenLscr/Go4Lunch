package com.lescour.ben.go4lunch.controller.fragment;

import com.lescour.ben.go4lunch.model.firestore.User;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

/**
 * Created by benja on 16/04/2019.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract void notifyRecyclerView(ArrayList<User> usersList);

    public void newDataForRecyclerView(ArrayList<User> usersList) {
        notifyRecyclerView(usersList);
    }
}
