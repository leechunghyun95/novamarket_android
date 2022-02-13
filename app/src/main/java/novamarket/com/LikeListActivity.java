package novamarket.com;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import novamarket.com.Fragment.FragmentLikeList_market;
import novamarket.com.Fragment.FragmentLikeList_nova;

public class LikeListActivity extends AppCompatActivity {
    String TAG = "LikeListActivity";
    static String userIdx,userData;
    Fragment fragment1, fragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);

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
        Toolbar toolbar = findViewById(R.id.toolbar_like_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("관심목록"); // 툴바 제목 설정

        fragment1 = new FragmentLikeList_market();
        fragment2 = new FragmentLikeList_nova();

        //판매중인 상품, 거래완료 상품 프래그먼트에 사용자 번호 전달
        Bundle bundle = new Bundle();
        bundle.putString("userIdx",userIdx);
        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager_like_list);
        viewPager2.setAdapter(new LikeListActivity.viewPagerAdapter(this));

        // 탭과 뷰페이저 연결
        TabLayout tabLayout = findViewById(R.id.tabLayout_like_list);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    // 첫번째 화면일 때
                    tab.setText("중고거래");
                } else {
                    // 두번째 화면일 때
                    tab.setText("팀노바생활");
                }
            }
        });
        tabLayoutMediator.attach();
    }


    // 뷰페이저2 어댑터
    private class viewPagerAdapter extends FragmentStateAdapter {


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }

}