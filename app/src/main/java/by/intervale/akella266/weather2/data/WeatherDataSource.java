package by.intervale.akella266.weather2.data;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.api.models.Response;
import by.intervale.akella266.weather2.data.db.City;

public interface WeatherDataSource {

    interface WeatherLoadedCallback{
        void onWeatherLoaded(List<WeatherData> data);
        void onDataNotAvailable();
    }

    interface CitiesLoadedCallback{
        void onCitiesLoaded(List<City> cities);
        void onDataNotAvailable();
    }

    interface CityDeletedCallback{
        void onCityDeleted(boolean isSuccefull);
    }

    void getWeather(String cities, @NonNull WeatherLoadedCallback callback);
    void findWeather(String city, @NonNull WeatherLoadedCallback callback);
    void getForecastByCityId(String id, @NonNull WeatherLoadedCallback callback);
    void saveCities(@NonNull CitiesLoadedCallback callback, City... cities);
    void removeCity(City city, @NonNull CityDeletedCallback callback);
    void getCities(@NonNull CitiesLoadedCallback callback);
}
