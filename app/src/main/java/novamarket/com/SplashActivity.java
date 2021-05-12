package novamarket.com;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 3000);
    }

    private class SplashHandler implements Runnable{

        @Override
        public void run() {
            startActivity(new Intent(getApplication(),StartActivity.class));
            SplashActivity.this.finish();
        }
    }
}
