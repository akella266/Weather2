package by.intervale.akella266.weather2.views;

public interface BasePresenter<T> {

    void takeView(T view);
    void dropView();
}
