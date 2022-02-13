package novamarket.com.Adapter;



import com.bumptech.glide.Glide;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import novamarket.com.DataModels.ChatDataModels;
import novamarket.com.R;
import novamarket.com.ShowLocationActivity;

public class ChatAdapter extends RecyclerView.Adapter{
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
      String TAG = "ChatAdapter";

      //리사이클러뷰에 넣을 데이터 리스트
      ArrayList<ChatDataModels> dataModels;
      Context context;


      //생성자를 통하여 데이터 리스트 context를 받음
      public ChatAdapter(Context context, ArrayList<ChatDataModels> dataModels){
          this.dataModels = dataModels;
          this.context = context;
      }


      @Override
      public int getItemViewType(int position){
          if(dataModels.get(position).getViewType() == 1){
              return 1;
          }else{
              return 2;
          }
      }

      @Override
      public int getItemCount() {
        return dataModels.size();
    }


    @NonNull
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          Log.d(TAG,"onCreateViewHolder");

          View view;

          if(viewType == 1){
              Log.d(TAG,"onCreateViewHolder: viewType = 1");

              view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_chat_other,parent,false);
              return new LeftViewHolder(view);
          }else{
              Log.d(TAG,"onCreateViewHolder: viewType = 2");

              view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_chat_me,parent,false);
              return new RightViewHolder(view);
          }
      }




      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Log.d(TAG,"onBindViewHolder");


          if(holder.getItemViewType() == 1){//상대방이 보낸 메세지
              LeftViewHolder leftViewHolder = (LeftViewHolder)holder;


              Glide.with(context)
                      .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/"+dataModels.get(position).getProfile_img())
                      .circleCrop()
                      .into(leftViewHolder.profile_img);


              leftViewHolder.textv_nicname.setText(dataModels.get(position).getNickname());
              leftViewHolder.textv_time_other.setText(dataModels.get(position).getTime_other());

              if(dataModels.get(position).getType().equals("img")){//상대방이 보낸 채팅이 이미지일때
                  leftViewHolder.cardView_location.setVisibility(View.GONE);
                  leftViewHolder.imageView_img_other.setVisibility(View.VISIBLE);
                  leftViewHolder.textv_msg_other.setVisibility(View.GONE);

                  Glide.with(context)
                          .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/chat_img/"+dataModels.get(position).getMsg_other())
                          .into(leftViewHolder.imageView_img_other);

              }else if(dataModels.get(position).getType().equals("location")){//상대방이 보낸 채팅이 위치정보일때

                  leftViewHolder.imageView_img_other.setVisibility(View.GONE);
                  leftViewHolder.textv_msg_other.setVisibility(View.GONE);
                  leftViewHolder.cardView_location.setVisibility(View.VISIBLE);
                  String location = dataModels.get(position).getMsg_other();

                  String[] loc = location.split("/");
                  String lat = loc[0];
                  String lon = loc[1];

                  leftViewHolder.latitude = Double.parseDouble(lat);
                  leftViewHolder.longitude = Double.parseDouble(lon);
              }

              else{//상대방이 보낸 채팅이 메세지일때
                  leftViewHolder.cardView_location.setVisibility(View.GONE);
                  leftViewHolder.imageView_img_other.setVisibility(View.GONE);
                  leftViewHolder.textv_msg_other.setVisibility(View.VISIBLE);
                  leftViewHolder.textv_msg_other.setText(dataModels.get(position).getMsg_other());
              }

          }else{// 내가 보낸 메세지
              RightViewHolder rightViewHolder = (RightViewHolder)holder;

              rightViewHolder.textv_time_me.setText(dataModels.get(position).getTime_me());

                if(dataModels.get(position).getType().equals("img")){//내가 보낸 채팅이 이미지일때
                  rightViewHolder.imageView_img_me.setVisibility(View.VISIBLE);
                  rightViewHolder.textv_msg_me.setVisibility(View.GONE);
                  rightViewHolder.cardView_location.setVisibility(View.GONE);
                  //내가 보낸직후와 db에서 이미지 가져올때의 과정이 다르기 때문에 분기 처리해야 함
                  //내가 보낸 직후는 uri값이고 db에서 가져올때는 url값임.
                  if(dataModels.get(position).getMsg_me().contains("/")){
                      Log.d(TAG,"갤러리에서 이미지 보낸 직후");
                      //이때는 uri값 일 때 즉, 내가 보낸 직후
                      Log.d(TAG,"전달 받은 이미지 주소:" + dataModels.get(position).getMsg_me());


                      Uri uri = Uri.parse(dataModels.get(position).getMsg_me());

                      rightViewHolder.imageView_img_me.setImageURI(uri);
                  }
//                  else if(dataModels.get(position).getMsg_me().contains(".")){
//                      Log.d(TAG,"카메라로 찍어서 이미지 보낸 직후");
//
//                      String img = dataModels.get(position).getMsg_me();
//                      Log.d(TAG,"img: " + img);
//                      byte[] decodedString = Base64.decode(img, Base64.URL_SAFE );
//                      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                      Log.d(TAG,"bitmap: " + decodedByte);
//                      rightViewHolder.imageView_img_me.setImageBitmap(decodedByte);
//
//                  }
                  else {
                      //db에서 가져올때
                      Glide.with(context)
                              .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/chat_img/"+dataModels.get(position).getMsg_me())
                              .into(rightViewHolder.imageView_img_me);

                  }



              }else if(dataModels.get(position).getType().equals("location")){//내가 보낸 채팅이 위치정보일때
                  rightViewHolder.imageView_img_me.setVisibility(View.GONE);
                  rightViewHolder.textv_msg_me.setVisibility(View.GONE);
                  rightViewHolder.cardView_location.setVisibility(View.VISIBLE);
                  String location = dataModels.get(position).getMsg_me();

                  String[] loc = location.split("/");
                  String lat = loc[0];
                  String lon = loc[1];

                  rightViewHolder.latitude = Double.parseDouble(lat);
                  rightViewHolder.longitude = Double.parseDouble(lon);


              }
              else{//내가 보낸 채팅이 메세지일때
                  rightViewHolder.imageView_img_me.setVisibility(View.GONE);
                  rightViewHolder.textv_msg_me.setVisibility(View.VISIBLE);
                  rightViewHolder.cardView_location.setVisibility(View.GONE);
                  rightViewHolder.textv_msg_me.setText(dataModels.get(position).getMsg_me());
              }
          }
      }





    public class LeftViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        Double latitude,longitude;
        TextView textv_nicname,textv_msg_other,textv_time_other;
        ImageView profile_img,imageView_img_other;

        CardView cardView_location;
        Button button_location;
        MapView map_view_chat;
        NaverMap naverMap;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            textv_nicname =  itemView.findViewById(R.id.textv_nicname);
            textv_msg_other =  itemView.findViewById(R.id.textv_msg_other);
            textv_time_other =  itemView.findViewById(R.id.textv_time_other);
            profile_img = itemView.findViewById(R.id.profile_img);
            imageView_img_other = itemView.findViewById(R.id.imageView_img_other);

            cardView_location = itemView.findViewById(R.id.cardView_location_other);
            button_location = itemView.findViewById(R.id.button_location_other);
            map_view_chat = itemView.findViewById(R.id.map_view_chat_other);

            map_view_chat.getMapAsync(this);

            button_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"클릭");
                    int pos = getAdapterPosition();
                    String latlng = dataModels.get(pos).getMsg_other();
                    Log.d(TAG,"위치: " + dataModels.get(pos).getMsg_other());
                    Intent intent = new Intent(context, ShowLocationActivity.class);
                    intent.putExtra("latlng",latlng);
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                }
            });
        }

        @Override
        public void onMapReady(@NonNull NaverMap naverMap) {
            this.naverMap = naverMap;

            Log.d(TAG,"latitude: " +latitude);
            Log.d(TAG,"longitude: " +longitude);

            CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude,longitude),15);
            naverMap.setCameraPosition(cameraPosition);

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        Double latitude,longitude;
        TextView textv_msg_me,textv_time_me;
        ImageView imageView_img_me;
        CardView cardView_location;
        Button button_location;
        MapView map_view_chat;

        NaverMap naverMap;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            textv_msg_me =  itemView.findViewById(R.id.textv_msg_me);
            textv_time_me =  itemView.findViewById(R.id.textv_time_me);
            imageView_img_me = itemView.findViewById(R.id.imageView_img_me);
            cardView_location = itemView.findViewById(R.id.cardView_location);
            button_location = itemView.findViewById(R.id.button_location);
            map_view_chat = itemView.findViewById(R.id.map_view_chat_me);
            map_view_chat.getMapAsync(this);


            button_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"클릭");
                    int pos = getAdapterPosition();
                    String latlng = dataModels.get(pos).getMsg_me();
                    Log.d(TAG,"위치: " + dataModels.get(pos).getMsg_me());
                    Intent intent = new Intent(context, ShowLocationActivity.class);
                    intent.putExtra("latlng",latlng);
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }




        @Override
        public void onMapReady(@NonNull NaverMap naverMap) {
            this.naverMap = naverMap;

            Log.d(TAG,"latitude: " +latitude);
            Log.d(TAG,"longitude: " +longitude);

            CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude,longitude),15);
            naverMap.setCameraPosition(cameraPosition);
        }
    }
}
