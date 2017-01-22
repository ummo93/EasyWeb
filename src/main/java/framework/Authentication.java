package framework;

public class Authentication {
    public static String salt = "somesalt";

    public static String encrypt(String str) {
        return App.sha1(App.MD5(str) + Authentication.salt);
    }
}