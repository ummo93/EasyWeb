import com.appartika.easyweb.Request;
import com.appartika.easyweb.Response;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    public static boolean mainPage(Request req, Response res) {
        Map<String, String> data = new HashMap<>();
        data.put("user", "GitHub user");
        return res.render("index.ftl", data);
    }
}