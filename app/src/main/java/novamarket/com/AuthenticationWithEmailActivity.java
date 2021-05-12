package novamarket.com;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class AuthenticationWithEmailActivity extends AppCompatActivity {
    String TAG = "AuthenticationWithEmailActivity";
    EditText input_email;
    Button sendCodeBtn;
    EditText input_code;
    Button submitCodeBtn;
    String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_with_email);
        
        input_email = findViewById(R.id.input_email);
        sendCodeBtn = findViewById(R.id.sendCodeBtn_with_email);
        input_code = findViewById(R.id.input_code_with_email);
        submitCodeBtn = findViewById(R.id.submitCodeBtn_with_email);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_AuthenticationWithEmail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("이메일로 계정 찾기"); // 툴바 제목 설정
        
        //인증번호 입력창, 버튼 안보이게 처리
        input_code.setVisibility(View.INVISIBLE);
        submitCodeBtn.setVisibility(View.INVISIBLE);

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {//인증메일 받기 버튼 클릭하면
            @Override
            public void onClick(View v) {
                
                //이메일 회원 DB에 있는지 검사
                // get방식 파라미터 추가
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/email_check.php").newBuilder();
                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                        .add("email", input_email.getText().toString())
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
                            if(responseData.equals("1")){//회원 DB에 일치하는 이메일 없으면
                                Toast.makeText(AuthenticationWithEmailActivity.this,"일치하는 이메일이 없습니다.",Toast.LENGTH_SHORT).show();
                            }else {// 회원 DB에 일치하는 이메일 있으면 인증메일 보내기


                                //6자리 난수 생성해서 code변수에 담기
                                code = numberGen(6,1);

                                // get방식 파라미터 추가
                                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/mail.php").newBuilder();
                                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
                                        String url = urlBuilder.build().toString();

                                        Log.i("tag",input_email.getText().toString());
                                Log.i("tag","code: "+code);
                                        // POST 파라미터 추가
                                        RequestBody formBody = new FormBody.Builder()
                                        .add("email", input_email.getText().toString())
                                        .add("code", code.toString())
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
                                            //인증번호 입력창, 버튼 보이게 처리
                                            input_code.setVisibility(View.VISIBLE);
                                            submitCodeBtn.setVisibility(View.VISIBLE);
                                            
                                            submitCodeBtn.setOnClickListener(new View.OnClickListener() {//인증번호 입력 버튼 클릭하면
                                                @Override
                                                public void onClick(View v) {
                                                    Log.d(TAG,"input_code: " + input_code.getText().toString());
                                                    Log.d(TAG,"code: " + code);
                                                    if(input_code.getText().toString().equals(code)){//인증번호입력값과 인증번호가 같으면
                                                        startActivityString(TelephoneChangeActivity.class,"email",input_email.getText().toString());
                                                    }else {
                                                        Toast.makeText(AuthenticationWithEmailActivity.this,"인증번호가 다릅니다.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                        e.printStackTrace();
                                        }
                                        }
                                        });
                                        }
                                        }
                                        });

                            }
                        } catch (Exception e) {
                        e.printStackTrace();
                        }
                        }
                        });
                        }
                        }
                        });

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