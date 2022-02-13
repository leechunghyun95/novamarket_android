package novamarket.com.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import novamarket.com.Adapter.LikeListMarketAdapter;
import novamarket.com.Adapter.LikeListNovaAdapter;
import novamarket.com.DataModels.SellListDataModels;
import novamarket.com.NovaDataModel;
import novamarket.com.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class FragmentLikeList_nova extends Fragment {
    String TAG = "FragmentLikeList_nova";
    private View view;
    String userIdx;

    ArrayList<NovaDataModel> dataModels = new ArrayList();
    RecyclerView recyclerview;
    LikeListNovaAdapter adapter;
    TextView textView_guideText_likeList_nova;





    public static FragmentLikeList_nova newInstance(){
        FragmentLikeList_nova fragmentLikeList_nova = new FragmentLikeList_nova();

        return fragmentLikeList_nova;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.frag_like_list_nova, container, false);

        Bundle bundle = getArguments();
        userIdx = bundle.getString("userIdx");

        adapter = new LikeListNovaAdapter(getContext(),dataModels);
        recyclerview = view.findViewById(R.id.recyclerview_likeList_nova);
        textView_guideText_likeList_nova = view.findViewById(R.id.textView_guideText_likeList_nova);

        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        //내가 판매중인 상품 가져오기
        //get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_nova_like.php").newBuilder();
        String url = urlBuilder.build().toString();

        //사용자 번호 post로 서버에 보내서 seller가 사용자 번호와 일치하는 데이터 가져오기
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

                        //판매중인 상품 제이슨 리스트로 가져오기
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
                                    textView_guideText_likeList_nova.setVisibility(View.VISIBLE);
                                }
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
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        dataModels.removeAll(dataModels);
    }
}
