package novamarket.com;
import novamarket.com.Fragment.FragmentCommunity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WriteCommunityPostActivity extends AppCompatActivity {
    String TAG = "WriteCommunityPostActivity";

    //다중 이미지 업로드 리사이클러뷰
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

    MultiImageAdapter_community adapter;  // 리사이클러뷰에 적용시킬 어댑터

    //뷰 객체
    Toolbar toolbar;
    Button done_WriteCommunity;
    TextView selected_subject,countphoto,countLocation;
    CheckBox checkBox_groupChat;
    RecyclerView recyclerView_location,recyclerView_img;
    EditText contents;
    ImageView uploadPhoto,uploadLocation;
    LinearLayout select_category_btn_com;

    public static Context context_com;

    int subject = 0;//게시글 주제 번호

    int userIdx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_community_post);

        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
        String userData = pref.getString("userData",null);
        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getInt("idx");
            Log.d(TAG,"유저넘버: " + userIdx);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setContentView();//화면 초기화

        reStoreComPost();//임시저장 불러오기

        context_com = this;

        // 툴바 생성
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("팀노바생활 글쓰기"); // 툴바 제목 설정


        //게시글 주제 선택버튼
        select_category_btn_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"게시글 주제 선택 버튼클릭");
                Intent intent = new Intent(WriteCommunityPostActivity.this,SubjectSelectActivity.class);
                intent.putExtra("subject",subject);
                Log.d(TAG,"원래 선택되어있던 주제 번호: " + subject);
                startActivityForResult(intent,1111);
            }
        });

        //사진 추가 버튼
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"사진 업로드 버튼 클릭");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//다중이미지를 가져올 수 있도록 세팅
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2222);
            }
        });

        //위치 추가 버튼
        uploadLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"위치 업로드 버튼 클릭");
                Intent intent = new Intent(WriteCommunityPostActivity.this,SearchLocationActivity.class);
                startActivityForResult(intent,3333);
            }
        });



        //완료 버튼
        done_WriteCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"완료버튼 클릭");
                if(subject != 0 && contents.getText().toString().length() > 0 ){//내용 , 주제 필수 입력 예외처리

                    SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd_HHmmss");
                    Date time = new Date();
                    String timeNow = format1.format(time);
                    Log.d(TAG,"현재시간: " + timeNow);


                    ArrayList<String> imgList = new ArrayList<>();// 이미지 파일명을 담을 ArrayList 객체
                    for (int i = 0; i < uriList.size(); i++){

                        Log.d(TAG,"uri: " + uriList.get(i));
                        String[] bits = uriList.get(i).toString().split("/");
                        String 기존파일명 = bits[bits.length-1];
                        Log.d(TAG,"파일명: " + 기존파일명);

                        String fileName = 기존파일명 +"_"+ timeNow;
                        Log.d(TAG,"업로드할 fileName: " + fileName);

                        imgList.add(fileName); //mysql에 보내기위해 담는 이미지 파일명 배열

                        String imgPath = getRealPathFromURI(uriList.get(i));
                        Log.d(TAG,"이미지 RealPathFromURI: " + imgPath);
                        File file = new File(imgPath);

                        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
                        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
                        TransferNetworkLossHandler.getInstance(getApplicationContext());

                        Log.d(TAG,"보내는 file: " + fileName);
                        TransferObserver uploadObserver = transferUtility.upload("novamarket/nova_post", fileName,file);
                        uploadObserver.setTransferListener(new TransferListener() {
                            @Override
                            public void onStateChanged(int id, TransferState state) {
                                Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());

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


                    }

                    Log.d(TAG,"imgList: " + imgList);

                    if(imgList.isEmpty()){//이미지 하나도 선택안했을때는 이미지 파일명에 카테고리 번호로 저장
                        Log.d(TAG,"이미지 하나도 선택안하고 게시글 작성 할때");
                        imgList.add(String.valueOf(subject));
                        Log.d(TAG,"imgList: " + imgList);

                    }

                    JSONObject novaObject = new JSONObject();

                    try {
                        novaObject.put("contents",contents.getText().toString());
                        novaObject.put("subject",subject);
                        novaObject.put("isChat",checkBox_groupChat.isChecked());
                        novaObject.put("writer",userIdx);

                        Log.d(TAG,"novaObject: " + novaObject);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e){
                    }

                    // get방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/write_nova_post.php").newBuilder();
                    urlBuilder.addQueryParameter("v", "1.0"); // 예시
                    String url = urlBuilder.build().toString();

                    // POST 파라미터 추가
                    RequestBody formBody = new FormBody.Builder()
                            .add("novaObject", novaObject.toString())
                            .add("imgList", String.valueOf(imgList))
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
                                            Toast.makeText(WriteCommunityPostActivity.this,   "게시글 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG,"responseData: " + responseData);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    });


                    startActivityC(MainActivity.class);


                }else {//내용,주제 입력안했을때
                    Toast.makeText(WriteCommunityPostActivity.this,"내용, 주제는 필수입력값입니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2222) {

            if (data == null) {
//                Toast.makeText(this,"이미지를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show();
            } else {//이미지를 하나라도 선택한 경우
                if (data.getClipData() == null) {//이미지를 하나만 선택한 경우
                    if(uriList.size() + 1 > 10){//한장을 추가했을 때 10장이 넘으면
                        Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.e("single choice: ", String.valueOf(data.getData()));
                        Uri imageUri = data.getData();
                        uriList.add(imageUri);

                        Log.d(TAG, "uriList.size():" + uriList.size());
                    }


                } else {//이미지를 여러장 선택한 경우
                    ClipData clipData = data.getClipData();
                    Log.e("clipData: ", String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 10) {//선택한 이미지가 11장 이상인 경우
                        Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "multiple choice");
                        if(uriList.size() + clipData.getItemCount() > 10){//여러장 가져온사진과 원래있던 사진이 합쳐서 10장이 넘는경우
                            Log.d(TAG,"여러장 가져온사진과 원래있던 사진이 합쳐서 10장이 넘는경우");
                            Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                            int pc = uriList.size() + clipData.getItemCount();
                            Log.d(TAG,"총 사진수: " + pc);
                        }else {
                            Log.d(TAG,"여러장 가져온사진과 원래있던 사진이 합쳐서 10장 이하인 경우");
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();//선택한 이미지들의 uri를 가져온다.

                                try {
                                    uriList.add(imageUri);
                                } catch (Exception e) {
                                    Log.e(TAG, "File select error", e);
                                }
                            }
                        }


                        adapter = new MultiImageAdapter_community(uriList, getApplicationContext());

                        Log.d(TAG, "adapter.getItemCount(): " + adapter.getItemCount());
                        countphoto.setText(adapter.getItemCount() + "/10");
                        if (adapter.getItemCount() == 10) {//사진 10개면 사진갯수 색상 빨간색으로
                            countphoto.setTextColor(Color.parseColor("#ff0000"));
                        }

                        recyclerView_img.setAdapter(adapter);//리사이클러뷰에 어댑터 세팅
                        recyclerView_img.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));//리사이클러뷰 수평 스크롤 적용
                    }
                }
            }

        }else if(requestCode == 1111){//게시글 주제선택
            Log.d(TAG,"주제선택1");
            if(resultCode == RESULT_OK){

                subject = data.getIntExtra("subject",0);
                Log.d(TAG,"선택한 주제 번호: " + subject);

                switch(subject){
                    case  1:
                        selected_subject.setText("같이해요");
                        checkBox_groupChat.setVisibility(View.VISIBLE);//그룹 채팅방 만들기 체크박스 보이게 하기
                        break;

                    case 2:
                        selected_subject.setText("동네맛집");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  3:
                        selected_subject.setText("일상");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기

                        break;
                    case  4:
                        selected_subject.setText("팀노바소식");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  5:
                        selected_subject.setText("취미생활");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  6:
                        selected_subject.setText("건강");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  7:
                        selected_subject.setText("코딩");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  8:
                        selected_subject.setText("기타");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;

                }
            }

        }
    }

    public void setContentView(){//화면 초기화
        toolbar = findViewById(R.id.toolbar_WriteCommunity);
        done_WriteCommunity = findViewById(R.id.done_WriteCommunity);
        selected_subject = findViewById(R.id.selected_subject);
        countphoto = findViewById(R.id.countphoto_community);
        countLocation = findViewById(R.id.countLocation);
        checkBox_groupChat = findViewById(R.id.checkBox_groupChat);
        recyclerView_location = findViewById(R.id.recyclerView_write_community_post_location);
        recyclerView_img = findViewById(R.id.recyclerView_write_community_post_img);
        contents = findViewById(R.id.contents_write_community_post);
        uploadLocation = findViewById(R.id.uploadLocation_community);
        uploadPhoto = findViewById(R.id.uploadPhoto_community);
        select_category_btn_com = findViewById(R.id.select_category_btn_com);
    }

    //툴바옵션
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
            // todo
                tmpSaveComPost();//게시글 임시저장
                finish();

            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }

    public void tmpSaveComPost(){//팀노바생활 게시글 임시저장 메서드
        if(contents.getText().length() > 0){//내용 있는지 예외처리
            Log.d(TAG,"작성중이던 게시글이 있음.");


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isStore",true);
                jsonObject.put("contents",contents.getText().toString());
                jsonObject.put("subject",subject);
                jsonObject.put("isChat",checkBox_groupChat.isChecked());
                for(int i = 0; i <uriList.size(); i++){
                    Uri uri = uriList.get(i);
                    jsonObject.put("photo"+i,uri.toString());
                }
                jsonObject.put("imageCount",uriList.size());

                Log.d(TAG,"임시저장: " + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences pref = getSharedPreferences("팀노바생활", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("임시저장",jsonObject.toString());
            editor.apply();

            Toast.makeText(this,"게시글이 임시 저장되었어요",Toast.LENGTH_SHORT).show();
//

        }else {
            Log.d(TAG,"작성중이던 게시글 없음");
        }
    }

    public void reStoreComPost(){
        //팀노바생활 게시글 임시저장 값 불러오기
        SharedPreferences pref2 = getSharedPreferences("팀노바생활", Activity.MODE_PRIVATE);
        String 임시저장 = pref2.getString("임시저장",null);
        Log.d(TAG,"임시저장: " + 임시저장);
        if(임시저장 == null){
            Log.d(TAG,"저장된 게시글 없음");
        }else {
            Log.d(TAG, "저장된 게시글 있음");
            try {
                JSONObject jsonObject = new JSONObject(임시저장);
                contents.setText(jsonObject.getString("contents"));
                subject = jsonObject.getInt("subject");
                checkBox_groupChat.setChecked(jsonObject.getBoolean("isChat"));

                for (int i = 0; i < jsonObject.getInt("imageCount"); i++){
                            String imageUri = jsonObject.getString("photo"+i);
                            Log.d(TAG,"imageUri: " + imageUri);
                            uriList.add(Uri.parse(imageUri));
                }

                Log.d(TAG,"uriList: " + uriList);

                adapter = new MultiImageAdapter_community(uriList,getApplicationContext());

                recyclerView_img.setAdapter(adapter);
                recyclerView_img.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));

                countphoto.setText(uriList.size()+"/10");

                //주제번호별 텍스트
                switch(subject){
                    case  1:
                        selected_subject.setText("같이해요");
                        checkBox_groupChat.setVisibility(View.VISIBLE);//그룹 채팅방 만들기 체크박스 보이게 하기
                        break;

                    case 2:
                        selected_subject.setText("동네맛집");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  3:
                        selected_subject.setText("일상");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기

                        break;
                    case  4:
                        selected_subject.setText("팀노바소식");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  5:
                        selected_subject.setText("취미생활");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  6:
                        selected_subject.setText("건강");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  7:
                        selected_subject.setText("코딩");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;
                    case  8:
                        selected_subject.setText("기타");
                        checkBox_groupChat.setVisibility(View.GONE);//그룹 채팅방 만들기 체크박스 안 보이게 하기
                        break;

                }


            }catch (Exception e){

            }
        }
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


    //uri로 파일의 경로를 반환하는 메서드
    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
    }
}

