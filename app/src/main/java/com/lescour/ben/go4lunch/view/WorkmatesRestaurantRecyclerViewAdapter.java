package com.lescour.ben.go4lunch.view;

import android.content.Context;
import android.content.res.Resources;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.firestore.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WorkmatesRestaurantRecyclerViewAdapter extends BaseRecyclerViewAdapterWorkmates {

    private ArrayList<User> listOfUserWithSameChoice;
    private RequestManager glide;
    private Context context;

    public WorkmatesRestaurantRecyclerViewAdapter(ArrayList<User> listOfUserWithSameChoice, RequestManager glide, Context context) {
        this.listOfUserWithSameChoice = listOfUserWithSameChoice;
        this.glide = glide;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderWorkmates holder, int position) {
        holder.user = listOfUserWithSameChoice.get(position);

        String workmateIsJoining = holder.user.getUserName() + context.getString(R.string.is_joining);
        holder.workmateText.setText(workmateIsJoining);
        if (holder.user.getUserUrlImage() != null) {
            glide.load(holder.user.getUserUrlImage()).apply(RequestOptions.circleCropTransform()).into(holder.workmateImage);
        } else {
            holder.workmateImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_account_circle_grey_24));
        }
    }

    @Override
    public int getItemCount() {
        return listOfUserWithSameChoice.size();
    }

}
