package novamarket.com;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import novamarket.com.Fragment.FragmentHome;


/*
직접 커스텀한 다이얼로그들을 띄워주고 다이얼로그 안에서의 동작을 정의하는 클래스 (싱글톤)
 */
public class CustomDialog extends Dialog {
    String TAG = "CustomDialog";
    private static CustomDialog customDialog;

    private CustomDialog(@NonNull Context context) {
        super(context);
    }

    public static CustomDialog getInstance(Context context) {
            customDialog = new CustomDialog(context);

        return customDialog;
    }

    //다이얼로그 생성하기
    public void showDefaultDialog() {
        //참조할 다이얼로그 화면을 연결한다.
        customDialog.setContentView(R.layout.dialog_tmp_store);

        //다이얼로그의 구성요소들이 동작할 코드작성

        Button button_ok = customDialog.findViewById(R.id.default_dialog_ok_btn);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"이어서 작성하기 버튼 클릭");
                startActivityC(WriteMarketPostActivity.class);
                customDialog.dismiss();

            }
        });
        Button button_cancel = customDialog.findViewById(R.id.default_dialog_cancel_btn);
        button_cancel.setOnClickListener(clickCancel);
        customDialog.show();
    }

    //취소버튼을 눌렀을때 일반적인 클릭리스너
    View.OnClickListener clickCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"새로 작성하기 버튼 클릭");


            SharedPreferences pref = getContext().getSharedPreferences("중고거래", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            Log.d(TAG,"임시저장글 삭제");
            String 임시저장 = pref.getString("임시저장",null);
            Log.d(TAG,"임시저장: " + 임시저장);
            startActivityC(WriteMarketPostActivity.class);

            customDialog.dismiss();
        }
    };

    // 액티비티 전환 함수
    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getContext().getApplicationContext(), c);
        getContext().startActivity(intent);
        // 화면전환 애니메이션 없애기
//        getContext().overridePendingTransition(0, 0);
    }
    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getContext().getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
        // 화면전환 애니메이션 없애기
//        getContext().overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name , String sendString) {
        Intent intent = new Intent(getContext().getApplicationContext(), c);
        intent.putExtra(name, sendString);
        getContext().startActivity(intent);
        // 화면전환 애니메이션 없애기
//        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c){
        Intent intent = new Intent(getContext().getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        // 화면전환 애니메이션 없애기
//        getContext().overridePendingTransition(0, 0);
    }

}
