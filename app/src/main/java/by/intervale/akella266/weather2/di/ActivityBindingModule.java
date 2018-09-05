package by.intervale.akella266.weather2.di;

import by.intervale.akella266.weather2.views.details.DetailsActivity;
import by.intervale.akella266.weather2.views.details.DetailsModule;
import by.intervale.akella266.weather2.views.main.MainActivity;
import by.intervale.akella266.weather2.views.main.MainModule;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = {MainModule.class})
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {DetailsModule.class})
    abstract DetailsActivity detailsActivity();
}
