package novamarket.com.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import novamarket.com.ChatMarketActivity;
import novamarket.com.MainActivity;
import novamarket.com.R;
import novamarket.com.StartActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ChatService extends Service {
    String TAG = "ChatService";
    String userIdx;
    public static Socket socket;
    String msg;
    String msgChatRoom;//받은 메세지 채팅방이름
    String chatRoom;//현재 채팅방이름
    String receiveMsg;//받은 메세지
    String postNumber;//게시글 번호
    String type;//메세지,이미지,위치공유인지 타입

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        //사용자 인덱스
        userIdx = intent.getStringExtra("userIdx");
        Log.d(TAG,"userIdx: " + userIdx);

        ClientThread thread = new ClientThread();
        thread.start();
        return START_REDELIVER_INTENT;//서비스 종료시 재시작 (전달된 마지막 인텐트로 onStartcommand() 호출)
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"onDestroy");
    }

    class ClientThread extends Thread{
        @Override
        public void run() {
            Log.d(TAG, "4");
            String host = "192.168.123.100";
            int port = 8888;

            if (socket != null) {//소켓이 널이 아니면
                Log.d(TAG,"소켓 있음");
            } else {
                Log.d(TAG,"소켓 없음");
                try {
                    Log.d(TAG, "5");
                    socket = new Socket(host, port);
                    Log.d(TAG, "소켓 생성");

                    //서버에 유저 인덱스 전달
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(userIdx);

                    while (true) {

                        Log.d(TAG, "6");
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        msg = inputStream.readUTF();
                        Log.d(TAG, "메세지: " + msg);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(msg);
                            receiveMsg = jsonObject.getString("msg");//메세지
                            msgChatRoom = jsonObject.getString("chatRoom");//채팅방
                            postNumber = jsonObject.getString("postNumber");//게시글 번호
                            type = jsonObject.getString("type");//채팅 타입

                            Log.d(TAG,"1. chatRoom:" + msgChatRoom + " postNumber: " + postNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //현재 액티비티 위치에 따른 알림
                        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                        ComponentName componentName = info.get(0).topActivity;
                        String ActivityName = null;
                        ActivityName = componentName.getShortClassName().substring(1);
                        Log.d(TAG, "ActivityName: " + ActivityName);




                        if (ActivityName.equals("ChatMarketActivity")) {//현재 채팅방 액티비티일때 -> 채팅방 액티비티에서 채팅방 이름까지 일치하면 알림x 아니면 알림
                            Log.d(TAG, "현재액티비티-1: " + ActivityName);
                            chatRoom = ((ChatMarketActivity) ChatMarketActivity.context_chat).chatRoom;
                            if (chatRoom.equals(msgChatRoom)) {//현재 채팅방이 메세지 받은 채팅방과 일치할때 -> 알림 x
                                Log.d(TAG, chatRoom + " : " + msgChatRoom);
                                //액티비티로 데이터 전달
                                Intent msgIntent = new Intent(getApplicationContext(), ChatMarketActivity.class);
                                msgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                msgIntent.putExtra("msg", msg);

                                startActivity(msgIntent);

                            } else {//현재 채팅방 액티비티에는 있으나 다른 채팅방일 떄. -> 알림 o
                                Log.d(TAG, chatRoom + " : " + msgChatRoom);
                                Log.d(TAG,"알림");

                                Log.d(TAG,"2. chatRoom:" + msgChatRoom + " postNumber: " + postNumber);


                                if(type.equals("msg")){
                                    createNotification("nova_noti_channel", 1, "노바마켓", receiveMsg,postNumber,msgChatRoom);

                                }else if(type.equals("img")){
                                    createNotification("nova_noti_channel", 1, "노바마켓", "(사진)",postNumber,msgChatRoom);

                                }else {
                                    createNotification("nova_noti_channel", 1, "노바마켓", "(장소 공유)",postNumber,msgChatRoom);

                                }
                            }

                        } else {//현재 채팅방 액티비티가 아닐때는 항상 알림
                            Log.d(TAG, "현재액티비티-2: " + ActivityName);
                            Log.d(TAG,"알림");

                            Log.d(TAG,"3. chatRoom:" + msgChatRoom + " postNumber: " + postNumber);

                            if(type.equals("msg")){
                                createNotification("nova_noti_channel", 1, "노바마켓", receiveMsg,postNumber,msgChatRoom);

                            }else if(type.equals("img")){
                                createNotification("nova_noti_channel", 1, "노바마켓", "(사진)",postNumber,msgChatRoom);

                            }else {
                                createNotification("nova_noti_channel", 1, "노바마켓", "(장소 공유)",postNumber,msgChatRoom);

                            }


                            if(ActivityName.equals("MainActivity")){
                                //액티비티로 데이터 전달
                                Intent msgIntent = new Intent(getApplicationContext(), MainActivity.class);
                                msgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                msgIntent.putExtra("msg", msg);

                                startActivity(msgIntent);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "nova_noti";
            String description = "nova_noti";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("nova_noti_channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    void createNotification(String channelId, int id, String title, String text, String postNumber, String chatRoom){
            Intent intent = new Intent(this, ChatMarketActivity.class);
            intent.putExtra("postNumber",postNumber);
            intent.putExtra("chatRoom",chatRoom);
            Log.d(TAG,"4. chatRoom:" + chatRoom + " postNumber: " + postNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(text)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(id, builder.build());
        }

}
