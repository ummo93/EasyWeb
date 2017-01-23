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
        this.pass = App.encrypt(pass, "userModelSalt");
        this.role = role;
    }
    public int getRole() {
        /** Getter for freemarker */
        return this.role;
    }
    public String getLogin() {
        /** Getter for freemarker */
        return this.login;
    }
}