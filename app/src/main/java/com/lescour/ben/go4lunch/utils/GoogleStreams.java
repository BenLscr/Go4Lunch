package com.lescour.ben.go4lunch.utils;

import com.lescour.ben.go4lunch.model.nearby.NearbyResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by benja on 25/03/2019.
 */
public class GoogleStreams {

    public static Observable<NearbyResponse> streamFetchNearbySearch(String location,
                                                                     int radius,
                                                                     String type,
                                                                     String apiKey){
        GoogleService googleService = GoogleService.retrofit.create(GoogleService.class);
        return googleService.getNearbySearch(location, radius, type, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    /**public static Observable<DetailsResponse> streamFetchDetailsSearch(String placeId,
                                                                       String apiKey){
        GoogleService googleService = GoogleService.retrofit.create(GoogleService.class);
        return googleService.getDetailsSearch(placeId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<DetailsResponse> streamFetchNearbySearchAndDetailsSearch(String location,
                                                                                      int radius,
                                                                                      String type,
                                                                                      String apiKey){
        return streamFetchNearbySearch(location, radius, type, apiKey)
                .map(NearbyResponse::getResults)
                .flatMapIterable(results -> results)
                .flatMap(result -> streamFetchDetailsSearch(result.getPlaceId(), apiKey));
    }*/

}
