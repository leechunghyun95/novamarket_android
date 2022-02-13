package novamarket.com;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.airbnb.lottie.L;

public class SubjectSelectActivity extends AppCompatActivity {

    Button ok_btn_sub;
    RadioGroup radioGroup_sub;
    RadioButton sub1,sub2,sub3,sub4,sub5,sub6,sub7,sub8;
    int subject;
    String TAG = "SubjectSelectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_select);

        setContentView();

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_subject_select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("주제 선택"); // 툴바 제목 설정


        Intent intent = getIntent();
        subject = intent.getIntExtra("subject",0);
        Log.d(TAG,"subject: " + subject);

        switch(subject){
            case  1:
                sub1.setChecked(true);
                break;

            case 2:
                sub2.setChecked(true);
                break;
            case  3:
                sub3.setChecked(true);
                break;
            case  4:
                sub4.setChecked(true);
                break;
            case  5:
                sub5.setChecked(true);
                break;
            case  6:
                sub6.setChecked(true);
                break;
            case  7:
                sub7.setChecked(true);
                break;
            case  8:
                sub8.setChecked(true);
                break;
        }

        radioGroup_sub.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case  R.id.sub1:
                                subject = 1;
                                break;

                            case R.id.sub2:
                                subject = 2;
                                break;

                            case R.id.sub3:
                                subject = 3;
                                break;

                            case R.id.sub4:
                                subject = 4;
                                break;

                            case R.id.sub5:
                                subject = 5;
                                break;

                            case R.id.sub6:
                                subject = 6;
                                break;

                            case R.id.sub7:
                                subject = 7;
                                break;

                            case R.id.sub8:
                                subject = 8;
                                break;


                        }
                    }
                });

        ok_btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"선택 완료 버튼 클릭");
                Log.d(TAG,"선택한 주제: " + subject);
                Intent intent = new Intent();
                intent.putExtra("subject",subject);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    public void setContentView(){
        ok_btn_sub = findViewById(R.id.ok_btn_sub);
        radioGroup_sub = findViewById(R.id.radioGroup_sub);
        sub1 = findViewById(R.id.sub1);
        sub2 = findViewById(R.id.sub2);
        sub3 = findViewById(R.id.sub3);
        sub4 = findViewById(R.id.sub4);
        sub5 = findViewById(R.id.sub5);
        sub6 = findViewById(R.id.sub6);
        sub7 = findViewById(R.id.sub7);
        sub8 = findViewById(R.id.sub8);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
            // todo
                finish();

            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }

}