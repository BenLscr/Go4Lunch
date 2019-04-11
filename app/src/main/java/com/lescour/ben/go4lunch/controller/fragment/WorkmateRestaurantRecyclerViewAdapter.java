package com.lescour.ben.go4lunch.controller.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.firestore.User;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<WorkmateRestaurantRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<User> listOfUserWithSameChoice;
    private final RequestManager glide;

    public WorkmateRestaurantRecyclerViewAdapter(ArrayList<User> listOfUserWithSameChoice, RequestManager glide) {
        this.listOfUserWithSameChoice = listOfUserWithSameChoice;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.user = listOfUserWithSameChoice.get(position);

        String workmateIsJoining = holder.user.getUserName() + " is joining !";
        holder.workmateText.setText(workmateIsJoining);
        if (holder.user.getUserUrlImage() != null) {
            glide.load(holder.user.getUserUrlImage()).apply(RequestOptions.circleCropTransform()).into(holder.workmateImage);
        }
    }

    @Override
    public int getItemCount() {
        return listOfUserWithSameChoice.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.workmate_image) ImageView workmateImage;
        @BindView(R.id.workmate_text) TextView workmateText;
        public User user;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
