package novamarket.com;

import android.widget.ImageView;

public class NovaDataModel {
    String subject,contents,nickname,time,likes,replyCounts,img_path,idx;


    public NovaDataModel(String subject, String contents, String nickname, String time, String likes, String replyCounts, String img_path,String idx) {
        this.subject = subject;
        this.contents = contents;
        this.nickname = nickname;
        this.time = time;
        this.likes = likes;
        this.replyCounts = replyCounts;
        this.img_path = img_path;
        this.idx = idx;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getReplyCounts() {
        return replyCounts;
    }

    public void setReplyCounts(String replyCounts) {
        this.replyCounts = replyCounts;
    }


    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
}
