package hr.from.kovacevic.ivor.ssdatingapp.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import hr.from.kovacevic.ivor.ssdatingapp.db.entities.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE name LIKE :userName LIMIT 1")
    User findByName(String userName);

    @Query("SELECT * FROM user ORDER BY uid DESC LIMIT 1")
    User getLast();

    @Query("UPDATE user SET name = :name WHERE uid = :id")
    void updateUserName(int id, String name);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("DELETE FROM user")
    void truncate();
}
