package br.edu.ucsal.colabmeiapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.edu.ucsal.colabmeiapp.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
    }
}
