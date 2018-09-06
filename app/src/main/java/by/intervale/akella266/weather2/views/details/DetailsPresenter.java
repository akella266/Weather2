package by.intervale.akella266.weather2.views.details;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import javax.inject.Inject;

import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.data.WeatherDataSource;
import by.intervale.akella266.weather2.data.WeatherRepository;
import by.intervale.akella266.weather2.data.db.City;

public class DetailsPresenter implements DetailsContract.Presenter {

    private Context mContext;
    private WeatherRepository mRepository;
    private DetailsContract.View mView;
    private String mCityId;
    private WeatherData mCity;
    private List<WeatherData> mList;
    private boolean isFavorite;

    @Inject
    DetailsPresenter(Context mContext, WeatherRepository mRepository,
                     String mCityId, boolean isFavorite) {
        this.mContext = mContext;
        this.mRepository = mRepository;
        this.mCityId = mCityId;
        this.isFavorite = isFavorite;
    }

    @Override
    public void loadDetails() {
        if (!checkNetworkAvailable()){
            mView.showMessage(mContext.getString(R.string.connect_error));
            return;
        }

        mRepository.getForecastByCityId(mCityId, new WeatherDataSource.WeatherLoadedCallback() {
            @Override
            public void onWeatherLoaded(List<WeatherData> data) {
                mCity = data.get(0);
                mList = data.subList(1, data.size());
                mView.showDetails(mCity);
                changeDayForecast(3);
            }

            @Override
            public void onDataNotAvailable() {
                mView.showMessage(mContext.getString(R.string.read_error));
            }
        });
    }

    @Override
    public void changeDayForecast(int countDays) {
        mView.showForecast(mList.subList(0, countDays));
    }

    @Override
    public void addToFavorite() {
        mRepository.saveCities(new WeatherDataSource.CitiesLoadedCallback() {
            @Override
            public void onCitiesLoaded(List<City> cities) {
                mView.showMessage(mContext.getString(R.string.city_added));
                mView.setFavoriteState();
            }

            @Override
            public void onDataNotAvailable() {
                mView.showMessage(mContext.getString(R.string.adding_city_error));
            }
        }, new City(mCityId));
    }

    @Override
    public void removeFromFavorite() {
        mRepository.removeCity(new City(mCityId), isSuccefull -> {
            if(isSuccefull) {
                mView.showMessage(mContext.getString(R.string.city_deleted));
                mView.setFavoriteState();
            }
            else mView.showMessage(mContext.getString(R.string.removing_city_error));
        });
    }

    @Override
    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public void takeView(DetailsContract.View view) {
        this.mView = view;
        loadDetails();
    }

    @Override
    public void dropView() {
        this.mView = null;
    }

    private boolean checkNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;

    }
}
