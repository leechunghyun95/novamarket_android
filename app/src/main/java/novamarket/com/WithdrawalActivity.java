package novamarket.com;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class WithdrawalActivity extends AppCompatActivity {
    String TAG = "WithdrawalActivity";
    String telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        telephone = pref.getString("telephone",null);

        Log.d(TAG,"[SharedPreferences] telephone: " + telephone);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_Withdrawal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("계정 삭제하기"); // 툴바 제목 설정

        Button withdrawCancleBtn = findViewById(R.id.withdrawCancleBtn);
        withdrawCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"더 사용해볼래요 버튼 클릭");
                finish();
            }
        });

        Button withdrawlOkBtn = findViewById(R.id.withdrawalOkBtn);
        withdrawlOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"네, 삭제할게요 버튼 클릭");
                // get방식 파라미터 추가
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/withdrawal.php").newBuilder();
                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                        .add("telephone", telephone)
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
                            if(responseData.equals("1")){
                                Log.d(TAG,"회원 탈퇴 성공");
                                startActivityC(StartActivity.class);

                                //자동로그인, 전화번호 값 삭제
                                SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.clear();
                                editor.apply();
                            }else {
                               Toast.makeText(WithdrawalActivity.this,"다시 시도해주세요.",Toast.LENGTH_SHORT).show();
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