package com.example.anand.retrofitsampal.retrofit;

import com.example.anand.retrofitsampal.model.PrivacyModel.PrivacyModelClass;
import com.example.anand.retrofitsampal.model.LoginModel.LoginInModelclass;
import com.example.anand.retrofitsampal.model.ProfileModel.ProfilePicUpdateModel;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterfaces {




    @FormUrlEncoded
    @POST("api/login")
    Call<LoginInModelclass> signInUser(@Field("email") String useremail,
                                       @Field("password") String userpassword,
                                       @Field("SubmitBtn") String UserLogin,
                                       @Field("type") String deviceType);


    @Multipart
    @POST("api/update_profile_picture")
    Call<ProfilePicUpdateModel> profileUpdateModle(@Part("user_id") RequestBody id,
                                                   @Part("SubmitBtn") RequestBody Update_Profile_Picture,
                                                   @Part MultipartBody.Part image);


    @GET("api/privacy_policy?SubmitBtn=privacy_policy")
    Call<PrivacyModelClass> getPrivacyPolicy();
}
