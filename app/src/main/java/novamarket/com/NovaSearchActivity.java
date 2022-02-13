package novamarket.com;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

public class NovaSearchActivity extends AppCompatActivity {
    String TAG = "NovaSearchActivity";

    ArrayList<NovaDataModel> filteredList = new ArrayList();
    ArrayList<NovaDataModel> dataModels = new ArrayList();
    novaRecyclerViewAdapter adapter;
    RecyclerView recyclerView_nova_search;
    EditText searchET_nova_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_search);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_nova_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        recyclerView_nova_search = findViewById(R.id.recyclerView_nova_search);
        searchET_nova_search = findViewById(R.id.searchET_nova_search);

        //okhttp 서버 연결
        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_nova_post.php").newBuilder();
        String url = urlBuilder.build().toString();

        Log.d(TAG,"url: " + url);


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
                    Log.d(TAG,"팀노바생활 게시글 가져오기 responseData: " + responseData);


                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length()/2; i++){
                            Log.d(TAG,"게시글 번호: " + jsonArray.getJSONObject(2*i).getString("idx"));

                            String subject = jsonArray.getJSONObject(2*i).getString("subject");
                            Log.d(TAG,"주제: " + subject);
                            switch(subject){
                                case  "1":
                                    subject = "같이해요";
                                    break;

                                case  "2":
                                    subject = "동네맛집";
                                    break;

                                case  "3":
                                    subject = "일상";
                                    break;

                                case  "4":
                                    subject = "팀노바소식";
                                    break;

                                case  "5":
                                    subject = "취미생활";
                                    break;

                                case  "6":
                                    subject = "건강";
                                    break;

                                case  "7":
                                    subject = "코딩";
                                    break;

                                case  "8":
                                    subject = "기타";
                                    break;

                                default:
                                    break;
                            }




                            dataModels.add(new NovaDataModel(subject,jsonArray.getJSONObject(2*i).getString("contents"),jsonArray.getJSONObject(2*i).getString("nickname"),jsonArray.getJSONObject(2*i).getString("time"),null,null,jsonArray.getJSONObject(2*i+1).getString("fileName"),jsonArray.getJSONObject(2*i).getString("idx")));
                        }
                        adapter = new novaRecyclerViewAdapter(getApplicationContext(),dataModels);

                        //UI 변경은 메인스레드에서만

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                recyclerView_nova_search.setAdapter(adapter);
                                recyclerView_nova_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        //검색
        searchET_nova_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = searchET_nova_search.getText().toString();
                searchFilter(searchText);

            }
        });
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


    public void searchFilter(String searchText) {
        filteredList.clear();

        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModels.get(i).getContents().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(dataModels.get(i));
            }
        }

        adapter.fileterList(filteredList);
    }

}