package hr.foi.air.foirun;


import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.SQLOutput;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private static String gId = "goo";
    private static String fbId = "fb";

    CallbackManager callbackManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        SignInButton signInButton = (SignInButton) findViewById(R.id.login_button_google);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                        //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();


                switch (v.getId()) {
                    case R.id.login_button_google:
                        signIn();
                        break;
                    // ...
                }

            }
        });


        //AppEventsLogger.activateApp(this);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_fb);
        loginButton.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                System.out.println("-----> FB LOGIN");
                System.out.println(Profile.getCurrentProfile().getName());

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
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
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //acct.getDisplayName()
            System.out.println("---->");
            System.out.println(acct.getDisplayName());
            System.out.println(acct.getEmail());
            System.out.println(acct.getIdToken());
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
            Intent i = new Intent(this, HomeScreen.class);
            Bundle args = new Bundle();
            args.putString("mail", acct.getEmail());
            args.putString("username", acct.getDisplayName());
            args.putString("token", acct.getIdToken());
            i.putExtras(args);
            startActivity(i);

        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }



    }

