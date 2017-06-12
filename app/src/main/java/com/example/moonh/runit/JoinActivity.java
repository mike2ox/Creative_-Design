package com.example.moonh.runit;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by moonh on 2017-06-12.
 */

public class JoinActivity extends AppCompatActivity {
    EditText User_email,User_name, User_PW,User_age,User_height,User_weight;
    RadioGroup gender;
    String user_email, user_name,user_password,user_age,user_gender,user_height,user_weight;
    //유저의 이메일, 이름, 비밀번호, 나이, 성별, 키, 몸무게 입력받도록 해줌
    AlertDialog.Builder alertBuilder; //여기서 ()안에는 Activity의 this가 되야함
    String data = "";

    SharedPreferences pref; //
    SharedPreferences.Editor editor;        //이부분 json이나 parse부분으로 대체 가능하지 않나?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        User_email = (EditText)findViewById(R.id.Text_Email);
        User_PW = (EditText)findViewById(R.id.Text_PW);
        ////////////////////////////////////////레이아웃 받아서 이부분 수정해야함
        User_name = (EditText)findViewById(R.id.Text_Name);
        User_age = (EditText)findViewById(R.id.Text_Age);
        gender = (RadioGroup)findViewById(R.id.Gender_radio);

        User_height = (EditText)findViewById(R.id.Text_Height);
        User_weight = (EditText)findViewById(R.id.Text_weight);
        /*
        User_email = (EditText)findViewById(R.id.Text_Email);
        User_PW = (EditText)findViewById(R.id.Text_PW);
        ////////////////////////////////////////레이아웃 받아서 이부분 수정해야함
        User_name = (EditText)findViewById(R.id.Text_Name);
        User_age = (EditText)findViewById(R.id.Text_Age);
        gender = (RadioGroup)findViewById(R.id.Gender_radio);

        User_height = (EditText)findViewById(R.id.Text_Height);
        User_weight = (EditText)findViewById(R.id.Text_weight);


        user_email = User_email.getText().toString();        //edittext로 받은걸 string으로 저장
        user_password = User_PW.getText().toString();
        user_name = User_name.getText().toString();
        user_age = User_age.getText().toString();

        //라디오 버튼에서 string화 해서 정보 저장
        int a = gender.getCheckedRadioButtonId();
        RadioButton t = (RadioButton)findViewById(a);
        user_gender = t.getText().toString();
        //user_gender = User_gender.getText().toString();

        user_height = User_height.getText().toString();
        user_weight = User_weight.getText().toString();*/



    }
    /* onClick에서 정의한 이름과 똑같은 이름으로 생성 */
    //로그인 버튼이 수행될때 적용
    public void bt_Complete(View view){                 //레이아웃에서   android:onClick="bt_Login" 부분에서 ""를 함수이름으로 넣어줘야함


        user_email = User_email.getText().toString();        //edittext로 받은걸 string으로 저장
        user_password = User_PW.getText().toString();
        user_name = User_name.getText().toString();
        user_age = User_age.getText().toString();

        //라디오 버튼에서 string화 해서 정보 저장
        int a = gender.getCheckedRadioButtonId();
        RadioButton t = (RadioButton)findViewById(a);
        user_gender = t.getText().toString();
        //user_gender = User_gender.getText().toString();

        user_height = User_height.getText().toString();
        user_weight = User_weight.getText().toString();

        registDB rDB = new registDB();
        rDB.execute();


        //이부분 의문
        /*
        if(Build.VERSION.SDK_INT >= 11){
           rDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            rDB.execute();
        }*/

        Toast.makeText(JoinActivity.this, "가입성공 성공", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        //////////////////이부분에서 회원가입이 완료됬을때 로그인 페이지로 다시 돌아가게 하려면 onresume과 onpause를 선언
        finish();
    }

    //안드로이드에서 서버로 데이터를 전송하는 클래스
    public class registDB extends AsyncTask<Void, Integer, Void> {  //순서대로 doin의 입력 파라미터, onprogressup의 입력 파라미터, doin의 반환형

           /* @Override
            protected void onPreExecute()
            {
                Log.v("onPreExecute","돌아가짐");
                super.onPreExecute();
            }
*/

        @Override
        protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */
            String param = "u_email=" +user_email+
                    "&u_name=" +user_name+
                    "&u_password=" +user_password+
                    "&u_age=" +user_age+
                    "&u_gender=" +user_gender+
                    "&u_height=" +user_height+
                    "&u_weight=" +user_weight+"";
            Log.e("POST",param);

            try {
            /* 서버연결 */
                URL url = new URL(
                        "http://163.180.141.149/walkingcourse_join.php");      //DB가 있는 웹서버 주소
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
                while ( ( line = in.readLine() ) != null ){
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                //서버에서 응답
                Log.e("RECV DATA",data);

                if(data.equals("0")){
                    Log.e("RESULT","성공적으로 처리");
                }
                else{
                    Log.e("RESULT","에러발생~ Errcode ="+data);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //alertBuilder = new AlertDialog.Builder(JoinActivity.this);
        /* 서버에서 응답 */
            Log.e("RECV DATA",data);

            /////클라이언트 상황에서 어떤 에러인지 보여주려고 alert만들고 있었음
                /*if(data.equals("0"))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");

                    alertBuilder
                            .setTitle("알림")
                            .setMessage("성공적으로 등록되었습니다!")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else
                {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }*/
        }

    }


    ///////////////6월 10일 17:50에 추가가
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
