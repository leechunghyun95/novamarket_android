package novamarket.com;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import novamarket.com.Adapter.SelectBuyer2Adapter;
import novamarket.com.DataModels.SelectBuyerDataModels;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelectBuyerActivity2 extends AppCompatActivity {
    String TAG = "SelectBuyerActivity2";
    RecyclerView recyclerView_select_buyer2;
    SelectBuyer2Adapter adapter;
    ArrayList<SelectBuyerDataModels> dataModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_buyer2);

        TextView textView_guideText_select_buyer2 = findViewById(R.id.textView_guideText_select_buyer2);
        RecyclerView recyclerView_select_buyer2 = findViewById(R.id.recyclerView_select_buyer2);

        adapter = new SelectBuyer2Adapter(getApplicationContext(),dataModels);
        recyclerView_select_buyer2.setAdapter(adapter);
        recyclerView_select_buyer2.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String postNumber = intent.getStringExtra("postNumber");
        
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_select_buyer2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("구매자 선택"); // 툴바 제목 설정

        //서버에 현재 게시글 번호 post로 전송
        //채팅방 DB에서 현재게시글번호와 일치하는 row에서 buyer값으로 USER테이블에 접근해서 사용자 번호, 사용자 닉네임, 사용자 이미지 가져오기
        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/update_market_search_buyer.php").newBuilder();
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
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++){
                            String buyerIdx = jsonArray.getJSONObject(i).getString("idx");
                            String buyerNickname = jsonArray.getJSONObject(i).getString("nickname");
                            String buyerImg = jsonArray.getJSONObject(i).getString("profile_image");

                            dataModels.add(new SelectBuyerDataModels(buyerIdx,buyerNickname,buyerImg,postNumber));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(responseData.equals("[]")){
                                    Log.d(TAG,"데이터 없다");
                                    textView_guideText_select_buyer2.setVisibility(View.VISIBLE);
                                }else {
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
            finish();

            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }

}