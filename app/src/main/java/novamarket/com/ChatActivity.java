//package novamarket.com;
//
//
//import com.bumptech.glide.Glide;
//
//import novamarket.com.Adapter.ChatAdapter;
//import novamarket.com.DataModels.ChatDataModels;
//import novamarket.com.Service.ChatService;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.HttpUrl;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Handler;
//import android.util.Log;
//import android.view.MenuItem;
//
//import androidx.appcompat.widget.Toolbar;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.airbnb.lottie.L;
//import com.google.gson.JsonObject;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//public class ChatActivity extends AppCompatActivity {
//    String TAG = "ChatActivity";
//
////    public static Context context_chat;
////    public static String chatRoom;//채팅방 이름
//
//    DataOutputStream outputStream;
//    DataInputStream inputStream;
//    String host;
//    int port;
//    TextView textView_market_title_chat,textView_market_price_chat;
//    ImageView imageView_market_chat,button_sendChat;
//    EditText editText_chat;
//    RecyclerView recyclerView;
//
//
//    String userData;
//
//    String userIdx;
//
//    String buyer;//구매 희망자
//    String postNumber;//상품 번호
//    String seller;//판매자
//    String chatType;//채팅 타입(갠톡인지 market  / 단톡인지 community)
//    String opponentNickname;//상대방 이름
//    String opponentProfileImg;//상대방 프로필 이미지
//    String opponentUserIdx;//상대방 번호
//    String SendTime;//메시지 전송 시간
//
//
//    ChatAdapter adapter;
//    ArrayList<ChatDataModels> dataModels = new ArrayList<>();
//
//
//    final static ChatService chatService = new ChatService();
//    private static Socket socket = ChatService.socket;
//
//    Handler handler = new Handler();//API의 기본 핸들러 객체 생성하기
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        Log.d(TAG,"onCreate");
//        setContentView();
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
//
//
//        adapter = new ChatAdapter(getApplicationContext(),dataModels);
//
//        //현재 사용중인 사용자 누구인지 체크
//        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        userData = pref.getString("userData",null);
//
//        try {
//            JSONObject jsonObject = new JSONObject(userData);
//            userIdx = jsonObject.getString("idx");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.d(TAG,"사용자 번호: " + userIdx);
//
//        // 툴바 생성
//        Toolbar toolbar = findViewById(R.id.toolbar_chat);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
//
//        //게시글 번호 가져오기
//        Intent intent = getIntent();
//        postNumber = intent.getStringExtra("postNumber");
//        buyer = intent.getStringExtra("buyer");//구매자번호 가져오기(채팅으로 거래하기 버튼클릭한사람 누구인지)
//        chatType = intent.getStringExtra("chatType");//마켓 채팅인지 팀노바생활 채팅인지 저장
//        opponentNickname = intent.getStringExtra("opponent");//상대방 닉네임
//        opponentUserIdx = intent.getStringExtra("opponentUserIdx");//상대방 번호
//        opponentProfileImg = intent.getStringExtra("opponentImg");//상대방 이미지
//
//        if(buyer == null){
//            Log.d(TAG,"구매자 정보 x");
//            chatRoom = intent.getStringExtra("chatRoom");//채팅방 이름
//            //마켓 개인 채팅만 코딩함 단톡은 나중에 생각...
//            buyer = chatRoom.substring(chatRoom.lastIndexOf("M")+1);
//            Log.d(TAG,"buyer: " + buyer);
//        }else {
//            Log.d(TAG,"구매자 정보 o");
//            Log.d(TAG,"상품번호: " + postNumber + " 구매자: " + buyer);
//            chatRoom = postNumber+"M"+buyer;//채팅방 이름
//        }
//
//        Log.d(TAG,"chatRoom: " + chatRoom);
//
//        // get방식 파라미터 추가
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/read_market_detail.php").newBuilder();
//        urlBuilder.addQueryParameter("postNumber", postNumber);
//        String url = urlBuilder.build().toString();
//
//
//                // 요청 만들기
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        // 응답 콜백
//        client.newCall(request).enqueue(new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                }
//
//        @Override
//        public void onResponse(Call call, final Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                // 응답 실패
//                Log.i("tag", "응답실패");
//                } else {
//                // 응답 성공
//                Log.i("tag", "응답 성공");
//        final String responseData = response.body().string();
//        Log.d(TAG,"responseData: " + responseData);
//                // 서브 스레드 Ui 변경 할 경우 에러
//                // 메인스레드 Ui 설정
//                runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                        try {
//                            JSONArray jsonArray = new JSONArray(responseData);
//                            getSupportActionBar().setTitle(opponentNickname); // 툴바 제목 설정
//                            textView_market_title_chat.setText(jsonArray.getJSONObject(0).getString("title"));//중고거래 게시글 제목
//                            textView_market_price_chat.setText(jsonArray.getJSONObject(0).getString("price")+" 원");//중고거래 게시글 가격
//                            seller = jsonArray.getJSONObject(0).getString("seller");//판매자
//                            Log.d(TAG,"판매자: " + seller);
//
//                            Glide.with(getApplicationContext())
//                                    .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/market_post/"+jsonArray.getJSONObject(2).getString("fileName"))
//                                    .into(imageView_market_chat);
//
//
//                        } catch (Exception e) {
//                        e.printStackTrace();
//                        }
//                        }
//                        });
//                        }
//                        }
//                        });
//
//                //채팅 전송 버튼
//                button_sendChat.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        // 입력한 메세지 가져옴
//                        String msg = editText_chat.getText().toString();
//                        //다시 빈칸으로 만들기
//                        editText_chat.setText("");
//
//                        //메시지 보낸 시간 가져오기
//                        long now = System.currentTimeMillis();
//                        Date mDate = new Date(now);
//
//                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        SendTime = simpleDate.format(mDate);
//
//                        JSONObject chat = new JSONObject();
//                        try {
//                            chat.put("chatRoom",chatRoom);
//                            chat.put("sender",userIdx);
//                            chat.put("msg",msg);
//                            chat.put("seller",seller);
//                            chat.put("buyer",buyer);
//                            chat.put("postNumber",postNumber);
//                            chat.put("chatType",chatType);
//                            chat.put("time",SendTime);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//                                try {
//                                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//                                    outputStream.writeUTF(String.valueOf(chat));
//
//
//                                    handler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//
//
//                                            dataModels.add(new ChatDataModels(null,null,null,msg,null,SendTime,true,2));
//                                            recyclerView.setAdapter(adapter);
//                                        }
//                                    });
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
//
//
//
//                    }
//                });
//
//
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Log.d(TAG,"onNewIntent");
//
//        String msg = intent.getStringExtra("msg");
//        Log.d(TAG,"서비스에서 보낸 msg: " + msg);
//
//        //받은 채팅 메세지
//        try {
//            JSONObject jsonObject = new JSONObject(msg);
//            String msgRoom = jsonObject.getString("chatRoom");
//            if(msgRoom.equals(chatRoom)){//받은 메세지의 채팅방이름과 현재 채팅방이름이 같으면
//                Log.d(TAG,"현재 해당 채팅방에 있음");
//                dataModels.add(new ChatDataModels(opponentProfileImg,opponentNickname,jsonObject.getString("msg"),null,jsonObject.getString("time"),null,false,1));
//                recyclerView.setAdapter(adapter);
//            }else {//채팅방 액티비티이지만 채팅방 명이 다르면
//                Log.d(TAG,"현재 다른 채팅방에 있음");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setContentView(){
//        textView_market_price_chat = findViewById(R.id.textView_market_price_chat);
//        textView_market_title_chat = findViewById(R.id.textView_market_title_chat);
//        imageView_market_chat = findViewById(R.id.imageView_market_chat);
//        editText_chat = findViewById(R.id.editText_chat);
//        button_sendChat = findViewById(R.id.button_sendChat);
//        recyclerView = findViewById(R.id.recyclerview_chat);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//            switch (item.getItemId()){
//            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
//            // todo
//                finish();
//            return true;
//            }
//            }
//            return super.onOptionsItemSelected(item);
//            }
//
//
//
//}