package novamarket.com;
import novamarket.com.Adapter.UpdateMultiImageAdapter;
import novamarket.com.Adapter.UpdateNovaMultiImageAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpdateNovaActivity extends AppCompatActivity {
    String TAG = "UpdateNovaActivity";

    Button done_UpdateNova;
    RecyclerView recyclerView_location_UpdateNova,recyclerView_img_UpdateNova;
    EditText contents_UpdateNova;
    ImageView uploadPhoto_UpdateNova,uploadLocation_UpdateNova;
    public static TextView countphoto_UpdateNova,countLocation_UpdateNova;

    ArrayList<UpdateImageDataModels> totalImgList = new ArrayList<>();//수정하기 액티비티에 보여지는 이미지 전체 리스트
    public ArrayList<String> removeList = new ArrayList<>();//삭제한 이미지 담을 변수

    String postNumber;//게시글 번호

    UpdateNovaMultiImageAdapter adapter;

    public static Context context_update_nova;

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nova);

        removeList = new ArrayList<>();

        context_update_nova = this;

        setContentsView();//뷰 초기화

        Intent intent = getIntent();
        //게시글 번호
        postNumber = intent.getStringExtra("postNumber");
        Log.d(TAG,"postNumber: " + postNumber);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_UpdateNova);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("팀노바생활 글 수정하기"); // 툴바 제목 설정
        
        //서버에서 게시글 가져오기
        // get방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/update_nova.php").newBuilder();
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
        Log.d(TAG,"responseData: " +responseData);
                // 서브 스레드 Ui 변경 할 경우 에러
                // 메인스레드 Ui 설정
                runOnUiThread(new Runnable() {
        @Override
        public void run() {
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    contents_UpdateNova.setText(jsonArray.getJSONObject(0).getString("contents"));


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
                    countphoto_UpdateNova.setText(totalImgList.size()+"/10");

                    adapter = new UpdateNovaMultiImageAdapter(getApplicationContext(),totalImgList);
                    recyclerView_img_UpdateNova.setAdapter(adapter);
                    recyclerView_img_UpdateNova.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
                } catch (Exception e) {
                e.printStackTrace();
                }
                }
                });
                }
                }
                });


                //사진추가 버튼
                uploadPhoto_UpdateNova.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//다중이미지를 가져올 수 있도록 세팅
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,2222);
                    }
                });

                //수정완료 버튼
                done_UpdateNova.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"수정완료 버튼 클릭");
                        Log.d(TAG,"삭제된 이미지 배열: " + removeList);


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

                        Log.d(TAG,"새로 서버에 업로드할 imgList: " + imgList);

                        JSONObject novaUpdateObject = new JSONObject();

                        try {
                            novaUpdateObject.put("contents",contents_UpdateNova.getText().toString());

                            Log.d(TAG,"novaUpdateObject: " + novaUpdateObject);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e){
                        }

                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/update_ok_nova.php").newBuilder();
                        urlBuilder.addQueryParameter("postNumber", postNumber);
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("novaUpdateObject", novaUpdateObject.toString())
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
                                                Toast.makeText(UpdateNovaActivity.this,   "게시글 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
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
                        countphoto_UpdateNova.setText(totalImgList.size()+"/10");
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

                            countphoto_UpdateNova.setText(totalImgList.size()+"/10");

                        }




                        adapter = new UpdateNovaMultiImageAdapter(getApplicationContext(),totalImgList);

                        Log.d(TAG,"usiList: " + uriList);
                        Log.d(TAG,"기존이미지 개수: " + adapter.getItemCount());

                        recyclerView_img_UpdateNova.setAdapter(adapter);
                        recyclerView_img_UpdateNova.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));

                    }
                }
            }

        }
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


            public void setContentsView(){
                done_UpdateNova = findViewById(R.id.done_UpdateNova);
                recyclerView_location_UpdateNova = findViewById(R.id.recyclerView_location_UpdateNova);
                recyclerView_img_UpdateNova = findViewById(R.id.recyclerView_img_UpdateNova);
                contents_UpdateNova = findViewById(R.id.contents_UpdateNova);
                uploadPhoto_UpdateNova = findViewById(R.id.uploadPhoto_UpdateNova);
                uploadLocation_UpdateNova = findViewById(R.id.uploadLocation_UpdateNova);
                countphoto_UpdateNova = findViewById(R.id.countphoto_UpdateNova);
                countLocation_UpdateNova = findViewById(R.id.countLocation_UpdateNova);
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