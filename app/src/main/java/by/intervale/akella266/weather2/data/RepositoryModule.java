package by.intervale.akella266.weather2.data;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import by.intervale.akella266.weather2.data.db.CityDao;
import by.intervale.akella266.weather2.data.db.Database;
import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Singleton
    @Provides
    Database provideWeatherRepository(Application context){
        return Room.databaseBuilder(context.getApplicationContext(), Database.class, "cities.db")
                .build();
    }

    @Singleton
    @Provides
    CityDao provideDao(Database db){
        return db.cityDao();
    }
}
