package novamarket.com;
import novamarket.com.Adapter.UpdateMarketMultiImageAdapter;
import novamarket.com.Adapter.UpdateMultiImageAdapter;
import novamarket.com.DataModels.UpdateImageDataModels;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.checkbox.MaterialCheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//수정액티비티는 이미지 수정 설계가 까다로웠는데
//이미지를 서버에서 가져온것(String 파일명)과 새로 갤러리에서 가져온것(Uri)를 리사이클러뷰 데이터모델에 타입을 나눠서 같이 String값으로 저장하고
//onBind에서 타입별로 처리하고
//삭제된 이미지 명 배열에 저장해서 서버에 삭제 request하고
//새로 추가된 이미지명만 배열에 저장해서 서버에 request 하고 s3에 업로드
public class UpdateMarketActivity extends AppCompatActivity {
    String TAG = "UpdateMarketActivity";
    String postNumber;
    public TextView countPhoto_update_market;
    public ArrayList<String> removeList = new ArrayList<>();//삭제한 이미지 담을 변수
    ArrayList<UpdateImageDataModels> totalImgList = new ArrayList<>();//수정하기 액티비티에 보여지는 이미지 전체 리스트
    TextView category_update_market_post;
    ImageView uploadPhoto_update_market;
    EditText title_update_market_post,price_update_market_post,contents_update_market_post;
    MaterialCheckBox price_offer_check_update_market_post;
    RecyclerView recyclerView_update_market_post,recyclerView_update_market_post_new;

    UpdateMultiImageAdapter adapter;

    Button done_update_market_post;

//    UpdateMarketMultiImageAdapter adapter;

    //다중 이미지 업로드 리사이클러뷰
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체


    public static Context context_update_market;

    int category = 0;//카테고리 번호


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_market);

        context_update_market = this;

        removeList = new ArrayList<>();

        setContentView();

        Intent intent = getIntent();
        //게시글 번호
        postNumber = intent.getStringExtra("postNumber");
        Log.d(TAG,"postNumber: " + postNumber);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_UpdateMarket);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("중고거래 글 수정하기"); // 툴바 제목 설정

        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/update_market.php").newBuilder();
                urlBuilder.addQueryParameter("postNumber", postNumber); // 예시
                String url = urlBuilder.build().toString();


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
        Log.d(TAG,"responseData: "+ responseData);
                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                runOnUiThread(new Runnable() {
        @Override
        public void run() {
                try {
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        title_update_market_post.setText(jsonArray.getJSONObject(0).getString("title"));
                        price_update_market_post.setText(jsonArray.getJSONObject(0).getString("price"));
                        contents_update_market_post.setText(jsonArray.getJSONObject(0).getString("contents"));
                        price_offer_check_update_market_post.setChecked(Boolean.parseBoolean(jsonArray.getJSONObject(0).getString("isNego")));


                        switch(jsonArray.getJSONObject(0).getString("category")){
                            case  "1":
                                category_update_market_post.setText("디지털/가전");
                                category = 1;
                                break;

                            case  "2":
                                category_update_market_post.setText("가구/인테리어");
                                category = 2;
                                break;

                            case  "3":
                                category_update_market_post.setText("유아용/유아도서");
                                category = 3;
                                break;

                            case  "4":
                                category_update_market_post.setText("생활/가공식품");
                                category = 4;
                                break;

                            case  "5":

                                category_update_market_post.setText("스포츠/레저");
                                category = 5;
                                break;

                            case  "6":

                                category_update_market_post.setText("여성잡화");
                                category = 6;
                                break;

                            case  "7":
                                category_update_market_post.setText("여성의류");
                                category = 7;
                                break;

                            case  "8":
                                category_update_market_post.setText("남성패션/잡화");
                                category = 8;
                                break;

                            case  "9":
                                category_update_market_post.setText("게임/취미");
                                category = 9;
                                break;

                            case  "10":
                                category_update_market_post.setText("뷰티/미용");
                                category = 10;
                                break;

                            case  "11":
                                category_update_market_post.setText("반려동물용품");
                                category = 11;
                                break;

                            case  "12":
                                category = 12;
                                category_update_market_post.setText("도서/티켓/음반");
                                break;

                            case  "13":
                                category = 13;
                                category_update_market_post.setText("식물");
                                break;

                            case  "14":
                                category_update_market_post.setText("기타 중고물품");
                                category = 14;
                                break;

                            case  "15":

                                category_update_market_post.setText("삽니다");
                                category = 15;
                                break;
                            default:
                                break;
                        }

                                //데이터 모델리스트
                                ArrayList<String> imgList = new ArrayList();

                                for (int i = 1; i < jsonArray.length(); i++){
                                    imgList.add(jsonArray.getJSONObject(i).getString("fileName"));
//                                    totalImgList.add(jsonArray.getJSONObject(i).getString("fileName"));
                                    //서버에서 받아온 기존 이미지는 String타입이고 0으로 지정
                                    totalImgList.add(new UpdateImageDataModels(0,jsonArray.getJSONObject(i).getString("fileName")));
                                }
                                Log.d(TAG,"기존 이미지 totalImgList: " + totalImgList.get(0).getImg());
                                Log.d(TAG,"totalImgList 이미지개수: " + totalImgList.size());

                                adapter = new UpdateMultiImageAdapter(getApplicationContext(),totalImgList);

                                countPhoto_update_market.setText(adapter.getItemCount()+"/10");
                                recyclerView_update_market_post.setAdapter(adapter);
                                recyclerView_update_market_post.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
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

                //사진 새로 업로드, 추가 버튼
                uploadPhoto_update_market.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//다중이미지를 가져올 수 있도록 세팅
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,2222);
                    }
                });

                //카테고리 선택버튼
                category_update_market_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UpdateMarketActivity.this,CategorySelectActivity.class);
                        intent.putExtra("category",category);
                        Log.d(TAG,"원래 선택되어있던 카테고리 번호: " + category);
                        startActivityForResult(intent,100);
                    }
                });


                //수정 완료버튼
                done_update_market_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"수정완료버튼 클릭");
                        Log.d(TAG,"삭제된 이미지 배열: " + removeList);
                        //제목,컨텐츠,카테고리 필수입력 예외처리

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
                            TransferObserver uploadObserver = transferUtility.upload("novamarket/market_post", fileName,file);
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

                        Log.d(TAG,"새로 서버에 업로드할 imgList: " + imgList);

                        JSONObject marketUpdateObject = new JSONObject();

                        try {
                            marketUpdateObject.put("title",title_update_market_post.getText().toString());
                            marketUpdateObject.put("contents",contents_update_market_post.getText().toString());
                            marketUpdateObject.put("price",price_update_market_post.getText().toString());
                            marketUpdateObject.put("category",category);
                            marketUpdateObject.put("isNego",price_offer_check_update_market_post.isChecked());

                            Log.d(TAG,"marketUpdateObject: " + marketUpdateObject);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e){
                        }

                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/update_ok_market.php").newBuilder();
                        urlBuilder.addQueryParameter("postNumber", postNumber);
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("marketUpdateObject", marketUpdateObject.toString())
                                .add("imgList", String.valueOf(imgList))
                                .add("removeList", String.valueOf(removeList))
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
                                    Log.i("tag", "수정 응답실패");
                                } else {
                                    // 응답 성공
                                    Log.i("tag", "수정 응답 성공");
                                    final String responseData = response.body().string();
                                    Log.d(TAG,"수정응답데이터: " + responseData);
                                    // 서브 스레드 Ui 변경 할 경우 에러
                                    // 메인스레드 Ui 설정
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(UpdateMarketActivity.this,   "게시글 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
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

                        //이미지 uri를 String타입으로 변환해서 데이터 모델에 담기 uri는 1타입으로 지정
                        String imgUri = imageUri.toString();
                        totalImgList.add(new UpdateImageDataModels(1,imgUri));

                        Log.d(TAG, "uriList.size():" + uriList.size());
                        countPhoto_update_market.setText(totalImgList.size()+"/10");
                    }


                } else {//이미지를 여러장 선택한 경우
                    ClipData clipData = data.getClipData();
                    Log.e("clipData: ", String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 10) {//선택한 이미지가 11장 이상인 경우
                        Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "multiple choice");
                        if(totalImgList.size() + clipData.getItemCount() > 10){//여러장 가져온사진과 원래있던 사진이 합쳐서 10장이 넘는경우
                            Log.d(TAG,"여러장 가져온사진과 원래있던 사진이 합쳐서 10장이 넘는경우");
                            Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                            int pc = uriList.size() + clipData.getItemCount();
                            Log.d(TAG,"총 사진수: " + pc);
                        }else {
                            Log.d(TAG,"여러장 가져온사진과 원래있던 사진이 합쳐서 10장 이하인 경우");
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();//선택한 이미지들의 uri를 가져온다.
                                String imgUri = imageUri.toString();

                                try {
                                    uriList.add(imageUri);
                                    totalImgList.add(new UpdateImageDataModels(1,imgUri));
                                } catch (Exception e) {
                                    Log.e(TAG, "File select error", e);
                                }
                            }
                            Log.d(TAG,"totalImgList: " + totalImgList);
                            Log.d(TAG,"totalImgList 이미지개수: " + totalImgList.size());

                            countPhoto_update_market.setText(totalImgList.size()+"/10");

                        }




                        adapter = new UpdateMultiImageAdapter(getApplicationContext(),totalImgList);

                        Log.d(TAG,"usiList: " + uriList);
                        Log.d(TAG,"기존이미지 개수: " + adapter.getItemCount());

                        recyclerView_update_market_post.setAdapter(adapter);
                        recyclerView_update_market_post.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));

                    }
                }
            }

        }else if(requestCode == 100){//카테고리 선택 할떄
            if(resultCode == RESULT_OK){
                category = data.getIntExtra("category",0);
                Log.d(TAG,"선택한 카테고리 번호: " + category);

                switch(category){
                    case  1:
                        category_update_market_post.setText("디지털/가전");
                        category = 1;
                        break;

                    case 2:
                        category_update_market_post.setText("가구/인테리어");
                        category = 2;
                        break;
                    case  3:
                        category_update_market_post.setText("유아용/유아도서");
                        category = 3;
                        break;
                    case  4:
                        category_update_market_post.setText("생활/가공식품");
                        category = 4;
                        break;
                    case  5:
                        category_update_market_post.setText("스포츠/레저");
                        category = 5;
                        break;
                    case  6:
                        category_update_market_post.setText("여성잡화");
                        category = 6;
                        break;
                    case  7:
                        category_update_market_post.setText("여성의류");
                        category = 7;
                        break;
                    case  8:
                        category_update_market_post.setText("남성패션/잡화");
                        category = 8;
                        break;
                    case  9:
                        category_update_market_post.setText("게임/취미");
                        category = 9;
                        break;
                    case  10:
                        category_update_market_post.setText("뷰티/미용");
                        category = 10;
                        break;
                    case  11:
                        category_update_market_post.setText("반려동물용품");

                        category = 11;
                        break;
                    case  12:
                        category_update_market_post.setText("도서/티켓/음반");
                        category = 12;

                        break;
                    case  13:
                        category_update_market_post.setText("식물");
                        category = 13;

                        break;
                    case  14:
                        category_update_market_post.setText("기타중고 물품");
                        category = 14;
                        break;
                    case  15:
                        category_update_market_post.setText("삽니다");

                        category = 15;
                        break;
                }
            }
        }
    }

    public void setContentView(){
        countPhoto_update_market = findViewById(R.id.countPhoto_update_market);
        category_update_market_post = findViewById(R.id.category_update_market_post);
        uploadPhoto_update_market = findViewById(R.id.uploadPhoto_update_market);
        title_update_market_post = findViewById(R.id.title_update_market_post);
        price_update_market_post = findViewById(R.id.price_update_market_post);
        category = 15;
        contents_update_market_post = findViewById(R.id.contents_update_market_post);
        price_offer_check_update_market_post = findViewById(R.id.price_offer_check_update_market_post);
        recyclerView_update_market_post = findViewById(R.id.recyclerView_update_market_post);
        done_update_market_post = findViewById(R.id.done_update_market_post);
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
    //uri로 파일의 경로를 반환하는 메서드
    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
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