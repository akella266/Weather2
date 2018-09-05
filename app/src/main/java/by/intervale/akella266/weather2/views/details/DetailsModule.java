package by.intervale.akella266.weather2.views.details;

import by.intervale.akella266.weather2.di.ActivityScoped;
import by.intervale.akella266.weather2.di.FragmentScoped;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DetailsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DetailsFragment detailsFragment();

    @ActivityScoped
    @Binds abstract DetailsContract.Presenter detailsPresenter(DetailsPresenter presenter);

    @Provides
    @ActivityScoped
    static String provideCityId(DetailsActivity detailsActivity){
        return detailsActivity.getIntent().getStringExtra(DetailsActivity.EXTRA_CITY_ID);
    }
}
