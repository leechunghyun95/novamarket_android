package novamarket.com;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {



    @Insert(onConflict = REPLACE)
    void insert(UserData userData);

    @Delete
    void delete(UserData userData);

    @Delete
    void reset(List<UserData> userData);

    @Query("UPDATE table_name SET nickname = :sNickname, email = :sEmail, profile_image =:sProfile_image  WHERE ID = :sID")
    void update(int sID, String sNickname,String sEmail,String sProfile_image);

    @Query("SELECT * FROM table_name ORDER BY id DESC LIMIT 1")
    UserData getUserData();

    @Query("SELECT * FROM table_name")
    List<UserData> getAll();
}
