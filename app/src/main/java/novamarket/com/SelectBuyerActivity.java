package novamarket.com;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bumptech.glide.Glide;

import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;

public class SelectBuyerActivity extends AppCompatActivity {
    String TAG = "SelectBuyerActivity";
    String postNumber,title,img;
    TextView textView_select_buyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_buyer);

        Intent intent = getIntent();

        postNumber = intent.getStringExtra("idx");
        title = intent.getStringExtra("title");
        img = intent.getStringExtra("img");

        ImageView imageView_postImg_select_buyer = findViewById(R.id.imageView_postImg_select_buyer);
        TextView textView_title_select_buyer = findViewById(R.id.textView_title_select_buyer);
        textView_select_buyer = findViewById(R.id.textView_select_buyer);
        textView_select_buyer.setPaintFlags(textView_select_buyer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        textView_title_select_buyer.setText(title);
        Glide.with(this)
                .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/market_post/"+img)
                .into(imageView_postImg_select_buyer);


        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_select_buyer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("구매자 선택"); // 툴바 제목 설정

        LottieAnimationView animationView = findViewById(R.id.lottie_check_select_buyer);
        animationView.setAnimation("check.json");
        animationView.loop(false);
        animationView.playAnimation();

        //채팅목록에서 구매자 찾기 버튼
        textView_select_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityString(SelectBuyerActivity2.class,"postNumber",postNumber);

            }
        });


        //지금 안할래요 버튼
        Button button_noSelect_select_buyer = findViewById(R.id.button_noSelect_select_buyer);
        button_noSelect_select_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"지금안할래요 버튼 클릭");
                //서버로 현재 게시글 번호 보내기
                //서버에서 현재 게시글 번호 참조해서 isSale 칼럼 false(0)으로 UPDATE
                //클라이언트에서 구매자 정보 넘기고/ 안넘기고에 따라서 서버에서 분기 처리
                //지금 안할래요 버튼클릭하면 구매자 정보 안넘김

                // get방식 파라미터 추가
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/update_market_select_buyer.php").newBuilder();
                String url = urlBuilder.build().toString();

                // POST 파라미터 추가
                RequestBody formBody = new FormBody.Builder()
                    .add("postNumber", postNumber)
                    .build();

                // 요청 만들기
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

                // 응답 콜백
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                        // 응답 실패
                        Log.i("tag", "응답실패");
                        } else {
                            // 응답 성공
                            Log.i("tag", "응답 성공");
                            final String responseData = response.body().string();
                            Log.d(TAG,"responseData: " + responseData);
                            // 서브 스레드 Ui 변경 할 경우 에러
                            // 메인스레드 Ui 설정
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

    }

    // 액티비티 전환 함수
    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }
    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name , String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c){
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
            finish();
            Toast.makeText(this,"거래 완료가 취소되었습니다.",Toast.LENGTH_SHORT).show();
            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }
}