package novamarket.com;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import novamarket.com.Adapter.SellListViewPagerAdapter;
import novamarket.com.Fragment.FragmentLikeList_market;
import novamarket.com.Fragment.FragmentSellList_done;
import novamarket.com.Fragment.FragmentSellList_now;

public class SellListActivity extends AppCompatActivity {
    Fragment fragment1, fragment2;
    static String userIdx,userData;
    String TAG = "SellListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_list);

        //현재 사용중인 사용자 누구인지 체크
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userData = pref.getString("userData",null);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");
            Log.d(TAG,"userIdx: " + userIdx);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_sell_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("판매 내역"); // 툴바 제목 설정

        fragment1 = new FragmentSellList_now();//뷰페이저 화면1
        fragment2 = new FragmentSellList_done();//뷰페이저 화면2

        //판매중인 상품, 거래완료 상품 프래그먼트에 사용자 번호 전달
        Bundle bundle = new Bundle();
        bundle.putString("userIdx",userIdx);
        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);


        ViewPager2 viewPager2 = findViewById(R.id.viewPager_sell_list);
        viewPager2.setAdapter(new viewPagerAdapter(this));

        // 탭과 뷰페이저 연결
        TabLayout tabLayout = findViewById(R.id.tabLayout_sell_list);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    // 첫번째 화면일 때
                    tab.setText("판매중");
                } else {
                    // 두번째 화면일 때
                    tab.setText("거래완료");
                }
            }
        });
        tabLayoutMediator.attach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 뷰페이저2 어댑터
    private class viewPagerAdapter extends FragmentStateAdapter{


        public viewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0){
                return fragment1;
            }else {
                return fragment2;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}