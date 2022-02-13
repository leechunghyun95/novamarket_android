package novamarket.com;



import com.bumptech.glide.Glide;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.acl.Group;
import java.util.ArrayList;

public class MarketActivity extends AppCompatActivity {
    String TAG = "MarketActivity";
    public static RecyclerView recyclerView_market_img;
    TextView textView_seller_nickname,textView_productName_market,textView_category_market,textView_uploadTime_market,textView_contents_market,textView_productPrice_market,textView_isNego,textView_done_market;
    ImageView imageView_seller_img,imageView_likeBtn_market;
    Button button_chat_market;

    String idx;

    String userData;

    String userIdx;
    
    Menu mMenu;

    String seller_nickName;
    String seller;//판매자
    String seller_img;//판매자 이미지

    int like = 0;//좋아요 기본값 0

    public static Context market_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        market_context = this;
        
        //현재 사용중인 사용자 누구인지 체크
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userData = pref.getString("userData",null);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContentView();



        Log.d(TAG,"onCreate");

        Intent intent = getIntent();

        idx = intent.getStringExtra("idx");

        Log.d(TAG,"게시글 번호: " + idx);
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_market);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("중고거래 게시글");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        imageView_likeBtn_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"좋아요 버튼 클릭");
                //이미 좋아요 상태일때
                if(like == 1) {
                    Log.d(TAG,"좋아요 취소");
                    imageView_likeBtn_market.setImageResource(R.drawable.heart_blank);
                    like = 0;
                } else {//아무 상태 아닐때
                    Log.d(TAG,"좋아요 등록");
                    imageView_likeBtn_market.setImageResource(R.drawable.heart);
                    Toast.makeText(MarketActivity.this,"관심 목록에 추가되었어요.",Toast.LENGTH_SHORT).show();
                    like = 1;
                }
                
                //좋아요 db에 저장
                // get방식 파라미터 추가
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/market_like.php").newBuilder();
                        urlBuilder.addQueryParameter("postNumber", idx); // 예시
                        String url = urlBuilder.build().toString();
                
                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                        .add("userNumber", userIdx)
                        .add("like", String.valueOf(like))
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
                Log.d(TAG,"responseData " + responseData);
                        // 서브 스레드 Ui 변경 할 경우 에러
                        // 메인스레드 Ui 설정
                        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        try {
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

        //인터넷 체크
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    //게시글 정보 가져오기
                    // get방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_market_detail.php").newBuilder();
                    urlBuilder.addQueryParameter("postNumber", idx); // 게시글 번호 GET방식 REQUEST
                    String url = urlBuilder.build().toString();

                    // POST 파라미터 추가
                    RequestBody formBody = new FormBody.Builder()
                            .add("userNumber",userIdx)
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
                                //서버에서 받은 responseData를 json배열형태로 바꿔서 하나씩 저장
                                try {
                                    JSONArray jsonArray = new JSONArray(responseData);
                                    seller_nickName = jsonArray.getJSONObject(0).getString("nickname");
                                    textView_seller_nickname.setText(jsonArray.getJSONObject(0).getString("nickname"));
                                    textView_productName_market.setText(jsonArray.getJSONObject(0).getString("title"));
                                    textView_category_market.setText(jsonArray.getJSONObject(0).getString("category"));
                                    textView_uploadTime_market.setText(jsonArray.getJSONObject(0).getString("time"));
                                    textView_contents_market.setText(jsonArray.getJSONObject(0).getString("contents"));
                                    if(jsonArray.getJSONObject(0).getString("price").isEmpty()){
                                        Log.d(TAG,"가격없다");
                                        textView_productPrice_market.setText("가격없음");
                                    }else {
                                        Log.d(TAG,"가격있다");
                                        textView_productPrice_market.setText(jsonArray.getJSONObject(0).getString("price")+"원");
                                    }

                                    if(jsonArray.getJSONObject(0).getString("isSale").equals("1")){
                                        Log.d(TAG,"판매중");
                                    }else {
                                        Log.d(TAG,"거래 완료");
                                        textView_done_market.setVisibility(View.VISIBLE);
                                        button_chat_market.setVisibility(View.GONE);
                                    }
                                    
                                    //판매자 이미지 저장
                                    seller_img = jsonArray.getJSONObject(0).getString("profile_image");
                                    
                                    Glide.with(getApplicationContext())
                                            .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/"+jsonArray.getJSONObject(0).getString("profile_image"))
                                            .circleCrop()
                                            .into(imageView_seller_img);


                                    //흥정여부
                                    if(jsonArray.getJSONObject(0).getString("isNego").equals("1")){//흥정가능할때
                                        textView_isNego.setText("가격제안불가");
                                    }else {//흥정불가
                                        textView_isNego.setText("가격제안가능");
                                    }


                                    ArrayList<String> imgList = new ArrayList<>();
                                    //배열에서 이미지 정보 가져오기
                                    for (int i = 2; i < jsonArray.length(); i++){
                                        imgList.add(jsonArray.getJSONObject(i).getString("fileName"));
                                    }
                                    marketImageRecyclerViewAdapter adapter;


                                    adapter = new marketImageRecyclerViewAdapter(getApplicationContext(),imgList);
                                    recyclerView_market_img.setAdapter(adapter);
                                    recyclerView_market_img.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));

                                    switch(jsonArray.getJSONObject(0).getString("category")){
                                      case  "1":
                                          textView_category_market.setText("디지털/가전");
                                        break;

                                        case  "2":
                                            textView_category_market.setText("가구/인테리어");
                                            break;

                                        case  "3":
                                            textView_category_market.setText("유아용/유아도서");
                                            break;

                                        case  "4":
                                            textView_category_market.setText("생활/가공식품");
                                            break;

                                        case  "5":

                                            textView_category_market.setText("스포츠/레저");
                                            break;

                                        case  "6":

                                            textView_category_market.setText("여성잡화");
                                            break;

                                        case  "7":
                                            textView_category_market.setText("여성의류");
                                            break;

                                        case  "8":
                                            textView_category_market.setText("남성패션/잡화");
                                            break;

                                        case  "9":
                                            textView_category_market.setText("게임/취미");
                                            break;

                                        case  "10":
                                            textView_category_market.setText("뷰티/미용");
                                            break;

                                        case  "11":
                                            textView_category_market.setText("반려동물용품");
                                            break;

                                        case  "12":
                                            textView_category_market.setText("도서/티켓/음반");
                                            break;

                                        case  "13":
                                            textView_category_market.setText("식물");
                                            break;

                                        case  "14":
                                            textView_category_market.setText("기타 중고물품");
                                            break;

                                        case  "15":

                                            textView_category_market.setText("삽니다");
                                            break;
                                      default:
                                        break;
                                    }

                                    Log.d(TAG,"작성자 번호: " + jsonArray.getJSONObject(0).getString("seller"));
                                    seller = jsonArray.getJSONObject(0).getString("seller");//판매자 번호
                                    Log.d(TAG,"사용자 번호: " + userIdx);
                                    //내가 작성한 글이면 수정 , 삭제 옵션 메뉴 보이게
                                    if(jsonArray.getJSONObject(0).getString("seller").equals(userIdx)) {
                                        MenuItem menu_mod = mMenu.findItem(R.id.menu_mod_market);
                                        MenuItem menu_delete = mMenu.findItem(R.id.menu_delete_market);
                                        MenuItem menu_report = mMenu.findItem(R.id.menu_report);

                                        menu_mod.setVisible(true);
                                        menu_delete.setVisible(true);
                                        menu_report.setVisible(false);

                                        button_chat_market.setText("채팅 목록 보기");
                                    }else {//내가 작성한 글이 아니면
                                        MenuItem menu_mod = mMenu.findItem(R.id.menu_mod_market);
                                        MenuItem menu_delete = mMenu.findItem(R.id.menu_delete_market);
                                        MenuItem menu_report = mMenu.findItem(R.id.menu_report);

                                        menu_mod.setVisible(false);
                                        menu_delete.setVisible(false);
                                        menu_report.setVisible(true);
                                    }

                                    //좋아요
                                    if(jsonArray.getJSONObject(1).getString("idx").equals("0")){
                                        Log.d(TAG,"좋아요 안한 게시글");
                                        imageView_likeBtn_market.setImageResource(R.drawable.heart_blank);
                                        like = 0;
                                    }else {
                                        Log.d(TAG,"좋아요한 게시글");
                                        imageView_likeBtn_market.setImageResource(R.drawable.heart);
                                        like = 1;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                            e.printStackTrace();
                            }
                            }
                            });
                            }
                            }
                            });

                }else {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

                //채팅으로 거래하기 버튼
                button_chat_market.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"채팅으로 거래하기 버튼 클릭");

                        

                        Intent intent = new Intent(getApplicationContext(), ChatMarketActivity.class);
                        intent.putExtra("postNumber", idx);
                        intent.putExtra("chatRoom",idx+"M"+userIdx);
//                        intent.putExtra("buyer", userIdx);
//                        intent.putExtra("chatType","market");
//                        intent.putExtra("opponent",seller_nickName);
//                        intent.putExtra("opponentImg",seller_img);
                        startActivity(intent);

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.market_menu,menu);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
            // todo
                finish();
            return true;
            }
                case R.id.menu_mod_market:
                    Log.d(TAG,"수정하기 버튼 클릭");
                    startActivityString(UpdateMarketActivity.class,"postNumber",idx);
                    return true;

                case R.id.menu_delete_market:
                    Log.d(TAG,"삭제하기 버튼 클릭");
                    show_delete_dialog();


                    return true;
                    
                case R.id.menu_report:
                    Log.d(TAG,"신고하기 버튼 클릭");
                    return true;
            }
            return super.onOptionsItemSelected(item);
            }


            public void setContentView(){
                textView_category_market = findViewById(R.id.textView_category_market);
                textView_contents_market = findViewById(R.id.textView_contents_market);
                textView_productName_market = findViewById(R.id.textView_productName_market);
                textView_seller_nickname = findViewById(R.id.textView_seller_nickname);
                textView_uploadTime_market = findViewById(R.id.textView_uploadTime_market);
                textView_isNego = findViewById(R.id.textView_isNego);
                textView_productPrice_market = findViewById(R.id.textView_productPrice_market);

                imageView_likeBtn_market = findViewById(R.id.imageView_likeBtn_market);
                imageView_seller_img = findViewById(R.id.imageView_seller_img);

                recyclerView_market_img = findViewById(R.id. recyclerView_market_img);

                button_chat_market = findViewById(R.id.button_chat_market);
                textView_done_market = findViewById(R.id.textView_done_market);
            }

    public void show_delete_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("정말 삭제하시겠어요?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getApplicationContext(),"예를 선택했습니다.", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"예를 선택했습니다.");

                        //게시글 테이블 삭제 request
                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/delete_market.php").newBuilder();
                                urlBuilder.addQueryParameter("postNumber", idx); // 예시
                                String url = urlBuilder.build().toString();

                                // POST 파라미터 추가
                                RequestBody formBody = new FormBody.Builder()
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


                                // 서브 스레드 Ui 변경 할 경우 에러
                                // 메인스레드 Ui 설정
                                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                    if(responseData.equals("1")){
                                        Toast.makeText(getApplicationContext(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                                        startActivityC(MainActivity.class);
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
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"아니오를 선택했습니다.");
                    }
                });
        builder.show();
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

}