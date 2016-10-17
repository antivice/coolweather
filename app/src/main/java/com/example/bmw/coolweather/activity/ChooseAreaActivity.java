package com.example.bmw.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmw.coolweather.R;
import com.example.bmw.coolweather.db.CoolWeatherDB;
import com.example.bmw.coolweather.model.City;
import com.example.bmw.coolweather.model.County;
import com.example.bmw.coolweather.model.Province;
import com.example.bmw.coolweather.util.HttpCallbackListener;
import com.example.bmw.coolweather.util.HttpUtil;
import com.example.bmw.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by bmw on 2016/10/15.
 */

public class ChooseAreaActivity extends Activity{

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ListView listView;
    private TextView titleText;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private int currentLevel;

    private boolean isFromCityWeather=false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        isFromCityWeather=getIntent().getBooleanExtra("from_city_weather",false);

        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    if (isMunicipality()){
                        selectedCity=cityList.get(position);
                        //如果是直辖市，不进行第三级查找，直接查询天气
                        Intent intent=new Intent(ChooseAreaActivity.this,CityWeatherActivity.class);
                        intent.putExtra("county_code",selectedCity.getCityCode());
                        intent.putExtra("county_name",selectedCity.getCityName());
                        startActivity(intent);
                        finish();
                    }else {
                        selectedCity=cityList.get(position);
                        queryCounties();
                    }
                }else if (currentLevel==LEVEL_COUNTY){
                    selectedCounty=countyList.get(position);
                    Intent intent=new Intent(ChooseAreaActivity.this,CityWeatherActivity.class);
                    intent.putExtra("county_code",selectedCounty.getCountyCode());
                    intent.putExtra("county_name",selectedCounty.getCountyName());
                    startActivity(intent);
                    finish();
                }
            }
        });

        queryProvinces();
    }

    private void queryProvinces(){
        provinceList=coolWeatherDB.loadProvinces();
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            if (null!=dataList.get(0)){
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText("中国");
                currentLevel=LEVEL_PROVINCE;
            }
        }else{
            queryFromServer(null,"province");
        }
    }

    private void queryCities(){
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            if (null!=dataList.get(0)){
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText(selectedProvince.getProvinceName());
                currentLevel=LEVEL_CITY;
            }
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryCounties(){
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            if (null!=dataList.get(0)){
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText(selectedCity.getCityName());
                currentLevel=LEVEL_COUNTY;
            }
        }else {
            queryFromServer(selectedCity.getCityPyName(),"county");
        }
    }

    private void queryFromServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address="http://flash.weather.com.cn/wmaps/xml/"+code+".xml";
        }else {
            address="http://flash.weather.com.cn/wmaps/xml/china.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if ("city".equals(type)){
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }

                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();;
                            }else if("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if (null==progressDialog){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (null!=progressDialog){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed(){
        if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else{
            if (isFromCityWeather){
                Intent intent=new Intent(this,CityWeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    //判断选中省份是否为直辖市
    private boolean isMunicipality(){
        String name=selectedProvince.getProvinceName();
        if ("北京".equals(name) || "上海".equals(name) || "天津".equals(name) ||"重庆".equals(name)|| "海南".equals(name)){
            return true;
        }
        return false;
    }
}
