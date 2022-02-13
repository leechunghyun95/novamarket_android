package novamarket.com;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "table_name")
public class UserData implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nickname")
    private String nickname;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "profile_image")
    private String profile_image;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }


    @Override
    public String toString() {
        return "\n{ id : " + this.id + " , nickname : " + this.nickname + ", email : " +this.email + ", profile_image : " + this.profile_image +"}";
    }



    }
