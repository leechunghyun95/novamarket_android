package novamarket.com;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class MarketSearchActivity extends AppCompatActivity {
    String TAG = "MarketSearchActivity";

    ArrayList<MarketDataModel> filteredList = new ArrayList();
    ArrayList<MarketDataModel> dataModels = new ArrayList();
    marketRecyclerViewAdapter adapter;
    RecyclerView recyclerView_market_search;
    EditText searchET_market_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_search);


        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_market_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        recyclerView_market_search = findViewById(R.id.recyclerView_market_search);
        searchET_market_search = findViewById(R.id.searchET_market_search);


        //okhttp 서버 연결
        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_market_post.php").newBuilder();
        String url = urlBuilder.build().toString();



        // 요청 만들기
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
                    Log.d(TAG,"2마켓게시글 가져오기 responseData: " + responseData);


                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length()/2; i++){
                            Log.d(TAG,"게시글 번호: " + jsonArray.getJSONObject(2*i).getString("idx"));


                            String price = jsonArray.getJSONObject(2*i).getString("price");

                            Log.d(TAG,"가격: " + price);

                            if(price.length() > 0){
                                Log.d(TAG,"가격 있다: " + price);
                                price = jsonArray.getJSONObject(2*i).getString("price")+" 원";


                            }else {
                                Log.d(TAG,"가격 없다: " + price);
                                price = "";
                            }

                            Log.d(TAG,"대표 이미지 파일명: " + jsonArray.getJSONObject(2*i+1).getString("fileName"));

                            dataModels.add(new MarketDataModel(jsonArray.getJSONObject(2*i+1).getString("fileName"),jsonArray.getJSONObject(2*i).getString("title"),jsonArray.getJSONObject(2*i).getString("time"),price,jsonArray.getJSONObject(2*i).getString("idx"))); }
                        recyclerView_market_search = findViewById(R.id.recyclerView_market_search);

                        //UI 변경은 메인스레드에서만!
//                             recyclerView.setAdapter(adapter);
//                             recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter = new marketRecyclerViewAdapter(getApplicationContext(),dataModels);
                                recyclerView_market_search.setAdapter(adapter);
                                recyclerView_market_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        //검색
        searchET_market_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = searchET_market_search.getText().toString();
                searchFilter(searchText);

            }
        });
    }

    //툴바 옵션
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


    public void searchFilter(String searchText) {
        filteredList.clear();

        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModels.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(dataModels.get(i));
            }
        }

        adapter.fileterList(filteredList);
    }
}