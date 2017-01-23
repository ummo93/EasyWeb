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
            User user = Storage.getUser(l, t);
            Map<String, User> data = new HashMap<String, User>();
            data.put("user", user);
            return res.render("index.ftl", data);
        } else {
            return res.redirect("/login");
        }
    }
}