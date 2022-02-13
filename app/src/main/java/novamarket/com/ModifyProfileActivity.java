package novamarket.com;

import novamarket.com.Fragment.FragmentMyAccount;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModifyProfileActivity extends AppCompatActivity {
    String TAG = "ModifyProfileActivity";

    CircleImageView profile_image_modify_profile;

    //저장된 userData받기 위한 변수 선언
    String user_nickname = "닉네임";
    String user_email="이메일";
    String user_profile_image_url="user.png";
    String user_telephone;
    
    //사진 파일 담을 변수
    File file;

    //사진 파일 명
    String profile_image_file_name;

    //회원정보 json데이터로 받기위한 변수
    JSONObject jsonObject;

    EditText nickname_modify_profile;
    EditText email_modify_profile;

    ProgressDialog dialog;
    
    //이미지 변경했나 안했나 삭제했나 알기위한 값
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        String userData = pref.getString("userData",null);
        Log.d(TAG,"저장된 userData: " + userData);

        try {
            jsonObject = new JSONObject(userData);
            user_telephone = jsonObject.getString("telephone");
            user_nickname = jsonObject.getString("nickname");
            user_email = jsonObject.getString("email");
            user_profile_image_url = jsonObject.getString("profile_image");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_ModifyProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("프로필 수정"); // 툴바 제목 설정

        //뷰 초기화
        profile_image_modify_profile = findViewById(R.id.profile_image_modify_profile);
        nickname_modify_profile = findViewById(R.id.nickname_modify_profile);
        email_modify_profile = findViewById(R.id.email_modify_profile);
        Button button_modify_profile = findViewById(R.id.button_modify_profile);

        nickname_modify_profile.setText(user_nickname);
        email_modify_profile.setText(user_email);
        Log.d(TAG,"user_profile_image_url: " + user_profile_image_url);
        Glide.with(this)
                .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/"+user_profile_image_url)
                .apply(new RequestOptions().circleCrop())
                .into(profile_image_modify_profile);

        registerForContextMenu(profile_image_modify_profile);

        button_modify_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"수정 완료 버튼 클릭");

                //현재 시간 구해서 이미지 파일명에 써먹음
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd_hhmmss");
                String getTime = simpleDate.format(mDate);

                if(state == 0){
                    Log.d(TAG,"프로필 이미지 그대로인 상태");
                    profile_image_file_name = user_profile_image_url;
                    Log.d(TAG,"profile_image_file_name: " +profile_image_file_name);

                }else if(state == 1){
                    Log.d(TAG,"프로필 이미지 변경한 상태");
                    profile_image_file_name = user_nickname+"_"+getTime;
                    Log.d(TAG,"profile_image_file_name: " +profile_image_file_name);

                }else {
                    Log.d(TAG,"프로필 이미지 삭제한 상태");
                    profile_image_file_name = "user.png";
                    Log.d(TAG,"profile_image_file_name: " +profile_image_file_name);
                    
                }

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                        if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                            Log.d(TAG,"인터넷 연결 확인");

                            // get방식 파라미터 추가
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/modify_profile.php").newBuilder();
                                    urlBuilder.addQueryParameter("v", "1.0"); // 예시
                                    String url = urlBuilder.build().toString();

                                    // POST 파라미터 추가
                            RequestBody formBody = null;
                            try {
                                formBody = new FormBody.Builder()
                                        .add("telephone", jsonObject.getString("telephone"))
                                        .add("email", email_modify_profile.getText().toString())
                                        .add("nickname", nickname_modify_profile.getText().toString())
                                        .add("profile_image", profile_image_file_name)
                                .build();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                                if(responseData.equals("1")){
                                    Log.d(TAG,"프로필 수정 USER 테이블에 UPDATE 성공");

                                    if(state == 1){
                                        Log.d(TAG,"사진 변경됐을때 => 사진 업로드");

                                        dialog = new ProgressDialog(ModifyProfileActivity.this);
                                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        dialog.setMessage("프로필 수정중입니다.");

                                        dialog.show();

                                        //프로필 사진 s3에 업로드한다.
                                        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
                                        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                                        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
                                        TransferNetworkLossHandler.getInstance(getApplicationContext());

                                        Log.d(TAG,"보내는 file: " + file);
                                        Log.d(TAG,"업로드할 파일명: " + profile_image_file_name);
                                        TransferObserver uploadObserver = transferUtility.upload("novamarket/profile_img", profile_image_file_name, file);
                                        uploadObserver.setTransferListener(new TransferListener() {
                                            @Override
                                            public void onStateChanged(int id, TransferState state) {
                                                Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());

                                                if(state.toString().equals("COMPLETED")) {
                                                    Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());
                                                    Log.d(TAG,"finish()");
                                                    dialog.dismiss();

                                                    //회원 데이터 클라이언트에 저장
                                                    JSONObject jsonObject_update = new JSONObject();
                                                    try {
                                                        jsonObject_update.put("telephone",user_telephone);
                                                        jsonObject_update.put("nickname",nickname_modify_profile.getText().toString());
                                                        jsonObject_update.put("email",email_modify_profile.getText().toString());
                                                        jsonObject_update.put("profile_image",profile_image_file_name);

                                                        Log.d(TAG,"userData: " + String.valueOf(jsonObject_update));

                                                        editor.putString("userData", String.valueOf(jsonObject_update));
                                                        editor.apply();

                                                        finish();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

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
                                    }else {
                                        Log.d(TAG,"사진 삭제됐을때 / 그대로 일 때 => 사진 업로드 X");

                                        //회원 데이터 클라이언트에 저장
                                        JSONObject jsonObject_2 = new JSONObject();
                                        try {
                                            jsonObject_2.put("telephone",user_telephone);
                                            jsonObject_2.put("nickname",nickname_modify_profile.getText().toString());
                                            jsonObject_2.put("email",email_modify_profile.getText().toString());
                                            jsonObject_2.put("profile_image",profile_image_file_name);

                                            Log.d(TAG,"userData: " +String.valueOf(jsonObject_2));

                                            editor.putString("userData", String.valueOf(jsonObject_2));
                                            editor.apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        finish();
                                    }


                                }else {
                                    Log.d(TAG,"프로필 수정 USER 테이블에 UPDATE 실패");

                                }
                                    }
                                    });
                                    }
                                    }
                                    });

                        }else {
                            Log.d(TAG,"인터넷 연결 안됨");
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }

                
//                dialog = new ProgressDialog(ModifyProfileActivity.this);
//                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                dialog.setMessage("프로필 수정중입니다.");
//
//                dialog.show();
//
//                //현재 시간 구해서 이미지 파일명에 써먹음
//                long now = System.currentTimeMillis();
//                Date mDate = new Date(now);
//
//                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd_hhmmss");
//                String getTime = simpleDate.format(mDate);
//
//                if(file == null){
//                    Log.d(TAG,"파일명 만들기위한 과정: 프로필 이미지 파일 존재안함");
//                    profile_image_file_name = user_profile_image_url;
//                }else {
//                    Log.d(TAG,"파일명 만들기위한 과정: 프로필 이미지 파일 존재");
//                    profile_image_file_name = nickname_modify_profile.getText().toString()+"_"+getTime;
//                }
//
//                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
//                        if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
//                            Log.d(TAG,"인터넷 연결 확인");
//
//                            // get방식 파라미터 추가
//                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/modify_profile.php").newBuilder();
//                                    urlBuilder.addQueryParameter("v", "1.0"); // 예시
//                                    String url = urlBuilder.build().toString();
//
//                                    // POST 파라미터 추가
//                            RequestBody formBody = null;
//                            try {
//                                formBody = new FormBody.Builder()
//                                        .add("telephone", jsonObject.getString("telephone"))
//                                        .add("email", email_modify_profile.getText().toString())
//                                        .add("nickname", nickname_modify_profile.getText().toString())
//                                        .add("profile_image", profile_image_file_name)
//                                .build();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            // 요청 만들기
//                                    OkHttpClient client = new OkHttpClient();
//                                    Request request = new Request.Builder()
//                                    .url(url)
//                                    .post(formBody)
//                                    .build();
//
//                            // 응답 콜백
//                                    client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                    e.printStackTrace();
//                                    }
//
//                            @Override
//                            public void onResponse(Call call, final Response response) throws IOException {
//                                    if (!response.isSuccessful()) {
//                                    // 응답 실패
//                                    Log.i("tag", "응답실패");
//                                    } else {
//                                    // 응답 성공
//                                    Log.i("tag", "응답 성공");
//                            final String responseData = response.body().string();
//
//
//                                    // 서브 스레드 Ui 변경 할 경우 에러
//                                    // 메인스레드 Ui 설정
//                                    runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                    try {
//                                        if(responseData.equals("1")){//회원 프로필 수정 성공했을 때
//                                            Log.d(TAG,"회원 프로필 수정 성공");
//                                            if(file == null){//프로필 이미지 파일이 없을때
//
//                                                Log.d(TAG,"파일 업로드 위한 과정: 프로필 이미지 파일 존재안함");
//
//                                            }else {//프로필 이미지 파일을 등록했을때
//
//                                                Log.d(TAG,"파일 업로드 위한 과정: 프로필 이미지 파일 존재");
//                                                //프로필 사진 s3에 업로드한다.
//                                                AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
//                                                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));
//
//                                                TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
//                                                TransferNetworkLossHandler.getInstance(getApplicationContext());
//
//                                                Log.d(TAG,"보내는 file: " + file);
//                                                Log.d(TAG,"업로드할 파일명: " + nickname_modify_profile.getText().toString()+"_"+getTime);
//                                                TransferObserver uploadObserver = transferUtility.upload("novamarket/profile_img", nickname_modify_profile.getText().toString()+"_"+getTime, file);
//                                                uploadObserver.setTransferListener(new TransferListener() {
//                                                    @Override
//                                                    public void onStateChanged(int id, TransferState state) {
//                                                        Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());
//
//                                                        if(state.toString().equals("COMPLETED")) {
//                                                            Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());
//                                                            Log.d(TAG,"finish()");
//                                                            dialog.dismiss();
//                                                            finish();
//                                                        }
//
//                                                    }
//
//                                                    @Override
//                                                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                                                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
//                                                        int percentDone = (int)percentDonef;
//                                                        Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
//
//                                                    }
//
//                                                    @Override
//                                                    public void onError(int id, Exception ex) {
//                                                        Log.e(TAG, ex.getMessage());
//                                                    }
//                                                });
//                                            }
//
//                                            JSONObject jsonObjectPutUserData = new JSONObject();
//                                            try {
//                                                jsonObjectPutUserData.put("telephone",jsonObject.getString("telephone"));
//                                                jsonObjectPutUserData.put("nickname",nickname_modify_profile.getText().toString());
//                                                jsonObjectPutUserData.put("email",email_modify_profile.getText().toString());
//                                                jsonObjectPutUserData.put("profile_image",profile_image_file_name);
//
//                                                editor.putString("userData", String.valueOf(jsonObjectPutUserData));
//                                                editor.apply();
//
//                                                pref.getString("userData",null);
//                                                Log.d(TAG,"userData 어떻게 저장됐나: " + pref.getString("userData",null));
//                                                Log.d(TAG,"sharedPreference에 userData 새로 저장");
//
//
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }else {
//                                            Log.d(TAG,"회원 프로필 수정 실패");
//                                        }
//                                    } catch (Exception e) {
//                                    e.printStackTrace();
//                                    }
//                                    }
//                                    });
//                                    }
//                                    }
//                                    });
//
//                        }else {
//                            Log.d(TAG,"인터넷 연결 안됨");
//                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
//                        }
                
            }
        });
    }

    //툴바 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
            finish();

            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
          case  R.id.context_menu_select:
              
              //갤러리 사진 가져오기 권한 설정
              PermissionListener permissionListener = new PermissionListener()
                              {
                                  @Override
                                  public void onPermissionGranted()
                                  {
                                      Toast.makeText(ModifyProfileActivity.this, "권한이 허가된 상태입니다", Toast.LENGTH_SHORT).show();
                                      Log.e("권한", "권한 허가 상태");

                                      Intent intent = new Intent();
                                      intent.setType("image/*");
                                      intent.setAction(Intent.ACTION_PICK);
                                      startActivityForResult(intent, 0);

                                      state = 1;
                                      Log.d(TAG,"사진 변경/ state: " +state);

                                  }

                                  @Override
                                  public void onPermissionDenied(List<String> deniedPermissions)
                                  {
                                      Toast.makeText(ModifyProfileActivity.this, "권한이 거부된 상태입니다", Toast.LENGTH_SHORT).show();
                                      Log.e("권한", "권한 거부 상태");
                                  }
                              };

                              TedPermission.with(ModifyProfileActivity.this)
                                      .setPermissionListener(permissionListener)
                                      .setRationaleMessage("카메라 권한이 필요합니다!")
                                      .setDeniedMessage("지금 거부하시더라도 '설정 > 권한'에서 다시 권한을 허용하실 수 있습니다")
                                      .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                      .check();

                return true;

          case R.id.context_menu_gobasic:
                profile_image_file_name = "user.png";


                //사진 삭제시
                state = 2;
                Log.d(TAG,"사진 삭제/ state: " +state);

              return true;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    Log.d(TAG,"앨범에서 사진 가져와서 글라이드로 로드");
                    Glide.with(this)
                            .load(img)
                            .into(profile_image_modify_profile);

//                    profile_image_modify_profile.setImageBitmap(img);
                    

                    //프로필 이미지 절대 경로
                    String imgPath = getRealPathFromURI(data.getData());
                    //파일 변수에 프로필 이미지 담음
                    file = new File(imgPath);

                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
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