package com.example.yumi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Studentsignup extends AppCompatActivity {

    EditText et_id, et_pw, et_pw_chk;
    String sId, sPw, sPw_chk;
    String JsonResultString;
    Studentsignup.GetData task;
    String idCheck = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("학생 회원가입");
        setContentView(R.layout.activity_studentsignup);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        et_id = (EditText) findViewById(R.id.id_text);
        et_pw = (EditText) findViewById(R.id.password_text);
        et_pw_chk = (EditText) findViewById(R.id.password_check2);

        Button id_check = findViewById(R.id.id_check);
        id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아이디 중복 체크
                //0-> ok,1->not ok
                String id = et_id.getText().toString();

                if(id.length()<=0){
                    Toast.makeText(Studentsignup.this, String.format("아이디를 입력해 주세요."), Toast.LENGTH_SHORT).show();
                }
                else{
                    task = new Studentsignup.GetData();
                    task.execute("http://1.234.38.211/id_check.php?id=" + id, "");
                }
            }
        });
    }


    //앱바 메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // User chose the "Settings" item, show the app settings UI...
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //계속하기 버튼을 누른 경우
    public void goNext(View view){

        /* 버튼을 눌렀을 때 동작하는 소스 */
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();

        /*아이디>=1 비밀번호>=6 비밀번호와 비밀번호 확인이 일치하여야 함*/
        if((sId.getBytes().length>=1 && sPw.getBytes().length>=6 && sPw_chk.getBytes().length>=6)){

            if(sPw.equals(sPw_chk)&&idCheck.equals("0"))
            {
                Toast.makeText(Studentsignup.this, String.format("비밀번호가 일치합니다"), Toast.LENGTH_SHORT).show();
                /* 패스워드 확인이 정상적으로 됨 */
                //다음 학생 정보 입력하는 화면으로 넘어감.
                Intent intent = new Intent(getApplicationContext(), Privateinformation2.class);

                intent.putExtra("id",sId); /*송신  id와 pw를 다음 개인정보 activity에 넘긴 후 한 번에 db에 insert*/
                intent.putExtra("pw",sPw);
                startActivity(intent);

            }
            else if(idCheck.equals("1")){
                Toast.makeText(Studentsignup.this, "" +
                        "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
            }
            else {
                /* 패스워드 확인이 불일치 함 */
                Toast.makeText(Studentsignup.this, "" +
                        "비밀번호가 불일치합니다", Toast.LENGTH_SHORT).show();
            }
        }
        else{  Toast.makeText(Studentsignup.this, "" +
                "입력 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            //Toast.makeText(Studentsignup.this, String.format("\n%d %d", sPw.getBytes().length, sId.getBytes().length), Toast.LENGTH_SHORT).show();
        }

    }

    private class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                JsonResultString = result;
                InitializeQuestionData();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                return null;
            }
        }
    }

    public void InitializeQuestionData() {
        String TAG_JSON = "webnautes";
        String TAG_CHECK = "check";

        try {
            JSONObject jsonObject = new JSONObject(JsonResultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONObject item = jsonArray.getJSONObject(0);
            idCheck = item.getString(TAG_CHECK);

            if (idCheck.equals("1")) {
                Toast.makeText(getApplicationContext(),"중복된 아이디가 존재합니다.", Toast.LENGTH_SHORT).show();
            }
            if (idCheck.equals("0")) {
                Toast.makeText(getApplicationContext(),"가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
        }
    }
}