package com.example.yumi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class stdPrfInform extends AppCompatActivity {
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON="webnautes";
    private Spinner mSpinner = null;
    private ArrayAdapter<String> mSpinnerAdapter = null;
    private Spinner gSpinner = null;
    private ArrayAdapter<String> gSpinnerAdapter = null;
    phpConnect task;
    phpInform changeInform;
    TextView id ;
    EditText nick ;
    String loginId;
    String nickname;
    String schoolType="";
    String grade="";
    String getSchool = "";
    String getGrade = "";
    String inputNick="";
    String JsonResultString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_student_information);

        SharedPreferences auto = getSharedPreferences("yumi", Activity.MODE_PRIVATE);
        loginId = auto.getString("id",null);
        nickname = auto.getString("nickName",null);
        schoolType = auto.getString("school", null);
        grade = auto.getString("grade", null);

        id = (TextView)findViewById(R.id.prfid);
        id.setText(loginId);

        nick = (EditText)findViewById(R.id.prfNick);
        nick.setHint(nickname);

        mSpinner = (Spinner) findViewById(R.id.school);
        mSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                (String[])getResources().getStringArray(R.array.school));
        mSpinnerAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);



        gSpinner = (Spinner) findViewById(R.id.grade);
        gSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                (String[])getResources().getStringArray(R.array.age));
        gSpinnerAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        gSpinner.setAdapter(gSpinnerAdapter);


        if(schoolType.equals("중학교")){
            mSpinner.setSelection(1);
        }
        else if(schoolType.equals("고등학교")){
            mSpinner.setSelection(2);
        }

        if(grade.equals("1학년")){
            gSpinner.setSelection(1);
        }
        else if(grade.equals("2학년")){
            gSpinner.setSelection(2);
        }
        else if(grade.equals("3학년")){
            gSpinner.setSelection(3);
    }

    }


    class phpConnect extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://1.234.38.211/stdChangeNick.php?id="+loginId+"&nickname="+inputNick;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                return sb.toString();

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            Log.d(TAG, "response  - " + result);

            if (result == null){
               // mTextViewResult.setText("error occurred");
            }
            else {
                JsonResultString = result;
                showResult();
            }
        }
    }

    class phpInform extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://1.234.38.211/stdChangeInform.php?id="+loginId+"&grade="+getGrade+"&school="+getSchool;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                return sb.toString();

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            Log.d(TAG, "response  - " + result);

            if (result == null){
                // mTextViewResult.setText("error occurred");
            }
            else {
                JsonResultString = result;
                Result();
            }
        }
    }

    public void showResult() {
        String TAG_JSON = "webnautes";
        String TAG_CHECK = "check";

        try {
            JSONObject jsonObject = new JSONObject(JsonResultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONObject item = jsonArray.getJSONObject(0);
            String check = item.getString(TAG_CHECK);

            if (check.equals("0")) {
                Toast.makeText(getApplicationContext(),"중복된 닉네임이 존재합니다.", Toast.LENGTH_SHORT).show();
            }
            if (check.equals("1")) {
                SharedPreferences sf = getSharedPreferences("yumi",MODE_PRIVATE);
                SharedPreferences.Editor editor =  sf.edit();
                editor.putString("nickName" , inputNick);
                editor.apply();
                nick = (EditText)findViewById(R.id.prfNick);
                nick.setHint(inputNick);
                Toast.makeText(getApplicationContext(),"닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

        }
    }


    public void Result(){
        String TAG_JSON = "webnautes";
        String TAG_CHECK = "check";

        try {
            JSONObject jsonObject = new JSONObject(JsonResultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONObject item = jsonArray.getJSONObject(0);
            String check = item.getString(TAG_CHECK);

            if (check.equals("0")) {
                Toast.makeText(getApplicationContext(),"학적 정보를 변경하지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
            if (check.equals("1")) {
                SharedPreferences sf = getSharedPreferences("yumi",MODE_PRIVATE);
                SharedPreferences.Editor editor =  sf.edit();
                editor.putString("grade" , getGrade);
                editor.putString("school",getSchool);
                editor.apply();
                Toast.makeText(getApplicationContext(),getSchool+" "+getGrade+"으로 변경되었습니다. ", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

        }
    }
    public void chgNick(View view){
        inputNick=nick.getText().toString();
        if (inputNick.equals("")){
            Toast.makeText(getApplicationContext(),"변경하시려면 빈 값을 입력하지 마세요", Toast.LENGTH_SHORT).show();
        }
        else if(inputNick.contains(" ")){
            Toast.makeText(getApplicationContext(),"띄어쓰기 없이 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else{
            task = new phpConnect();
            task.execute();
            nick.setText("");
        }
    }

    public void chgPWD(View view){
        Intent i = new Intent(stdPrfInform.this, changePassWord.class);
        startActivity(i);
    }

    public void chgInform(View view){
        getSchool = mSpinner.getSelectedItem().toString();
        getGrade = gSpinner.getSelectedItem().toString();
        //Toast.makeText(getApplicationContext(),"->" + schoolType + " " + grade, Toast.LENGTH_SHORT).show();

        if (schoolType.equals(getSchool) && grade.equals(getGrade)){
            //Toast.makeText(getApplicationContext(),"변경사항 발생 X", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(getApplicationContext(),"변경사항 발생 0", Toast.LENGTH_SHORT).show();
            changeInform = new phpInform();
            changeInform.execute();
        }
        Intent i = new Intent(stdPrfInform.this, StudentQuestionlist.class);
        startActivity(i);

    }

}