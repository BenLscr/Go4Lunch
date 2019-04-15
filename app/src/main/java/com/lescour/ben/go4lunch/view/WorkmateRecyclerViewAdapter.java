package com.lescour.ben.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.WorkmatesListFragment.OnListFragmentInteractionListener;
import com.lescour.ben.go4lunch.model.firestore.User;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateRecyclerViewAdapter extends RecyclerView.Adapter<WorkmateRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<User> usersList;
    private final OnListFragmentInteractionListener mListener;
    private final RequestManager glide;
    private final Context context;

    public WorkmateRecyclerViewAdapter(ArrayList<User> usersList, OnListFragmentInteractionListener listener, RequestManager glide, Context context) {
        this.usersList = usersList;
        mListener = listener;
        this.glide = glide;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.user = usersList.get(position);

        if (holder.user.getUserChoicePlaceId().equals("")) {
            String workmateChoice = holder.user.getUserName() + " hasn't decided yet.";
            holder.workmateText.setText(workmateChoice);
            holder.workmateText.setTextColor(ContextCompat.getColor(context, R.color.quantum_grey));
        } else {
            String workmateChoice = holder.user.getUserName() + " wants to eat at " + holder.user.getUserChoiceRestaurantName() + ".";
            holder.workmateText.setText(workmateChoice);
        }

        if (holder.user.getUserUrlImage() != null) {
            glide.load(holder.user.getUserUrlImage()).apply(RequestOptions.circleCropTransform()).into(holder.workmateImage);
        } else {
            holder.workmateImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_account_circle_white_24));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.user.getUserChoicePlaceId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
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
