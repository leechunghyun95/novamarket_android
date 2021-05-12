package novamarket.com;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import novamarket.com.Fragment.FragmentChat;
import novamarket.com.Fragment.FragmentCommunity;
import novamarket.com.Fragment.FragmentHome;
import novamarket.com.Fragment.FragmentMyAccount;

public class MainActivity extends AppCompatActivity {


        private String TAG = "메인";

        Fragment fragment_home;
        Fragment fragment_community;
        Fragment fragment_chat;
        Fragment fragment_myaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String telephone = pref.getString("telephone",null);
        Log.d(TAG,"telephone: " + telephone);


        //프래그먼트 생성
        fragment_home = new FragmentHome();
        fragment_community = new FragmentCommunity();
        fragment_chat = new FragmentChat();
        fragment_myaccount = new FragmentMyAccount();


        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_home).commitAllowingStateLoss();

        // 바텀 네비게이션
        BottomNavigationView bottomNavigationView;
        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "바텀 네비게이션 클릭");

                switch (item.getItemId()){
                    case R.id.home:
                        Log.i(TAG,"home 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_home).commitAllowingStateLoss();
                        return true;

                    case R.id.community:
                        Log.i(TAG,"community 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_community).commitAllowingStateLoss();
                        return true;

                    case R.id.chat:
                        Log.i(TAG,"chat 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_chat).commitAllowingStateLoss();
                        return true;

                    case R.id.myaccount:
                        Log.i(TAG,"myaccount 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_myaccount).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });

    }
}