package novamarket.com.Adapter;

import com.bumptech.glide.Glide;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//import novamarket.com.ChatActivity;
import novamarket.com.ChatMarketActivity;
import novamarket.com.DataModels.ChatListDataModels;
import novamarket.com.NovaDataModel;
import novamarket.com.R;
import novamarket.com.novaRecyclerViewAdapter;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{


    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<ChatListDataModels> dataModels;
    Context context;


    //생성자를 통하여 데이터 리스트 context를 받음
    public ChatListAdapter(Context context, ArrayList<ChatListDataModels> dataModels){
        this.dataModels = dataModels;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_chat_list, parent, false);    //리사이클러뷰나 뷰페이저에 등록할 아이템뷰

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {

        ViewHolder myViewHolder = (ViewHolder)holder;

        //채팅목록 왼쪽 상대방 이미지
        Glide.with(context)
                .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/profile_img/" + dataModels.get(position).getProfileImage())
                .circleCrop()
                .into(myViewHolder.profileImg);
        
        //게시글 대표 이미지
        if(dataModels.get(position).getPostImg().length() < 3){//이미지 없을때
            myViewHolder.postImg.setVisibility(View.GONE);
        }else {//이미지 있을때

        }

        if(dataModels.get(position).isMarket()) {//마켓 게시글 일때
            //게시글 대표 이미지
            if(dataModels.get(position).getPostImg().length() < 3){//이미지 없을때
                myViewHolder.postImg.setVisibility(View.GONE);
            }else {//이미지 있을때
                Glide.with(context)
                    .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/market_post/" + dataModels.get(position).getPostImg())
                    .into(myViewHolder.postImg);
            }

            myViewHolder.nickName.setText(dataModels.get(position).getTitle());
            myViewHolder.recMsg.setText(dataModels.get(position).getMsg());
            myViewHolder.time.setText(dataModels.get(position).getTime());

        }else {//팀노바 생활 그룹 채팅일때
            //게시글 대표 이미지
            if(dataModels.get(position).getPostImg().length() < 3){//이미지 없을때
                myViewHolder.postImg.setVisibility(View.GONE);
            }else {//이미지 있을때
                Glide.with(context)
                    .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/nova_post/" + dataModels.get(position).getPostImg())
                    .into(myViewHolder.postImg);
            }
        }


        

    }


    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg,postImg;
        TextView nickName,recMsg,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image_chat_list);
            postImg = itemView.findViewById(R.id.post_img_chat_list);
            nickName = itemView.findViewById(R.id.nickname_chat_list);
            recMsg = itemView.findViewById(R.id.recMsg_chat_list);
            time = itemView.findViewById(R.id.time_chat_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String postNumber = dataModels.get(pos).getPostNumber();
                    String chatRoom = dataModels.get(pos).getChatRoom();
                    String opponent = dataModels.get(pos).getTitle();
                    String opponentUserIdx = dataModels.get(pos).getOpponentUserIdx();
                    String opponentImg = dataModels.get(pos).getProfileImage();
                    Intent intent = new Intent(context.getApplicationContext(), ChatMarketActivity.class);
                    intent.putExtra("postNumber",postNumber);
                    intent.putExtra("chatRoom",chatRoom);
//                    intent.putExtra("opponent", opponent);
//                    intent.putExtra("opponentImg", opponentImg);
                    context.startActivity(intent);

                }
            });
        }
    }
}

