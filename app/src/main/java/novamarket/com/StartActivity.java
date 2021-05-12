package novamarket.com;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    String TAG = "StartActivity";
    Button startBtn;
    Boolean islogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
        islogined = pref.getBoolean("islogined",false);
        Log.d(TAG,"islogined: " + islogined);


        if(islogined){//로그인 했으면 자동로그인
            startActivityC(MainActivity.class);
        }
        
        startBtn = findViewById(R.id.startBtn);
        //노바마켓 시작하기 버튼 클릭하면
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //전화번호 인증 액티비티로 이동
                startActivityC(AuthenticationActivity.class);
            }
        });
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