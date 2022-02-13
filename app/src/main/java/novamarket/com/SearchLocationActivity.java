package novamarket.com;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SearchLocationActivity extends AppCompatActivity {
    String TAG = "SearchLocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        Log.d(TAG,"작업새1");

        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://dapi.kakao.com/v2/local/search/address.jsonquery").newBuilder();
                urlBuilder.addQueryParameter("query", "전북 삼성동 100"); // 예시
                String url = urlBuilder.build().toString();


                // 요청 만들기
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","KakaoAK 1d8442210110fed0098391001ffb24d1")
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
                // 서브 스레드 Ui 변경 할 경우 에러
                // 메인스레드 Ui 설정
                runOnUiThread(new Runnable() {
        @Override
        public void run() {
                try {
                Toast.makeText(getApplicationContext(), "응답" + responseData, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                e.printStackTrace();
                }
                }
                });
                }
                }
                });



    }
}