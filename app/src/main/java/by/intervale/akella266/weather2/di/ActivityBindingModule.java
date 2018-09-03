package by.intervale.akella266.weather2.di;

import by.intervale.akella266.weather2.views.main.MainActivity;
import by.intervale.akella266.weather2.views.main.MainModule;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = {MainModule.class})
    abstract MainActivity mainActivity();
}
