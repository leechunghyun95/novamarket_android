package novamarket.com;

import com.airbnb.lottie.L;
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
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class marketRecyclerViewAdapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
      String TAG = "marketRecyclerViewAdapter";

      //리사이클러뷰에 넣을 데이터 리스트
      ArrayList<MarketDataModel> dataModels;
      Context context;

      //생성자를 통하여 데이터 리스트 context를 받음
      public marketRecyclerViewAdapter(Context context, ArrayList<MarketDataModel> dataModels){
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
          myViewHolder.textView_time_market.setText(dataModels.get(position).getTime());
          myViewHolder.textView_price_market.setText(dataModels.get(position).getPrice());

          Log.d(TAG,"이미지 경로: " + dataModels.get(position).getImg_path());
          if(dataModels.get(position).getImg_path().length() < 3){
              myViewHolder.imageView.setVisibility(View.GONE);
          }else {
              Glide.with(context)
                      .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/market_post/" + dataModels.get(position).getImg_path())
                      .into(myViewHolder.imageView);
          }

      }

      //필터
      public void fileterList(ArrayList<MarketDataModel> filteredList){
            dataModels = filteredList;
            notifyDataSetChanged();
      }



      public class MyViewHolder extends RecyclerView.ViewHolder {
          ImageView imageView;
          TextView textView_title_market;
          TextView textView_time_market;
          TextView textView_price_market;

          public MyViewHolder(@NonNull View itemView) {
              super(itemView);
              imageView = itemView.findViewById(R.id.imageView_market_recyclerView);
              textView_title_market =  itemView.findViewById(R.id.textView_title_market);
              textView_time_market = itemView.findViewById(R.id.textView_time_market);
              textView_price_market = itemView.findViewById(R.id.textView_price_market);

              //아이템뷰 클릭
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int pos = getAdapterPosition();
                      dataModels.get(pos);
                      Log.d(TAG,"dataModels.get(pos): " + dataModels.get(pos).getTitle());

                      startActivityString(MarketActivity.class,"idx",dataModels.get(pos).getIdx());
                  }
              });
          }


          // 액티비티 전환 함수
          // 인텐트 액티비티 전환함수
          public void startActivityC(Class c) {
              Intent intent = new Intent(context.getApplicationContext(), c);
              context.startActivity(intent);
              // 화면전환 애니메이션 없애기
//              context.overridePendingTransition(0, 0);
          }
          // 인텐트 화면전환 하는 함수
          // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
          public void startActivityflag(Class c) {
              Intent intent = new Intent(context.getApplicationContext(), c);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              context.startActivity(intent);
              // 화면전환 애니메이션 없애기
//              overridePendingTransition(0, 0);
          }

          // 문자열 인텐트 전달 함수
          public void startActivityString(Class c, String name , String sendString) {
              Intent intent = new Intent(context.getApplicationContext(), c);

              //테스트
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

              intent.putExtra(name, sendString);
              context.startActivity(intent);
              // 화면전환 애니메이션 없애기
//              overridePendingTransition(0, 0);
          }

          // 백스택 지우고 새로 만들어 전달
          public void startActivityNewTask(Class c){
              Intent intent = new Intent(context.getApplicationContext(), c);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              context.startActivity(intent);
              // 화면전환 애니메이션 없애기
//              overridePendingTransition(0, 0);
          }

         }

}
