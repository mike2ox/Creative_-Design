package com.example.moonh.runit;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapView;

/**
 * Created by moonh on 2017-06-12.
 */

public class DepartureActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
    private Context mContext =null;
    private boolean m_bTrackingMode = true;

    private TMapView tmapview = null;
    private TMapGpsManager tmapgps = null;

    @Override
    public void onLocationChange(Location location){
        if(m_bTrackingMode){
            tmapview.setLocationPoint(location.getLongitude(), location.getLongitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departure);

        mContext = this;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view);
        tmapview = new TMapView(this);//지도
        linearLayout.addView(tmapview);



        tmapview.setSKPMapApiKey("4794f288-075b-3db6-aa91-84edab861b20");//키값

        tmapview.setCompassMode(true);//현재 보는 방향
        tmapview.setIconVisibility(true);//현위치 아이콘 표시
        tmapview.setZoomLevel(15);//줌레벨
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);


        tmapgps = new TMapGpsManager(DepartureActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        //tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.setProvider(tmapgps.GPS_PROVIDER);
        tmapgps.OpenGps();

        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


    }
}
