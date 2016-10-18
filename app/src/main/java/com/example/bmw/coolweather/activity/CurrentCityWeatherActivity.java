package com.example.bmw.coolweather.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmw.coolweather.R;
import com.example.bmw.coolweather.model.City;
import com.example.bmw.coolweather.service.AutoUpdateService;
import com.example.bmw.coolweather.util.HttpCallbackListener;
import com.example.bmw.coolweather.util.HttpUtil;
import com.example.bmw.coolweather.util.Utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by bmw on 2016/10/17.
 */

public class CurrentCityWeatherActivity extends Activity implements View.OnClickListener {
    public static final int GET_CITY_CODE_SUCCESS=2001;
    public static final int GET_CURRENT_CITY_WEATHER_SUCCESS=2010;
    public static final int REQUEST_CODE_FOR_INTENET=1100;
    public static final int REQUEST_CODE_FOR_LOCATION=1200;

    private LinearLayout currentCityWeatherInfoLayout;
    private TextView cuurrentCityNameText;
    private TextView currentPublishText;
    private TextView currentCityDateText;
    private TextView currentCityWeatherDespText;
    private TextView currentCityTmpMin;
    private TextView currentCityTmpMax;
    private Button chooseCity;
    private Button refreshCurrentCityWeather;

    private String currentCityName="";
    private String currentCityCode="";

    private LocationManager locationManager;
    private Location currentLocation;
    private String provider;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case GET_CITY_CODE_SUCCESS:{
                    String currentCityCode=(String)msg.obj;
                    break;
                }
                case GET_CURRENT_CITY_WEATHER_SUCCESS:
                    showWeather();
                    break;
                default:
                    break;
            }
        }
    };
    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation=location;
            getCurrentLocationWeather(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(CurrentCityWeatherActivity.this,"location provider is disabled",Toast.LENGTH_LONG).show();
        }
    }
;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_city_weather_layout);

        checkAndRequestPermission(this, Manifest.permission.INTERNET,REQUEST_CODE_FOR_INTENET);
        checkAndRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,REQUEST_CODE_FOR_LOCATION);

        chooseCity=(Button)findViewById(R.id.choose_city);
        refreshCurrentCityWeather=(Button)findViewById( R.id.refresh_current_city_weather);
        cuurrentCityNameText=(TextView)findViewById(R.id.current_city_name);
        currentPublishText=(TextView)findViewById(R.id.current_city_publish_date);
        currentCityWeatherDespText=(TextView)findViewById(R.id.current_weather_desp);
        currentCityDateText=(TextView)findViewById(R.id.current_city_date);
        currentCityTmpMax=(TextView)findViewById(R.id.current_tmp_max);
        currentCityTmpMin=(TextView)findViewById(R.id.current_tmp_min);
        currentCityWeatherInfoLayout=(LinearLayout) findViewById(R.id.current_city_weather_info_layout);

        currentCityWeatherInfoLayout.setVisibility(View.INVISIBLE);
        cuurrentCityNameText.setVisibility(View.INVISIBLE);

        chooseCity.setOnClickListener(this);
        refreshCurrentCityWeather.setOnClickListener(this);

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList=locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)){
            provider=LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.PASSIVE_PROVIDER)){
            provider=LocationManager.PASSIVE_PROVIDER;
        }else{
            Toast.makeText(this,"No location provider to use",Toast.LENGTH_LONG).show();
            return;
        }

        Location location=locationManager.getLastKnownLocation(provider);
        currentLocation=location;
        if (null!=currentLocation){
            getCurrentLocationWeather(currentLocation);
        }
        locationManager.requestLocationUpdates(provider,5000,1,locationListener);
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if (null!=locationManager){
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.choose_city:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                startActivity(intent);
                break;
            case R.id.refresh_current_city_weather:
                if (null!=currentLocation){
                    currentPublishText.setText("同步中");
                    getCurrentLocationWeather(currentLocation);
                }
                break;
            default:
                break;
        }
    }

    private void getCurrentLocationWeather(Location location){
        StringBuilder urlString=new StringBuilder();
        urlString.append("http://api.map.baidu.com/geocoder/v2/?ak=dEaPMdGszZjn5ckDHaQvcMGb08iO8TSx&location=");
        urlString.append(location.getLatitude()).append(",");
        urlString.append(location.getLongitude());
        urlString.append("&output=json&mcode=EF:41:E8:D0:85:69:1C:FC:F3:85:F3:6D:21:61:02:19:C7:41:57:F2;com.example.bmw.locationtest");

        String address=urlString.toString();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (Utility.handleGeocoderResponse(CurrentCityWeatherActivity.this,response)){
                    getCurrentCityCode(CurrentCityWeatherActivity.this);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();;
            }
        });
    }

    private void getCurrentCityCode(Context context){
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        //final String currentDistrict=prefs.getString("current_district","");
        final String currentDistrict=prefs.getString("current_city","");
        final int length=currentDistrict.length();
        if (!TextUtils.isEmpty(currentDistrict)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection=null;
                    try{
                        StringBuilder urlString=new StringBuilder();
                        urlString.append("http://apis.baidu.com/apistore/weatherservice/cityinfo?cityname=");
                        //urlString.append("http://apis.baidu.com/apistore/weatherservice/cityinfo?cityname=%E5%8C%97%E4%BA%AC");
                        urlString.append(java.net.URLEncoder.encode(currentDistrict.substring(0,length-1)));
                        //urlString.append(java.net.URLEncoder.encode("北京"));

                        URL url=new URL(urlString.toString());
                        connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestProperty("apikey","e7dbc64abff2da60832f6f9848ba41c6");
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(8000);
                        connection.setConnectTimeout(8000);

                        InputStream in=connection.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                        StringBuilder response=new StringBuilder();
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                        JSONObject jsonObject=new JSONObject(response.toString());
                        int errNum=jsonObject.getInt("errNum");
                        if (0==errNum){
                            JSONObject subObject=jsonObject.getJSONObject("retData");
                            String cityCode=subObject.getString("cityCode");
                            String zipCode=subObject.getString("zipCode");
                            String telAreaCode=subObject.getString("telAreaCode");
                            getCurrentCityWeatherByCityCode(cityCode);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("citycode",e.toString());
                    }finally {
                        if (null!=connection){
                            connection.disconnect();
                        }
                    }
                }
            }).start();
        }
    }

    private void getCurrentCityWeatherByCityCode(String cityCode){
        String address="https://api.heweather.com/x3/weather?cityid=CN"+cityCode+"&key=61db33bb544c4cdaaee7c69fc8e709bb";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (Utility.handleCurrentLocationWeatherResponse(CurrentCityWeatherActivity.this,response)){
                    //Message msg=new Message();
                    //msg.what=GET_CURRENT_CITY_WEATHER_SUCCESS;
                    //handler.sendMessage(msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean checkAndRequestPermission(final Context context, final String permission, final int requestCode){
        boolean bResult=false;
        if (ContextCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale((CurrentCityWeatherActivity)context,permission)){
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Notice").setMessage("App need permission"+permission);
                dialogBuilder.setNegativeButton("Cancel",null);
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((CurrentCityWeatherActivity)context,new String[]{permission},requestCode);
                    }
                });
                dialogBuilder.create().show();
            }else{
                ActivityCompat.requestPermissions((CurrentCityWeatherActivity)context,new String[]{permission},requestCode);
            }
        }else{
            bResult=true;
        }
        return bResult;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantedResult){
        switch (requestCode){
            case REQUEST_CODE_FOR_INTENET:
                if (grantedResult.length>0 && grantedResult[0]==PackageManager.PERMISSION_GRANTED){
                    //申请权限成功后执行的代码
                }
                break;
            case REQUEST_CODE_FOR_LOCATION:
                if (grantedResult.length>0 && grantedResult[0]==PackageManager.PERMISSION_GRANTED){
                    //申请权限成功后执行的代码
                }
            default:
                break;
        }
    }


    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cuurrentCityNameText.setText(prefs.getString("current_location",""));
        currentCityWeatherDespText.setText(prefs.getString("current_city_weather_desp",""));
        currentCityTmpMax.setText(prefs.getString("current_city_max_tmp",""));
        currentCityTmpMin.setText(prefs.getString("current_city_min_tmp",""));
        currentPublishText.setText(prefs.getString("current_city_publish","")+"发布");
        currentCityDateText.setText(prefs.getString("current_city_date",""));
        currentCityWeatherInfoLayout.setVisibility(View.VISIBLE);
        cuurrentCityNameText.setVisibility(View.VISIBLE);
    }
}
