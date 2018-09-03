package by.intervale.akella266.weather2.views.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.intervale.akella266.weather2.R;
import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    Lazy<MainFragment> mMainFragmentProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);


        Fragment fragment =
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragment = mMainFragmentProvider.get();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, "MAIN").commit();
        }
    }
}
