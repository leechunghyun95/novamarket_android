package novamarket.com;


import com.bumptech.glide.Glide;

import android.content.Context;
import android.net.Uri;
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

public class novaImageRecyclerViewAdapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
    String TAG = "novaImageRecyclerViewAdapter";

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<String> imgList;
    Context context;

    //생성자를 통하여 데이터 리스트 context를 받음
    public novaImageRecyclerViewAdapter(Context context, ArrayList<String> imgList){
        this.imgList = imgList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return imgList.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        Log.d(TAG,"getItemCount: " + getItemCount());

        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itmeview_nova_img,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");

        MyViewHolder myViewHolder = (MyViewHolder)holder;


        if(imgList.get(0).length() < 3){
            Log.d(TAG,"게시글에 사진 없다");
            myViewHolder.imageView.setVisibility(View.GONE);

        }else {
            Log.d(TAG,"게시글에 사진 있다");
            Glide.with(context)
                    .load("https://novamarket.s3.ap-northeast-2.amazonaws.com/nova_post/"+imgList.get(position))
                    .into(myViewHolder.imageView);

        }


    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.nova_img);
        }
    }

}
