package com.example.moonh.runit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by moonh on 2017-06-12.
 */

public class RecommendActivity  extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{


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
    TextView Text_Kcal ;
    ImageView image_State;
    TextView Text_KM ;

    boolean ne = true;
    double tempdistance;
    static double acc;
    double level;
    String kcal;
    //상태


    private String myJSON = "";
    private JSONArray paths ;

    private String data;
    private String param;
    ArrayList<String> mArrayLineID;     //경로관리를 ID로 하기에 정보를 지울때 필요함
    private static    int    mLineID;   //임시변수

    //거리
    boolean n=true;
    String distance;//거리
    static Location tempDestination= new Location("tempDestination");//목적지
    static Location tempDeparture=new Location("tempDeparture");//출발지

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.recommend);
        //여기에 c_lati,c_long을 보내 줘야 근처 코스 추천이 가능함.
        Text_Kcal= (TextView)findViewById(R.id.Text_Kcal);//칼로리
        Text_Timer = (TextView) findViewById(R.id.Text_Timer);//타이머
        Text_KM = (TextView) findViewById(R.id.Text_KM);//거리
        image_State=(ImageView)findViewById(R.id.image_State);
        final ToggleButton button_Start = (ToggleButton) findViewById(R.id.button_Start);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view);
        tmapview = new TMapView(this);//지도
        linearLayout.addView(tmapview);

        tmapview.setSKPMapApiKey("4794f288-075b-3db6-aa91-84edab861b20");//키값

        tmapview.setCompassMode(true);//현재 보는 방향
        tmapview.setIconVisibility(true);//현위치 아이콘 표시
        tmapview.setZoomLevel(15);//줌레벨
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(RecommendActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);
        tmapgps.OpenGps();
        tpoint=tmapgps.getLocation();

        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
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


    //안드로이드에서 서버로 데이터를 전송하는 클래스
    public class registDB extends AsyncTask<Void, Integer, Void> {  //순서대로 doin의 입력 파라미터, onprogressup의 입력 파라미터, doin의 반환형

        @Override
        protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */

            Log.e("POST", param);

            try {
        /* 서버연결 */
                URL url = new URL(
                        "http://163.180.141.149/course_select.php");      //DB가 있는 웹서버 주소
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

/* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();


/* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                //서버에서 응답
                Log.e("RECV DATA", data);

                if (data.equals("0")) {
                    Log.e("RESULT", "성공적으로 처리");
                } else {
                    Log.e("RESULT", "에러발생~ Errcode =" + data);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        public void getData(String url) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {

                    String uri = params[0];

                    BufferedReader bufferedReader = null;
                    try {
                        URL url = new URL(uri);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        StringBuilder sb = new StringBuilder();

                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String json;
                        while ((json = bufferedReader.readLine()) != null) {
                            sb.append(json + "\n");
                        }

                        return sb.toString().trim();

                    } catch (Exception e) {
                        return null;
                    }


                }

                @Override
                protected void onPostExecute(String result) {
                    myJSON = result;
                    List<String> entrypoints = null;
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(myJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        paths = jsonObj.getJSONArray("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < paths.length(); i++) {
                        JSONObject c = null;
                        try {
                            c = paths.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String id = null;
                        try {
                            id = c.getString("Path");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        entrypoints = Arrays.asList(id.split(","));
                    }
                    tmapview.setCenterPoint(Double.parseDouble(entrypoints.get(0)), Double.parseDouble(entrypoints.get(1)), true);
                    TMapPolyLine polyLine = new TMapPolyLine();
                    polyLine.setLineColor(Color.BLUE);
                    polyLine.setLineWidth(5);

                    //고도가 있어서 증가간격을 +3으로 해줌(위도 경도 고도)
                    for (int i = 0; i < entrypoints.size(); i += 3) {
                        TMapPoint point = new TMapPoint(Double.parseDouble(entrypoints.get(i + 1)), Double.parseDouble(entrypoints.get(i)));
                        polyLine.addLinePoint(point);
                    }
//
                    String strID = String.format("line%d", mLineID++);
                    tmapview.addTMapPolyLine(strID, polyLine);
                    mArrayLineID.add(strID);
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute(url);
        }
    }
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
            tmapview.setCenterPoint(location.getLongitude(), location.getLatitude());
            tpoint = tmapgps.getLocation();
            tempDestination.setLatitude(tpoint.getLatitude());
            tempDestination.setLongitude(tpoint.getLongitude());


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

