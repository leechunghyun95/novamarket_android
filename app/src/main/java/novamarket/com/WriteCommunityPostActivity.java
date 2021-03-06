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

    //?????? ????????? ????????? ??????????????????
    ArrayList<Uri> uriList = new ArrayList<>();     // ???????????? uri??? ?????? ArrayList ??????

    MultiImageAdapter_community adapter;  // ????????????????????? ???????????? ?????????

    //??? ??????
    Toolbar toolbar;
    Button done_WriteCommunity;
    TextView selected_subject,countphoto,countLocation;
    CheckBox checkBox_groupChat;
    RecyclerView recyclerView_location,recyclerView_img;
    EditText contents;
    ImageView uploadPhoto,uploadLocation;
    LinearLayout select_category_btn_com;

    public static Context context_com;

    int subject = 0;//????????? ?????? ??????

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
            Log.d(TAG,"????????????: " + userIdx);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setContentView();//?????? ?????????

        reStoreComPost();//???????????? ????????????

        context_com = this;

        // ?????? ??????
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ??????, ???????????? true??? ?????? ???????????? ??????
        getSupportActionBar().setTitle("??????????????? ?????????"); // ?????? ?????? ??????


        //????????? ?????? ????????????
        select_category_btn_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"????????? ?????? ?????? ????????????");
                Intent intent = new Intent(WriteCommunityPostActivity.this,SubjectSelectActivity.class);
                intent.putExtra("subject",subject);
                Log.d(TAG,"?????? ?????????????????? ?????? ??????: " + subject);
                startActivityForResult(intent,1111);
            }
        });

        //?????? ?????? ??????
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"?????? ????????? ?????? ??????");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//?????????????????? ????????? ??? ????????? ??????
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2222);
            }
        });

        //?????? ?????? ??????
        uploadLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"?????? ????????? ?????? ??????");
                Intent intent = new Intent(WriteCommunityPostActivity.this,SearchLocationActivity.class);
                startActivityForResult(intent,3333);
            }
        });



        //?????? ??????
        done_WriteCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"???????????? ??????");
                if(subject != 0 && contents.getText().toString().length() > 0 ){//?????? , ?????? ?????? ?????? ????????????

                    SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd_HHmmss");
                    Date time = new Date();
                    String timeNow = format1.format(time);
                    Log.d(TAG,"????????????: " + timeNow);


                    ArrayList<String> imgList = new ArrayList<>();// ????????? ???????????? ?????? ArrayList ??????
                    for (int i = 0; i < uriList.size(); i++){

                        Log.d(TAG,"uri: " + uriList.get(i));
                        String[] bits = uriList.get(i).toString().split("/");
                        String ??????????????? = bits[bits.length-1];
                        Log.d(TAG,"?????????: " + ???????????????);

                        String fileName = ??????????????? +"_"+ timeNow;
                        Log.d(TAG,"???????????? fileName: " + fileName);

                        imgList.add(fileName); //mysql??? ??????????????? ?????? ????????? ????????? ??????

                        String imgPath = getRealPathFromURI(uriList.get(i));
                        Log.d(TAG,"????????? RealPathFromURI: " + imgPath);
                        File file = new File(imgPath);

                        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARO4SECGUZ76ZHGXW", "N4lS5uC+wAa6mGIBF8HJXaNdfFpTVnToPYyBuWPT");
                        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
                        TransferNetworkLossHandler.getInstance(getApplicationContext());

                        Log.d(TAG,"????????? file: " + fileName);
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

                    if(imgList.isEmpty()){//????????? ????????? ????????????????????? ????????? ???????????? ???????????? ????????? ??????
                        Log.d(TAG,"????????? ????????? ??????????????? ????????? ?????? ??????");
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

                    // get?????? ???????????? ??????
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/write_nova_post.php").newBuilder();
                    urlBuilder.addQueryParameter("v", "1.0"); // ??????
                    String url = urlBuilder.build().toString();

                    // POST ???????????? ??????
                    RequestBody formBody = new FormBody.Builder()
                            .add("novaObject", novaObject.toString())
                            .add("imgList", String.valueOf(imgList))
                            .build();


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
                                        try {
                                            Toast.makeText(WriteCommunityPostActivity.this,   "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
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


                }else {//??????,?????? ??????????????????
                    Toast.makeText(WriteCommunityPostActivity.this,"??????, ????????? ????????????????????????.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2222) {

            if (data == null) {
//                Toast.makeText(this,"???????????? ???????????? ???????????????.",Toast.LENGTH_SHORT).show();
            } else {//???????????? ???????????? ????????? ??????
                if (data.getClipData() == null) {//???????????? ????????? ????????? ??????
                    if(uriList.size() + 1 > 10){//????????? ???????????? ??? 10?????? ?????????
                        Toast.makeText(this, "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.e("single choice: ", String.valueOf(data.getData()));
                        Uri imageUri = data.getData();
                        uriList.add(imageUri);

                        Log.d(TAG, "uriList.size():" + uriList.size());
                    }


                } else {//???????????? ????????? ????????? ??????
                    ClipData clipData = data.getClipData();
                    Log.e("clipData: ", String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 10) {//????????? ???????????? 11??? ????????? ??????
                        Toast.makeText(this, "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "multiple choice");
                        if(uriList.size() + clipData.getItemCount() > 10){//????????? ?????????????????? ???????????? ????????? ????????? 10?????? ????????????
                            Log.d(TAG,"????????? ?????????????????? ???????????? ????????? ????????? 10?????? ????????????");
                            Toast.makeText(this, "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                            int pc = uriList.size() + clipData.getItemCount();
                            Log.d(TAG,"??? ?????????: " + pc);
                        }else {
                            Log.d(TAG,"????????? ?????????????????? ???????????? ????????? ????????? 10??? ????????? ??????");
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();//????????? ??????????????? uri??? ????????????.

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
                        if (adapter.getItemCount() == 10) {//?????? 10?????? ???????????? ?????? ???????????????
                            countphoto.setTextColor(Color.parseColor("#ff0000"));
                        }

                        recyclerView_img.setAdapter(adapter);//????????????????????? ????????? ??????
                        recyclerView_img.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));//?????????????????? ?????? ????????? ??????
                    }
                }
            }

        }else if(requestCode == 1111){//????????? ????????????
            Log.d(TAG,"????????????1");
            if(resultCode == RESULT_OK){

                subject = data.getIntExtra("subject",0);
                Log.d(TAG,"????????? ?????? ??????: " + subject);

                switch(subject){
                    case  1:
                        selected_subject.setText("????????????");
                        checkBox_groupChat.setVisibility(View.VISIBLE);//?????? ????????? ????????? ???????????? ????????? ??????
                        break;

                    case 2:
                        selected_subject.setText("????????????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  3:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????

                        break;
                    case  4:
                        selected_subject.setText("???????????????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  5:
                        selected_subject.setText("????????????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  6:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  7:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  8:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;

                }
            }

        }
    }

    public void setContentView(){//?????? ?????????
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

    //????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
            case android.R.id.home:{ //toolbar??? back??? ????????? ??? ??????
            // todo
                tmpSaveComPost();//????????? ????????????
                finish();

            return true;
            }
            }
            return super.onOptionsItemSelected(item);
            }

    public void tmpSaveComPost(){//??????????????? ????????? ???????????? ?????????
        if(contents.getText().length() > 0){//?????? ????????? ????????????
            Log.d(TAG,"??????????????? ???????????? ??????.");


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

                Log.d(TAG,"????????????: " + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences pref = getSharedPreferences("???????????????", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("????????????",jsonObject.toString());
            editor.apply();

            Toast.makeText(this,"???????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
//

        }else {
            Log.d(TAG,"??????????????? ????????? ??????");
        }
    }

    public void reStoreComPost(){
        //??????????????? ????????? ???????????? ??? ????????????
        SharedPreferences pref2 = getSharedPreferences("???????????????", Activity.MODE_PRIVATE);
        String ???????????? = pref2.getString("????????????",null);
        Log.d(TAG,"????????????: " + ????????????);
        if(???????????? == null){
            Log.d(TAG,"????????? ????????? ??????");
        }else {
            Log.d(TAG, "????????? ????????? ??????");
            try {
                JSONObject jsonObject = new JSONObject(????????????);
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

                //??????????????? ?????????
                switch(subject){
                    case  1:
                        selected_subject.setText("????????????");
                        checkBox_groupChat.setVisibility(View.VISIBLE);//?????? ????????? ????????? ???????????? ????????? ??????
                        break;

                    case 2:
                        selected_subject.setText("????????????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  3:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????

                        break;
                    case  4:
                        selected_subject.setText("???????????????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  5:
                        selected_subject.setText("????????????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  6:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  7:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;
                    case  8:
                        selected_subject.setText("??????");
                        checkBox_groupChat.setVisibility(View.GONE);//?????? ????????? ????????? ???????????? ??? ????????? ??????
                        break;

                }


            }catch (Exception e){

            }
        }
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


    //uri??? ????????? ????????? ???????????? ?????????
    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
    }
}

