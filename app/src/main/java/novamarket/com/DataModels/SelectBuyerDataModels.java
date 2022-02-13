package novamarket.com.DataModels;

public class SelectBuyerDataModels {
    String buyerIdx,buyerImg,buyerNickname,postNumber;

    public SelectBuyerDataModels(String buyerIdx,String buyerNickname ,String buyerImg, String postNumber) {
        this.buyerIdx = buyerIdx;
        this.buyerNickname = buyerNickname;
        this.buyerImg = buyerImg;
        this.postNumber = postNumber;
    }

    public String getBuyerIdx() {
        return buyerIdx;
    }

    public void setBuyerIdx(String buyerIdx) {
        this.buyerIdx = buyerIdx;
    }

    public String getBuyerImg() {
        return buyerImg;
    }

    public void setBuyerImg(String buyerImg) {
        this.buyerImg = buyerImg;
    }



    public String getBuyerNickname() {
        return buyerNickname;
    }

    public void setBuyerNickname(String buyerNickname) {
        this.buyerNickname = buyerNickname;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }
}
