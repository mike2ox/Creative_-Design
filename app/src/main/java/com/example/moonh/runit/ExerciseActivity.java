package com.example.moonh.runit;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

/**
 * Created by moonh on 2017-06-12.
 */

public class ExerciseActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{


    private Toolbar mToolbar;
    //지도
    private Context mContext =null;
    private boolean m_bTrackingMode = true;
    private TMapView tmapview = null;
    private TMapGpsManager tmapgps = null;
    private TMapPoint tpoint = null;//추가
    //타이머
    static int counter=0;
    TextView Text_Timer ;
    //거리
    boolean n=true;
    TextView Text_KM ;
    double tempdistance;
    String distance;//거리
    static Location tempDestination= new Location("tempDestination");//목적지
    static Location tempDeparture=new Location("tempDeparture");//출발지
    //속도->kcal
    TextView Text_Kcal ;
    static double acc;
    double level;
    String kcal;
    //상태
    ImageView image_State;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Text_Kcal= (TextView)findViewById(R.id.Text_Kcal);//칼로리
        Text_Timer = (TextView) findViewById(R.id.Text_Timer);//타이머
        Text_KM = (TextView) findViewById(R.id.Text_KM);//거리
        image_State=(ImageView)findViewById(R.id.image_State);
        final ToggleButton button_Start = (ToggleButton) findViewById(R.id.button_Start);

        button_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_Start.isChecked()){
                    mHandler.sendEmptyMessage(0);
                }
                else{
                    mHandler.removeMessages(0);
                }
            }
        });


        //지도
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

        tmapgps = new TMapGpsManager(ExerciseActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);
        tmapgps.OpenGps();
        tpoint=tmapgps.getLocation();

        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);




        //툴바 액션바로 만들기//메인으로..
        mToolbar = (Toolbar) findViewById(R.id.toolbar_Exercise);
        //mToolbar.setNavigationIcon(R.mipmap.menu);
        setSupportActionBar(mToolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeAsUpIndicator(R.drawable.back);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //타이머
            counter++;
            int numHours = (int) Math.floor(counter / 3600);
            int numMins = (int) Math.floor(counter / 60- numHours * 60);
            int numSecs = (int) Math.floor(counter - numMins * 60 - numHours * 3600);
            Text_Timer.setText(((numHours < 10 ? "0" : "") + numHours)+":" + ((numMins < 10 ? "0" : "") + numMins) +":" + ((numSecs < 10 ? "0" : "") + numSecs));
            //거리

            if(n){
                tempDeparture.setLatitude(tpoint.getLatitude());
                tempDeparture.setLongitude(tpoint.getLongitude());
                n=false;
            }

            tempdistance+=tempDeparture.distanceTo(tempDestination);//거리구하기
            distance=String.format("%.0f", tempdistance);
            Text_KM.setText(distance+"m");
            tempDeparture.setLatitude(tpoint.getLatitude());
            tempDeparture.setLongitude(tpoint.getLongitude());
            // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
            this.sendEmptyMessageDelayed(0,1000);

            //칼로리->운동강도 * 체중 * 시간(h) * 1.05 & 상태

            acc=(tempdistance*10)/(counter*36);

            if (acc <1.5) {
                level = 1.5;
                image_State.setImageResource(R.drawable.walking);
                image_State.setPadding(50, 50, 50, 50);
            } else if (acc < 4) {
                level = 2.8;
                image_State.setImageResource(R.drawable.walking);
                image_State.setPadding(50, 50, 50, 50);
            } else if (acc < 5) {
                level = 3.5;
                image_State.setImageResource(R.drawable.walking);
                image_State.setPadding(50, 50, 50, 50);
            } else if (acc < 6.4) {
                level = 5;
                image_State.setImageResource(R.drawable.walking);
                image_State.setPadding(50, 50, 50, 50);
            } else if (acc < 7.3) {
                level = 6;
                image_State.setImageResource(R.drawable.walking);
                image_State.setPadding(50, 50, 50, 50);
            } else if (acc >8) {
                level = 8;
                image_State.setImageResource(R.drawable.runner);
            } else if (acc > 19.6) {
                level = 10;
                image_State.setImageResource(R.drawable.runner);
            } else if (acc >11.2 ) {
                level = 13.5;
                image_State.setImageResource(R.drawable.runner);
            }  else {
                level=16;
                image_State.setImageResource(R.drawable.runner);
            }
            //몸무게가져와서 곱하기!!!*****
            /*
            if(weight==0) {
                if (성별 == 여) {
                    weight = 50;
                } else {
                    weight = 65;
                }
            }
             */

            double tempkcal=level*((double)counter/3600)*1.5;
            kcal=String.format("%.2f", tempkcal);

            Text_Kcal.setText(kcal+"kcal");
            //상태

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
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


}