package by.intervale.akella266.weather2.api;

import by.intervale.akella266.weather2.api.models.Response;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("data/2.5/forecast")
    Single<Response> getWeatherByCityId(@Query("id") String cityId,
                                          @Query("units") String units,
                                          @Query("APPID") String key);

    @GET("data/2.5/group")
    Single<Response> getWeatherGroupCities(@Query("id") String id,
                                                @Query("units") String units,
                                                @Query("APPID") String key);

    @GET("data/2.5/find")
    Single<Response> findCities(@Query("q") String nameOfCity,
                                     @Query("units") String units,
                                     @Query("APPID") String key);
}
