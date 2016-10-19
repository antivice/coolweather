package com.example.bmw.coolweather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bmw.coolweather.R;
import com.example.bmw.coolweather.model.SevenDaysWeather;

import java.util.List;

/**
 * Created by bmw on 2016/10/19.
 */

public class SevenDaysForecastAdapter extends ArrayAdapter<SevenDaysWeather> {
    private int resourceId;

    public SevenDaysForecastAdapter(Context context, int textViewResourceId, List<SevenDaysWeather> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        SevenDaysWeather sevenDaysWeather=getItem(position);

        TextView date=(TextView)view.findViewById(R.id.seven_days_date);
        TextView weatherDesp=(TextView)view.findViewById(R.id.seven_days_daynight_weather);
        TextView temperature=(TextView)view.findViewById(R.id.seven_days_temp);
        TextView sunRiseTime=(TextView)view.findViewById(R.id.seven_days_sunriseTime);
        TextView sunSetTime=(TextView)view.findViewById(R.id.seven_days_sunsetTime);
        TextView windCondition=(TextView)view.findViewById(R.id.seven_days_wind);
        TextView pressure=(TextView)view.findViewById(R.id.seven_days_pressure);
        TextView updateTime=(TextView)view.findViewById(R.id.seven_days_updateTime);

        date.setText(sevenDaysWeather.getDate());
        weatherDesp.setText(sevenDaysWeather.getWeatherDesp());
        temperature.setText(sevenDaysWeather.getTempature());
        sunRiseTime.setText(sevenDaysWeather.getSunRiseTime());
        sunSetTime.setText(sevenDaysWeather.getSunSetTime());
        windCondition.setText(sevenDaysWeather.getWindCondition());
        pressure.setText(sevenDaysWeather.getPressure());
        updateTime.setText(sevenDaysWeather.getUpdateTime());

        return view;
    }
}
