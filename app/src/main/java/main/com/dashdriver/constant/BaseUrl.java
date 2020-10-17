package main.com.dashdriver.constant;

/**
 * Created by technorizen on 5/6/17.
 */

public class BaseUrl {
    public static String baseurl = "http://lezdash.com/DashTaxi/webservice/";
    public static String image_baseurl = "http://lezdash.com/DashTaxi/uploads/images/";
    public static String stripe_publish = "pk_test_3oQpHM18Yv2mFAK6vSE5I1oz";
    public static String privacy = "http://lezdash.com/DashTaxi/privacy.php";
    public static String termsconditions = "http://lezdash.com/DashTaxi/terms.php";

    public static BaseUrl get() {
        return new BaseUrl();
    }

    public String AddVehicle() {
        return baseurl.concat("add_vehicle");
    }

    public String getCar() {
        return baseurl.concat("car_list");
    }

    public String getVehicle() {
        return baseurl.concat("vehicle_list");
    }

    public String addExpanse() {
        return baseurl.concat("add_expance");
    }

    public String getExpanse() {
        return baseurl.concat("get_expance");
    }

    public String BookNow() {
        return baseurl.concat("booking_request_driver");
    }

    public String getVerify() {
        return baseurl.concat("get_number_profile");
    }

    public String sendWalletAmount() {
        return baseurl.concat("send_wallet_amount");
    }

    public String getStatement() {
        return baseurl.concat("get_driver_statement_old");
    }
    public String filterEarning() {
        return baseurl.concat("filter_earning");
    }
}