import java.util.HashMap;
import java.util.Map;

/**
 * Environment
 */
public class Environment {
//TODO тут будет ссылка на .env файл
    public static HashMap<String, String> getEnv() {
        HashMap envs = new HashMap<String, String> ();
        envs.put("host", "mysql://127.0.0.1");
        envs.put("port", "3306");
        envs.put("username", "root");
        envs.put("pass", "root");
        envs.put("schema", "test");
        return envs;
    }
}