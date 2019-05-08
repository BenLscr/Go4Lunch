package com.lescour.ben.go4lunch.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.fragment.BaseFragment;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.ArrayList;

public class WorkmatesRecyclerViewAdapter extends BaseRecyclerViewAdapterWorkmates {

    private ParcelableRestaurantDetails mParcelableRestaurantDetails;
    private ArrayList<User> usersList;
    private BaseFragment.OnListFragmentInteractionListener mListener;
    private RequestManager glide;
    private Context context;

    public WorkmatesRecyclerViewAdapter(ParcelableRestaurantDetails mParcelableRestaurantDetails,
                                        ArrayList<User> usersList,
                                        BaseFragment.OnListFragmentInteractionListener listener,
                                        RequestManager glide,
                                        Context context) {
        this.mParcelableRestaurantDetails = mParcelableRestaurantDetails;
        this.usersList = usersList;
        this.mListener = listener;
        this.glide = glide;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderWorkmates holder, int position) {
        holder.user = usersList.get(position);

        if (holder.user.getUserChoicePlaceId().equals("")) {
            String workmateChoice = holder.user.getUserName() + context.getString(R.string.hasnt_decided_yet);
            holder.workmateText.setText(workmateChoice);
            holder.workmateText.setTextColor(ContextCompat.getColor(context, R.color.quantum_grey));
        } else {
            String workmateChoice = holder.user.getUserName() + context.getString(R.string. wants_to_eat_at) + holder.user.getUserChoiceRestaurantName() + ".";
            holder.workmateText.setText(workmateChoice);
        }

        if (holder.user.getUserUrlImage() != null) {
            glide.load(holder.user.getUserUrlImage()).apply(RequestOptions.circleCropTransform()).into(holder.workmateImage);
        } else {
            holder.workmateImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_account_circle_grey_24));
        }

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                if (!holder.user.getUserChoicePlaceId().equals("")) {
                    int j = 0;
                    Result result;
                    PlaceDetailsResponse placeDetailsResponse;
                    do {
                        result = mParcelableRestaurantDetails.getNearbyResults().get(j);
                        placeDetailsResponse = mParcelableRestaurantDetails.getPlaceDetailsResponses().get(j);
                        j++;
                    } while (!result.getPlaceId().equals(holder.user.getUserChoicePlaceId()));
                    mListener.onListFragmentInteraction(result, placeDetailsResponse);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
