package by.intervale.akella266.weather2.views.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.data.WeatherDataSource;
import by.intervale.akella266.weather2.data.WeatherRepository;
import rx.Subscription;

public class MainPresenter implements MainContract.Presenter {

    private Context mContext;
    private MainContract.View mView;
    private WeatherRepository mRepository;
    private Subscription mSearchSubscription;

    @Inject
    public MainPresenter(Context mContext, WeatherRepository repository) {
        this.mContext = mContext;
        this.mRepository = repository;
        mSearchSubscription = null;
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
        String[] cities = mContext.getResources().getStringArray(R.array.cities);
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
    public void openWeatherDetails(WeatherData data) {

    }

    @Override
    public void addWeatherToFavorite(WeatherData data) {

    }

    @Override
    public void removeWeatherFromFavorite(WeatherData data) {

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
    public void takeView(MainContract.View view) {
        this.mView = view;
        loadWeather(true);
    }

    @Override
    public void dropView() {
        this.mView = null;
    }

    public boolean checkNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;

    }
}
