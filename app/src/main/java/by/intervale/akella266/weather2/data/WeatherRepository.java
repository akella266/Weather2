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
import by.intervale.akella266.weather2.data.db.City;
import by.intervale.akella266.weather2.data.db.CityDao;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class WeatherRepository implements WeatherDataSource {

    private Context mContext;
    private String mKey;
    private String mUnits;
    private ApiService mApiService;
    private CityDao mDao;

    @Inject
    public WeatherRepository(Context context, ApiService api, CityDao dao) {
        this.mContext = context;
        this.mApiService = api;
        this.mDao = dao;
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
                        e.printStackTrace();
                        callback.onDataNotAvailable();
                    }
                });
    }

    @Override
    public void getForecastByCityId(String id, @NonNull WeatherLoadedCallback callback) {
        mApiService.getWeatherByCityId(id, mUnits, mKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.d("ApiService", "Forecast succesfully found");
                        ArrayList<WeatherData> list = responseToWeatherData(response);
                        callback.onWeatherLoaded(filterForecast(list));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ApiService", "Forecast NOT succesfully found\n" + e.getMessage());
                        callback.onDataNotAvailable();
                    }
                });
    }

    @Override
    public void saveCities(@NonNull CitiesLoadedCallback callback, City... cities) {
        Completable.fromAction(() -> mDao.insert(cities))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        callback.onCitiesLoaded(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onDataNotAvailable();
                    }
                });
    }

    @Override
    public void removeCity(City city, @NonNull CityDeletedCallback callback) {
        Completable.fromAction(() -> mDao.delete(city.getCityId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("Database", "City succesfully removed");
                        callback.onCityDeleted(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Database", "City NOT succesfully removed\n" + e.getMessage());
                        callback.onCityDeleted(false);
                    }
                });
    }

    @Override
    public void getCities(@NonNull CitiesLoadedCallback callback) {
        mDao.getCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<java.util.List<City>>() {
                    @Override
                    public void onSuccess(java.util.List<City> cities) {
                        callback.onCitiesLoaded(cities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onDataNotAvailable();
                    }
                });
    }

    private ArrayList<WeatherData> responseToWeatherData(Response response){
        ArrayList<WeatherData> data = new ArrayList<>();
        for(List item : response.getList()){
            String cityName, country;
            if (response.getCity() == null){
                cityName = item.getName();
                country = item.getSys().getCountry();
            }
            else {
                cityName = response.getCity().getName();
                country = response.getCity().getCountry();
            }

            data.add(new WeatherData(
                    item.getId() + "",
                    cityName,
                    country,
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
    private ArrayList<WeatherData> filterForecast(ArrayList<WeatherData> weatherResult){
        ArrayList<WeatherData> filterList = new ArrayList<>();
        filterList.add(weatherResult.get(0));
        for (int i = 1; i < weatherResult.size(); i++) {
            if (weatherResult.get(i).getDay() != weatherResult.get(i - 1).getDay()) {
                //set middle value for temperature
                int lastIndex = filterList.size() - 1;
                filterList.get(lastIndex).setTemp((filterList.get(lastIndex).getMaxTemp()
                        + filterList.get(lastIndex).getMinTemp()) / 2);
                WeatherData weatherData = weatherResult.get(i);
                filterList.add(weatherData);
            } else {
                //set min and max temp from all list to every day
                if (weatherResult.get(i).getMinTemp() < weatherResult.get(i - 1).getMinTemp()) {
                    filterList.get(filterList.size() - 1).setMinTemp(weatherResult.get(i).getMinTemp());
                }
                if (weatherResult.get(i).getMaxTemp() > weatherResult.get(i - 1).getMaxTemp()) {
                    filterList.get(filterList.size() - 1).setMaxTemp(weatherResult.get(i).getMaxTemp());
                }
            }
        }
        return filterList;
    }
}
