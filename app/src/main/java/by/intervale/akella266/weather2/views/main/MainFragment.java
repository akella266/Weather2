package by.intervale.akella266.weather2.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.WeatherData;

public class MainFragment extends Fragment
        implements MainContract.View{

    private Unbinder unbinder;

    @BindView(R.id.text_view_favorite)
    TextView mFavorite;
    @BindView(R.id.recycler_weather)
    RecyclerView mRecycler;
    MainAdapter mAdapter;
    @BindView(R.id.text_view_no_weather)
    TextView mNoData;
    @BindView(R.id.swipe_refresh)
    ScrollChildSwipeRefreshLayout mSwipeRefresh;
    boolean isSearch;
    SearchView mSearch;

    @Inject
    MainContract.Presenter mPresenter;

    @Inject
    public MainFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MainAdapter(getContext(), false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, view);
        isSearch = false;

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter);


        mSwipeRefresh.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        mSwipeRefresh.setScrollView(mRecycler);
        mSwipeRefresh.setOnRefreshListener(() -> {
            if(isSearch){
                String query = mSearch.getQuery().toString();
                mPresenter.search(query);
            }
            else mPresenter.loadWeather(true);
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
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearch = (SearchView) item.getActionView();
        MenuItem.OnActionExpandListener expandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                showNoWeather();
                isSearch = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mPresenter.loadWeather(false);
                isSearch = false;
                return true;
            }
        };
        item.setOnActionExpandListener(expandListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:{
                mPresenter.initSearch();
                mPresenter.setUpSearchListener((SearchView) item.getActionView());
                return true;
            }
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showFavoriteWeather(List<WeatherData> data) {
        mRecycler.setVisibility(View.VISIBLE);
        mFavorite.setVisibility(View.VISIBLE);
        mNoData.setVisibility(View.GONE);
        mAdapter.setList(data);
    }

    @Override
    public void hideFavoriteWeather() {
        mFavorite.setVisibility(View.GONE);
    }

    @Override
    public void showFoundWeather(List<WeatherData> data) {
        mRecycler.setVisibility(View.VISIBLE);
        mFavorite.setVisibility(View.GONE);
        mNoData.setVisibility(View.GONE);
        mAdapter.setList(data);
    }

    @Override
    public void showWeatherDetails(WeatherData data) {

    }

    @Override
    public void showNoWeather() {
        mRecycler.setVisibility(View.GONE);
        mFavorite.setVisibility(View.GONE);
        mNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mRecycler, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingIndicator(boolean active) {
        if (getView() == null) {
            return;
        }
        mSwipeRefresh.post(() -> mSwipeRefresh.setRefreshing(active));
    }
}
