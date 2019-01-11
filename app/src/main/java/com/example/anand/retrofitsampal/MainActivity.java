package com.example.anand.retrofitsampal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.anand.retrofitsampal.retrofit.ApiUtils;
import com.example.anand.retrofitsampal.retrofit.RetrofitInterfaces;
import com.example.anand.retrofitsampal.model.PrivacyModel.PrivacyModelClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView privacy_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        privacy_txt=findViewById(R.id.privacy_txt);

        Get_priavcy_Network_call();
    }


    public void Get_priavcy_Network_call(){

        RetrofitInterfaces iRestInterfaces = ApiUtils.getAPIService();
        Call<PrivacyModelClass> getCategWithOutOtherModelCall = iRestInterfaces.getPrivacyPolicy();
        getCategWithOutOtherModelCall.enqueue(new Callback<PrivacyModelClass>() {
            @Override
            public void onResponse(Call<PrivacyModelClass> call, Response<PrivacyModelClass> response) {

                if (response.isSuccessful()) {

                    //Toast.makeText(MainActivity.this, ""+response.body().getResult().get(0).getStatus(), Toast.LENGTH_SHORT).show();

                    //Toast.makeText(MainActivity.this, ""+response.body().getResult().get(0).getMsg(), Toast.LENGTH_SHORT).show();

                    String priavyText=response.body().getResult().get(0).getData().getDescription();

                    privacy_txt.setText(priavyText);


                }
            }

            @Override
            public void onFailure(Call<PrivacyModelClass> call, Throwable t) {

            }
        });

    }
}
