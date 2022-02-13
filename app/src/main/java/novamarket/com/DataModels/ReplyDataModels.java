package novamarket.com.DataModels;

public class ReplyDataModels {
    String reply;
    String writer;
    String time;
    String profileImage;

    public ReplyDataModels(String reply, String writer, String time, String profileImage) {
        this.reply = reply;
        this.writer = writer;
        this.time = time;
        this.profileImage = profileImage;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
