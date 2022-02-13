package novamarket.com.DataModels;

public class ChatListDataModels {
    String profileImage,postImg;
    String title;
    String opponent;
    boolean market;
    boolean group;
    String msg;
    String time;
    String chatRoom;
    String postNumber;
    String opponentUserIdx;//상대방 번호

    public String getOpponentUserIdx() {
        return opponentUserIdx;
    }

    public void setOpponentUserIdx(String opponentUserIdx) {
        this.opponentUserIdx = opponentUserIdx;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatListDataModels(String chatRoom, String opponentUserIdx ,String profileImage, String postImg, String title, String opponent, boolean market, boolean group, String msg ,String time, String postNumber) {
        this.chatRoom = chatRoom;
        this.profileImage = profileImage;
        this.postImg = postImg;
        this.title = title;
        this.opponent = opponent;
        this.market = market;
        this.group = group;
        this.msg = msg;
        this.time = time;
        this.postNumber = postNumber;
        this.opponentUserIdx = opponentUserIdx;

    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public boolean isMarket() {
        return market;
    }

    public void setMarket(boolean market) {
        this.market = market;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
