package novamarket.com;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.content.SharedPreferences;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import novamarket.com.Adapter.BuyListAdapter;
import novamarket.com.Adapter.SellListDoneAdapter;
import novamarket.com.DataModels.SellListDataModels;

public class BuyListActivity extends AppCompatActivity {
    String TAG = "BuyListActivity";

    BuyListAdapter adapter;
    ArrayList<SellListDataModels> dataModels = new ArrayList<>();
    String userData,userIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_list);

        //현재 사용중인 사용자 누구인지 체크
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userData = pref.getString("userData",null);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");
            Log.d(TAG,"userIdx: " + userIdx);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView_guideText_buy_list = findViewById(R.id.textView_guideText_buy_list);

        adapter = new BuyListAdapter(getApplicationContext(),dataModels);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_buy_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_buy_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("구매내역"); // 툴바 제목 설정


        //구매내역 데이터 받기
        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_market_buy.php").newBuilder();
        String url = urlBuilder.build().toString();

        // POST 파라미터 추가
         RequestBody formBody = new FormBody.Builder()
            .add("userIdx", userIdx)
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
                        if(responseData.equals("[]")){
                            textView_guideText_buy_list.setVisibility(View.VISIBLE);
                        }
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length()/2; i++){
                            dataModels.add(new SellListDataModels(jsonArray.getJSONObject(2*i+1).getString("fileName"),jsonArray.getJSONObject(2*i).getString("title"),jsonArray.getJSONObject(2*i).getString("time"),jsonArray.getJSONObject(2*i).getString("idx"),jsonArray.getJSONObject(2*i).getString("price")));
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
                                adapter.notifyDataSetChanged();
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