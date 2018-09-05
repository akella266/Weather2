package by.intervale.akella266.weather2.data.db;

import android.arch.persistence.room.RoomDatabase;

@android.arch.persistence.room.Database(entities = {City.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract CityDao cityDao();
}
