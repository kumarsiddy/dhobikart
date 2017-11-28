package freakydevelopers.dhobikart.Resources;

import okhttp3.MediaType;

/**
 * Created by PURUSHOTAM on 8/14/2017.
 */

public class MyURL {
    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String CARTADD;
    public static String CARTTOTAL;
    public static String CARTDELETE;
    public static String CARTGETALL;
    public static String CARTADDONE;
    public static String CARTREMOVEONE;


    //This is OTP Management URL
    public static String OTPGET;  //This URL also verifies if user exist or not
    public static String OTPVERIFY;
    public static String OTPRESEND;
    public static String OTPWITHPHONE;

    //This is Address Management URL
    public static String ADDRESSGET;
    public static String ADDRESSSAVE;
    public static String ADDRESSDELETE;
    public static String ADDRESSUPDATE;


    //This is User Management URL
    public static String USEREXIST;
    public static String USERSIGNIN;
    public static String USERSIGNUP;
    public static String USERCHANGEPWD;


    //This is Cloth List Management URL
    public static String CLOTHLISTMEN;
    public static String CLOTHLISTWOMEN;
    public static String CLOTHLISTOTHER;


    //This is Order Management URL
    public static String ORDERGET;
    public static String ORDERPLACE;
    public static String ORDERCLOTHES;
    public static String ORDERCANCEL;


    //This is Refer Management URL
    public static String REFERWALLET;
    public static String REFERCHECKDEVICE;
    public static String REFERTEXT;
    public static String REFERCODE;


    //This is DHPoint Management URL
    public static String DHPOINTURL;


    //This is message Management URL
    public static String MESSAGETEXTURL;


    public static void setBaseURL(String baseURL) {

        CARTADD = baseURL + "cart/add";
        CARTTOTAL = baseURL + "cart/total";
        CARTDELETE = baseURL + "cart/delete";
        CARTGETALL = baseURL + "cart/get";
        CARTADDONE = baseURL + "cart/addone";
        CARTREMOVEONE = baseURL + "cart/removeone";


        //This is OTP Management URL
        OTPGET = baseURL + "otp/get";  //This URL also verifies if user exist or not
        OTPVERIFY = baseURL + "otp/verify";
        OTPRESEND = baseURL + "otp/resend";
        OTPWITHPHONE = baseURL + "otp/getwithphone";


        //This is Address Management URL
        ADDRESSGET = baseURL + "address/get";
        ADDRESSSAVE = baseURL + "address/save";
        ADDRESSDELETE = baseURL + "address/delete";
        ADDRESSUPDATE = baseURL + "address/update";


        //This is User Management URL
        USEREXIST = baseURL + "user/check";
        USERSIGNIN = baseURL + "user/old";
        USERSIGNUP = baseURL + "user/new";
        USERCHANGEPWD = baseURL + "user/verify/changepwd";


        //This is Cloth List Management URL
        CLOTHLISTMEN = baseURL + "cloth/men";
        CLOTHLISTWOMEN = baseURL + "cloth/women";
        CLOTHLISTOTHER = baseURL + "cloth/other";


        //This is Order Management URL
        ORDERGET = baseURL + "order/get";
        ORDERPLACE = baseURL + "order/place";
        ORDERCLOTHES = baseURL + "order/cloth";
        ORDERCANCEL = baseURL + "order/cancel";


        //This is Refer Management URL
        REFERWALLET = baseURL + "refer/wallet";
        REFERCHECKDEVICE = baseURL + "refer/checkdevice";
        REFERTEXT = baseURL + "refer/text";
        REFERCODE = baseURL + "refer/code";


        //This is DHPoint Management URL
        DHPOINTURL = baseURL + "dhpoint/get";


        //This is message Management URL
        MESSAGETEXTURL = baseURL + "message/text";
    }
}
