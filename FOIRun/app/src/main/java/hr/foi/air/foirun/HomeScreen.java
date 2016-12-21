package hr.foi.air.foirun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

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
    }

    @OnClick(R.id.homeItem)
    public void homeClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
