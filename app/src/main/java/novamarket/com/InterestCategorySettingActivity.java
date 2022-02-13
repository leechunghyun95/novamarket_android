package novamarket.com;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class InterestCategorySettingActivity extends AppCompatActivity {
    CheckBox checkBox_cat0,checkBox_cat1,checkBox_cat2,checkBox_cat3,checkBox_cat4,checkBox_cat5,checkBox_cat6,checkBox_cat7,checkBox_cat8,checkBox_cat9,checkBox_cat10,checkBox_cat11,checkBox_cat12,checkBox_cat13,checkBox_cat14,checkBox_cat15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_category_setting);
        
        setContentView();//화면 초기화
        
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_interest_category_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("관심 카테고리 설정"); // 툴바 제목 설정

        checkBox_cat0.setChecked(true);
        checkBox_cat1.setChecked(true);
        checkBox_cat2.setChecked(true);
        checkBox_cat3.setChecked(true);
        checkBox_cat4.setChecked(true);
        checkBox_cat5.setChecked(true);
        checkBox_cat6.setChecked(true);
        checkBox_cat7.setChecked(true);
        checkBox_cat8.setChecked(true);
        checkBox_cat9.setChecked(true);
        checkBox_cat10.setChecked(true);
        checkBox_cat11.setChecked(true);
        checkBox_cat12.setChecked(true);
        checkBox_cat13.setChecked(true);
        checkBox_cat14.setChecked(true);
        checkBox_cat15.setChecked(true);

        //전체 선택
        checkBox_cat0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat1.setChecked(true);
                    checkBox_cat2.setChecked(true);
                    checkBox_cat3.setChecked(true);
                    checkBox_cat4.setChecked(true);
                    checkBox_cat5.setChecked(true);
                    checkBox_cat6.setChecked(true);
                    checkBox_cat7.setChecked(true);
                    checkBox_cat8.setChecked(true);
                    checkBox_cat9.setChecked(true);
                    checkBox_cat10.setChecked(true);
                    checkBox_cat11.setChecked(true);
                    checkBox_cat12.setChecked(true);
                    checkBox_cat13.setChecked(true);
                    checkBox_cat14.setChecked(true);
                    checkBox_cat15.setChecked(true);
                }else {
                    checkBox_cat1.setChecked(false);
                    checkBox_cat2.setChecked(false);
                    checkBox_cat3.setChecked(false);
                    checkBox_cat4.setChecked(false);
                    checkBox_cat5.setChecked(false);
                    checkBox_cat6.setChecked(false);
                    checkBox_cat7.setChecked(false);
                    checkBox_cat8.setChecked(false);
                    checkBox_cat9.setChecked(false);
                    checkBox_cat10.setChecked(false);
                    checkBox_cat11.setChecked(false);
                    checkBox_cat12.setChecked(false);
                    checkBox_cat13.setChecked(false);
                    checkBox_cat14.setChecked(false);
                    checkBox_cat15.setChecked(false);
                }
            }
        });

        //1번 카테고리 선택
        checkBox_cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //2번 카테고리 선택
        checkBox_cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //3번 카테고리 선택
        checkBox_cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //4번 카테고리 선택
        checkBox_cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //5번 카테고리 선택
        checkBox_cat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //6번 카테고리 선택
        checkBox_cat6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //7번 카테고리 선택
        checkBox_cat7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //8번 카테고리 선택
        checkBox_cat8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //9번 카테고리 선택
        checkBox_cat9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //10번 카테고리 선택
        checkBox_cat10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //11번 카테고리 선택
        checkBox_cat11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //12번 카테고리 선택
        checkBox_cat12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //13번 카테고리 선택
        checkBox_cat13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //14번 카테고리 선택
        checkBox_cat14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });

        //15번 카테고리 선택
        checkBox_cat15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox_cat0.isChecked()){
                    checkBox_cat0.setChecked(false);
                }else if(checkBox_cat1.isChecked()&&checkBox_cat2.isChecked()&&checkBox_cat3.isChecked()&&checkBox_cat4.isChecked()&&checkBox_cat5.isChecked()&&checkBox_cat6.isChecked()&&checkBox_cat7.isChecked()&&checkBox_cat8.isChecked()&&checkBox_cat9.isChecked()&&checkBox_cat10.isChecked()&&checkBox_cat11.isChecked()&&checkBox_cat12.isChecked()&&checkBox_cat13.isChecked()&&checkBox_cat14.isChecked()&&checkBox_cat15.isChecked())
                {
                    checkBox_cat0.setChecked(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
            // todo

            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }
            
            public void setContentView(){
                checkBox_cat0 = findViewById(R.id.checkBox_cat0);
                checkBox_cat1 = findViewById(R.id.checkBox_cat1);
                checkBox_cat2 = findViewById(R.id.checkBox_cat2);
                checkBox_cat3 = findViewById(R.id.checkBox_cat3);
                checkBox_cat4 = findViewById(R.id.checkBox_cat4);
                checkBox_cat5 = findViewById(R.id.checkBox_cat5);
                checkBox_cat6 = findViewById(R.id.checkBox_cat6);
                checkBox_cat7 = findViewById(R.id.checkBox_cat7);
                checkBox_cat8 = findViewById(R.id.checkBox_cat8);
                checkBox_cat9 = findViewById(R.id.checkBox_cat9);
                checkBox_cat10 = findViewById(R.id.checkBox_cat10);
                checkBox_cat11 = findViewById(R.id.checkBox_cat11);
                checkBox_cat12 = findViewById(R.id.checkBox_cat12);
                checkBox_cat13 = findViewById(R.id.checkBox_cat13);
                checkBox_cat14 = findViewById(R.id.checkBox_cat14);
                checkBox_cat15 = findViewById(R.id.checkBox_cat15);
            }


            public void saveInterestCategory(){//관심 카테고리 저장메서드드
                if(checkBox_cat0.isChecked()){//전체 카테고리 선택 되어있으면.
                    SharedPreferences pref = getSharedPreferences("category", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor= pref.edit();
                    editor.putString("category","all");
                    editor.apply();
                }else{

                }
            }

}