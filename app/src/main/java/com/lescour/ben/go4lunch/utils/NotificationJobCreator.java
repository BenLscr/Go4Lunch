package com.lescour.ben.go4lunch.utils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by benja on 30/04/2019.
 */
public class NotificationJobCreator implements JobCreator {

    @Nullable
    @Override
    public Job create(@NonNull String s) {
        return new NotificationJob();
    }

}
