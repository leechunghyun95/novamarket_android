package novamarket.com.Adapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bumptech.glide.Glide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Delete;

import novamarket.com.CustomDialog;
import novamarket.com.CustomDialog_reply;
import novamarket.com.DataModels.ReplyDataModels;
import novamarket.com.NovaActivity;
import novamarket.com.R;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class ReplyAdapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
      String TAG = "RecyclerViewAdapter";


      //리사이클러뷰에 넣을 데이터 리스트
      ArrayList<ReplyDataModels> dataModels;
      Context context;

      //생성자를 통하여 데이터 리스트 context를 받음
      public ReplyAdapter(Context context, ArrayList<ReplyDataModels> dataModels){
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
                  .inflate(R.layout.itemview_reply,parent,false);
          MyViewHolder viewHolder = new MyViewHolder(view);


          //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
          return viewHolder;
      }




      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Log.d(TAG,"onBindViewHolder");

          MyViewHolder myViewHolder = (MyViewHolder)holder;

          myViewHolder.textView_contents_reply.setText(dataModels.get(position).getReply());
          myViewHolder.textView_writer_reply.setText(dataModels.get(position).getWriter());
          myViewHolder.textView_time_reply.setText(dataModels.get(position).getTime());
          //작성자 프로필 이미지
          Glide.with(context)
                  .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/"+dataModels.get(position).getProfileImage())
                  .circleCrop()
                  .into(myViewHolder.imageView_reply_profile);


      }



      public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
          TextView textView_writer_reply, textView_time_reply, textView_contents_reply;
          ImageView imageView_reply_profile, reply_btn;

          public MyViewHolder(@NonNull View itemView) {
              super(itemView);
              textView_writer_reply = itemView.findViewById(R.id.textView_writer_reply);
              textView_contents_reply = itemView.findViewById(R.id.textView_contents_reply);
              textView_time_reply = itemView.findViewById(R.id.textView_time_reply);
              imageView_reply_profile = itemView.findViewById(R.id.imageView_reply_profile);
              reply_btn = itemView.findViewById(R.id.reply_btn);

              reply_btn.setOnCreateContextMenuListener(this);

          }

          @Override
          public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
              int pos = getAdapterPosition();
              String replyWriter = dataModels.get(pos).getWriter();
              Log.d(TAG, "댓글 작성자: " + replyWriter);

              NovaActivity novaActivity = new NovaActivity();

              String userNickname = novaActivity.userNickname;
              Log.d(TAG, "현재 사용자: " + userNickname);

              if (replyWriter.equals(userNickname)) {//사용자와 댓글 작성자 같을때
                  Log.d(TAG, "사용자와 댓글 작성자 같을때");
                  MenuItem Delete = menu.add(Menu.NONE, 1001, 1, "삭제하기");
                  Delete.setOnMenuItemClickListener(onEditMenu);
              } else {//사용자와 댓글 작성자 다를때
                  Log.d(TAG, "사용자와 댓글 작성자 다를때");
                  MenuItem Report = menu.add(Menu.NONE, 1002, 2, "신고하기");
                  Report.setOnMenuItemClickListener(onEditMenu);
              }
          }

          MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
              @Override
              public boolean onMenuItemClick(MenuItem item) {
                  switch(item.getItemId()){
                    case  1001:


                        String reply = dataModels.get(getAdapterPosition()).getReply();


                        //댓글 서버에서 가져올때 쿼리 잘못써서 댓글 인덱스를 못 가져오고 대신 사용자인덱스로 가져와 짐 -> 일단 댓글 텍스트 일치하는 것 삭제로 시연 마무리
                        //서버 통신 - 댓글 삭제
                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/reply_delete.php").newBuilder();
                                urlBuilder.addQueryParameter("reply", reply); // 댓글 번호
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
                        Log.d(TAG,"responseData: " + responseData);
                                // 서브 스레드 Ui 변경 할 경우 에러
                                // 메인스레드 Ui 설정
                                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                } catch (Exception e) {
                                e.printStackTrace();
                                }
                                }
                                });
                                }
                                }
                                });

                        //리사이클러뷰 삭제
                        dataModels.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(),dataModels.size());


                        break;

                    case 1002:
                      break;
                  }
                  return true;
              }
          };
      }


}

