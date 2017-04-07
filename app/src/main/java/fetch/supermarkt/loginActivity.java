package fetch.supermarkt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by stefg on 06/04/2017.
 */

public class loginActivity extends Activity {
    Button mButtonLogin;
    Button mButtonCheck;
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mButtonLogin = (Button)findViewById(R.id.button_login);
        mButtonCheck = (Button)findViewById(R.id.button_Check);
        mEdit   = (EditText)findViewById(R.id.text_username);


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToSharedPreferences(mEdit.getText().toString());
            }
        });

        mButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFromSharedPreferences();
            }
        });
    }

    private void writeToSharedPreferences(String username) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.user_name), username);
        editor.commit();
    }

    private void readFromSharedPreferences(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = "mislukt";
        String username = sharedPref.getString(getString(R.string.user_name), defaultValue);


        Toast.makeText(getActivity(), username,
                Toast.LENGTH_LONG).show();
    }
    
    public Activity getActivity() {
        return this;
    }
}
