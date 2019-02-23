package com.example.googlessmsretrieverapi;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SmsBroadcastReceiver.OTPReceiveListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    GoogleApiClient googleApiClient;
    SmsBroadcastReceiver receiver = new SmsBroadcastReceiver();
    EditText otpTextView;
    TextView yourHashCodeTv;
    Button hashCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        otpTextView = findViewById(R.id.otpTextView);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();


        yourHashCodeTv = findViewById(R.id.yourHashCodeTv);
        hashCodeButton = findViewById(R.id.hashCodeButton);

        hashCodeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                AppSignature appSignature = new AppSignature(MainActivity.this);
                List<String> codes = appSignature.getAppSignatures();
                yourHashCodeTv.setText(codes.get(0));
                Log.d(TAG, "onClickAppS: " + codes.get(0));

            }
        });

        startSMSListener();

        receiver.initOTPListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    private void startSMSListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Listening SMS", Toast.LENGTH_SHORT).show();

        });

        task.addOnFailureListener(e -> {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onOTPReceived(@NotNull String var1) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
        String sixDigitOtp = extractDigits(var1);
        otpTextView.setText(sixDigitOtp);
    }

    @Override
    public void onOTPTimeOut() {
        Toast.makeText(MainActivity.this, " SMS Retriever Timeout", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static String extractDigits(final String in) {
        final Pattern p = Pattern.compile("(\\d{6})");
        final Matcher m = p.matcher(in);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }
}
