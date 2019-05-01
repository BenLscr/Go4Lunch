package com.lescour.ben.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.evernote.android.job.DailyJob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.controller.HomeActivity;
import com.lescour.ben.go4lunch.model.firestore.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by benja on 30/04/2019.
 */
public class NotificationJob extends DailyJob {

    public static final String TAG = "MyDailyJob";
    private final int NOTIFICATION_ID = 456;
    private final String NOTIFICATION_TAG = "FIREBASE";

    private String messageBody;
    private User user;
    private String userUid;

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {
        Log.e("JOB_ANDROID", "NOTIFICATION");
        this.getUserUidInSharedPreferences();
        this.retrievesUserData();
        return DailyJobResult.SUCCESS;
    }

    private void getUserUidInSharedPreferences() {
        SharedPreferences mSharedPreferences = getContext().getSharedPreferences("currentUser", MODE_PRIVATE);
        userUid = mSharedPreferences.getString("currentUserUid", null);
    }

    private void retrievesUserData() {
        UserHelper.getUser(userUid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                user = documentSnapshot.toObject(User.class);
                if (user != null && !user.getUserChoicePlaceId().equals("")) {
                    retrievesOtherUsersWithSameChoice();
                }
            }
        });
    }

    private void retrievesOtherUsersWithSameChoice() {
        ArrayList<User> listOfUserWithSameChoice = new ArrayList<>();
        UserHelper.getUsersWhoHaveSameChoice(user.getUserChoicePlaceId()).addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                List<DocumentSnapshot> listOfWorkmatesWithSameChoice = new ArrayList<>(queryDocumentSnapshots.getDocuments());
                if (listOfWorkmatesWithSameChoice.size() != 0) {
                    for (DocumentSnapshot documentSnapshot : listOfWorkmatesWithSameChoice) {
                        User userTemp = documentSnapshot.toObject(User.class);
                        if (userTemp != null && !user.getUid().equals(userTemp.getUid())) {
                            listOfUserWithSameChoice.add(documentSnapshot.toObject(User.class));
                        }
                    }
                }
            }
            buildBodyMessage(listOfUserWithSameChoice);
        });
    }

    private void buildBodyMessage(ArrayList<User> listOfUserWithSameChoice) {
        if (listOfUserWithSameChoice.size() != 0) {
            StringBuilder workmatesName = new StringBuilder();
            int i = 0;
            do {
                if (i == listOfUserWithSameChoice.size() -1) {
                    workmatesName.append(listOfUserWithSameChoice.get(i).getUserName()).append(".");
                } else {
                    workmatesName.append(listOfUserWithSameChoice.get(i).getUserName()).append(", ");
                }
                i++;
            } while (i != listOfUserWithSameChoice.size());
            messageBody = user.getUserChoiceRestaurantName() + getContext().getString(R.string.located_at) + user.getUserChoiceRestaurantAddress() + getContext().getString(R.string.with) + workmatesName.toString();
        } else {
            messageBody = user.getUserChoiceRestaurantName() + getContext().getString(R.string.located_at) + user.getUserChoiceRestaurantAddress() + ".";
        }
        this.sendVisualNotification();
    }

    private void sendVisualNotification() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getContext().getString(R.string.big_content_title));
        inboxStyle.addLine(messageBody);

        String channelId = getContext().getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext(), channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(getContext().getString(R.string.app_name))
                        .setContentText(getContext().getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getContext().getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

}
