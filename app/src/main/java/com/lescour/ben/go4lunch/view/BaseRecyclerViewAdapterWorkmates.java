package com.lescour.ben.go4lunch.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lescour.ben.go4lunch.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by benja on 16/04/2019.
 */
public abstract class BaseRecyclerViewAdapterWorkmates extends RecyclerView.Adapter<ViewHolderWorkmates>{

    @NonNull
    @Override
    public ViewHolderWorkmates onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmate, parent, false);
        return new ViewHolderWorkmates(view);
    }

}
