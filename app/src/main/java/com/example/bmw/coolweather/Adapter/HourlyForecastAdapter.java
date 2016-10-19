package com.example.bmw.coolweather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bmw.coolweather.R;
import com.example.bmw.coolweather.model.HourlyWeather;

import java.util.List;

/**
 * Created by bmw on 2016/10/19.
 */

public class HourlyForecastAdapter extends ArrayAdapter<HourlyWeather> {

    private int resourceId;

    public HourlyForecastAdapter(Context context, int textViewResourceId, List<HourlyWeather> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        HourlyWeather hourlyWeather=getItem(position);

        TextView hourlyForecastTime=(TextView)view.findViewById(R.id.hourly_forecast_time);
        TextView hourlyForecastPressure=(TextView)view.findViewById(R.id.hourly_forecast_pressure);
        TextView hourlyForecastWind=(TextView)view.findViewById(R.id.hourly_forecast_wind);
        TextView hourlyForecastTemp=(TextView)view.findViewById(R.id.hourly_forecast_temp);
        TextView hourlyForecastRainpop=(TextView)view.findViewById(R.id.hourly_forecast_rain_pop);

        hourlyForecastPressure.setText(hourlyWeather.getPressure());
        hourlyForecastTime.setText(hourlyWeather.getTime());
        hourlyForecastWind.setText(hourlyWeather.getWindCondition());
        hourlyForecastTemp.setText(hourlyWeather.getTempature());
        hourlyForecastRainpop.setText(hourlyWeather.getRainPop());

        return view;
    }
}
