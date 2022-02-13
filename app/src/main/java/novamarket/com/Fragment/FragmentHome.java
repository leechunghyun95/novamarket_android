package novamarket.com.Fragment;

import com.bumptech.glide.Glide;


import novamarket.com.InterestCategorySettingActivity;
import novamarket.com.MarketDataModel;
import novamarket.com.MarketSearchActivity;
import novamarket.com.marketRecyclerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;


import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import novamarket.com.CustomDialog;
import novamarket.com.MainActivity;
import novamarket.com.R;
import novamarket.com.WriteMarketPostActivity;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class FragmentHome extends Fragment {

    private View view;

    private String TAG = "프래그먼트";
    
    ImageView filter_btn_home,search_btn_home;//툴바 필터,검색 버튼
    FloatingActionButton write_market_btn;
    RecyclerView recyclerView_market;

    marketRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    //데이터 모델리스트
    ArrayList<MarketDataModel> dataModels = new ArrayList();

    ArrayList<MarketDataModel> filteredList = new ArrayList();

    //카테고리선택 스피너
    Spinner spinner_market;
    ArrayList<String> spinnerList;
    ArrayAdapter<String> arrayAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView");
            view = inflater.inflate(R.layout.frag_home, container, false);
            
            setContentView();

            // 툴바 생성
            Toolbar toolbar = view.findViewById(R.id.toolbar_fragment_home);
            ((MainActivity)getActivity()).setSupportActionBar(toolbar);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("중고거래"); // 툴바 제목 설정


        //카테고리 선택 스피너
        spinnerList = new ArrayList<>();
        spinnerList.add("전체");
        spinnerList.add("디지털/가전");
        spinnerList.add("가구/인테리어");
        spinnerList.add("유아용/유아도서");
        spinnerList.add("생활/가공식품");
        spinnerList.add("스포츠/레저");
        spinnerList.add("여성잡화");
        spinnerList.add("여성의류");
        spinnerList.add("남성패션/잡화");
        spinnerList.add("게임/취미");
        spinnerList.add("뷰티/미용");
        spinnerList.add("반려동물용품");
        spinnerList.add("도서/티켓/음반");
        spinnerList.add("식물");
        spinnerList.add("기타 중고물품");
        spinnerList.add("삽니다");
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerList);

//        spinner_market.setAdapter(arrayAdapter);
//
//        spinner_market.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG,position+"번이 선택되었습니다.");
//
//                //서버에 카테고리 맞는 게시글 요청
//                // get방식 파라미터 추가
//                HttpUrl.Builder urlBuilder = HttpUrl.parse("your_server_url").newBuilder();
//                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
//                        String url = urlBuilder.build().toString();
//
//                        // POST 파라미터 추가
//                        RequestBody formBody = new FormBody.Builder()
//                        .add("username", "test")
//                        .add("password", "test")
//                        .build();
//
//                        // 요청 만들기
//                        OkHttpClient client = new OkHttpClient();
//                        Request request = new Request.Builder()
//                        .url(url)
//                        .post(formBody)
//                        .build();
//
//                // 응답 콜백
//                        client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                        }
//
//                @Override
//                public void onResponse(Call call, final Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                        // 응답 실패
//                        Log.i("tag", "응답실패");
//                        } else {
//                        // 응답 성공
//                        Log.i("tag", "응답 성공");
//                final String responseData = response.body().string();
//                        // 서브 스레드 Ui 변경 할 경우 에러
//                        // 메인스레드 Ui 설정
//                        runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                        try {
//                        } catch (Exception e) {
//                        e.printStackTrace();
//                        }
//                        }
//                        });
//                        }
//                        }
//                        });
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        //필터버튼 / 관심 카테고리 설정
//        filter_btn_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(getContext(),"필터버튼 클릭",Toast.LENGTH_SHORT).show();
//                startActivityC(InterestCategorySettingActivity.class);
//            }
//        });

        //검색버튼
        search_btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityString(MarketSearchActivity.class,"category","");
            }
        });


         write_market_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"글쓰기 버튼 클릭");

                //중고거래 게시글 임시저장 값 불러오기
                SharedPreferences pref2 = getActivity().getSharedPreferences("중고거래", Activity.MODE_PRIVATE);
                String 임시저장 = pref2.getString("임시저장",null);
                Log.d(TAG,"임시저장: " + 임시저장);
                if(임시저장 == null){
                    Log.d(TAG,"저장된 게시글 없음");
                    startActivityC(WriteMarketPostActivity.class);
                }else {
                    Log.d(TAG,"저장된 게시글 있음");
                    show_tmp_store_dialog(view);
                }


            }
        });





//            //okhttp 서버 연결
//             // get방식 파라미터 추가
             HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_market_post.php").newBuilder();
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
                             recyclerView = view.findViewById(R.id.recyclerView_market);
                             adapter = new marketRecyclerViewAdapter(getContext(),dataModels);

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



//                         //데이터 모델리스트
//                         ArrayList<MarketDataModel> dataModels = new ArrayList();
//
//
//
//
//                         recyclerView = view.findViewById(R.id.recyclerView_market);
//                         adapter = new marketRecyclerViewAdapter(getContext(),dataModels);
//                         recyclerView.setAdapter(adapter);
//                         recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));






        return view;
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


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");

        dataModels.clear();

    }


            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                    switch (item.getItemId()){
                    case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                    // todo

                    return true;
                    }
                    }
                    return super.onOptionsItemSelected(item);
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
                CustomDialog.getInstance(getActivity()).showDefaultDialog();
            }



            public void setContentView(){
                filter_btn_home = view.findViewById(R.id.filter_btn_home);
                search_btn_home = view.findViewById(R.id.search_btn_home);
                write_market_btn = view.findViewById(R.id.write_market_btn);
                recyclerView_market = view.findViewById(R.id.recyclerView_market);

//                spinner_market = view.findViewById(R.id.spinner_market);

            }
}


