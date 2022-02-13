package novamarket.com.DataModels;

public class SellListDataModels {
    String img,title,date,postNumber,price;

    public SellListDataModels(String img, String title, String date, String postNumber, String price) {
        this.img = img;
        this.title = title;
        this.date = date;
        this.postNumber = postNumber;
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
