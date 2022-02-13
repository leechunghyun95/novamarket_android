package novamarket.com;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import android.app.Dialog;

/*
직접 커스텀한 다이얼로그들을 띄워주고 다이얼로그 안에서의 동작을 정의하는 클래스 (싱글톤)
 */
public class CustomDialog_reply extends Dialog {

    private static CustomDialog_reply customDialog;

    private CustomDialog_reply(@NonNull Context context) {
        super(context);
    }

    public static CustomDialog_reply getInstance(Context context) {
            customDialog = new CustomDialog_reply(context);

        return customDialog;
    }

    //다이얼로그 생성하기
    public void showDefaultDialog() {
        //참조할 다이얼로그 화면을 연결한다.
        customDialog.setContentView(R.layout.dialog_default);

        //다이얼로그의 구성요소들이 동작할 코드작성

        Button button_ok = customDialog.findViewById(R.id.button_delete_dialog);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.dialog_default_txtview);
                textView.setText("삭제 버튼을 눌렀습니다. :)");
            }
        });
        Button button_cancel = customDialog.findViewById(R.id.button_cancle_dialog);
        button_cancel.setOnClickListener(clickCancel);
        customDialog.show();
    }

    //취소버튼을 눌렀을때 일반적인 클릭리스너
    View.OnClickListener clickCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "취소 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
            customDialog.dismiss();
        }
    };

}
