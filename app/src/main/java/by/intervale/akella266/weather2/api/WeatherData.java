package by.intervale.akella266.weather2.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherData implements Parcelable {

    private String cityId;
    private String cityName;
    private String date;
    private String country;
    private int day;
    private double temp;
    private double minTemp;
    private double maxTemp;
    private String humidity;
    private String description;
    private String icon;

    public WeatherData(String cityId, String cityName, String country, long timeStamp, double temp, double minTemp, double maxTemp,
                       double humidity, String description, String iconName){

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        this.cityName = cityName;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp*1000);
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat dateFormater = new SimpleDateFormat("EEEE, d MMM", Locale.getDefault());

        this.cityId = cityId;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.country = country;
        this.date = dateFormater.format(calendar.getTime());
        this.temp = temp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = NumberFormat.getPercentInstance().format(humidity/100.0);
        this.description = description;
        this.icon = iconName;
    }

    protected WeatherData(Parcel in) {
        cityName = in.readString();
        date = in.readString();
        minTemp = in.readDouble();
        maxTemp = in.readDouble();
        humidity = in.readString();
        description = in.readString();
        icon = in.readString();
        day = in.readInt();
        temp = in.readDouble();
        cityId = in.readString();
        country = in.readString();
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(date);
        dest.writeDouble(minTemp);
        dest.writeDouble(maxTemp);
        dest.writeString(humidity);
        dest.writeString(description);
        dest.writeString(icon);
        dest.writeInt(day);
        dest.writeDouble(temp);
        dest.writeString(cityId);
        dest.writeString(country);
    }

    public String getCityName() {
        return cityName == null ? "" : cityName;
    }

    public String getDate() {
        return date;
    }

    public int getDay() {
        return day;
    }

    public double getTemp() {
        return temp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
