package com.lescour.ben.go4lunch.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.utils.NotificationJob;
import com.lescour.ben.go4lunch.utils.NotificationJobCreator;
import com.lescour.ben.go4lunch.utils.UserHelper;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.settings_user_image) ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        updateUserImage();
    }

    //USER IMAGE\\
    private void updateUserImage() {
        if (this.getCurrentUser() != null) {
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }
        }
    }

    //DELETE ACCOUNT BOUTON\\
    @OnClick(R.id.settings_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> deleteUserFromFirebase())
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            UserHelper.deleteUser(this.getCurrentUser().getUid())
                    .addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(SettingsActivity.this, AuthActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    //NOTIFICATION\\
    @OnCheckedChanged(R.id.notification_switch)
    public void onCheckedChangeListener (CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Log.e("tag", "do something");
            this.prepareAndroidJob();
        } else {
            Log.e("tag", "do an other thing");
            this.disableAndroidJob();
        }
    }

    private void prepareAndroidJob() {
        JobManager.create(this).addJobCreator(new NotificationJobCreator());
        DailyJob.schedule(new JobRequest.Builder(NotificationJob.TAG), TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(12));
    }

    private void disableAndroidJob() {
        //TODO : Disable JobManager
    }

}
