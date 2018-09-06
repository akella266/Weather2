package by.intervale.akella266.weather2.views.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.WeatherData;
import by.intervale.akella266.weather2.views.adapter.MainAdapter;

public class DetailsFragment extends Fragment
        implements DetailsContract.View{

    private Unbinder unbinder;
    @BindView(R.id.text_view_date)
    TextView mDate;
    @BindView(R.id.text_view_max_min)
    TextView mMaxMin;
    @BindView(R.id.text_view_temperature)
    TextView mTemp;
    @BindView(R.id.text_view_humidity)
    TextView mHumidity;
    @BindView(R.id.text_view_desciption)
    TextView mDescription;
    @BindView(R.id.image_view_condition)
    ImageView mCondition;
    @BindView(R.id.tab_layout_day_forecast)
    TabLayout mTabs;
    @BindView(R.id.recycler_forecast)
    RecyclerView mRecycler;
    MainAdapter mAdapter;
    MenuItem mFavoriteItem;
    @Inject
    DetailsContract.Presenter mPresenter;

    @Inject
    public DetailsFragment() {    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MainAdapter(getContext(), true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter);

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        mPresenter.changeDayForecast(3);
                        break;
                    case 1:
                        mPresenter.changeDayForecast(5);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.takeView(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details, menu);
        mFavoriteItem = menu.findItem(R.id.item_favorite);
        setFavoriteState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_favorite:{
                if(mPresenter.isFavorite()){
                    mPresenter.setFavorite(false);
                    mPresenter.removeFromFavorite();
                }
                else{
                    mPresenter.setFavorite(true);
                    mPresenter.addToFavorite();
                }
                return true;
            }
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showDetails(WeatherData data) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null){
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) actionBar.setTitle(data.getCityName());
        }

        mDate.setText(data.getDate());
        String maxMin = getString(R.string.high_temp, data.getMaxTemp())
                + getString(R.string.low_temp, data.getMinTemp());
        mMaxMin.setText(maxMin);
        mTemp.setText(getString(R.string.temperature, data.getTemp()));
        mHumidity.setText(getString(R.string.humidity, data.getHumidity()));
        mDescription.setText(data.getDescription());
        Glide.with(getContext())
                .load(data.getIcon())
                .into(mCondition);
    }

    @Override
    public void showForecast(List<WeatherData> forecast) {
        mAdapter.setList(forecast);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mRecycler, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setFavoriteState(){
        if(mPresenter.isFavorite()) {
            mFavoriteItem.setIcon(R.drawable.ic_star_fill);
            mFavoriteItem.setTitle(R.string.remove_from_favorite);
        }
        else{
            mFavoriteItem.setIcon(R.drawable.ic_star_border);
            mFavoriteItem.setTitle(R.string.add_to_favorite);
        }
    }
}
