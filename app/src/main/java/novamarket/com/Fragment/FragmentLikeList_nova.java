package novamarket.com.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import novamarket.com.Adapter.LikeListMarketAdapter;
import novamarket.com.Adapter.LikeListNovaAdapter;
import novamarket.com.DataModels.SellListDataModels;
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

public class FragmentLikeList_nova extends Fragment {
    String TAG = "FragmentLikeList_nova";
    private View view;
    String userIdx;

    ArrayList<NovaDataModel> dataModels = new ArrayList();
    RecyclerView recyclerview;
    LikeListNovaAdapter adapter;
    TextView textView_guideText_likeList_nova;





    public static FragmentLikeList_nova newInstance(){
        FragmentLikeList_nova fragmentLikeList_nova = new FragmentLikeList_nova();

        return fragmentLikeList_nova;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.frag_like_list_nova, container, false);

        Bundle bundle = getArguments();
        userIdx = bundle.getString("userIdx");

        adapter = new LikeListNovaAdapter(getContext(),dataModels);
        recyclerview = view.findViewById(R.id.recyclerview_likeList_nova);
        textView_guideText_likeList_nova = view.findViewById(R.id.textView_guideText_likeList_nova);

        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        //?????? ???????????? ?????? ????????????
        //get?????? ???????????? ??????
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_nova_like.php").newBuilder();
        String url = urlBuilder.build().toString();

        //????????? ?????? post??? ????????? ????????? seller??? ????????? ????????? ???????????? ????????? ????????????
        // POST ???????????? ??????
        RequestBody formBody = new FormBody.Builder()
                .add("userIdx", userIdx)
                .build();

        // ?????? ?????????
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        // ?????? ??????
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // ?????? ??????
                    Log.i("tag", "????????????");
                } else {
                    // ?????? ??????
                    Log.i("tag", "?????? ??????");
                    final String responseData = response.body().string();
                    Log.d(TAG,"responseData: " + responseData);
                    try {

                        //???????????? ?????? ????????? ???????????? ????????????
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length()/2; i++){
                            Log.d(TAG,"????????? ??????: " + jsonArray.getJSONObject(2*i).getString("idx"));

                            String subject = jsonArray.getJSONObject(2*i).getString("subject");
                            Log.d(TAG,"??????: " + subject);
                            switch(subject){
                                case  "1":
                                    subject = "????????????";
                                    break;

                                case  "2":
                                    subject = "????????????";
                                    break;

                                case  "3":
                                    subject = "??????";
                                    break;

                                case  "4":
                                    subject = "???????????????";
                                    break;

                                case  "5":
                                    subject = "????????????";
                                    break;

                                case  "6":
                                    subject = "??????";
                                    break;

                                case  "7":
                                    subject = "??????";
                                    break;

                                case  "8":
                                    subject = "??????";
                                    break;

                                default:
                                    break;
                            }




                            dataModels.add(new NovaDataModel(subject,jsonArray.getJSONObject(2*i).getString("contents"),jsonArray.getJSONObject(2*i).getString("nickname"),jsonArray.getJSONObject(2*i).getString("time"),null,null,jsonArray.getJSONObject(2*i+1).getString("fileName"),jsonArray.getJSONObject(2*i).getString("idx")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // ?????? ????????? Ui ?????? ??? ?????? ??????
                    // ??????????????? Ui ??????
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(responseData.equals("[]")){
                                    Log.d(TAG,"????????? ??????");
                                    textView_guideText_likeList_nova.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        dataModels.removeAll(dataModels);
    }
}
