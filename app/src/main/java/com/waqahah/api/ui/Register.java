package com.waqahah.api.ui;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.waqahah.R;
import com.waqahah.api.service.PostClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {
    private static String url;
    Retrofit.Builder builder ;
    Retrofit retrofit;

    PostClient apiService ;

    EditText email;
    EditText username;
    EditText password;
    EditText firstname;
    EditText lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        url = this.getResources().getString(R.string.url);
         builder = new Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        apiService = retrofit.create(PostClient.class);

        findViewById(R.id.reg_btn).setOnClickListener((view)->{Register();  });
    }

    private void Register()
    {
        email = (EditText) findViewById(R.id.reg_email);
        username = (EditText) findViewById(R.id.reg_username);
        password = (EditText) findViewById(R.id.reg_password);
        firstname = (EditText) findViewById(R.id.reg_firstname);
        lastname = (EditText) findViewById(R.id.reg_lastname);
        String emailstr = email.getText().toString();
        String usernamestr = username.getText().toString();
        String passwordstr = password.getText().toString();
        String fnamestr = firstname.getText().toString();
        String lnamestr = lastname.getText().toString();
        Call<ResponseBody> call = apiService.register(emailstr,usernamestr,passwordstr,fnamestr,lnamestr);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                {
                    Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                }
                else
                {
                    Toast.makeText(Register.this, "Failed to register", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Register.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
