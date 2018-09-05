package by.intervale.akella266.weather2.views.details;

import java.util.List;

import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.views.BasePresenter;
import by.intervale.akella266.weather2.views.BaseView;

public interface DetailsContract {
    interface View extends BaseView<Presenter>{
        void showDetails(WeatherData data);
        void showMessage(String message);
        void showForecast(List<WeatherData> forecast);
    }
    interface Presenter extends BasePresenter<View> {
        void loadDetails();
        void changeDayForecast(int countDays);
    }
}
