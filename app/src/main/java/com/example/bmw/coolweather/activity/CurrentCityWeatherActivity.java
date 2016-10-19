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

import org.json.JSONArray;
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
    private TextView currentCityLocation;
    private TextView currentCityNameText;
    private TextView currentCityUpdateText;
    //private TextView currentCityStatus;
    private TextView currentCityWeatherText;
    private TextView currentCityTempature;
    private TextView currentCityWind;
    private TextView currentCityPop;
    private TextView currentCitySunriseTime;
    private TextView currentCitySunsetTime;

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
        currentCityLocation=(TextView)findViewById(R.id.current_city_location);
        currentCityNameText=(TextView)findViewById(R.id.current_city_name);
        currentCityUpdateText=(TextView)findViewById(R.id.current_city_updateTime);
        //currentCityStatus=(TextView)findViewById(R.id.current_city_status);
        currentCityWeatherText=(TextView)findViewById(R.id.current_city_daynight_weather);
        currentCityTempature=(TextView)findViewById(R.id.current_city_temp);
        currentCityWind=(TextView)findViewById(R.id.current_city_wind);
        currentCityPop=(TextView)findViewById(R.id.current_city_pop);
        currentCitySunriseTime=(TextView)findViewById(R.id.current_city_sunriseTime);
        currentCitySunsetTime=(TextView)findViewById(R.id.current_city_sunsetTime);
        currentCityWeatherInfoLayout=(LinearLayout) findViewById(R.id.current_city_weather_info_layout);

        currentCityWeatherInfoLayout.setVisibility(View.INVISIBLE);

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
                    //currentCityStatus.setVisibility(View.VISIBLE);
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
                    Log.i("weather","getCurrentLocationWeather executed");
                    getCurrentCityCode(CurrentCityWeatherActivity.this);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();;
            }
        });
    }
/*目前实现方法是：
* 1、获取当前的经纬度
* 2、根据经纬度在http://api.map.baidu.com/geocoder/v2/?上进行逆地理解析，获取当前的城市名
* 3、根据城市名在http://apis.baidu.com/apistore/weatherservice/citylist?cityname=获取城市ID
* 4、根据城市ID在https://api.heweather.com/x3/weather上查询天气
* 存在以下问题：
* 第二步返回的城市名例如“北京市”，“朝阳区”，第三步中应将“市”、“区”、“县”这样的字眼去掉
* 第三步中根据城市名获取城市ID存在重名问题，比如“北京朝阳区”和“辽宁朝阳市”
* 第二步返回的城市名例如“围场满族蒙古族自治县”，第三步中“围场”可以查询成功，用“围场满族蒙古族自治县”查询则失败，这个问题尚未解决*/
    private void getCurrentCityCode(Context context){
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        final String currentDistrict=prefs.getString("current_district","");
        //final String currentDistrict=prefs.getString("current_city","");
        final int length=currentDistrict.length();
        if (!TextUtils.isEmpty(currentDistrict)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("weather","getCurrentCityCode executed");
                    HttpURLConnection connection=null;
                    String currentAddress="";
                    try{
                        StringBuilder urlString=new StringBuilder();
                        //urlString.append("http://apis.baidu.com/apistore/weatherservice/cityinfo?cityname=");
                        urlString.append("http://apis.baidu.com/apistore/weatherservice/citylist?cityname=");
                        //urlString.append("http://apis.baidu.com/apistore/weatherservice/cityinfo?cityname=%E5%8C%97%E4%BA%AC");
                        if (length==2 || currentDistrict.contains("自治")){
                            //如果县名只有两个字或者是自治县，需要全部保留，比如“叶县”、“围场满族蒙古族自治县”
                            currentAddress=currentDistrict;
                        }else {
                            //如果不满足以上两条，将最后一个字去掉
                            currentAddress=currentDistrict.substring(0,length-1);
                        }
                        urlString.append(java.net.URLEncoder.encode(currentAddress));
                        //urlString.append(java.net.URLEncoder.encode("围场"));

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
                            JSONArray jsonArray=jsonObject.getJSONArray("retData");
                            JSONObject subObject=jsonArray.getJSONObject(0);
                            String cityCode=subObject.getString("area_id");
                            String provinceName=subObject.getString("province_cn");
                            String cityName=subObject.getString("district_cn");
                            //String zipCode=subObject.getString("zipCode");
                            //String telAreaCode=subObject.getString("telAreaCode");
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
            public void onFinish(final String response) {
                Log.i("weather","getCurrentCityWeatherByCityCode executed");
                if (Utility.handleCurrentLocationWeatherResponse(CurrentCityWeatherActivity.this,response)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //showWeather();
                            parseAndShowWeather(response);
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
        currentCityNameText.setText(prefs.getString("current_location",""));
        //currentCityStatus.setVisibility(View.INVISIBLE);
        currentCityWeatherInfoLayout.setVisibility(View.VISIBLE);
        currentCityLocation.setVisibility(View.VISIBLE);
    }

    private void parseAndShowWeather(String httpResponse){

        try {
            JSONObject jsonObject=new JSONObject(httpResponse);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject subJsonObject=jsonArray.getJSONObject(0);
            String status=subJsonObject.getString("status");
            if ("ok".equals(status)){
                //解析出当天天气
                JSONArray dailyForecastSubJsonArray=subJsonObject.getJSONArray("daily_forecast");

                //此处目前只解析第一天的天气预报，可使用 for (int i=0;i<dailyForecastSubJsonArray.length();i++)解析未来七天的天气预报
                JSONObject currentWeatherJsonObject=dailyForecastSubJsonArray.getJSONObject(0);
                String minTmp=currentWeatherJsonObject.getJSONObject("tmp").getString("min");
                String maxTmp=currentWeatherJsonObject.getJSONObject("tmp").getString("max");
                String condFirst=currentWeatherJsonObject.getJSONObject("cond").getString("txt_d");
                String condSecond=currentWeatherJsonObject.getJSONObject("cond").getString("txt_n");
                String sunRiseTime=currentWeatherJsonObject.getJSONObject("astro").getString("sr");
                String sunSetTime=currentWeatherJsonObject.getJSONObject("astro").getString("ss");
                String windSpeed=subJsonObject.getJSONObject("now").getJSONObject("wind").getString("spd");
                String windDirection=subJsonObject.getJSONObject("now").getJSONObject("wind").getString("dir");
                String windScale=subJsonObject.getJSONObject("now").getJSONObject("wind").getString("sc");
                String pm25=subJsonObject.getJSONObject("aqi").getJSONObject("city").getString("pm25");
                String airQuality=subJsonObject.getJSONObject("aqi").getJSONObject("city").getString("qlty");
                String date=currentWeatherJsonObject.getString("date");

                JSONObject basicJsonObject=subJsonObject.getJSONObject("basic");
                String updateTime=basicJsonObject.getJSONObject("update").getString("loc");
                String weatherDesp="";
                if (condFirst.equals(condSecond)){
                    weatherDesp=condFirst;
                }else {
                    weatherDesp=condFirst+"转"+condSecond;
                }
                String tempature=minTmp+"℃~"+maxTmp+"℃";
                String wind="风力："+windScale+"级"+windDirection+"  风速："+windSpeed+"Kmph";

                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                currentCityLocation.setText(prefs.getString("current_location",""));
                currentCityNameText.setText(prefs.getString("current_district",""));
                currentCityUpdateText.setText("发布时间："+updateTime);
                currentCityWeatherText.setText("天气："+weatherDesp);
                currentCityTempature.setText("温度："+tempature);
                currentCityWind.setText(wind);
                currentCityPop.setText("PM2.5:"+pm25+"\n"+airQuality);
                currentCitySunriseTime.setText("日出："+sunRiseTime);
                currentCitySunsetTime.setText("日落："+sunSetTime);
                //currentCityStatus.setVisibility(View.INVISIBLE);
                currentCityWeatherInfoLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
