package novamarket.com.DataModels;

public class ChatDataModels {
    String profile_img;
    String nickname;
    String msg_other,msg_me;
    String time_other,time_me;
    String type;
    int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public ChatDataModels(String profile_img, String nickname, String msg_other, String msg_me, String time_other, String time_me, boolean me, int viewType, String type) {
        this.profile_img = profile_img;
        this.nickname = nickname;
        this.msg_other = msg_other;
        this.msg_me = msg_me;
        this.time_other = time_other;
        this.time_me = time_me;
        this.viewType = viewType;
        this.type = type;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMsg_other() {
        return msg_other;
    }

    public void setMsg_other(String msg_other) {
        this.msg_other = msg_other;
    }

    public String getMsg_me() {
        return msg_me;
    }

    public void setMsg_me(String msg_me) {
        this.msg_me = msg_me;
    }

    public String getTime_other() {
        return time_other;
    }

    public void setTime_other(String time_other) {
        this.time_other = time_other;
    }

    public String getTime_me() {
        return time_me;
    }

    public void setTime_me(String time_me) {
        this.time_me = time_me;
    }
}
