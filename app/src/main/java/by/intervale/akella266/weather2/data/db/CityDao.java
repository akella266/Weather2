package by.intervale.akella266.weather2.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CityDao {

    @Insert
    void insert(City city);

    @Insert
    void insert(City... cities);

    @Query("delete from cities where cityId = :cityId")
    void delete(String cityId);

    @Query("select * from cities")
    Single<List<City>> getCities();
}
