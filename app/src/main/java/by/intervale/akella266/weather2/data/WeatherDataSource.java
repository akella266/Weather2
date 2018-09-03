package by.intervale.akella266.weather2.data;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.api.models.Response;

public interface WeatherDataSource {

    interface WeatherLoadedCallback{
        void onWeatherLoaded(List<WeatherData>   data);
        void onDataNotAvailable();
    }

    void getWeather(String cities, @NonNull WeatherLoadedCallback callback);
    void findWeather(String city, @NonNull WeatherLoadedCallback callback);
}
