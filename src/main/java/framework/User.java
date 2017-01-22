package framework;

import java.util.HashMap;
import java.util.Map;


public class User {

    public int role;
    public String login;
    public String pass;
    public Map<String, String> memory = new HashMap<String, String>();

    public User(String login, String pass, int role) {
        this.login = login;
        this.pass = Authentication.encrypt(pass);
        this.role = role;
    }
}