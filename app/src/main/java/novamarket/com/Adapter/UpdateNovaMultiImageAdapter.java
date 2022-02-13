package novamarket.com.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import novamarket.com.DataModels.UpdateImageDataModels;
import novamarket.com.R;
import novamarket.com.UpdateMarketActivity;
import novamarket.com.UpdateNovaActivity;

public class UpdateNovaMultiImageAdapter extends RecyclerView.Adapter {

    /*
       어댑터의 동작원리 및 순서
       1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
       2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
       3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
       4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
       */
    String TAG = "UpdateMultiImageAdapter";

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<UpdateImageDataModels> dataModels;
    Context context;

    //생성자를 통하여 데이터 리스트 context를 받음
    public UpdateNovaMultiImageAdapter(Context context, ArrayList<UpdateImageDataModels> dataModels){
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
                .inflate(R.layout.multi_image_item,parent,false);
        UpdateNovaMultiImageAdapter.MyViewHolder viewHolder = new UpdateNovaMultiImageAdapter.MyViewHolder(view);


        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");

        UpdateNovaMultiImageAdapter.MyViewHolder myViewHolder = (UpdateNovaMultiImageAdapter.MyViewHolder)holder;

        if(dataModels.get(position).getImg().length() < 3){//이미지 파일명이 3자리 보다 작으면 -> 이미지 없으면 카테고리 번호로 이미지명 설정해서 최대 두자리 수임
            Log.d(TAG,"보여줄 사진업당");
            myViewHolder.imageView.setVisibility(View.GONE);
            myViewHolder.cancle_button.setVisibility(View.GONE);

        }else {
            if(dataModels.get(position).getType() == 0){//서버에서 가져온 이미지일때 파일명으로 가져옴

                Glide.with(context)
                        .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/nova_post/"+dataModels.get(position).getImg())
                        .into(myViewHolder.imageView);


            }else {//갤러리에서 uri로 가져온 이미지
                //String 타입의 이미지 Uri 데이터를 Uri타입으로 변환
                Uri imgUri = Uri.parse(dataModels.get(position).getImg());

                Glide.with(context)
                        .load(imgUri)
                        .into(myViewHolder.imageView);

            }
        }




    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView cancle_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cancle_button =  itemView.findViewById(R.id.cancel_button);
            imageView = itemView.findViewById(R.id.image);

            //이미지 x버튼 클릭
            cancle_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG,pos+ "번째 이미지 삭제 버튼 클릭");

                    if(dataModels.get(pos).getType() == 0){//서버에서 불러온 이미지
                        Log.d(TAG,"서버에서 불러온 이미지 삭제버튼 클릭");
                        ((UpdateNovaActivity)UpdateNovaActivity.context_update_nova).removeList.add(dataModels.get(pos).getImg());
                    }else {//갤러리에서 가져온 이미지
                        Log.d(TAG,"갤러리에서 불러온 이미지 삭제버튼 클릭");
                    }


                    dataModels.remove(pos);//리사이클러뷰에서 삭제
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, dataModels.size());

                    ((UpdateNovaActivity)UpdateNovaActivity.context_update_nova).countphoto_UpdateNova.setText(dataModels.size()+"/10");

                }
            });
        }
    }
}
