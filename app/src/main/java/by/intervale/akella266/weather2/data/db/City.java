package by.intervale.akella266.weather2.data.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "cities")
public class City {

    @PrimaryKey
    @NonNull
    private String id;
    private String cityId;

    public City(@NonNull String id, String cityId) {
        this.id = id;
        this.cityId = cityId;
    }
    @Ignore
    public City(String cityId) {
        this(cityId.hashCode() + "", cityId);
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if (!(obj instanceof City)) return false;
        City city = (City)obj;
        return city.getCityId().equals(cityId);
    }

    @Override
    public int hashCode() {
        return cityId.hashCode();
    }

    @Override
    public String toString() {
        return cityId;
    }
}
