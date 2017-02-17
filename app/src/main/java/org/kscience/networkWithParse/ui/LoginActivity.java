package org.kscience.networkWithParse.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.kscience.networkWithParse.NetworkWithParseApp;
import org.kscience.networkWithParse.R;


public class LoginActivity extends Activity {

    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mLoginButton;

    protected TextView mTitleView;
    protected TextView mSubtitleView;
    protected TextView mSignUpText;

    protected TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main_login);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mSignUpTextView = (TextView)findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mTitleView = (TextView) findViewById(R.id.title);
        mSubtitleView = (TextView) findViewById(R.id.subtitle);
        Typeface myTypefaceTitle = Typeface.createFromAsset(getAssets(), "GreatVibes-Regular.ttf");
        Typeface myTypefaceSubtitle = Typeface.createFromAsset(getAssets(), "CraftyGirls.ttf");
        mTitleView.setTypeface(myTypefaceTitle);
        mSubtitleView.setTypeface(myTypefaceSubtitle);

        mUserName = (EditText)findViewById(R.id.usernameField);
        mUserName.setTypeface(myTypefaceSubtitle);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mPassword.setTypeface(myTypefaceSubtitle);
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setTypeface(myTypefaceSubtitle);
        mSignUpText = (TextView) findViewById(R.id.signUpText);
        mSignUpText.setTypeface(myTypefaceSubtitle);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();

                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    //Login
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                //Success!!
                                //for push up notifications (we use it in RecipientActivity). method is defined in NetworkWithParseApp class
                                NetworkWithParseApp.updateParseInstallation(user);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                    }
            }
        });
    }

}
