package hr.foi.air.database.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import hr.foi.air.database.FoiDatabase;

@Table(database = FoiDatabase.class)
public class ActivityType extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column int id;

    @Column String name;

    public ActivityType(String name) {

        this.name = name;

    }

    public ActivityType() {

    }

    public static List<ActivityType> getAll(){
        return SQLite.select().from(ActivityType.class).queryList();
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public static ActivityType getByName(String mTypeName) {

        return new Select().from(ActivityType.class)
                .where(ActivityType_Table.name.eq(mTypeName))
                .querySingle();

    }
}
