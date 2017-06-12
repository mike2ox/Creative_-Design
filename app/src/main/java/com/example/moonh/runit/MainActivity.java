package com.example.moonh.runit;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{


    private Toolbar mToolbar;
    //지도
    private Context mContext =null;
    private boolean m_bTrackingMode = true;
    private TMapView tmapview = null;
    private TMapGpsManager tmapgps = null;
    private TMapPoint tpoint = null;//추가
    //타이머
    static int counter=0;


    static Location tempDestination= new Location("tempDestination");//목적지
    static Location tempDeparture=new Location("tempDeparture");//출발지


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_Departure = (Button) findViewById(R.id.button_Departure);
        Button button_Destination = (Button) findViewById(R.id.button_Destination);
        Button button_Recomand = (Button) findViewById(R.id.button_Recomand);
        Button button_common = (Button)findViewById(R.id.button_common);


        // final ToggleButton button_Start = (ToggleButton) findViewById(R.id.button_Start);

        button_Departure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent departureIntent = new Intent(MainActivity.this, DepartureActivity.class);
                startActivity(departureIntent);
            }

        });
        button_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commonIntent = new Intent(MainActivity.this, ExerciseActivity.class);
                startActivity(commonIntent);
            }
        });
        button_Destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Intent destinationIntent = new Intent(MainActivity.this, ExerciseActivity.class);
                //  MainActivity.this.startActivity(destinationIntent);
            }
        });
        button_Recomand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exerciseIntent = new Intent(MainActivity.this, RecommendActivity.class);
                startActivity(exerciseIntent);
            }
        });



        //지도
        mContext = this;

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.map_view);
        tmapview = new TMapView(this);//지도

        tmapview.setSKPMapApiKey("4794f288-075b-3db6-aa91-84edab861b20");//키값
        tmapview.setCompassMode(true);//현재 보는 방향
        tmapview.setIconVisibility(true);//현위치 아이콘 표시
        tmapview.setZoomLevel(15);//줌레벨
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);
        tmapgps.OpenGps();
        tpoint=tmapgps.getLocation();

        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


        frameLayout.addView(tmapview);
//        setContentView(frameLayout);

        //툴바 액션바로 만들기//메인으로..
        mToolbar = (Toolbar) findViewById(R.id.toolbar_Exercise);
        //mToolbar.setNavigationIcon(R.mipmap.menu);
        setSupportActionBar(mToolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeAsUpIndicator(R.drawable.back);
    }

    // mToolbar = (Toolbar) findViewById(R.id.toolbar_Main);
    //mToolbar.setNavigationIcon(R.mipmap.menu);
    //setSupportActionBar(mToolbar);


    @Override
    public void onLocationChange(Location location){
        if(m_bTrackingMode){
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
            tmapview.setCenterPoint(location.getLongitude(),location.getLatitude());
            tpoint=tmapgps.getLocation();
            tempDestination.setLatitude(tpoint.getLatitude());
            tempDestination.setLongitude(tpoint.getLongitude());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//}
}
