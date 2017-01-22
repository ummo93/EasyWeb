import java.util.HashMap;
import java.util.Map;

import framework.Storage;


import framework.User;
import framework.App.Request;
import framework.App.Response;

public class Controller {

    public static boolean mainPage(Request req, Response res) {
        String l = req.cookie("login");
        String t = req.cookie("token");
        if(Storage.hasUser(l, t)) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("user", "GitHub user");
            return res.render("index.ftl", data);
        } else {
            return res.redirect("/login");
        }
    }
    public static boolean logIn(Request req, Response res) {
        String[] credCouple = req.query.split("&");
        User u = new User(credCouple[0].split("=")[1], credCouple[1].split("=")[1], 1);
        Storage.push(u);  
        req.cookie("login", u.login, 600);
        req.cookie("token", u.pass, 600);
        
        return res.redirect("/");
    }
    public static boolean loginPage(Request req, Response res) {
        res.type("text/html");
        return res.sendFile("login.html");
    }
}