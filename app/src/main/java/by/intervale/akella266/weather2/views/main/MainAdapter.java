package by.intervale.akella266.weather2.views.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.intervale.akella266.weather2.R;
import by.intervale.akella266.weather2.api.WeatherData;

public class MainAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<WeatherData> mList;
    private boolean isForecast;

    public MainAdapter(Context mContext, boolean isForecast) {
        this.mContext = mContext;
        this.mList = new ArrayList<>();
        this.isForecast = isForecast;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MainViewHolder holder = (MainViewHolder)viewHolder;
        WeatherData data = mList.get(position);
        StringBuilder builder = new StringBuilder();
        if (isForecast) builder.append(data.getDate());
        else  builder.append(data.getCityName()).append(" ").append(data.getCountry());
        holder.tvTitle.setText(builder.toString());
        holder.tvDescription.setText(data.getDescription());
        holder.tvMax.setText(mContext.getString(R.string.temperature, data.getMaxTemp()));
        holder.tvMin.setText(mContext.getString(R.string.temperature, data.getMinTemp()));
        Glide.with(mContext)
                .load(data.getIcon())
                .into(holder.ivCondition);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(List<WeatherData> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    class MainViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.text_view_title)
        TextView tvTitle;
        @BindView(R.id.text_view_desciption)
        TextView tvDescription;
        @BindView(R.id.text_view_max)
        TextView tvMax;
        @BindView(R.id.text_view_min)
        TextView tvMin;
        @BindView(R.id.image_view_condition)
        ImageView ivCondition;

        public MainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
