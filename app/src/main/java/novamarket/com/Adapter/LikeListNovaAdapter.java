package novamarket.com.Adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import novamarket.com.NovaActivity;
import novamarket.com.NovaDataModel;
import novamarket.com.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class LikeListNovaAdapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
      String TAG = "RecyclerViewAdapter";

      //리사이클러뷰에 넣을 데이터 리스트
      ArrayList<NovaDataModel> dataModels;
      Context context;

      //생성자를 통하여 데이터 리스트 context를 받음
      public LikeListNovaAdapter(Context context, ArrayList<NovaDataModel> dataModels){
          this.dataModels = dataModels;
          this.context = context;
      }

      @Override
      public int getItemCount() {
          //데이터 리스트의 크기를 전달해주어야 함
          return dataModels.size();
      }


      @NonNull
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          Log.d(TAG,"onCreateViewHolder");

          //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
          View view = LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.itemview_nova,parent,false);
          MyViewHolder viewHolder = new MyViewHolder(view);


          //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
          return viewHolder;
      }




      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Log.d(TAG,"onBindViewHolder");

          MyViewHolder myViewHolder = (MyViewHolder)holder;

          myViewHolder.subject.setText(dataModels.get(position).getSubject());
          myViewHolder.contents.setText(dataModels.get(position).getContents());
          myViewHolder.nickname.setText(dataModels.get(position).getNickname());
          myViewHolder.time.setText(dataModels.get(position).getTime());
          myViewHolder.likes.setText(dataModels.get(position).getLikes());
          myViewHolder.replyCounts.setText(dataModels.get(position).getReplyCounts());

          Log.d(TAG,"이미지 경로: " + dataModels.get(position).getImg_path());
          if(dataModels.get(position).getImg_path().length() < 3){
              myViewHolder.imageView.setVisibility(View.GONE);
          }else {
              Glide.with(context)
                      .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/nova_post/" + dataModels.get(position).getImg_path())
                      .into(myViewHolder.imageView);
          }
      }



      public class MyViewHolder extends RecyclerView.ViewHolder {
          TextView subject,contents,nickname,time,likes,replyCounts;
          ImageView imageView;
          public MyViewHolder(@NonNull View itemView) {
              super(itemView);

              subject = itemView.findViewById(R.id.subject_nova);
              contents = itemView.findViewById(R.id.contents_nova);
              nickname = itemView.findViewById(R.id.nickname_nova);
              time = itemView.findViewById(R.id.time_nova);
              likes = itemView.findViewById(R.id.likes_nova);
              replyCounts = itemView.findViewById(R.id.replyCount_nova);

              imageView = itemView.findViewById(R.id.imageView_nova);

              //아이템뷰 클릭했을때
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int pos = getAdapterPosition();
                      String postNumber = dataModels.get(pos).getIdx();
                      Log.d(TAG,"postNumber: " + postNumber);

                      Intent intent = new Intent(context,NovaActivity.class);
                      intent.putExtra("postNumber",postNumber);

                      context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                  }
              });

              //롱 클릭하면 게시글 관심목록에서 제거 할수있는 옵션
              itemView.setOnLongClickListener(new View.OnLongClickListener() {
                  @Override
                  public boolean onLongClick(View v) {

                      int pos = getAdapterPosition();

                      //현재 사용중인 사용자 누구인지 체크
                      SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                      String userData = pref.getString("userData",null);


                      AlertDialog.Builder builder = new AlertDialog.Builder(context);
                      builder.setTitle("관심목록에서 삭제하시겠어요?");
                      builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              int pos = getAdapterPosition();
                              Log.d(TAG,"예를 선택했습니다.");

                              String userIdx = null;
                              String postNumber;

                              postNumber = dataModels.get(pos).getIdx();

                              try {
                                  JSONObject jsonObject = new JSONObject(userData);
                                  userIdx = jsonObject.getString("idx");
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }



                              //좋아요 DB 삭제
                              // get방식 파라미터 추가
                              HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/delete_nova_like.php").newBuilder();
                              String url = urlBuilder.build().toString();

                              // POST 파라미터 추가
                              RequestBody formBody = new FormBody.Builder()
                                      .add("postNumber", postNumber)
                                      .add("userNumber", userIdx)
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
                                          Log.d(TAG,"responseData: "  +responseData);
                                          // 서브 스레드 Ui 변경 할 경우 에러
                                          // 메인스레드 Ui 설정
                                          runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  try {
                                                      Toast.makeText(context,"관심목록에서 삭제되었습니다.",Toast.LENGTH_SHORT).show();

                                                      //실제 리사이클러뷰에서도 제거
                                                      dataModels.remove(pos);
                                                      notifyItemRemoved(pos);
                                                      notifyItemRangeChanged(pos,dataModels.size());

                                                  } catch (Exception e) {
                                                      e.printStackTrace();
                                                  }
                                              }
                                          });
                                      }
                                  }
                              });

                          }
                      });
                      builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              Log.d(TAG,"아니오를 선택했습니다.");
                          }
                      });
                      builder.show();

                      return false;
                  }
              });


          }
         }
}
