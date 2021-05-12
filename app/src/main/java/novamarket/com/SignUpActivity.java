package novamarket.com;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    String TAG = "SignUpActivity";
    private static final int REQUEST_CODE = 0;
    ImageView setting_profile_image;
    ImageView cameraIcon;
    EditText input_nickname;
    EditText input_email;
    Button nicknameCheckBtn;
    boolean canNickname;//닉네임 사용가능여부 변수
    String profile_image_file_name;



    File file;//프로필 이미지 담을 파일 변수
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //입력받은 전화번호 가져오기
        Intent intent = getIntent();
        String telephone = intent.getExtras().getString("telephone");

        Log.d(TAG,"telephone: " + telephone);

        nicknameCheckBtn = findViewById(R.id.nicknameCheckBtn);
        setting_profile_image = findViewById(R.id.setting_profile_image);
        cameraIcon = findViewById(R.id.cameraIcon);
        input_nickname = findViewById(R.id.input_nickname);
        input_email = findViewById(R.id.input_email);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_SignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("회원 가입"); // 툴바 제목 설정

        //닉네임 중복검사 버튼 클릭
        nicknameCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //닉네임값 입력널값 검사
                if(input_nickname.getText().toString().length() > 0){//입력했을때
                    // get방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/nickname_check.php").newBuilder();
                    urlBuilder.addQueryParameter("v", "1.0"); // 예시
                    String url = urlBuilder.build().toString();

                    // POST 파라미터 추가
                    RequestBody formBody = new FormBody.Builder()
                            .add("nickname", input_nickname.getText().toString())
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
                                            if(responseData.equals("1")){//사용 가능한 닉네임이면
                                                Toast.makeText(SignUpActivity.this,"사용가능한 닉네임입니다.",Toast.LENGTH_SHORT).show();
                                                canNickname = true;
                                            }else{

                                                Toast.makeText(SignUpActivity.this,"사용중인 닉네임 입니다.",Toast.LENGTH_SHORT).show();
                                                canNickname = false;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }else {//닉네임 입력 안했을때
                    Toast.makeText(SignUpActivity.this,"닉네임을 입력하세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //약관 체크박스들...
        //전체동의
        CheckBox checkBox=findViewById(R.id.checkBox);
        //필수 서비스이용약관
        CheckBox checkBox2=findViewById(R.id.checkBox2);
        //필수 개인정보
        CheckBox checkBox3=findViewById(R.id.checkBox3);
        //선택 위치정보
        CheckBox checkBox4=findViewById(R.id.checkBox4);

        //전체동의 클릭시
        //전체 true / 전체 false 로 변경
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox4.setChecked(true);
                }else {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                }
            }
        });
        //2 클릭시
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //만약 전체 클릭이 true 라면 false로 변경
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    //각 체크박스 체크 여부 확인해서  전체동의 체크박스 변경
                }else if(checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()){
                    checkBox.setChecked(true);
                }
            }
        });
        //3 클릭시
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                }else if(checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()){
                    checkBox.setChecked(true);
                }
            }
        });

        //4클릭시
        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                }else if(checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()){
                    checkBox.setChecked(true);
                }
            }
        });


        //이용약관 버튼 - 서비스
        Button btn_agr = findViewById(R.id.btn_agr);
        btn_agr.setText(R.string.underlined_text);
        btn_agr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                //다이얼로그 창의 제목 입력
                builder.setTitle("서비스 이용약관 ");
                //다이얼로그 창의 내용 입력
                builder.setMessage(R.string.app_arg); //이용약관 내용 추가  ,예시는 res-values-string 에 추가해서 사용
                //다이얼로그창에 취소 버튼 추가
                builder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println(TAG + "이용약관 닫기");
                            }
                        });
                //다이얼로그 보여주기
                builder.show();
            }
        });

        //이용약관 버튼2 - 위치정보
        Button btn_agr2 = findViewById(R.id.btn_agr2);
        btn_agr2.setText(R.string.underlined_text);
        btn_agr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                //다이얼로그 창의 제목 입력
                builder.setTitle("위치 정보 이용 약관 ");
                //다이얼로그 창의 내용 입력
                builder.setMessage(R.string.app_arg2); //이용약관 내용 추가 , 예시는 res-values-string 에 추가해서 사용
                //다이얼로그창에 취소 버튼 추가
                builder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println(TAG + "이용약관 닫기");
                            }
                        });
                //다이얼로그 보여주기
                builder.show();
            }
        });

        //이용약관 버튼3 - 개인정보
        Button btn_agr3 = findViewById(R.id.btn_agr3);
        btn_agr3.setText(R.string.underlined_text);
        btn_agr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                //다이얼로그 창의 제목 입력
                builder.setTitle("개인정보처리방침 ");
                //다이얼로그 창의 내용 입력
                builder.setMessage(R.string.app_arg3); //이용약관 내용 추가 , 예시는 res-values-string 에 추가해서 사용
                //다이얼로그창에 취소 버튼 추가
                builder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println(TAG + "이용약관 닫기");
                            }
                        });
                //다이얼로그 보여주기
                builder.show();
            }
        });

        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {//회원가입 버튼 클릭리스너
            @Override
            public void onClick(View v) {
                //현재 시간 구해서 이미지 파일명에 써먹음
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd_hhmmss");
                String getTime = simpleDate.format(mDate);

                if(file == null){
                    Log.d(TAG,"파일명 만들기위한 과정: 프로필 이미지 파일 존재안함");
                    profile_image_file_name = "user.png";
                }else {
                    Log.d(TAG,"파일명 만들기위한 과정: 프로필 이미지 파일 존재");
                    profile_image_file_name = input_nickname.getText().toString()+"_"+getTime;
                }


                if(ischecked(checkBox,checkBox2,checkBox3)){//이용약관 동의검사: 이용약관 동의 했음.
                    //닉네임, 이메일 널값검사
                    if(input_nickname.getText().toString().length() > 0 && input_email.getText().toString().length() > 0 ){//닉네임,이메일값이 널값아니면
                        if(canNickname){//닉네임 사용 가능할때
                            //회원가입 , 서버에 프로필 이미지 업로드
                            // get방식 파라미터 추가
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/sign_up.php").newBuilder();
                                    urlBuilder.addQueryParameter("v", "1.0"); // 예시
                                    String url = urlBuilder.build().toString();

                                    // POST 파라미터 추가
                                    RequestBody formBody = new FormBody.Builder()
                                    .add("telephone", telephone)
                                    .add("email", input_email.getText().toString())
                                    .add("nickname", input_nickname.getText().toString())
                                    .add("profile_image", profile_image_file_name)
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
                                    if(responseData.equals("1")){//회원가입 성공했을 때
                                        if(file == null){//프로필 이미지 파일이 없을때

                                            Log.d(TAG,"파일 업로드 위한 과정: 프로필 이미지 파일 존재안함");

                                        }else {//프로필 이미지 파일을 등록했을때

                                            Log.d(TAG,"파일 업로드 위한 과정: 프로필 이미지 파일 존재");
                                            //프로필 사진 s3에 업로드한다.
                                            AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
                                            AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                                            TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
                                            TransferNetworkLossHandler.getInstance(getApplicationContext());

                                            Log.d(TAG,"보내는 file: " + file);
                                            TransferObserver uploadObserver = transferUtility.upload("novamarket/profile_img", input_nickname.getText().toString()+"_"+getTime, file);
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

                                        
                                        //자동로그인과 전화번호를 SharedPreferences에 저장한다.
                                        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putBoolean("islogined",true);
                                        editor.putString("telephone",telephone);
                                        editor.apply();
                                        //메인액티비티로 이동
                                        startActivityC(MainActivity.class);
                                        
                                        
                                    }else {
                                        Toast.makeText(SignUpActivity.this,"회원가입에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                    }
                                    }
                                    });
                                    }
                                    }
                                    });

                        }else {//닉네임 이미 사용중일때,
                            Toast.makeText(SignUpActivity.this,"닉네임을 다시 입력하세요.",Toast.LENGTH_SHORT).show();
                        }
                    }else {//둘중 하나라도 입력 안했으면
                        Toast.makeText(SignUpActivity.this,"회원 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }


                }else {//이용약관 동의검사: 이용약관 동의 안했음.
                    Toast.makeText(SignUpActivity.this,"약관에 동의해주세요",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //툴바 동작
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

    public void setting_profile_image(View view){//프로필 사진 설정 이미지 클릭했을때 메소드
        //갤러리 권한 설정
        PermissionListener permissionListener = new PermissionListener() {
                            @Override
                            public void onPermissionGranted()
                            {
                                Toast.makeText(SignUpActivity.this, "권한이 허가된 상태입니다", Toast.LENGTH_SHORT).show();
                                Log.e("권한", "권한 허가 상태");
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(intent, REQUEST_CODE);


                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions)
                            {
                                Toast.makeText(SignUpActivity.this, "권한이 거부된 상태입니다", Toast.LENGTH_SHORT).show();
                                Log.e("권한", "권한 거부 상태");
                            }
        };

                        TedPermission.with(SignUpActivity.this)
                                .setPermissionListener(permissionListener)
                                .setRationaleMessage("카메라 권한이 필요합니다")
                                .setDeniedMessage("지금 거부하시더라도 '설정 > 권한'에서 다시 권한을 허용하실 수 있습니다")
                                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();


    }

    private boolean ischecked(CheckBox checkBox,CheckBox checkBox2,CheckBox checkBox3) {

        if (checkBox.isChecked() || checkBox2.isChecked() && checkBox3.isChecked()) {
            return true;
        }//회원가입시 체크박스 확인할때 사용하세요.
        //체크박스 확인
        else {
            Toast.makeText(getApplicationContext(), "이용약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    setting_profile_image.setImageBitmap(img);
                    cameraIcon.setVisibility(View.INVISIBLE);

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