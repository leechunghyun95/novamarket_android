package novamarket.com;


import android.app.PendingIntent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.airbnb.lottie.LottieAnimationView;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import novamarket.com.Adapter.ChatAdapter;
import novamarket.com.DataModels.ChatDataModels;
import novamarket.com.Service.ChatService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatMarketActivity extends AppCompatActivity {
    public static Context context_chat;

    String TAG = "ChatMarketActivity";
    public static String postNumber,chatRoom;
    String postTitle,postImg,postPrice;
    String buyerIdx,buyerImg,buyerNickname;
    String sellerIdx,sellerImg,sellerNickname;
    String userData,userIdx;
    String opponentIdx,opponentImg,opponentNickname;
    String msgChatRoom,msgPostNumber;//알림으로 부터 받은 채팅방이름,게시글 번호
    String sendTime;

    TextView textView_market_title_chat,textView_market_price_chat;
    ImageView imageView_market_chat,button_sendChat,imageView_add_data_button,imageView_close_data_button;
    EditText editText_chat;
    RecyclerView recyclerView;
    LinearLayout linearLayout_post,linearLayout_add_data,btn_add_photo_chat,btn_add_location_chat,btn_add_camera_chat;

    Handler handler = new Handler();//API의 기본 핸들러 객체 생성하기

    ChatAdapter adapter;
    ArrayList<ChatDataModels> dataModels = new ArrayList<>();

    int page = 1;



    private static Socket socket = ChatService.socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_market);
        Log.d(TAG,"onCreate");

        setContentView();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

        //스크롤 리스너
        //채팅 페이징
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//                int itemTotalCount = 0;
//
//                if (lastVisibleItemPosition == itemTotalCount) {
//                    //처음위치에 도착하면 서버에 요청해서 데이터 불러오기
//                    Log.d(TAG, "first Position...");
//                    LottieAnimationView animationView = findViewById(R.id.lottie_update_chat);
//                    animationView.setAnimation("loading.json");
//                    animationView.loop(true);
//                    animationView.playAnimation();
//
//                    page = page + 1;
//
//                    String pageString = Integer.toString(page);
//                    // get방식 파라미터 추가
//                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.125.83.14/read_chat.php").newBuilder();
//                    String url = urlBuilder.build().toString();
//
//                    // POST 파라미터 추가
//                     RequestBody formBody = new FormBody.Builder()
//                        .add("chatRoom", chatRoom)
//                        .add("page", pageString)
//                        .build();
//
//                     // 요청 만들기
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                        .url(url)
//                        .post(formBody)
//                        .build();
//
//                    // 응답 콜백
//                    client.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onResponse(Call call, final Response response) throws IOException {
//                            if (!response.isSuccessful()) {
//                            // 응답 실패
//                            Log.i("tag", "응답실패");
//                            } else {
//                                // 응답 성공
//                                Log.i("tag", "응답 성공");
//                                final String responseData = response.body().string();
//                                Log.d(TAG,"responseData: " + responseData);
//                                // 서브 스레드 Ui 변경 할 경우 에러
//                                // 메인스레드 Ui 설정
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            JSONArray jsonArray2 = new JSONArray(responseData);
//                                            //채팅 메세지 가져와서 어뎁터에 넣기
//                                            for (int i = 5; i < jsonArray2.length(); i++){
//                                                if(userIdx.equals(jsonArray2.getJSONObject(i).getString("sender"))){//내가 보낸 메세지면
//                                                    dataModels.add(0,new ChatDataModels(null,null,null,jsonArray2.getJSONObject(i).getString("msg"),null,jsonArray2.getJSONObject(i).getString("time"),true,2,jsonArray2.getJSONObject(i).getString("type")));
//                                                    adapter.notifyDataSetChanged();
//                                                    recyclerView.scrollToPosition(page*10-1);
//
//                                                }else {//상대방이 보낸 메세지면
//                                                    dataModels.add(1,new ChatDataModels(opponentImg,opponentNickname,jsonArray2.getJSONObject(i).getString("msg"),null,jsonArray2.getJSONObject(i).getString("time"),null,false,1,jsonArray2.getJSONObject(i).getString("type")));
//                                                    recyclerView.setAdapter(adapter);
//                                                    adapter.notifyDataSetChanged();
//                                                    recyclerView.scrollToPosition(page*10-1);
//
//
//                                                }
//                                            }
//                                            animationView.setVisibility(View.GONE);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                    });
//
//                }
//
//            }
//        });

        adapter = new ChatAdapter(getApplicationContext(),dataModels);

        //현재 사용중인 사용자 누구인지 체크
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userData = pref.getString("userData",null);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        postNumber = intent.getStringExtra("postNumber");//게시글 번호
        chatRoom = intent.getStringExtra("chatRoom");//채팅방 이름
        if(postNumber == null){
            Bundle bundle = getIntent().getExtras();
            postNumber = bundle.getString("postNumber");
            chatRoom = bundle.getString("chatRoom");
        }
        Log.d(TAG,"postNumber: " + postNumber);
        Log.d(TAG,"chatRoom: " + chatRoom);
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        //게시글 번호를 이용해서 게시글 제목, 이미지, 가격 가져오기
        //채팅방 이름을 이용해서 판매자와 구매자의 번호, 이미지, 닉네임 가져오기
        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_chatRoom.php").newBuilder();
        String url = urlBuilder.build().toString();

        // POST 파라미터 추가
        RequestBody formBody = new FormBody.Builder()
                .add("postNumber", postNumber)
                .add("chatRoom", chatRoom)
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
                    //응답 받은 데이터로 채팅방 액티비티 정보 가져오기
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        //게시글 제목,가격,이미지 가져오기
                        postTitle = jsonArray.getJSONObject(0).getString("title");
                        postPrice = jsonArray.getJSONObject(0).getString("price");
                        postImg = jsonArray.getJSONObject(1).getString("fileName");

                        //판매자 번호, 닉네임, 이미지 가져오기
                        sellerIdx = jsonArray.getJSONObject(0).getString("seller");
                        sellerImg = jsonArray.getJSONObject(2).getString("profile_image");
                        sellerNickname = jsonArray.getJSONObject(2).getString("nickname");

                        //구매자 번호, 닉네임, 이미지 가져오기
                        if(jsonArray.isNull(3)){
                            buyerIdx = userIdx;
                        }else {
                            buyerIdx = jsonArray.getJSONObject(3).getString("buyer");
                            buyerImg = jsonArray.getJSONObject(4).getString("profile_image");
                            buyerNickname = jsonArray.getJSONObject(4).getString("nickname");

                        }
                        //채팅방이 없어서 새로 만들었을 경우: 채팅으로 거래하기 누르고 들어왔을 경우
                        if(buyerIdx == null){
                            Log.d(TAG,"채팅방 새로 만들어야 대자나");
                            buyerIdx = userIdx;
                            Log.d(TAG,"buyerIdx: " +buyerIdx);
                            Log.d(TAG,"sellerIdx: " + sellerIdx);

                        }

                        
                        if(userIdx.equals(sellerIdx)){//현재 사용자가 판매자이면
                            Log.d(TAG,"현재 사용자가 판매자이면");
                            opponentIdx = buyerIdx;
                            opponentNickname = buyerNickname;
                            opponentImg = buyerImg;
                            Log.d(TAG,"opponentIdx: " +opponentIdx);
                        }else {//현재 사용자가 구매자이면
                            Log.d(TAG,"현재 사용자가 구매자이면");

                            opponentIdx = sellerIdx;
                            opponentNickname = sellerNickname;
                            opponentImg = sellerImg;

                            Log.d(TAG,"opponentIdx: " +opponentIdx);

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
                                getSupportActionBar().setTitle(opponentNickname); // 툴바 제목 설정
                                textView_market_title_chat.setText(postTitle); // 채팅방에 속한 게시글 제목
                                textView_market_price_chat.setText(postPrice); // 중고거래 게시글 가격
                                Glide.with(getApplicationContext())//중고거래 게시글 이미지
                                        .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/market_post/"+postImg)
                                        .into(imageView_market_chat);
                                JSONArray jsonArray2 = new JSONArray(responseData);
                                //채팅 메세지 가져와서 어뎁터에 넣기
                                for (int i = 5; i < jsonArray2.length(); i++){
                                    if(userIdx.equals(jsonArray2.getJSONObject(i).getString("sender"))){//내가 보낸 메세지면
                                        dataModels.add(new ChatDataModels(null,null,null,jsonArray2.getJSONObject(i).getString("msg"),null,jsonArray2.getJSONObject(i).getString("time"),true,2,jsonArray2.getJSONObject(i).getString("type")));
                                        recyclerView.setAdapter(adapter);

                                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                                    }else {//상대방이 보낸 메세지면
                                        dataModels.add(new ChatDataModels(opponentImg,opponentNickname,jsonArray2.getJSONObject(i).getString("msg"),null,jsonArray2.getJSONObject(i).getString("time"),null,false,1,jsonArray2.getJSONObject(i).getString("type")));
                                        recyclerView.setAdapter(adapter);

                                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                                    }
                                }
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });


//        imageView_add_photo_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_PICK);
//                startActivityForResult(intent, 0);
//            }
//        });



        //메세지 보내기 버튼 클릭
        button_sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력한 메세지 가져옴
                String msg = editText_chat.getText().toString();
                //다시 빈칸으로 만들기
                editText_chat.setText("");

                //메시지 보낸 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                sendTime = simpleDate.format(mDate);

                //채팅방번호, 게시글 번호, 보낸사람 번호, 메세지, 보낸 시간
                JSONObject chat = new JSONObject();
                try {
                    chat.put("chatRoom",chatRoom);
                    chat.put("postNumber",postNumber);
                    chat.put("sender",userIdx);
                    chat.put("msg",msg);
                    chat.put("time",sendTime);
                    chat.put("receiver",opponentIdx);
                    chat.put("buyer",buyerIdx);
                    chat.put("seller",sellerIdx);
                    chat.put("type","msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Log.d(TAG,"보내는 chat: " + chat);
                            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                            outputStream.writeUTF(String.valueOf(chat));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dataModels.add(new ChatDataModels(null,null,null,msg,null,sendTime,true,2,"msg"));
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //게시글 클릭
        linearLayout_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_post = new Intent(getApplicationContext(),MarketActivity.class);
                intent_post.putExtra("idx",postNumber);

                startActivity(intent_post);
            }
        });



        //플러스 버튼 클릭
        imageView_add_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_add_data.setVisibility(View.VISIBLE);
                imageView_add_data_button.setVisibility(View.GONE);
                imageView_close_data_button.setVisibility(View.VISIBLE);          }
        });

        // 플러스 버튼 닫기
        imageView_close_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_add_data.setVisibility(View.GONE);
                imageView_add_data_button.setVisibility(View.VISIBLE);
                imageView_close_data_button.setVisibility(View.GONE);
            }
        });

        //갤러리에서 사진 보내기
        btn_add_photo_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_add_photo = new Intent(Intent.ACTION_PICK);
                intent_add_photo.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent_add_photo.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent_add_photo, 0);
            }
        });

        //카메라에서 사진 보내기
//        btn_add_camera_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent,1);
//            }
//        });

//        위치공유 버튼
        btn_add_location_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),MapActivity.class);
                startActivityForResult(intent1,2);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent");

        Log.d(TAG,"chatRoom: " + intent.getStringExtra("chatRoom"));
        String msg = intent.getStringExtra("msg");
        Log.d(TAG,"서비스에서 보낸 msg: " + msg);

        //받은 채팅 메세지
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String msgRoom = jsonObject.getString("chatRoom");
            if(msgRoom.equals(chatRoom)){//받은 메세지의 채팅방이름과 현재 채팅방이름이 같으면
                Log.d(TAG,"현재 해당 채팅방에 있음");
                dataModels.add(new ChatDataModels(opponentImg,opponentNickname,jsonObject.getString("msg"),null,jsonObject.getString("time"),null,false,1,jsonObject.getString("type")));
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
            }else {//채팅방 액티비티이지만 채팅방 명이 다르면
                Log.d(TAG,"현재 다른 채팅방에 있음");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setContentView(){
        textView_market_price_chat = findViewById(R.id.textView_market_price_chat);
        textView_market_title_chat = findViewById(R.id.textView_market_title_chat);
        imageView_market_chat = findViewById(R.id.imageView_market_chat);
        editText_chat = findViewById(R.id.editText_chat);
        button_sendChat = findViewById(R.id.button_sendChat);
        recyclerView = findViewById(R.id.recyclerview_chat);
        linearLayout_post = findViewById(R.id.linearLayout_post);
        imageView_add_data_button = findViewById(R.id.imageView_add_data_button);
        linearLayout_add_data = findViewById(R.id.linearLayout_add_data);
        imageView_close_data_button = findViewById(R.id.imageView_close_data_button);
        btn_add_photo_chat = findViewById(R.id.btn_add_photo_chat);
        btn_add_location_chat = findViewById(R.id.btn_add_location_chat);
//        btn_add_camera_chat = findViewById(R.id.btn_add_camera_chat);
    }

    //채팅 이미지 전송
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                //메시지 보낸 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                sendTime = simpleDate.format(mDate);

                linearLayout_add_data.setVisibility(View.GONE);
                imageView_add_data_button.setVisibility(View.VISIBLE);
                imageView_close_data_button.setVisibility(View.GONE);
                Log.d(TAG,"data: " + data.getData());
                Uri imgUri = data.getData();
                String img = imgUri.toString();
                String imgPath = getRealPathFromURI(imgUri);
                Log.d(TAG,"imgPath: " + imgPath);
                dataModels.add(new ChatDataModels(null,null,null,img,null,sendTime,true,2,"img"));
                adapter.notifyDataSetChanged();

                LottieAnimationView animationView = findViewById(R.id.lottie_upload_img);
                animationView.setAnimation("loading.json");
                animationView.loop(true);
                animationView.playAnimation();

                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                //이미지 파일명
                String[] bits = imgPath.split("/");
                String fileName = sendTime + bits[bits.length-1];
                Log.d(TAG,"fileName: " + fileName);

                //전송할 파일
                File file = new File(imgPath);


                //사진 파일 업로드
                AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
                TransferNetworkLossHandler.getInstance(getApplicationContext());

                Log.d(TAG,"보내는 file: " + file);
                TransferObserver uploadObserver = transferUtility.upload("novamarket/chat_img", fileName, file);
                uploadObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());
                        if(state.toString().equals("COMPLETED")){
                            Log.d(TAG,"업로드 완료댐");
                            animationView.setVisibility(View.GONE);

                            //업로드 완료 되면 상대방한테 채팅으로 전송
                            //왜냐면 그래야 s3에서 url로 가져올수 있음
                            //채팅방번호, 게시글 번호, 보낸사람 번호, 메세지, 보낸 시간
                            JSONObject chat = new JSONObject();
                            try {
                                chat.put("chatRoom",chatRoom);
                                chat.put("postNumber",postNumber);
                                chat.put("sender",userIdx);
                                chat.put("msg",fileName);
                                chat.put("time",sendTime);
                                chat.put("receiver",opponentIdx);
                                chat.put("buyer",buyerIdx);
                                chat.put("seller",sellerIdx);
                                chat.put("type","img");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    try {
                                        Log.d(TAG,"보내는 chat: " + chat);
                                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                                        outputStream.writeUTF(String.valueOf(chat));
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                        }else {
                            Log.d(TAG,"업로드 완료안댐");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int)percentDonef;
                        Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                });

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == 1){
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            String img = imageBitmap.toString();
            Log.d(TAG,"imageBitmap.toString(): " + imageBitmap.toString());

            dataModels.add(new ChatDataModels(null,null,null,img,null,sendTime,true,2,"img"));
            adapter.notifyDataSetChanged();


        }else{

            if(data == null){

            }else {
                //메시지 보낸 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                sendTime = simpleDate.format(mDate);


                Double latitude = (Double) data.getExtras().get("latitude");//위도
                Double longitude = (Double) data.getExtras().get("longitude");//경도

                String location = latitude.toString()+"/"+longitude.toString();

                dataModels.add(new ChatDataModels(null,null,null,location,null,sendTime,true,2,"location"));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                JSONObject chat = new JSONObject();
                try {
                    chat.put("chatRoom",chatRoom);
                    chat.put("postNumber",postNumber);
                    chat.put("sender",userIdx);
                    chat.put("msg",location);
                    chat.put("time",sendTime);
                    chat.put("receiver",opponentIdx);
                    chat.put("buyer",buyerIdx);
                    chat.put("seller",sellerIdx);
                    chat.put("type","location");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Log.d(TAG,"보내는 chat: " + chat);
                            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                            outputStream.writeUTF(String.valueOf(chat));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }

        }

    }



    //uri로 파일의 경로를 반환하는 메서드
    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
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


}