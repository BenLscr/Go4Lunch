package com.lescour.ben.go4lunch.utils;

import com.lescour.ben.go4lunch.model.details.DetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.NearbyResponse;
import com.lescour.ben.go4lunch.model.photo.PhotoResponse;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by benja on 25/03/2019.
 */
public interface GoogleService {

    @GET("nearbysearch/json?")
    Observable<NearbyResponse> getNearbySearch(@Query("location") String location,
                                               @Query("radius") int radius,
                                               @Query("type") String type,
                                               @Query("key") String apiKey);

    @GET("details/json?")
    Observable<DetailsResponse> getDetailsSearch(@Query("placeid") String placeId,
                                                 @Query("key") String apiKey);

    @GET("photo?")
    Observable<PhotoResponse> getPhoto(@Query("maxwidth") String maxWidth,
                                       @Query("photoreference") String photoReference,
                                       @Query("key") String apiKey);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    Retrofit retrofitWithOutGson = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

}
