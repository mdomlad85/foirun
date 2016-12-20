package hr.foi.air.foirun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.OnClick;
import hr.foi.air.database.entities.User;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //System.out.println(getIntent().getExtras().getString("username"));
        TextView t = (TextView) findViewById(R.id.hello_text);
        t.setText("Hello, "+ getIntent().getExtras().getString("username"));
        User user = new User();
        user.setName(getIntent().getExtras().getString("username"));
        user.setEmail(getIntent().getExtras().getString("mail"));
        user.setToken(getIntent().getExtras().getString("token"));

        final long insert = user.insert();
        if(insert == -1){
            System.out.println("Greška");
        }else{
            System.out.println("ISPRAVNO");
        }
    }

    @OnClick(R.id.homeItem)
    public void homeClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
