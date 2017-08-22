package hr.foi.air.database.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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


    public static List<Achievement> getAll() {
        return SQLite.select().from(Achievement.class).queryList();
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
}
