package novamarket.com;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import novamarket.com.Service.ChatService;

public class SplashActivity extends AppCompatActivity {
    Boolean islogined;
    String TAG = "SplashActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 3000);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        islogined = pref.getBoolean("islogined",false);
        Log.d(TAG,"islogined: " + islogined);


        
    }

    private class SplashHandler implements Runnable{

        @Override
        public void run() {

            if(islogined){//로그인 했으면 자동로그인
                Log.d(TAG,"로그인 되있는 상태 -> 자동 로그인");
                startActivity(new Intent(getApplication(),MainActivity.class));
                SplashActivity.this.finish();
            }else {//아니면 시작하기 액티비티
                Log.d(TAG,"로그인 안되어있는상태 -> 시작하기 화면이동");
                startActivity(new Intent(getApplication(),StartActivity.class));
                SplashActivity.this.finish();
            }
        }
    }
}
