package by.intervale.akella266.weather2.di;

import android.app.Application;

import javax.inject.Singleton;

import by.intervale.akella266.weather2.BaseApplication;
import by.intervale.akella266.weather2.api.ApiModule;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {ApiModule.class,
        AppModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder{
        @BindsInstance
        AppComponent.Builder application(Application application);
        AppComponent build();
    }
}
