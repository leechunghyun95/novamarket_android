package novamarket.com;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TelephoneChangeActivity extends AppCompatActivity {
    String TAG = "TelephoneChangeActivity";
    EditText input_telephone_telephone_change;
    Button sendCodeBtn_telephone_change;
    EditText input_code_telephone_change;
    Button submitCodeBtn_telephone_change;
    String telephone;
    String code;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_change);

        input_telephone_telephone_change = findViewById(R.id.input_telephone_telephone_change);
        sendCodeBtn_telephone_change = findViewById(R.id.sendCodeBtn_telephone_change);
        input_code_telephone_change = findViewById(R.id.input_code_telephone_change);
        submitCodeBtn_telephone_change = findViewById(R.id.submitCodeBtn_telephone_change);

        input_code_telephone_change.setVisibility(View.INVISIBLE);
        submitCodeBtn_telephone_change.setVisibility(View.INVISIBLE);


        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_telephone_change);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("전화번호 변경"); // 툴바 제목 설정

        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        Log.d(TAG,"email: " + email);

        sendCodeBtn_telephone_change.setOnClickListener(new View.OnClickListener() {//인증문자 받기 버튼 클릭하면
            @Override
            public void onClick(View v) {
                if(input_telephone_telephone_change.getText().toString().length() == 11){//11자리 숫자를 입력하면 인증문자 발송하고 인증번호 입력창과 확인버튼 보이기.
                    //telephone변수에 입력한 전화번호값 담기
                    telephone = input_telephone_telephone_change.getText().toString();
                    //인증문자 발송하기(아직 안만듬)
                    //6자리 난수 생성해서 code변수에 담기
                    code = numberGen(6,1);
                    //Toast.makeText(getApplicationContext(),"코드는: "+code,Toast.LENGTH_SHORT).show();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        Log.d(TAG,"===sms전송을 위한 퍼미션 확인===");

                        boolean permission = getWritePermission();
                        if(permission){
                            //if permission Already Granted
                            //Send You SMS here
                            Log.d(TAG,"===퍼미션 허용===");
                            try{
                                Log.d(TAG,"===문자 전송 시작====");

                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(telephone,null,"[노바마켓]\n인증번호는 [" + code + "]입니다.",null,null);
                                Log.d(TAG,"code: "+code);
                            }catch (Exception e){
                                Log.d(TAG,"===문자 전송 실패=== 에러코드 e: " + e);
                                e.printStackTrace();

                            }
                        }
                    }
                    else {
                        //Send Your SMS. You don't need Run time permission
                        Log.d(TAG,"===퍼미션 필요 없는 버전임===");
                    }


                    //인증번호 입력창과 확인버튼 보이기
                    input_code_telephone_change.setVisibility(View.VISIBLE);
                    submitCodeBtn_telephone_change.setVisibility(View.VISIBLE);
                }else {//전화번호 11자리 입력안했을 때 잘못됐다는 안내 메시지
                    Toast.makeText(getApplicationContext(),"전화번호가 잘못되었어요. \n다시 한번 확인해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitCodeBtn_telephone_change.setOnClickListener(new View.OnClickListener() {//인증번호 확인버튼 클릭하면
            @Override
            public void onClick(View v) {
                Log.d(TAG,"code: " + code);
                Log.d(TAG,"입력한 코드: " +input_code_telephone_change.getText().toString());
                if(code.equals(input_code_telephone_change.getText().toString())){//인증번호 맞을때
                    Log.d(TAG,"인증번호 일치");
                    //회원 DB에 전화번호 update

                    //인터넷 연결 체크
                    int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                    if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                        Log.d(TAG,"인터넷 연결 체크: 연결됨");
                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/telephone_change.php").newBuilder();
                                urlBuilder.addQueryParameter("v", "1.0"); // 예시
                                String url = urlBuilder.build().toString();

                                // POST 파라미터 추가
                                RequestBody formBody = new FormBody.Builder()
                                .add("telephone", input_telephone_telephone_change.getText().toString())
                                .add("email", email)
                                .build();

                                // 요청 만들기
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                .url(url)
                                .post(formBody)
                                .build();

                        // 응답 콜백
                                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                // 응답 실패
                                Log.i("tag", "응답실패");
                                } else {
                                // 응답 성공
                                Log.i("tag", "응답 성공");
                        final String responseData = response.body().string();


                                // 서브 스레드 Ui 변경 할 경우 에러
                                // 메인스레드 Ui 설정
                                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                    if(responseData.equals("1")){//전화번호 update 성공했으면

                                        //로그인 시에 회원 전화번호랑 자동로그인값 트루로 SharedPreferences에 저장
                                        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("telephone",input_telephone_telephone_change.getText().toString());
                                        editor.putBoolean("islogined",true);
                                        editor.apply();

                                        startActivityC(MainActivity.class);
                                    }else {//실패했을때
                                        Toast.makeText(TelephoneChangeActivity.this,"다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                e.printStackTrace();
                                }
                                }
                                });
                                }
                                }
                                });


                    }else {
                        Log.d(TAG,"인터넷 연결 체크: 연결안됨");
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }


                }else {//인증번호 틀릴때
                    Log.d(TAG,"인증번호 불일치");
                }
            }
        });

    }

    //툴바 조작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                // todo
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //인증번호 생성 메서드
    public static String numberGen(int len, int dupCd ) {

        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for(int i=0;i<len;i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i-=1;
                }
            }
        }
        return numStr;
    }


    public boolean getWritePermission(){
        boolean hasPermission =
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS) ==
                        PackageManager.PERMISSION_GRANTED);
        if(!hasPermission){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},10);
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode){
            case  10:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission is Granted
                    //Send your SMS here
                }
            }

        }
    }

    // 액티비티 전환 함수
    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }
    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name , String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c){
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

}