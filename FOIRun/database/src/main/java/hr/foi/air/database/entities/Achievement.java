package hr.foi.air.database.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.List;

import hr.foi.air.database.FoiDatabase;

/**
 * Created by Matej on 22/08/2017.
 */

@Table(database = FoiDatabase.class)
public class Achievement extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    int id;

    @Column
    String type;

    @Column
    String name;

    @Column
    Date date;

    @Column
    int user_id;


    public static List<Achievement> getAll() {
        return SQLite.select().from(Achievement.class).queryList();
    }

    public static List<Achievement> getByUserId(int uid) {
        return new Select().from(Achievement.class)
                .where(Achievement_Table.user_id.eq(uid))
                .queryList();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
