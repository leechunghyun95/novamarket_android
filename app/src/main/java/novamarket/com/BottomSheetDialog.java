package novamarket.com;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetDialog extends BottomSheetDialogFragment {

    // 초기변수 설정
    private View view;
    // 바텀시트 숨기기 버튼
    private Button btn_hide_bt_sheet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        LinearLayout btn_add_photo = view.findViewById(R.id.btn_add_photo_bottom_sheet_dialog);
        LinearLayout btn_add_location = view.findViewById(R.id.btn_add_location_bottom_sheet_dialog);


        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }



}
