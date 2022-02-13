package novamarket.com;

import android.widget.ImageView;

public class MarketDataModel {
    String img_path;
    String title;
    String time;
    String price;
    String idx;

    public MarketDataModel(String img_path, String title, String time, String price, String idx) {
        this.img_path = img_path;
        this.title = title;
        this.time = time;
        this.price = price;
        this.idx = idx;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
}
