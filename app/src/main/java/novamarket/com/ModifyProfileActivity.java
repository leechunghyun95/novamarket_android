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

    //????????? userData?????? ?????? ?????? ??????
    String user_nickname = "?????????";
    String user_email="?????????";
    String user_profile_image_url="user.png";
    String user_telephone;
    
    //?????? ?????? ?????? ??????
    File file;

    //?????? ?????? ???
    String profile_image_file_name;

    //???????????? json???????????? ???????????? ??????
    JSONObject jsonObject;

    EditText nickname_modify_profile;
    EditText email_modify_profile;

    ProgressDialog dialog;
    
    //????????? ???????????? ????????? ???????????? ???????????? ???
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        String userData = pref.getString("userData",null);
        Log.d(TAG,"????????? userData: " + userData);

        try {
            jsonObject = new JSONObject(userData);
            user_telephone = jsonObject.getString("telephone");
            user_nickname = jsonObject.getString("nickname");
            user_email = jsonObject.getString("email");
            user_profile_image_url = jsonObject.getString("profile_image");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ?????? ??????
        Toolbar toolbar = findViewById(R.id.toolbar_ModifyProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ??????, ???????????? true??? ?????? ???????????? ??????
        getSupportActionBar().setTitle("????????? ??????"); // ?????? ?????? ??????

        //??? ?????????
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
                Log.d(TAG,"?????? ?????? ?????? ??????");

                //?????? ?????? ????????? ????????? ???????????? ?????????
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd_hhmmss");
                String getTime = simpleDate.format(mDate);

                if(state == 0){
                    Log.d(TAG,"????????? ????????? ???????????? ??????");
                    profile_image_file_name = user_profile_image_url;
                    Log.d(TAG,"profile_image_file_name: " +profile_image_file_name);

                }else if(state == 1){
                    Log.d(TAG,"????????? ????????? ????????? ??????");
                    profile_image_file_name = user_nickname+"_"+getTime;
                    Log.d(TAG,"profile_image_file_name: " +profile_image_file_name);

                }else {
                    Log.d(TAG,"????????? ????????? ????????? ??????");
                    profile_image_file_name = "user.png";
                    Log.d(TAG,"profile_image_file_name: " +profile_image_file_name);
                    
                }

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                        if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                            Log.d(TAG,"????????? ?????? ??????");

                            // get?????? ???????????? ??????
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/modify_profile.php").newBuilder();
                                    urlBuilder.addQueryParameter("v", "1.0"); // ??????
                                    String url = urlBuilder.build().toString();

                                    // POST ???????????? ??????
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

                            // ?????? ?????????
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                    .url(url)
                                    .post(formBody)
                                    .build();

                            // ?????? ??????
                                    client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                    // ?????? ??????
                                    Log.i("tag", "????????????");
                                    } else {
                                    // ?????? ??????
                                    Log.i("tag", "?????? ??????");
                            final String responseData = response.body().string();


                                    // ?????? ????????? Ui ?????? ??? ?????? ??????
                                    // ??????????????? Ui ??????
                                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(responseData.equals("1")){
                                    Log.d(TAG,"????????? ?????? USER ???????????? UPDATE ??????");

                                    if(state == 1){
                                        Log.d(TAG,"?????? ??????????????? => ?????? ?????????");

                                        dialog = new ProgressDialog(ModifyProfileActivity.this);
                                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        dialog.setMessage("????????? ??????????????????.");

                                        dialog.show();

                                        //????????? ?????? s3??? ???????????????.
                                        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
                                        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                                        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
                                        TransferNetworkLossHandler.getInstance(getApplicationContext());

                                        Log.d(TAG,"????????? file: " + file);
                                        Log.d(TAG,"???????????? ?????????: " + profile_image_file_name);
                                        TransferObserver uploadObserver = transferUtility.upload("novamarket/profile_img", profile_image_file_name, file);
                                        uploadObserver.setTransferListener(new TransferListener() {
                                            @Override
                                            public void onStateChanged(int id, TransferState state) {
                                                Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());

                                                if(state.toString().equals("COMPLETED")) {
                                                    Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());
                                                    Log.d(TAG,"finish()");
                                                    dialog.dismiss();

                                                    //?????? ????????? ?????????????????? ??????
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
                                        Log.d(TAG,"?????? ??????????????? / ????????? ??? ??? => ?????? ????????? X");

                                        //?????? ????????? ?????????????????? ??????
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
                                    Log.d(TAG,"????????? ?????? USER ???????????? UPDATE ??????");

                                }
                                    }
                                    });
                                    }
                                    }
                                    });

                        }else {
                            Log.d(TAG,"????????? ?????? ??????");
                        Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }

                
//                dialog = new ProgressDialog(ModifyProfileActivity.this);
//                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                dialog.setMessage("????????? ??????????????????.");
//
//                dialog.show();
//
//                //?????? ?????? ????????? ????????? ???????????? ?????????
//                long now = System.currentTimeMillis();
//                Date mDate = new Date(now);
//
//                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd_hhmmss");
//                String getTime = simpleDate.format(mDate);
//
//                if(file == null){
//                    Log.d(TAG,"????????? ??????????????? ??????: ????????? ????????? ?????? ????????????");
//                    profile_image_file_name = user_profile_image_url;
//                }else {
//                    Log.d(TAG,"????????? ??????????????? ??????: ????????? ????????? ?????? ??????");
//                    profile_image_file_name = nickname_modify_profile.getText().toString()+"_"+getTime;
//                }
//
//                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
//                        if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
//                            Log.d(TAG,"????????? ?????? ??????");
//
//                            // get?????? ???????????? ??????
//                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/modify_profile.php").newBuilder();
//                                    urlBuilder.addQueryParameter("v", "1.0"); // ??????
//                                    String url = urlBuilder.build().toString();
//
//                                    // POST ???????????? ??????
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
//                            // ?????? ?????????
//                                    OkHttpClient client = new OkHttpClient();
//                                    Request request = new Request.Builder()
//                                    .url(url)
//                                    .post(formBody)
//                                    .build();
//
//                            // ?????? ??????
//                                    client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                    e.printStackTrace();
//                                    }
//
//                            @Override
//                            public void onResponse(Call call, final Response response) throws IOException {
//                                    if (!response.isSuccessful()) {
//                                    // ?????? ??????
//                                    Log.i("tag", "????????????");
//                                    } else {
//                                    // ?????? ??????
//                                    Log.i("tag", "?????? ??????");
//                            final String responseData = response.body().string();
//
//
//                                    // ?????? ????????? Ui ?????? ??? ?????? ??????
//                                    // ??????????????? Ui ??????
//                                    runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                    try {
//                                        if(responseData.equals("1")){//?????? ????????? ?????? ???????????? ???
//                                            Log.d(TAG,"?????? ????????? ?????? ??????");
//                                            if(file == null){//????????? ????????? ????????? ?????????
//
//                                                Log.d(TAG,"?????? ????????? ?????? ??????: ????????? ????????? ?????? ????????????");
//
//                                            }else {//????????? ????????? ????????? ???????????????
//
//                                                Log.d(TAG,"?????? ????????? ?????? ??????: ????????? ????????? ?????? ??????");
//                                                //????????? ?????? s3??? ???????????????.
//                                                AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
//                                                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));
//
//                                                TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
//                                                TransferNetworkLossHandler.getInstance(getApplicationContext());
//
//                                                Log.d(TAG,"????????? file: " + file);
//                                                Log.d(TAG,"???????????? ?????????: " + nickname_modify_profile.getText().toString()+"_"+getTime);
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
//                                                Log.d(TAG,"userData ????????? ????????????: " + pref.getString("userData",null));
//                                                Log.d(TAG,"sharedPreference??? userData ?????? ??????");
//
//
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }else {
//                                            Log.d(TAG,"?????? ????????? ?????? ??????");
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
//                            Log.d(TAG,"????????? ?????? ??????");
//                        Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
//                        }
                
            }
        });
    }

    //?????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar??? back??? ????????? ??? ??????
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
              
              //????????? ?????? ???????????? ?????? ??????
              PermissionListener permissionListener = new PermissionListener()
                              {
                                  @Override
                                  public void onPermissionGranted()
                                  {
                                      Toast.makeText(ModifyProfileActivity.this, "????????? ????????? ???????????????", Toast.LENGTH_SHORT).show();
                                      Log.e("??????", "?????? ?????? ??????");

                                      Intent intent = new Intent();
                                      intent.setType("image/*");
                                      intent.setAction(Intent.ACTION_PICK);
                                      startActivityForResult(intent, 0);

                                      state = 1;
                                      Log.d(TAG,"?????? ??????/ state: " +state);

                                  }

                                  @Override
                                  public void onPermissionDenied(List<String> deniedPermissions)
                                  {
                                      Toast.makeText(ModifyProfileActivity.this, "????????? ????????? ???????????????", Toast.LENGTH_SHORT).show();
                                      Log.e("??????", "?????? ?????? ??????");
                                  }
                              };

                              TedPermission.with(ModifyProfileActivity.this)
                                      .setPermissionListener(permissionListener)
                                      .setRationaleMessage("????????? ????????? ???????????????!")
                                      .setDeniedMessage("?????? ????????????????????? '?????? > ??????'?????? ?????? ????????? ???????????? ??? ????????????")
                                      .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                      .check();

                return true;

          case R.id.context_menu_gobasic:
                profile_image_file_name = "user.png";


                //?????? ?????????
                state = 2;
                Log.d(TAG,"?????? ??????/ state: " +state);

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

                    Log.d(TAG,"???????????? ?????? ???????????? ??????????????? ??????");
                    Glide.with(this)
                            .load(img)
                            .into(profile_image_modify_profile);

//                    profile_image_modify_profile.setImageBitmap(img);
                    

                    //????????? ????????? ?????? ??????
                    String imgPath = getRealPathFromURI(data.getData());
                    //?????? ????????? ????????? ????????? ??????
                    file = new File(imgPath);

                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_LONG).show();
            }
        }
    }

    //uri??? ????????? ????????? ???????????? ?????????
    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
    }

    // ???????????? ?????? ??????
    // ????????? ???????????? ????????????
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }
    // ????????? ???????????? ?????? ??????
    // FLAG_ACTIVITY_CLEAR_TOP = ????????? ???????????? ?????? ?????? ???????????? ?????????.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }

    // ????????? ????????? ?????? ??????
    public void startActivityString(Class c, String name , String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }

    // ????????? ????????? ?????? ????????? ??????
    public void startActivityNewTask(Class c){
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }


}