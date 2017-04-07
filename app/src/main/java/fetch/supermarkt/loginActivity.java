package fetch.supermarkt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private Button mButtonLogin;
    private EditText mEdit;

    public static String applicationUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mButtonLogin = (Button) findViewById(R.id.button_login);
        mEdit = (EditText) findViewById(R.id.text_username);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToSharedPreferences(mEdit.getText().toString());
                checkIfUsernameIsSet();
            }
        });

        checkIfUsernameIsSet();
    }

    private void checkIfUsernameIsSet() {
        String username = readFromSharedPreferences();
        if (!username.equals("usernameZettenIsMislukt") && username != null && !username.isEmpty()) {
            applicationUser = username;
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        } else {
            Toast.makeText(getActivity(), "Please fill in your username",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void writeToSharedPreferences(String username) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.user_name), username);
        editor.commit();
    }

    private String readFromSharedPreferences() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = "usernameZettenIsMislukt";
        String username = sharedPref.getString(getString(R.string.user_name), defaultValue);
        return username;
    }

    private void deleteSharedPreferences() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().apply();
    }

    public Activity getActivity() {
        return this;
    }
}
