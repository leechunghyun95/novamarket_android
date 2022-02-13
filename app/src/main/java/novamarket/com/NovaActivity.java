package novamarket.com;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import novamarket.com.Adapter.ReplyAdapter;
import novamarket.com.DataModels.ReplyDataModels;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NovaActivity extends AppCompatActivity {
    Menu mMenu;
    String userData;
    public static String userIdx;
    String postNumber;
    String nickname;
    String TAG = "NovaActivity";
    TextView textView_subjcet_nova,textView_nickname_nova,textView_time_nova,textView_contents_nova,textView_likes_nova,textView_reply_count_nova,textView_noReply;
    ImageView imageView_writer_img_nova,imageView_reply_btn_nova,imageView_likeBtn_nova;
    RecyclerView recyclerView_img_nova,recyclerView_reply_nova;
    Button button_chat_nova;
    boolean myPost;
    EditText editText_reply_nova;
    public static String userNickname;
    String userProfileImage;


    int like = 0;//좋아요 기본값 0

    //데이터 모델리스트
    ArrayList<ReplyDataModels> dataModels = new ArrayList();

    ReplyAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova);

        setContentView();

        //현재 사용중인 사용자 누구인지 체크
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userData = pref.getString("userData",null);
        Log.d(TAG,"현재사용자: " + userData);
        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");
            userNickname = jsonObject.getString("nickname");
            userProfileImage = jsonObject.getString("profile_image");
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //게시글 번호 받아오기
        Intent intent = getIntent();
        postNumber = intent.getStringExtra("postNumber");
        Log.d(TAG,"게시글 번호: " + postNumber);



        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_nova);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("팀노바생활 게시글"); // 툴바 제목 설정

        //좋아요 버튼
        imageView_likeBtn_nova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"좋아요 버튼 클릭");
                if(like == 1) {//이미 좋아요 상태일때
                    Log.d(TAG,"좋아요 취소");
                    imageView_likeBtn_nova.setImageResource(R.drawable.heart_blank);
                    like = 0;
                } else {//아무 상태 아닐때
                    Log.d(TAG,"좋아요 등록");
                    imageView_likeBtn_nova.setImageResource(R.drawable.heart);
                    Toast.makeText(NovaActivity.this,"관심 목록에 추가되었어요.",Toast.LENGTH_SHORT).show();
                    like = 1;
                }

                //좋아요 db에 저장
                // get방식 파라미터 추가
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/nova_like.php").newBuilder();
                urlBuilder.addQueryParameter("postNumber", postNumber); // 예시
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

        //게시글 읽어오기
        //인터넷 체크
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            // get방식 파라미터 추가
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_nova_detail.php").newBuilder();
            urlBuilder.addQueryParameter("postNumber", postNumber);
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
//                                        textView_subjcet_nova.setText(jsonArray.getJSONObject(0).getString("subject"));
                                        textView_nickname_nova.setText(jsonArray.getJSONObject(0).getString("nickname"));
                                        textView_time_nova.setText(jsonArray.getJSONObject(0).getString("time"));
                                        textView_contents_nova.setText(jsonArray.getJSONObject(0).getString("contents"));
                                        Glide.with(getApplicationContext())
                                                .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/"+jsonArray.getJSONObject(0).getString("profile_image"))
                                                .circleCrop()
                                                .into(imageView_writer_img_nova);

                                        nickname = jsonArray.getJSONObject(0).getString("nickname");
                                        String subject = jsonArray.getJSONObject(0).getString("subject");
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

                                        textView_subjcet_nova.setText(subject);



                                        novaImageRecyclerViewAdapter adapter;


                                        ArrayList<String> imgList = new ArrayList<>();
                                        //배열에서 이미지 정보 가져오기
                                        for (int i = 2; i < jsonArray.length(); i++){
                                            imgList.add(jsonArray.getJSONObject(i).getString("fileName"));
                                        }
                                        Log.d(TAG,"imgList: " + imgList);

                                        adapter = new novaImageRecyclerViewAdapter(getApplicationContext(),imgList);
                                        recyclerView_img_nova.setAdapter(adapter);
                                        recyclerView_img_nova.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                                        Log.d(TAG,"작성자 번호: " + jsonArray.getJSONObject(0).getString("writer"));
                                        Log.d(TAG,"사용자 번호: " + userIdx);

                                        //내가 작성한 글인지 체크
                                        if(jsonArray.getJSONObject(0).getString("writer").equals(userIdx)) {
                                                Log.d(TAG,"체크/내가 작성한 글");
                                                MenuItem menu_mod_nova = mMenu.findItem(R.id.menu_mod_nova);
                                                MenuItem menu_delete_nova = mMenu.findItem(R.id.menu_delete_nova);
                                                MenuItem menu_report_nova = mMenu.findItem(R.id.menu_report_nova);

                                                menu_mod_nova.setVisible(true);
                                                menu_delete_nova.setVisible(true);
                                                menu_report_nova.setVisible(false);

                                            myPost = true;
                                        }else {//내가 작성한 글이 아니면
                                                myPost = false;
                                            Log.d(TAG,"체크/내가 작성한 글 아님");

                                            MenuItem menu_mod_nova = mMenu.findItem(R.id.menu_mod_nova);
                                            MenuItem menu_delete_nova = mMenu.findItem(R.id.menu_delete_nova);
                                            MenuItem menu_report_nova = mMenu.findItem(R.id.menu_report_nova);

                                            menu_mod_nova.setVisible(false);
                                            menu_delete_nova.setVisible(false);
                                            menu_report_nova.setVisible(true);
                                        }

                                        //게시글데이터에서 채팅방 여부 체크 후 그룹채팅 버튼 활성화
                                        if(jsonArray.getJSONObject(0).getString("isChat").equals("1")){
                                            button_chat_nova.setVisibility(View.VISIBLE);
                                        }else {
                                            button_chat_nova.setVisibility(View.GONE);
                                        }

                                        //좋아요
                                        if(jsonArray.getJSONObject(1).getString("check").equals("0")){
                                            Log.d(TAG,"좋아요 안한 게시글");
                                            imageView_likeBtn_nova.setImageResource(R.drawable.heart_blank);
                                            like = 0;
                                        }else {
                                            Log.d(TAG,"좋아요한 게시글");
                                            imageView_likeBtn_nova.setImageResource(R.drawable.heart);
                                            like = 1;
                                        }

                                        textView_likes_nova.setText("좋아요 "+jsonArray.getJSONObject(1).getString("count")+"개");

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

            //댓글 read 데이터 요청
            // get방식 파라미터 추가
            HttpUrl.Builder urlBuilder_reply = HttpUrl.parse("http://3.37.128.131/reply_read.php").newBuilder();
            urlBuilder_reply.addQueryParameter("postNumber", postNumber); // 예시
            String url_reply = urlBuilder_reply.build().toString();

                    // POST 파라미터 추가
                    RequestBody formBody_reply = new FormBody.Builder()
                    .build();

                    // 요청 만들기
                    OkHttpClient client_reply = new OkHttpClient();
                    Request request_reply = new Request.Builder()
                    .url(url_reply)
                    .post(formBody_reply)
                    .build();

            // 응답 콜백
            client_reply.newCall(request_reply).enqueue(new Callback() {
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
            Log.d(TAG,"댓글 responseData: " + responseData);
                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    try {
                        if(responseData.equals("0")){//댓글 없을때
                            textView_noReply.setVisibility(View.VISIBLE);
                            textView_noReply.setText("댓글이 아직 하나도 없어요.\n댓글을 남겨주세요.");
                            recyclerView_reply_nova.setVisibility(View.INVISIBLE);
                        }else {//댓글 있을때


                            textView_noReply.setVisibility(View.INVISIBLE);
                            recyclerView_reply_nova.setVisibility(View.VISIBLE);




                            JSONArray jsonArray = new JSONArray(responseData);
                            for (int i = 0; i < jsonArray.length(); i++){
                                dataModels.add(new ReplyDataModels(jsonArray.getJSONObject(i).getString("reply"),jsonArray.getJSONObject(i).getString("nickname"),jsonArray.getJSONObject(i).getString("time"),jsonArray.getJSONObject(i).getString("profile_image")));
                            }

                                    adapter = new ReplyAdapter(NovaActivity.this,dataModels);
                                    recyclerView_reply_nova.setAdapter(adapter);
                                    recyclerView_reply_nova.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
                        }
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                    }
                    });
                    }
                    }
                    });



            //댓글 작성 버튼 클릭
            imageView_reply_btn_nova.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"게시글 인덱스: " + postNumber);
                    Log.d(TAG,"작성자: " + userIdx);
                    Log.d(TAG,"내용: " + editText_reply_nova.getText().toString());

                    // get방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/reply_create.php").newBuilder();
                            String url = urlBuilder.build().toString();

                            // POST 파라미터 추가
                            RequestBody formBody = new FormBody.Builder()
                            .add("postNumber", postNumber)
                            .add("userNumber", userIdx)
                            .add("reply", editText_reply_nova.getText().toString())
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
                    Log.d(TAG,"responseData: " +responseData);
                            // 서브 스레드 Ui 변경 할 경우 에러
                            // 메인스레드 Ui 설정
                            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
                                Date time = new Date();

                                String cusrTime = format1.format(time);

                                dataModels.add(new ReplyDataModels(editText_reply_nova.getText().toString(),userNickname,cusrTime,userProfileImage));
                                adapter = new ReplyAdapter(getApplicationContext(),dataModels);
                                recyclerView_reply_nova.setAdapter(adapter);
                                editText_reply_nova.setText("");
                                textView_noReply.setVisibility(View.INVISIBLE);
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


        }else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }


    public void setContentView(){
        textView_contents_nova = findViewById(R.id.textView_contents_nova);
        textView_likes_nova = findViewById(R.id.textView_likes_nova);
        textView_nickname_nova = findViewById(R.id.textView_nickname_nova);
        textView_reply_count_nova = findViewById(R.id.textView_reply_count_nova);
        textView_subjcet_nova = findViewById(R.id.textView_subjcet_nova);
        textView_time_nova = findViewById(R.id.textView_time_nova);

        imageView_reply_btn_nova = findViewById(R.id.imageView_reply_btn_nova);
        imageView_writer_img_nova = findViewById(R.id.imageView_writer_img_nova);

        button_chat_nova = findViewById(R.id.button_chat_nova);

        recyclerView_img_nova = findViewById(R.id.recyclerView_img_nova);
        recyclerView_reply_nova = findViewById(R.id.recyclerView_reply_nova);

        imageView_likeBtn_nova = findViewById(R.id.imageView_likeBtn_nova);

        editText_reply_nova = findViewById(R.id.editText_reply_nova);

        textView_noReply = findViewById(R.id.textView_noReply);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.nova_menu,menu);

        MenuItem modMenu = menu.findItem(R.id.menu_mod_nova);
        MenuItem deletMenu = menu.findItem(R.id.menu_delete_nova);
        MenuItem reportMenu = menu.findItem(R.id.menu_report_nova);



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

                case R.id.menu_mod_nova:
                    Log.d(TAG,"수정하기 버튼 클릭");
                    startActivityString(UpdateNovaActivity.class,"postNumber",postNumber);
                    return true;

                case R.id.menu_delete_nova:
                    Log.d(TAG,"삭제하기 버튼클릭");
                    show_delete_dialog();
                    return true;
            }
            return super.onOptionsItemSelected(item);
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
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/delete_nova.php").newBuilder();
                        urlBuilder.addQueryParameter("postNumber", postNumber); // 예시
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