package novamarket.com.Fragment;
import novamarket.com.Adapter.ChatListAdapter;
import novamarket.com.DataModels.ChatListDataModels;
import novamarket.com.NovaDataModel;
import novamarket.com.novaRecyclerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import novamarket.com.MainActivity;
import novamarket.com.R;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class FragmentChat extends Fragment {

    public Context context_fragChat;

    private View view;
    private String TAG = "프래그먼트";
    RecyclerView recyclerView_chat_list;
    String userData,userIdx;//현재 사용자 데이터

    ChatListAdapter chatListAdapter;
    //데이터 모델리스트
    ArrayList<ChatListDataModels> chatListDataModels = new ArrayList();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");


        //현재 사용중인 사용자 누구인지 체크
        SharedPreferences pref = getActivity().getSharedPreferences("pref",Activity.MODE_PRIVATE);
        userData = pref.getString("userData",null);
        Log.d(TAG,"userData: " + userData);

        try {
            JSONObject jsonObject = new JSONObject(userData);
            userIdx = jsonObject.getString("idx");//현재 사용자 번호
        } catch (JSONException e) {
            e.printStackTrace();
        }


        view = inflater.inflate(R.layout.frag_chat, container, false);
        setContentView();

        context_fragChat = getActivity().getApplicationContext();


        // 툴바 생성
        Toolbar toolbar = view.findViewById(R.id.toolbar_fragment_chat);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("채팅"); // 툴바 제목 설정

        chatListAdapter = new ChatListAdapter(getContext(),chatListDataModels);
        recyclerView_chat_list.setAdapter(chatListAdapter);
        recyclerView_chat_list.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

        //네트워크 통신
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.37.128.131/chat_list.php").newBuilder();
        String url = urlBuilder.build().toString();

        // POST 파라미터 추가
        RequestBody formBody = new FormBody.Builder()
                .add("userIdx", userIdx)//사용자 번호를 post파라미터로 보내기
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
                }
                else {
                    // 응답 성공
                    Log.i("tag", "응답 성공");
                    final String responseData = response.body().string();
                    Log.d(TAG,"responseData: " + responseData);

                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length()/4; i++){
                            if(jsonArray.getJSONObject(4*i).getString("chat_type").equals("market")){//개인톡일때
                                if(jsonArray.getJSONObject(4*i).getString("buyer").equals(userIdx)){//내가 구매자일때
                                    Log.d(TAG,"내가 구매자일때");
                                    //판매자 이미지를 채팅 왼쪽 이미지에 보여주기
                                    if(jsonArray.getJSONObject(4*i).getString("type").equals("img")){//최근 채팅이 이미지 일때
                                        chatListDataModels.add(new ChatListDataModels(jsonArray.getJSONObject(4*i).getString("roomName"),jsonArray.getJSONObject(4*i+1).getString("idx"),jsonArray.getJSONObject(4*i+1).getString("profile_image"),jsonArray.getJSONObject(4*i+3).getString("fileName"),jsonArray.getJSONObject(4*i+1).getString("nickname"),jsonArray.getJSONObject(4*i+1).getString("idx"),true,false,"(사진)",jsonArray.getJSONObject(4*i).getString("rec_time"),jsonArray.getJSONObject(4*i).getString("post_number")));

                                    }else if(jsonArray.getJSONObject(4*i).getString("type").equals("location")){//최근 채팅이 위치공유일때
                                        chatListDataModels.add(new ChatListDataModels(jsonArray.getJSONObject(4*i).getString("roomName"),jsonArray.getJSONObject(4*i+1).getString("idx"),jsonArray.getJSONObject(4*i+1).getString("profile_image"),jsonArray.getJSONObject(4*i+3).getString("fileName"),jsonArray.getJSONObject(4*i+1).getString("nickname"),jsonArray.getJSONObject(4*i+1).getString("idx"),true,false,"(장소공유)",jsonArray.getJSONObject(4*i).getString("rec_time"),jsonArray.getJSONObject(4*i).getString("post_number")));

                                    }else{//최근 채팅이 메세지 일때
                                        chatListDataModels.add(new ChatListDataModels(jsonArray.getJSONObject(4*i).getString("roomName"),jsonArray.getJSONObject(4*i+1).getString("idx"),jsonArray.getJSONObject(4*i+1).getString("profile_image"),jsonArray.getJSONObject(4*i+3).getString("fileName"),jsonArray.getJSONObject(4*i+1).getString("nickname"),jsonArray.getJSONObject(4*i+1).getString("idx"),true,false,jsonArray.getJSONObject(4*i).getString("rec_msg"),jsonArray.getJSONObject(4*i).getString("rec_time"),jsonArray.getJSONObject(4*i).getString("post_number")));
                                    }
                                }else {//내가 판매자일때
                                    Log.d(TAG,"내가 판매자일때");
                                    //구매자 이미지를 채팅 왼쪽 이미지에 보여주기

                                    //판매자 이미지를 채팅 왼쪽 이미지에 보여주기
                                    if(jsonArray.getJSONObject(4*i).getString("type").equals("img")){//최근 채팅이 이미지 일때
                                        chatListDataModels.add(new ChatListDataModels(jsonArray.getJSONObject(4*i).getString("roomName"),jsonArray.getJSONObject(4*i+2).getString("idx"),jsonArray.getJSONObject(4*i+2).getString("profile_image"),jsonArray.getJSONObject(4*i+3).getString("fileName"),jsonArray.getJSONObject(4*i+2).getString("nickname"),jsonArray.getJSONObject(4*i+2).getString("idx"),true,false,"(사진)",jsonArray.getJSONObject(4*i).getString("rec_time"),jsonArray.getJSONObject(4*i).getString("post_number")));

                                    }else if(jsonArray.getJSONObject(4*i).getString("type").equals("location")){//최근 채팅이 위치공유일때
                                        chatListDataModels.add(new ChatListDataModels(jsonArray.getJSONObject(4*i).getString("roomName"),jsonArray.getJSONObject(4*i+2).getString("idx"),jsonArray.getJSONObject(4*i+2).getString("profile_image"),jsonArray.getJSONObject(4*i+3).getString("fileName"),jsonArray.getJSONObject(4*i+2).getString("nickname"),jsonArray.getJSONObject(4*i+2).getString("idx"),true,false,"(장소공유)",jsonArray.getJSONObject(4*i).getString("rec_time"),jsonArray.getJSONObject(4*i).getString("post_number")));
                                    }else {//최근 채팅이 메세지 일때
                                        chatListDataModels.add(new ChatListDataModels(jsonArray.getJSONObject(4*i).getString("roomName"),jsonArray.getJSONObject(4*i+2).getString("idx"),jsonArray.getJSONObject(4*i+2).getString("profile_image"),jsonArray.getJSONObject(4*i+3).getString("fileName"),jsonArray.getJSONObject(4*i+2).getString("nickname"),jsonArray.getJSONObject(4*i+2).getString("idx"),true,false,jsonArray.getJSONObject(4*i).getString("rec_msg"),jsonArray.getJSONObject(4*i).getString("rec_time"),jsonArray.getJSONObject(4*i).getString("post_number")));
                                    }
                                }
                            }
                            else {//단체톡 일때

                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                chatListAdapter.notifyDataSetChanged();
                                setChatList("111M9","메세지","8.29");

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
        chatListDataModels.removeAll(chatListDataModels);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
        chatListDataModels.removeAll(chatListDataModels);
    }

    public void setContentView(){
        recyclerView_chat_list = view.findViewById(R.id.recyclerView_chat_list);
    }

    public void setChatList(String chatRoom ,String msg, String time){
        Log.d(TAG,"setChatList 호출됨");
        Log.d(TAG,"chatListAdapter.getItemCount(): " + chatListAdapter.getItemCount());
        Log.d(TAG,"chatListDataModels.size(): " + chatListDataModels.size());

        for (int i = 0; i < chatListDataModels.size(); i++){
            if(chatListDataModels.get(i).getChatRoom().equals(chatRoom)){
                Log.d(TAG,"chatListDataModels.get(i).getChatRoom(): " + chatListDataModels.get(i).getChatRoom());
                String opponentUserIdx = chatListDataModels.get(i).getOpponentUserIdx();
                String profileImage = chatListDataModels.get(i).getProfileImage();
                String postImg = chatListDataModels.get(i).getPostImg();
                String title = chatListDataModels.get(i).getTitle();
                String opponent = chatListDataModels.get(i).getOpponent();
                String postNumber = chatListDataModels.get(i).getPostNumber();
                boolean market = chatListDataModels.get(i).isMarket();
                boolean group = chatListDataModels.get(i).isGroup();
                chatListDataModels.remove(i);
                chatListAdapter.notifyItemRemoved(i);
                chatListDataModels.add(new ChatListDataModels(chatRoom,opponentUserIdx,profileImage,postImg,title,opponent,market,group,msg,time,postNumber));
                chatListAdapter.notifyDataSetChanged();
            }

        }
    }


}
