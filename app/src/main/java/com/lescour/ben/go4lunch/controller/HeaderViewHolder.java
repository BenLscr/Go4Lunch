package com.lescour.ben.go4lunch.controller;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lescour.ben.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by benja on 18/03/2019.
 */
public class HeaderViewHolder extends BaseActivity {

    @BindView(R.id.user_image) ImageView userImage;
    @BindView(R.id.user_name) TextView userName;
    @BindView(R.id.user_mail) TextView userMail;

    private Context context;
    private View view;

    /**
     * Constructor of the header of menu drawer.
     */
    public HeaderViewHolder(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this, view);
    }

    protected void updateMainMenuWithUserInfo() {

        if (this.getCurrentUser() != null) {

            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(context)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }

            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String name = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_name_found) : this.getCurrentUser().getDisplayName();

            userMail.setText(email);
            userName.setText(name);
        }
    }

}
