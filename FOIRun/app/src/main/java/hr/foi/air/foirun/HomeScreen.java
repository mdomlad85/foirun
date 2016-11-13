package hr.foi.air.foirun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import butterknife.OnClick;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //System.out.println(getIntent().getExtras().getString("username"));
        TextView t = (TextView) findViewById(R.id.hello_text);
        t.setText("Hello, "+ getIntent().getExtras().getString("username"));
    }

    @OnClick(R.id.homeItem)
    public void homeClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
