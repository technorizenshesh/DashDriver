package main.com.dashdriver.restapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by technorizen on 14/2/18.
 */

public interface ApiInterface {
    @GET("language_list?")
    Call<ResponseBody> languageCall() ;
   @GET("login?")
    Call<ResponseBody> loginCall(@Query("mobile") String email, @Query("password") String password, @Query("register_id") String register_id, @Query("lat") String lat, @Query("lon") String lon, @Query("type") String type,@Query("continue") String login_sts) ;
    @GET("signup?")
    Call<ResponseBody> SignupCall(@Query("cname") String name_et_str, @Query("cnpjcode") String input_cnpj_et_str, @Query("iecode") String input_ie_et_str, @Query("atype") String accounttype_str, @Query("email") String input_email_str, @Query("password") String input_password_str, @Query("firstname") String first_name_et_str, @Query("lastname") String last_name_str, @Query("telename") String mobile_et_str, @Query("cep") String address_et_str, @Query("website") String website_et_str, @Query("pais") String country_id, @Query("eastado") String state_id, @Query("paislevel") String city_id) ;
    @GET("forgot_password?")
    Call<ResponseBody> ForgotCall(@Query("email") String email,@Query("type") String type) ;
    @GET("getstateapp?")
    Call<ResponseBody> GetStateCall(@Query("pais") String pais);
    @GET("getcityapp?")
    Call<ResponseBody> GetCityCall(@Query("cityid") String cityid);
    @GET("getcategorytype")
    Call<ResponseBody> getMainCategoryCall();
    @GET("getcategoriesapp?")
    Call<ResponseBody> getSubCategory(@Query("type") String type);
    @GET("showsubapp?")
    Call<ResponseBody> getExtraSubCategory(@Query("type") String type, @Query("id") String id);
    @GET("showitemapp?")
    Call<ResponseBody> getAutoExtaraSub(@Query("type") String type, @Query("id") String id, @Query("subc") String subc);
   @GET("get_my_transaction?")
    Call<ResponseBody> getMyTransection(@Query("user_id") String user_id);
    @GET("plangetapp")
    Call<ResponseBody> getAppPricePlan();
 @GET("car_list?")
 Call<ResponseBody> getVehicleType() ;
 @GET("vehicle_list?")
 Call<ResponseBody> getMyVehicleList(@Query("driver_id") String driver_id) ;

//http://hitchride.net/webservice/get_my_transaction?user_id=1
//http://testing.bigclicki.com/webservice/plangetapp
//http://testing.bigclicki.com/webservice/getcategorytype
// http://testing.bigclicki.com/webservice/getcategoriesapp?type=atacado
//http://testing.bigclicki.com/webservice/showsubapp?type=servicos&id=2240
// http://testing.bigclicki.com/webservice/showitemapp?id=24&type=automoveis&subc=3042
}
