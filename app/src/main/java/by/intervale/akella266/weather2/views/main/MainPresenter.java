package by.intervale.akella266.weather2.views.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.data.WeatherDataSource;
import by.intervale.akella266.weather2.data.WeatherRepository;
import by.intervale.akella266.weather2.data.db.City;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

import static android.content.Context.MODE_PRIVATE;

public class MainPresenter implements MainContract.Presenter {

    private final String SHARED_FIRST_LAUNCH = "FIRST_LAUNCH";
    private Context mContext;
    private MainContract.View mView;
    private WeatherRepository mRepository;
    private Subscription mSearchSubscription;
    private boolean isFirstLaunch;

    @Inject
    MainPresenter(Context mContext, WeatherRepository repository) {
        this.mContext = mContext;
        this.mRepository = repository;
        mSearchSubscription = null;
        isFirstLaunch = checkOnFirstLaunch();
    }

    @Override
    public void loadWeather(boolean indicator) {
        if (indicator)
            mView.showLoadingIndicator(true);

        if (!checkNetworkAvailable()){
            mView.showMessage(mContext.getString(R.string.connect_error));
            mView.showLoadingIndicator(false);
            return;
        }

        if (isFirstLaunch) {
            isFirstLaunch = false;
            String[] cities = mContext.getResources().getStringArray(R.array.cities);
            List<City> lCities = new ArrayList<>();
            for(String city : cities){
                lCities.add(new City(city));
            }
            mRepository.saveCities(new WeatherDataSource.CitiesLoadedCallback() {
                @Override
                public void onCitiesLoaded(List<City> cities) {}

                @Override
                public void onDataNotAvailable() {}
            }, lCities.toArray(new City[lCities.size()]));
            load(indicator, lCities);
        }
        else mRepository.getCities(new WeatherDataSource.CitiesLoadedCallback() {
            @Override
            public void onCitiesLoaded(List<City> cities) {
                load(indicator, cities);
            }

            @Override
            public void onDataNotAvailable() {
                mView.showMessage(mContext.getString(R.string.db_error));
            }
        });
    }

    @Override
    public void setUpSearchListener(SearchView searchView) {
        mSearchSubscription = RxSearchView.queryTextChanges(searchView)
                .distinctUntilChanged()
                .filter(charSequence -> charSequence.length() > 2)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(this::search);
    }

    @Override
    public void removeSearchListener() {
        if (mSearchSubscription != null)
            mSearchSubscription.unsubscribe();
    }

    @Override
    public void openWeatherDetails(final String cityId) {
        mRepository.getCities(new WeatherDataSource.CitiesLoadedCallback() {
            @Override
            public void onCitiesLoaded(List<City> cities) {
                if (cities.contains(new City(cityId))) mView.showWeatherDetails(cityId, true);
                else mView.showWeatherDetails(cityId, false);
            }

            @Override
            public void onDataNotAvailable() {
                mView.showWeatherDetails(cityId, false);
            }
        });
    }

    @Override
    public void addWeatherToFavorite(WeatherData data) {
        mRepository.saveCities(new WeatherDataSource.CitiesLoadedCallback() {
            @Override
            public void onCitiesLoaded(List<City> cities) {
                mView.showMessage(mContext.getString(R.string.city_added));
            }

            @Override
            public void onDataNotAvailable() {
                mView.showMessage(mContext.getString(R.string.adding_city_error));
            }
        }, new City(data.getCityId()));
    }

    @Override
    public void removeWeatherFromFavorite(WeatherData data) {
        mRepository.removeCity(new City(data.getCityId()), isSuccefull -> {
            if(isSuccefull) {
                mView.showMessage(mContext.getString(R.string.city_deleted));
                loadWeather(false);
            }
            else mView.showMessage(mContext.getString(R.string.removing_city_error));
        });
    }

    @Override
    public void initDialog(WeatherData data) {
        mView.showDialog(data);
    }

    @Override
    public void search(CharSequence text) {
        if (text.toString().matches(".*\\d+.*")) {
            Log.e("Search", "Error query " + text);
            mView.showMessage(mContext.getString(R.string.query_error));
            return;
        }
        mView.showLoadingIndicator(true);
        mRepository.findWeather(text.toString(),
                new WeatherDataSource.WeatherLoadedCallback() {
                    @Override
                    public void onWeatherLoaded(List<WeatherData> data) {
                        if(data.isEmpty()) mView.showNoWeather();
                        else mView.showFoundWeather(data);
                        mView.showLoadingIndicator(false);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        if (!checkNetworkAvailable()) mView.showMessage(mContext.getString(R.string.connect_error));
                        mView.showNoWeather();
                        mView.showLoadingIndicator(false);
                    }
                });

    }

    @Override
    public void initSearch() {
        mView.showNoWeather();
    }

    @Override
    public void checkOnFavorite(String cityId) {

    }

    @Override
    public void takeView(MainContract.View view) {
        this.mView = view;
        loadWeather(true);
    }

    @Override
    public void dropView() {
        this.mView = null;
    }

    private void load(boolean indicator, List<City> cities){
        String q = TextUtils.join(",", cities);
        mRepository.getWeather(q, new WeatherDataSource.WeatherLoadedCallback() {
            @Override
            public void onWeatherLoaded(List<WeatherData> data) {
                mView.showFavoriteWeather(data);
                if (indicator)
                    mView.showLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mView.showMessage(mContext.getString(R.string.read_error));
                if (indicator)
                    mView.showLoadingIndicator(false);
            }
        });
    }

    private boolean checkOnFirstLaunch(){
        SharedPreferences sp = mContext.getSharedPreferences("SharedFirst", MODE_PRIVATE);
        if (sp.getBoolean(SHARED_FIRST_LAUNCH, true)){
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean(SHARED_FIRST_LAUNCH, false);
            ed.apply();
            return true;
        }
        return false;
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
