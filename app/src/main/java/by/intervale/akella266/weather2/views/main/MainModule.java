package by.intervale.akella266.weather2.views.main;

import by.intervale.akella266.weather2.di.ActivityScoped;
import by.intervale.akella266.weather2.di.FragmentScoped;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract MainFragment mainFragment();

    @ActivityScoped
    @Binds abstract MainContract.Presenter mainPresenter(MainPresenter mainPresenter);
}
