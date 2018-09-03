package by.intervale.akella266.weather2.views.main;

import android.support.v7.widget.SearchView;

import java.util.List;

import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.views.BasePresenter;
import by.intervale.akella266.weather2.views.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter>{
        void showFavoriteWeather(List<WeatherData> data);
        void hideFavoriteWeather();
        void showFoundWeather(List<WeatherData> data);
        void showWeatherDetails(WeatherData data);
        void showNoWeather();
        void showMessage(String message);
        void showLoadingIndicator(boolean active);
    }
    interface Presenter extends BasePresenter<View>{
        void loadWeather(boolean indicator);
        void setUpSearchListener(SearchView searchView);
        void removeSearchListener();
        void openWeatherDetails(WeatherData data);
        void addWeatherToFavorite(WeatherData data);
        void removeWeatherFromFavorite(WeatherData data);
        void initSearch();
        void search(CharSequence text);
    }
}