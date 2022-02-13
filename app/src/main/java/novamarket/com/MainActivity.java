package novamarket.com;
import novamarket.com.Adapter.ChatListAdapter;
import novamarket.com.DataModels.ChatDataModels;
import novamarket.com.DataModels.ChatListDataModels;
import novamarket.com.Fragment.RoomDB;
import novamarket.com.Service.ChatService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.RoomDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import novamarket.com.Fragment.FragmentChat;
import novamarket.com.Fragment.FragmentCommunity;
import novamarket.com.Fragment.FragmentHome;
import novamarket.com.Fragment.FragmentMyAccount;



public class MainActivity extends AppCompatActivity {


        private String TAG = "메인";
        String userIdx;

        Fragment fragment_home;
        Fragment fragment_community;
        Fragment fragment_chat;
        Fragment fragment_myaccount;

        String userData;

        RoomDB userDB;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String telephone = pref.getString("telephone",null);
        Log.d(TAG,"telephone: " + telephone);





        //인터넷 연결 체크
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


                    // get방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/user_check.php").newBuilder();
                            urlBuilder.addQueryParameter("v", "1.0"); // 예시
                            String url = urlBuilder.build().toString();
                    
                            // POST 파라미터 추가
                            RequestBody formBody = new FormBody.Builder()
                            .add("telephone", telephone)
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
                                Log.d(TAG,"0");
                                // 응답 성공
                                Log.i("tag", "응답 성공");
                                final String responseData = response.body().string();

                                Log.d(TAG,"1");
                                //유저 데이터 String -> json 타입 변경
                                try {
                                    Log.d(TAG,"2");
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    userIdx = jsonObject.getString("idx");
                                    Log.d(TAG,"userIdx: " + userIdx);

                                    Log.d(TAG,"3");
                                    //서비스 시작
                                    Intent service_intent = new Intent(getApplicationContext(),ChatService.class);
                                    service_intent.putExtra("userIdx",userIdx);
                                    startService(service_intent);
                                    Log.d(TAG,"4");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.d(TAG,"서버에서 받아온 유저정보: " + responseData);
                                SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("userData",responseData);
                                editor.apply();




//                                    JSONObject jsonObject = new JSONObject(responseData);
                                //받아온 회원 정보 담기
//                                    UserData userData = new UserData();
//                                    userData.setNickname(jsonObject.getString("nickname"));
//                                    userData.setEmail(jsonObject.getString("email"));
//                                    userData.setProfile_image(jsonObject.getString("profile_image"));
//                                    //roomDB에 회원정보 저장
//                                    userDB = RoomDB.getInstance(getApplicationContext());
//                                    userDB.userDao().insert(userData);


                                // 서브 스레드 Ui 변경 할 경우 에러
                            // 메인스레드 Ui 설정
                            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                if(responseData.equals("1")){//회원DB에 없으면
                                    Toast.makeText(MainActivity.this,"데이터를 연결해 주세요.",Toast.LENGTH_SHORT).show();
                                }else {
                                    Log.d(TAG,"responseData: " + responseData);
                                    userData = responseData;


//                                    //내정보 탭으로 회원 데이터 넘기기
//                                    Bundle bundle_myaccount = new Bundle();
//                                    Log.d(TAG,"userData: " + userData);
//                                    bundle_myaccount.putString("userData",userData);
//
//                                    Log.d(TAG,"[서버요청]userData: " + userData);
//
//                                    fragment_myaccount.setArguments(bundle_myaccount);
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
        


        //프래그먼트 생성
        fragment_home = new FragmentHome();
        fragment_community = new FragmentCommunity();
        fragment_chat = new FragmentChat();
        fragment_myaccount = new FragmentMyAccount();







        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout,fragment_home)
                .commitAllowingStateLoss();

        // 바텀 네비게이션
        BottomNavigationView bottomNavigationView;
        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "바텀 네비게이션 클릭");

                switch (item.getItemId()){
                    case R.id.home:
                        Log.i(TAG,"home 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_home).commitAllowingStateLoss();
                        return true;

                    case R.id.community:
                        Log.i(TAG,"community 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_community).commitAllowingStateLoss();
                        return true;

                    case R.id.chat:
                        Log.i(TAG,"chat 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_chat).commitAllowingStateLoss();
                        return true;

                    case R.id.myaccount:
                        Log.i(TAG,"myaccount 들어옴");
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragment_myaccount).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent");

        String chat = intent.getStringExtra("msg");
        Log.d(TAG,"서비스에서 보낸 msg: " + chat);

        FragmentChat fragment = new FragmentChat();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(chat);
            String chatRoom = jsonObject.getString("chatRoom");
            String msg = jsonObject.getString("msg");
            String time = jsonObject.getString("time");
//            fragment.setChatList(chatRoom,msg,time);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //받은 채팅 메세지
//        try {
//            JSONObject jsonObject = new JSONObject(chat);
//            String chatRoom = jsonObject.getString("chatRoom");
//            String msg = jsonObject.getString("msg");
//            String time = jsonObject.getString("time");
//
//            for (int i = 0; i < chatListDataModels.size(); i++){
//                Log.d(TAG,i+"번째 데이터");
//                if(chatListDataModels.get(i).getChatRoom().equals(chatRoom)){
//                    String opponentUserIdx = chatListDataModels.get(i).getOpponentUserIdx();
//                    String profileImage = chatListDataModels.get(i).getProfileImage();
//                    String postImg = chatListDataModels.get(i).getPostImg();
//                    String title = chatListDataModels.get(i).getTitle();
//                    String opponent = chatListDataModels.get(i).getOpponent();
//                    String postNumber = chatListDataModels.get(i).getPostNumber();
//                    boolean market = chatListDataModels.get(i).isMarket();
//                    boolean group = chatListDataModels.get(i).isGroup();
//
//
//                    chatListDataModels.remove(i);
//                    chatListAdapter.notifyItemRemoved(i);
//                    chatListDataModels.add(new ChatListDataModels(chatRoom,opponentUserIdx,profileImage,postImg,title,opponent,market,group,msg,time,postNumber));
//                    chatListAdapter.notifyDataSetChanged();
//                }
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



    }
}