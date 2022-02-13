package novamarket.com.Fragment;

import novamarket.com.BuyListActivity;
import novamarket.com.LikeListActivity;
import novamarket.com.MainActivity;
import novamarket.com.ModifyProfileActivity;
import novamarket.com.SellListActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import novamarket.com.NetworkStatus;
import novamarket.com.R;
import novamarket.com.SplashActivity;
import novamarket.com.StartActivity;
import novamarket.com.WithdrawalActivity;

public class FragmentMyAccount extends Fragment {

    private View view;

    private String TAG = "프래그먼트";
    ImageView profile_image;
    TextView nickname;
    TextView email;
    Button profile_show_btn;
    String user_nickname;
    String user_email;
    String user_profile_image_url;
    SharedPreferences pref;
    String userIdx;//사용자 번호



    RoomDB userDB;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.frag_myaccount, container, false);

        user_nickname = "닉네임";
        user_email="이메일";
        user_profile_image_url="user.png";

        pref = getActivity().getSharedPreferences("pref",Activity.MODE_PRIVATE);
        String userData = pref.getString("userData",null);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //판매내역 버튼
        LinearLayout linearLayout_sellList = view.findViewById(R.id.linearLayout_sellList);
        linearLayout_sellList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityString(SellListActivity.class,"userIdx",userIdx);
            }
        });

        //구매내역 버튼
        LinearLayout linearLayout_buyList = view.findViewById(R.id.linearLayout_buyList);
        linearLayout_buyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityString(BuyListActivity.class,"userIdx",userIdx);
            }
        });
        
        //관심목록 버튼
        LinearLayout linearLayout_likeList = view.findViewById(R.id.linearLayout_likeList);
        linearLayout_likeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityString(LikeListActivity.class,"userIdx",userIdx);
            }
        });


            Button logoutBtn = view.findViewById(R.id.logoutBtn);
            logoutBtn.setOnClickListener(new View.OnClickListener() {//로그아웃 버튼 클릭하면
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"로그아웃 버튼 클릭");
                    startActivityC(StartActivity.class);//첫 화면으로 이동
                    //islogined,전화번호 값 다 지우기
//                    SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();
                }
            });

            Button withdrawalBtn = view.findViewById(R.id.withdrawalBtn);
            withdrawalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"회원 탈퇴 버튼 클릭");
                    startActivityC(WithdrawalActivity.class);
                }
            });

            return view;
            }

    // 액티비티 전환 함수
    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getActivity().getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        getActivity().overridePendingTransition(0, 0);
    }
    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getActivity().getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        getActivity().overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name , String sendString) {
        Intent intent = new Intent(getActivity().getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        getActivity().overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c){
        Intent intent = new Intent(getActivity().getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");


        String userData = pref.getString("userData",null);
        Log.d(TAG,"저장된 userData: " + userData);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            user_nickname = jsonObject.getString("nickname");
            user_email = jsonObject.getString("email");
            user_profile_image_url = jsonObject.getString("profile_image");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        profile_image = view.findViewById(R.id.profile_image);
        nickname = view.findViewById(R.id.nickname);
        email = view.findViewById(R.id.email);

        nickname.setText(user_nickname);
        email.setText(user_email);

        Glide.with(this)
                .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/"+user_profile_image_url)
                .apply(new RequestOptions().circleCrop())
                .into(profile_image);


        //프로필 보기 버튼
        profile_show_btn = view.findViewById(R.id.profile_show_btn);
        profile_show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"프로필 보기 버튼 클릭");
                startActivityString(ModifyProfileActivity.class,"userData",userData);
            }
        });
    }
}
