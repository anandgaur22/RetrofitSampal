package com.example.anand.retrofitsampal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anand.retrofitsampal.retrofit.ApiUtils;
import com.example.anand.retrofitsampal.retrofit.RetrofitInterfaces;
import com.example.anand.retrofitsampal.model.LoginModel.LoginInModelclass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_email,et_password;
    Button btn_login,btn_next;
    private  String useremail,userpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        btn_login=findViewById(R.id.btn_login);
        btn_next=findViewById(R.id.btn_next);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremail=et_email.getText().toString();
                userpassword=et_password.getText().toString();
                Login_network_call();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ProfilePicUpdateActivity.class);
                startActivity(intent);

            }
        });

    }


    public void Login_network_call(){


        RetrofitInterfaces iRestInterfaces = ApiUtils.getAPIService();
        Call<LoginInModelclass> loginInModelclassCall = iRestInterfaces.signInUser(useremail, userpassword, "UserLogin", "android");

        loginInModelclassCall.enqueue(new Callback<LoginInModelclass>() {
            @Override
            public void onResponse(Call<LoginInModelclass> call, Response<LoginInModelclass> response) {


                if (response.isSuccessful()) {

                    assert response.body() != null;

                    String status=response.body().getResult().get(0).getStatus().toString();

                    if (status.equalsIgnoreCase("true")){

                        String name=response.body().getResult().get(0).getData().getFirstName();
                        String dob=response.body().getResult().get(0).getData().getDob();
                        String email=response.body().getResult().get(0).getData().getEmail();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        Toast.makeText(LoginActivity.this, ""+response.body().getResult().get(0).getMsg(), Toast.LENGTH_SHORT).show();


                    }

                }
            }

            @Override
            public void onFailure(Call<LoginInModelclass> call, Throwable t) {

                //Toast.makeText(LoginActivity.this, "Server Error"+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
