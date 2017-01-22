import java.util.HashMap;
import java.util.Map;
import framework.App.Request;
import framework.App.Response;

public class Controller {

    public static boolean mainPage(Request req, Response res) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("user", "GitHub user");
        return res.render("index.ftl", data);
    }

}