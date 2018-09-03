package by.intervale.akella266.weather2.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.ApiService;
import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.api.models.List;
import by.intervale.akella266.weather2.api.models.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class WeatherRepository implements WeatherDataSource {

    private Context mContext;
    private String mKey;
    private String mUnits;

    @Inject
    ApiService mApiService;

    @Inject
    public WeatherRepository(Context context) {
        this.mContext = context;
        this.mKey = context.getString(R.string.api_key);
        this.mUnits = context.getString(R.string.units);
    }

    @Override
    public void getWeather(String cities, @NonNull final WeatherLoadedCallback callback) {
        mApiService.getWeatherGroupCities(cities, mUnits, mKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.d("ApiService", "Weather succesfully loaded");
                        callback.onWeatherLoaded(responseToWeatherData(response));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ApiService", "Weather NOT succesfully loaded\n" + e.getMessage());
                        callback.onDataNotAvailable();
                    }
                });
    }

    @Override
    public void findWeather(String city, @NonNull WeatherLoadedCallback callback) {
        mApiService.findCities(city, mUnits, mKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.d("ApiService", "Weather succesfully found");
                        callback.onWeatherLoaded(responseToWeatherData(response));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ApiService", "Weather NOT succesfully found\n" + e.getMessage());
                        callback.onDataNotAvailable();
                    }
                });
    }

    private ArrayList<WeatherData> responseToWeatherData(Response response){
        ArrayList<WeatherData> data = new ArrayList<>();
        for(List item : response.getList()){
            data.add(new WeatherData(
                    item.getId() + "",
                    item.getName(),
                    item.getSys().getCountry(),
                    item.getDt(),
                    item.getMain().getTemp(),
                    item.getMain().getTempMin(),
                    item.getMain().getTempMax(),
                    item.getMain().getHumidity(),
                    item.getWeather().get(0).getDescription(),
                    mContext.getString(R.string.image_url, item.getWeather().get(0).getIcon())
            ));
        }
        return data;
    }
}
