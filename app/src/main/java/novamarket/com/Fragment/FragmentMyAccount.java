package novamarket.com.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;

import novamarket.com.R;
import novamarket.com.SplashActivity;
import novamarket.com.StartActivity;
import novamarket.com.WithdrawalActivity;

public class FragmentMyAccount extends Fragment {

    private View view;

    private String TAG = "프래그먼트";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView");
            view = inflater.inflate(R.layout.frag_myaccount, container, false);

            Button logoutBtn = view.findViewById(R.id.logoutBtn);
            logoutBtn.setOnClickListener(new View.OnClickListener() {//로그아웃 버튼 클릭하면
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"로그아웃 버튼 클릭");
                    startActivityC(StartActivity.class);//첫 화면으로 이동
                    //islogined,전화번호 값 다 지우기
                    SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
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

}
