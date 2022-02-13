package novamarket.com.Adapter;
import novamarket.com.SellListActivity;
import novamarket.com.StartActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bumptech.glide.Glide;

import android.content.Context;
import android.content.Intent;
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

import novamarket.com.DataModels.SelectBuyerDataModels;
import novamarket.com.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SelectBuyer2Adapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
      String TAG = "RecyclerViewAdapter";

      //리사이클러뷰에 넣을 데이터 리스트
      ArrayList<SelectBuyerDataModels> dataModels;
      Context context;

      //생성자를 통하여 데이터 리스트 context를 받음
      public SelectBuyer2Adapter(Context context, ArrayList<SelectBuyerDataModels> dataModels){
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
                  .inflate(R.layout.itemview_select_buyer,parent,false);
          MyViewHolder viewHolder = new MyViewHolder(view);


          //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
          return viewHolder;
      }




      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Log.d(TAG,"onBindViewHolder");

          MyViewHolder myViewHolder = (MyViewHolder)holder;

          myViewHolder.textView_nickname_select_buyer.setText(dataModels.get(position).getBuyerNickname());

          Glide.with(context)
                  .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/" + dataModels.get(position).getBuyerImg())
                  .circleCrop()
                  .into(myViewHolder.imageView_img_select_buyer);

      }



      public class MyViewHolder extends RecyclerView.ViewHolder {
          TextView textView_nickname_select_buyer;
          ImageView imageView_img_select_buyer;
          public MyViewHolder(@NonNull View itemView) {
              super(itemView);
              textView_nickname_select_buyer =  itemView.findViewById(R.id.textView_nickname_select_buyer);

              imageView_img_select_buyer = itemView.findViewById(R.id.imageView_img_select_buyer);

              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int pos = getAdapterPosition();
                      String buyerIdx = dataModels.get(pos).getBuyerIdx();
                      String postNumber = dataModels.get(pos).getPostNumber();
                      // get방식 파라미터 추가
                      HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/update_market_select_buyer.php").newBuilder();
                      String url = urlBuilder.build().toString();

                      // POST 파라미터 추가
                       RequestBody formBody = new FormBody.Builder()
                          .add("postNumber",postNumber)
                          .add("buyer", buyerIdx)
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
                                  Intent intent = new Intent(context.getApplicationContext(),SellListActivity.class);
                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                  context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                              }
                              }
                      });

                  }
              });
          }
         }




}
