package novamarket.com;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CategorySelectActivity extends AppCompatActivity {
    Button ok_btn_cat;
    RadioGroup radioGroup;
    RadioButton cat1,cat2,cat3,cat4,cat5,cat6,cat7,cat8,cat9,cat10,cat11,cat12,cat13,cat14,cat15;
    int category;
    String TAG = "CategorySelectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_select);

        setContentView();//화면 초기화
        
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_category_select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("카테고리 선택"); // 툴바 제목 설정

        Intent intent = getIntent();
        category = intent.getIntExtra("category",0);
        Log.d(TAG,"category: " + category);

        //원래 선택되어있던거 보여주기위함
        switch(category){
          case  1:
              cat1.setChecked(true);
            break;

            case 2:
                cat2.setChecked(true);
                break;
            case  3:
                cat3.setChecked(true);
                break;
            case  4:
                cat4.setChecked(true);
                break;
            case  5:
                cat5.setChecked(true);
                break;
            case  6:
                cat6.setChecked(true);
                break;
            case  7:
                cat7.setChecked(true);
                break;
            case  8:
                cat8.setChecked(true);
                break;
            case  9:
                cat9.setChecked(true);
                break;
            case  10:
                cat10.setChecked(true);
                break;
            case  11:
                cat11.setChecked(true);
                break;
            case  12:
                cat12.setChecked(true);
                break;
            case  13:
                cat13.setChecked(true);
                break;
            case  14:
                cat14.setChecked(true);
                break;
            case  15:
                cat15.setChecked(true);
                break;

        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case  R.id.cat1:
                                category = 1;
                                break;

                            case R.id.cat2:
                                category = 2;
                                break;

                            case R.id.cat3:
                                category = 3;
                                break;

                            case R.id.cat4:
                                category = 4;
                                break;

                            case R.id.cat5:
                                category = 5;
                                break;

                            case R.id.cat6:
                                category = 6;
                                break;

                            case R.id.cat7:
                                category = 7;
                                break;

                            case R.id.cat8:
                                category = 8;
                                break;

                            case R.id.cat9:
                                category = 9;
                                break;

                            case R.id.cat10:
                                category = 11;
                                break;

                            case R.id.cat12:
                                category = 12;
                                break;

                            case R.id.cat13:
                                category = 13;
                                break;

                            case R.id.cat14:
                                category = 14;
                                break;

                            case R.id.cat15:
                                category = 15;
                                break;
                        }
                    }
                });

        ok_btn_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"선택 완료 버튼 클릭");
                Intent intent = new Intent();
                intent.putExtra("category",category);
                setResult(RESULT_OK,intent);
                finish();
            }
        });


    }
    
    public void setContentView(){
        ok_btn_cat = findViewById(R.id.ok_btn_cat);
        radioGroup = findViewById(R.id.radioGroup);
        cat1 = findViewById(R.id.cat1);
        cat2 = findViewById(R.id.cat2);
        cat3 = findViewById(R.id.cat3);
        cat4 = findViewById(R.id.cat4);
        cat5 = findViewById(R.id.cat5);
        cat6 = findViewById(R.id.cat6);
        cat7 = findViewById(R.id.cat7);
        cat8 = findViewById(R.id.cat8);
        cat9 = findViewById(R.id.cat9);
        cat10 = findViewById(R.id.cat10);
        cat11 = findViewById(R.id.cat11);
        cat12 = findViewById(R.id.cat12);
        cat13 = findViewById(R.id.cat13);
        cat14 = findViewById(R.id.cat14);
        cat15 = findViewById(R.id.cat15);

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