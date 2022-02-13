package novamarket.com.DataModels;

public class UpdateImageDataModels {
    int type;
    String img;

    public UpdateImageDataModels(int type, String img) {
        this.type = type;
        this.img = img;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
