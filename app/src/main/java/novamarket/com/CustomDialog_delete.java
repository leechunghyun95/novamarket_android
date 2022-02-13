package novamarket.com;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

/*
직접 커스텀한 다이얼로그들을 띄워주고 다이얼로그 안에서의 동작을 정의하는 클래스 (싱글톤)
 */
    public class CustomDialog_delete extends Dialog {

        private static CustomDialog_delete customDialog;

        private CustomDialog_delete(@NonNull Context context) {
            super(context);
        }

        public static CustomDialog_delete getInstance(Context context) {
                customDialog = new CustomDialog_delete(context);

            return customDialog;
        }

        //다이얼로그 생성하기
        public void showDefaultDialog() {
            //참조할 다이얼로그 화면을 연결한다.
            customDialog.setContentView(R.layout.dialog_default);

            //다이얼로그의 구성요소들이 동작할 코드작성
            Button button_delete_dialog = customDialog.findViewById(R.id.button_delete_dialog);
            button_delete_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            customDialog.show();
        }

        //취소버튼을 눌렀을때 일반적인 클릭리스너
        View.OnClickListener clickCancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        };

    }

