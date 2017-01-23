import framework.Storage;
import framework.User;
import framework.App.Request;
import framework.App.Response;

public class Authentication {

    public static boolean logIn(Request req, Response res) {
        String[] credCouple = req.query.split("&");
        User u = new User(credCouple[0].split("=")[1], credCouple[1].split("=")[1], 0);

        if(Storage.hasUser(u.login, u.pass)) {
            req.cookie("login", u.login, 600);
            req.cookie("token", u.pass, 600);
        }
        return res.redirect("/");
    }
    public static boolean loginPage(Request req, Response res) {
        res.type("text/html");
        return res.sendFile("login.html");
    }
    public static boolean logOut(Request req, Response res) {
        req.removeCookie("login");
        req.removeCookie("token");
        return res.redirect("/login");
    }
}