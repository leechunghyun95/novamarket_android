package novamarket.com;

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

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder> {

    private ArrayList<Uri> mData = null;
    private Context mContext = null;


    MultiImageAdapter(ArrayList<Uri> list, Context context){
        mData = list;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageView cancel_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            cancel_button = itemView.findViewById(R.id.cancel_button);

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d("pos", String.valueOf(pos));

                    mData.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, mData.size());

                    TextView countPhoto = ((WriteMarketPostActivity)WriteMarketPostActivity.context_write).countPhoto;

                    countPhoto.setText(mData.size()+"/10");
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.multi_image_item,parent,false);// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        MultiImageAdapter.ViewHolder vh = new MultiImageAdapter.ViewHolder(view);

        return vh;
     }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri image_uri = mData.get(position);

        Glide.with(mContext).load(image_uri).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}
