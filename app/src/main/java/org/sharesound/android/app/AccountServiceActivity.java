package org.sharesound.android.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.apache.thrift.TException;
import org.sharesound.android.rpc.Services;
import org.sharesound.android.rpc.account.AccountService;
import org.sharesound.android.rpc.shared.Session;

public class AccountServiceActivity extends AppCompatActivity {

    private static final String TAG = AccountServiceActivity.class.getName();

    private String mAddress;
    private int mPort;

    private EditText mEditRegEmail, mEditRegUsername, mEditRegPassword;
    private EditText mEditLoginEmail, mEditLoginPassword;

    private DB mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_service);

        Intent intent = getIntent();
        mAddress = intent.getStringExtra(Shared.INTENT_SERVER_ADDRESS_KEY);
        mPort = intent.getIntExtra(Shared.INTENT_SERVER_PORT_KEY, -1);
        if(mAddress == null || mPort < 0){
            Log.e(TAG, "Wrong address or port data");
            finish();
        }

        mEditRegEmail = (EditText)findViewById(R.id.edit_register_email);
        mEditRegUsername = (EditText)findViewById(R.id.edit_register_username);
        mEditRegPassword = (EditText)findViewById(R.id.edit_register_password);

        mEditLoginEmail = (EditText)findViewById(R.id.edit_login_email);
        mEditLoginPassword = (EditText)findViewById(R.id.edit_login_password);

        try {
            mDb = DBFactory.open(this, Shared.SESSION_DB_NAME);
        } catch (SnappydbException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening session database", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void onDestroy(){

        if(mDb != null){
            try {
                mDb.close();
            } catch (SnappydbException e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    public void onRegisterClick(View v){
        final String email = mEditRegEmail.getText().toString();
        final String username = mEditRegUsername.getText().toString();
        final String password = mEditRegPassword.getText().toString();

        (new AsyncTask<Void,Void,Session>(){

            @Override
            protected Session doInBackground(Void... params) {
                AccountService.Client client = null;
                try{
                    client = Services.getAccountService(mAddress, mPort);

                    return client.registerAccount(email, username, password);
                }catch (TException e){
                    Toast.makeText(AccountServiceActivity.this, "Error from RPC",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Got Thrift Exception");
                    e.printStackTrace();
                    return null;
                }finally {
                    Services.closeTransport(client);
                }
            }

            @Override
            protected void onPostExecute(Session session) {
                if(session != null){
                    Log.v(TAG, "Session auth_token: " + session.auth_token);
                    Toast.makeText(AccountServiceActivity.this, "Success!",
                            Toast.LENGTH_LONG).show();
                    try {
                        mDb.put(Shared.SESSION_DB_KEY, session.auth_token);
                    } catch (SnappydbException e) {
                        Log.e(TAG, "Store session key failed");
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(AccountServiceActivity.this, "Error from RPC result",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Session null");
                }
            }
        }).execute();
    }

    public void onLoginClick(View v){
        final String email = mEditLoginEmail.getText().toString();
        final String password = mEditLoginPassword.getText().toString();

        (new AsyncTask<Void,Void,Session>(){

            @Override
            protected Session doInBackground(Void... params) {
                AccountService.Client client = null;
                try{
                    client = Services.getAccountService(mAddress, mPort);

                    return client.login(email, password);
                }catch (TException e){
                    Toast.makeText(AccountServiceActivity.this, "Error from RPC",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Got Thrift Exception");
                    e.printStackTrace();
                    return null;
                }finally {
                    Services.closeTransport(client);
                }
            }

            @Override
            protected void onPostExecute(Session session) {
                if(session != null){
                    Log.v(TAG, "Session auth_token: " + session.auth_token);
                    Toast.makeText(AccountServiceActivity.this, "Success!",
                            Toast.LENGTH_LONG).show();
                    try {
                        mDb.put(Shared.SESSION_DB_KEY, session.auth_token);
                    } catch (SnappydbException e) {
                        Log.e(TAG, "Store session key failed");
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(AccountServiceActivity.this, "Error from RPC result",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Session null");
                }
            }
        }).execute();
    }
}
