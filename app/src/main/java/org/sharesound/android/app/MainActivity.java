package org.sharesound.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //public static final String TAG = MainActivity.class.getName();

    private EditText mEditAccountServiceAddress, mEditAccountServicePort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpAccountServiceInput();
    }

    private void setUpAccountServiceInput(){
        View layoutView = findViewById(R.id.input_account_service);
        if(layoutView != null){
            mEditAccountServiceAddress = (EditText) layoutView.findViewById(R.id.edit_server_address);
            mEditAccountServicePort = (EditText) layoutView.findViewById(R.id.edit_server_port);
        }
    }
    public void onAccountServiceClick(View v){
        if(mEditAccountServiceAddress != null && mEditAccountServicePort != null){
            String address = mEditAccountServiceAddress.getText().toString();
            int port;
            try{
                port = Integer.parseInt(mEditAccountServicePort.getText().toString());
            }catch (NumberFormatException e){
                port = -1;
            }

            if(address.length() <= 0 || port < -1){
                Toast.makeText(MainActivity.this, "Server address or port format error", Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                Intent intent = new Intent(MainActivity.this, AccountServiceActivity.class);
                intent.putExtra(Shared.INTENT_SERVER_ADDRESS_KEY, address);
                intent.putExtra(Shared.INTENT_SERVER_PORT_KEY, port);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Connect to Server Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
