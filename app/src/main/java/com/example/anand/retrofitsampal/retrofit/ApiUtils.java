package com.example.anand.retrofitsampal.retrofit;



public class ApiUtils {

    private ApiUtils() {}

    /*
    * BaseUrl of this application
    * */

    public static final String BASE_URL = "http://cmsbox.in/app/athlete/";

    public static RetrofitInterfaces getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(RetrofitInterfaces.class);
    }
}
