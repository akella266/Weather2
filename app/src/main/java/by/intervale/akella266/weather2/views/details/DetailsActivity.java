package by.intervale.akella266.weather2.views.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.intervale.akella266.weather2.R;
import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;

public class DetailsActivity extends DaggerAppCompatActivity {

    public final static String EXTRA_CITY_ID = "CITY_ID";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    Lazy<DetailsFragment> mDetailsFragmentProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment =
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = mDetailsFragmentProvider.get();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, "DETAILS").commit();
        }
    }
}
