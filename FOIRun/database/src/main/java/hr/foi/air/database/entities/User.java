package hr.foi.air.database.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import hr.foi.air.core.HashHelper;
import hr.foi.air.database.FoiDatabase;

@Table(database = FoiDatabase.class)
public class User extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column int id;

    @Column
    String name;

    @Column
    @Unique
    String email;
    //This is a hash value

    @Column
    String password;

    @Column
    String token;

    @Column
    boolean google;

    @Column
    int age;

    @Column
    int height;

    @Column
    int weight;

    public User(String name, String email, String token, boolean isGoogle, int age, int height, int weight) {
        this.name = name;
        this.email = email;

        if(isGoogle){
            this.token = token;
        }else {
            this.password = HashHelper.sha1Hash(token);
        }

        this.google = isGoogle;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public User() {
    }

    public static List<User> getAll(){
        return SQLite.select().from(User.class).queryList();
    }

    public static User getById(int id){
        User user = new Select().from(User.class).where(User_Table.id.eq(id)).querySingle();
        return user;
    }

    public static User getByName(String name){
        User user = new Select().from(User.class)
                .where(User_Table.name.eq(name))
                .querySingle();

        return user;
    }

    public static User getByEmail(String email){
        User user = new Select().from(User.class)
                .where(User_Table.email.eq(email))
                .querySingle();

        return user;
    }

    public static User getByMailName(String name){
        User user = new Select().from(User.class)
                .where(User_Table.email.like(name))
                .querySingle();

        return user;
    }

    public static boolean isValid(String name, String password){
        User user = User.getByName(name);

        if(user == null){
            user = User.getByMailName(name);
        }

        String hash = HashHelper.sha1Hash(password);

        return user != null  && user.password.equals(hash);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = HashHelper.sha1Hash(password);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isGoogle() {
        return google;
    }

    public void setGoogle(boolean google) {
        this.google = google;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isValid(String password) {
        return HashHelper.sha1Hash(password).equals(this.password);
    }
}
