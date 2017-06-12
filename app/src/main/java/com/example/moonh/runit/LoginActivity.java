package com.example.moonh.runit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;
import java.net.*;


/**
 * Created by moonh on 2017-05-29.
 * project name :  inbody-based-walk-recommendation
 * brief : 제일 처음 로그인할 때 발생하는 액티비티를 다루는 class
 */

public class LoginActivity extends AppCompatActivity{
    private final static String TAG = LoginActivity.class.getSimpleName();

    EditText User_email, User_PW;
    String email, password;
    String data = "";

    //SharedPreferences pref; //
    //SharedPreferences.Editor editor;        //이부분 json이나 parse부분으로 대체 가능하지 않나?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //startLoading();
        User_email = (EditText)findViewById(R.id.Input_Email);
        User_PW = (EditText)findViewById(R.id.Input_Pw);


    }

    /* onClick에서 정의한 이름과 똑같은 이름으로 생성 */
    //로그인 버튼이 수행될때 적용
    public void bt_Login(View view){
        try {
            email = User_email.getText().toString();        //edittext로 받은걸 string으로 저장
            password = User_PW.getText().toString();
        }
        catch (NullPointerException e){
            Log.e("err",e.getMessage());
        }

        loginDB lDB = new loginDB();
        lDB.execute();


/*
       Boolean validation = LoginValidation(email,password);
        if(validation){
            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
        }*/
    }

    //회원가입이 실행될때 작용
    public void bt_Join(View view){
        Intent i = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(i);
        finish();       //이게 시행되면 액티비티가 종료됨
        //문제 : 회원가입을 완료하고 다시 액티비티를 실행시키면 해결?
    }
    /*
        private boolean LoginValidation(String ID, String Password){
            if(pref.getString("id","").equals(ID) && pref.getString("pw","").equals(Password)) {
                // login success
                return true;
            } else if (pref.getString("id","").equals(null)){
                // sign in first
                Toast.makeText(LoginActivity.this, "Please Sign in first", Toast.LENGTH_LONG).show();
                return false;
            } else {
                // login failed
                return false;
            }
        }*/
    //DB - 클라이언트 연동 : 로그인 기능
    public class loginDB extends AsyncTask<Void, Integer, Void> {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this); //여기서 ()안에는 Activity의 this가 되야함

        @Override
        protected Void doInBackground(Void... unused) {
            Log.i("AsyncTask", "doinbackground");
/* 인풋 파라메터값 생성 */
            /*String param = "User_email=" +email+
                    "&User_name=" +""+
                    "&User_password=" +password+
                    "&User_age=" +""+
                    "&User_gender=" +""+
                    "&User_height=" +""+
                    "&User_weight=" +"";
*/          //이렇게 줄여도 되는지 확인
            String param = "u_email=" +email+
                    "&u_password=" +password+"";
            Log.e("POST",param);

            try {
/* 서버연결 */
                URL url = new URL(
                        "http://163.180.141.149/walkingcourse_Login_server.php");
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
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

/* 서버에서 응답 */
                Log.e("RECV DATA",data);

                if(data.contains("ACCESS"))    {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
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

        /* 서버에서 응답 */
            //Log.e("RECV DATA",data);

            /////클라이언트 상황에서 어떤 에러인지 보여주려고 alert만들고 있었음

            if(data.contains("ACCESS"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");

                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("USER_EMAIL",email);
                                intent.putExtra("USER_PW",password);        //인텐트에 값을 저장해서 main에 넘김
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else if(data.contains("NOTACCESS"))
            {
                Log.e("RESULT","비밀번호가 일치하지 않습니다.");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("비밀번호가 일치하지 않습니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }/*
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
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }*/
        }

    }
    //안드로이드 사이클

    @Override
    protected void onResume() {
        super.onResume();
        /*registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
           // final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.i(TAG, "액티비티가 다시 시작됨");
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindService(mServiceConnection);
        //mBluetoothLeService = null;
    }

}

