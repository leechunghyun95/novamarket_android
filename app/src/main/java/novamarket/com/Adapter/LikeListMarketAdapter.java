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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
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

import org.json.JSONException;
import org.json.JSONObject;

import novamarket.com.DataModels.SellListDataModels;
import novamarket.com.MarketActivity;
import novamarket.com.R;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class LikeListMarketAdapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
      String TAG = "LikeListMarketAdapter";

      //리사이클러뷰에 넣을 데이터 리스트
      ArrayList<SellListDataModels> dataModels;
      Context context;

      //생성자를 통하여 데이터 리스트 context를 받음
      public LikeListMarketAdapter(Context context, ArrayList<SellListDataModels> dataModels){
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
                  .inflate(R.layout.itemview_market,parent,false);
          MyViewHolder viewHolder = new MyViewHolder(view);


          //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
          return viewHolder;
      }




      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Log.d(TAG,"onBindViewHolder");

          MyViewHolder myViewHolder = (MyViewHolder)holder;

          myViewHolder.textView_title_market.setText(dataModels.get(position).getTitle());
          myViewHolder.textView_time_market.setText(dataModels.get(position).getDate());

          if(dataModels.get(position).getPrice().equals("")){
              myViewHolder.textView_price_market.setText("");
          }else {
              myViewHolder.textView_price_market.setText(dataModels.get(position).getPrice()+" 원");
          }

          Glide.with(context)
                  .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/market_post/" + dataModels.get(position).getImg())
                  .into(myViewHolder.imageView_market_recyclerView);

      }



      public class MyViewHolder extends RecyclerView.ViewHolder {
          TextView textView_title_market,textView_time_market,textView_price_market;
          ImageView imageView_market_recyclerView;
          public MyViewHolder(@NonNull View itemView) {
              super(itemView);
              textView_title_market =  itemView.findViewById(R.id.textView_title_market);
              textView_time_market =  itemView.findViewById(R.id.textView_time_market);
              textView_price_market =  itemView.findViewById(R.id.textView_price_market);

              imageView_market_recyclerView = itemView.findViewById(R.id.imageView_market_recyclerView);
              
              
              //클릭하면 게시글로 이동
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int pos = getAdapterPosition();
                      String postNumber = dataModels.get(pos).getPostNumber();

                      Intent intent = new Intent(context, MarketActivity.class);
                      intent.putExtra("idx",postNumber);

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

                              postNumber = dataModels.get(pos).getPostNumber();

                              try {
                                  JSONObject jsonObject = new JSONObject(userData);
                                  userIdx = jsonObject.getString("idx");
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }
                              

                          
                              //좋아요 DB 삭제
                              // get방식 파라미터 추가
                              HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.24.112/delete_market_like.php").newBuilder();
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
