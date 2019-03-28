package com.lescour.ben.go4lunch.utils;

import com.lescour.ben.go4lunch.model.details.DetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.NearbyResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;
import com.lescour.ben.go4lunch.model.photo.PhotoResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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

    public static Observable<DetailsResponse> streamFetchDetailsSearch(String placeId,
                                                                       String apiKey){
        GoogleService googleService = GoogleService.retrofit.create(GoogleService.class);
        return googleService.getDetailsSearch(placeId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    /**public static Observable<DetailsResponse> streamFetchNearbySearchAndDetailsSearch(String location,
                                                                                             int radius,
                                                                                             String type,
                                                                                             String apiKey){
        return streamFetchNearbySearch(location, radius, type, apiKey)
                .map(new Function<NearbyResponse, List<Result>>() {
                    @Override
                    public List<Result> apply(NearbyResponse nearbyResponse) throws Exception {
                        return nearbyResponse.getResults();
                    }
                })
                .flatMap(new Function<List<Result>, Observable<DetailsResponse>>() {
                    @Override
                    public Observable<DetailsResponse> apply(List<Result> result) throws Exception {
                        return streamFetchDetailsSearch(result.get(0).getPlaceId(), apiKey);
                    }
                });
    }*/

    public static Observable<PhotoResponse> streamFetchPhoto(String maxWidth,
                                                                     String photoReference,
                                                                     String apiKey){
        GoogleService googleService = GoogleService.retrofitWithOutGson.create(GoogleService.class);
        return googleService.getPhoto(maxWidth, photoReference, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

}
