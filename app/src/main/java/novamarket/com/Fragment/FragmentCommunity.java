package novamarket.com.Fragment;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import novamarket.com.CustomDialog;
import novamarket.com.CustomDialog_nova;
import novamarket.com.MainActivity;
import novamarket.com.MarketDataModel;
import novamarket.com.NovaDataModel;
import novamarket.com.NovaSearchActivity;
import novamarket.com.R;
import novamarket.com.WriteCommunityPostActivity;
import novamarket.com.WriteMarketPostActivity;
import novamarket.com.marketRecyclerViewAdapter;
import novamarket.com.novaRecyclerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class FragmentCommunity extends Fragment{

    private View view;

    private String TAG = "프래그먼트";

    FloatingActionButton write_nova_btn;
    RecyclerView recyclerView_nova;

    novaRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    //데이터 모델리스트
    ArrayList<NovaDataModel> dataModels = new ArrayList();

    ArrayList<NovaDataModel> filteredList = new ArrayList();
    EditText searchET_nova;

    ImageView search_btn_com;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView");
            view = inflater.inflate(R.layout.frag_community, container, false);

            setContentView();

            // 툴바 생성
            Toolbar toolbar = view.findViewById(R.id.toolbar_fragment_com);
            ((MainActivity)getActivity()).setSupportActionBar(toolbar);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("팀노바생활"); // 툴바 제목 설정


            //검색버튼 클릭
            search_btn_com.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityC(NovaSearchActivity.class);
                }
            });
            
            
            //글쓰기 버튼 클릭
             write_nova_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //팀노바생활 게시글 임시저장 값 불러오기
                    SharedPreferences pref2 = getActivity().getSharedPreferences("팀노바생활", Activity.MODE_PRIVATE);
                    String 임시저장 = pref2.getString("임시저장",null);
                    Log.d(TAG,"임시저장: " + 임시저장);
                    if(임시저장 == null){
                        Log.d(TAG,"저장된 게시글 없음");
                        startActivityC(WriteCommunityPostActivity.class);
                    }else {
                        Log.d(TAG,"저장된 게시글 있음");
                        show_tmp_store_dialog(view);
                    }
                }


            });

        //okhttp 서버 연결
        //get방식 파라미터 추가
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
                        recyclerView = view.findViewById(R.id.recyclerView_nova);
                        adapter = new novaRecyclerViewAdapter(getContext(),dataModels);

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
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });






        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");

        dataModels.clear();

    }

    // 액티비티 전환 함수
            // 인텐트 액티비티 전환함수
            public void startActivityC(Class c) {
                Intent intent = new Intent(getActivity().getApplicationContext(), c);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                getActivity().overridePendingTransition(0, 0);
            }
            // 인텐트 화면전환 하는 함수
            // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
            public void startActivityflag(Class c) {
                Intent intent = new Intent(getActivity().getApplicationContext(), c);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                getActivity().overridePendingTransition(0, 0);
            }

            // 문자열 인텐트 전달 함수
            public void startActivityString(Class c, String name , String sendString) {
                Intent intent = new Intent(getActivity().getApplicationContext(), c);
                intent.putExtra(name, sendString);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                getActivity().overridePendingTransition(0, 0);
            }

            // 백스택 지우고 새로 만들어 전달
            public void startActivityNewTask(Class c){
                Intent intent = new Intent(getActivity().getApplicationContext(), c);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                getActivity().overridePendingTransition(0, 0);
            }

    //글쓰기 버튼 클릭할떄 임시저장 있으면 띄워는 다이얼로그
    public void show_tmp_store_dialog(View v){
        //클릭시 defaultDialog 를 띄워준다
        CustomDialog_nova.getInstance(getActivity()).showDefaultDialog();
    }

    public void setContentView(){
        write_nova_btn = view.findViewById(R.id.write_nova_btn);
        recyclerView_nova = view.findViewById(R.id.recyclerView_nova);
        search_btn_com = view.findViewById(R.id.search_btn_com);
    }


}
