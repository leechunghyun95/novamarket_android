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

import novamarket.com.Adapter.SellListDoneAdapter;
import novamarket.com.Adapter.SellListNowAdapter;
import novamarket.com.DataModels.SellListDataModels;
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

public class FragmentSellList_done extends Fragment{
    String TAG = "FragmentSellList_done";

    private View view;
    String userIdx;

    ArrayList<SellListDataModels> dataModels = new ArrayList();
    RecyclerView recyclerview;
    SellListDoneAdapter adapter;
    TextView textView_guideText_sell_list_done;

    public static FragmentSellList_done newInstance(){
        FragmentSellList_done fragmentSellList_done = new FragmentSellList_done();

        return fragmentSellList_done;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_sell_list_done, container, false);

        Bundle bundle = getArguments();
        userIdx = bundle.getString("userIdx");

        adapter = new SellListDoneAdapter(getContext(),dataModels);
        recyclerview = view.findViewById(R.id.recyclerview_sellList_done);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        recyclerview.setAdapter(adapter);

        textView_guideText_sell_list_done = view.findViewById(R.id.textView_guideText_sell_list_done);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        //?????? ???????????? ?????? ????????????
        //get?????? ???????????? ??????
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/read_market_done.php").newBuilder();
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
                            dataModels.add(new SellListDataModels(jsonArray.getJSONObject(2*i+1).getString("fileName"),jsonArray.getJSONObject(2*i).getString("title"),jsonArray.getJSONObject(2*i).getString("time"),jsonArray.getJSONObject(2*i).getString("idx"),jsonArray.getJSONObject(2*i).getString("price")));
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
                                textView_guideText_sell_list_done.setVisibility(View.VISIBLE);
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
        Log.d(TAG,"onPauser");
        dataModels.removeAll(dataModels);
    }
}
