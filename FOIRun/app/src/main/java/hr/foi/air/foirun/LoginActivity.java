package hr.foi.air.foirun;


import android.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import butterknife.ButterKnife;
import hr.foi.air.database.entities.User;

public class LoginActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";

    Button mBtnSignIn;
    Button mBtnGoogleSign;
    AutoCompleteTextView mEmail;
    EditText mPassword;
    private static final String[] INITIAL_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int initial_request = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPerm();
        }

        FlowManager.init(new FlowConfig.Builder(this).build());

        mBtnSignIn = (Button) findViewById(R.id.email_sign_in_button);
        mBtnGoogleSign = (Button) findViewById(R.id.google_sign_in_button);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

// Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mBtnGoogleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_sign_in_button:
                        signIn();
                        break;
                    // ...
                }

            }
        });

        final Activity that = this;

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                Intent i = new Intent(that, MainActivity.class);
                Bundle args = new Bundle();
                args.putString("mail", email);
                String username = email.substring(0, email.indexOf('@'));
                args.putString("username", username);

                User existingUser = User.getByEmail(email);

                boolean isValid = true;
                String msg = "";
                if(existingUser != null){
                    if(!existingUser.isValid(password)){
                        msg = "Combination of username and password is wrong";
                        isValid = false;
                    } else {
                        msg = "You are sucessfully logged in";
                    }
                } else {
                    new User(username, email, password, false).insert();
                    msg = "You are sucessfully registered";

                }

                Toast.makeText(that, msg, Toast.LENGTH_LONG).show();

               if(isValid){

                   User user = User.getByMailName(email);

                   args.putInt("uid", user.getId());

                   i.putExtras(args);
                   startActivity(i);
               }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        String msg = result.getStatus().getStatusMessage();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
            Intent i = new Intent(this, MainActivity.class);
            Bundle args = new Bundle();

            String username = acct.getDisplayName();
            String token = acct.getIdToken();
            String mail = acct.getEmail();

            if(User.getByMailName(mail) == null){
                new User(username, mail, token, true).save();
            }

            User user = User.getByMailName(mail);

            args.putInt("uid", user.getId());
            i.putExtras(args);
            startActivity(i);

        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPerm() {
        requestPermissions(INITIAL_PERMS, initial_request);

    }


    }

