package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Storage {

    public static List<User> container = new ArrayList<User>();
    {
        // Default user
        container.add(new User("admin", "admin", 0));
    }
    public static User getUser(String login, String pass) {
        for(User user : container) {
            if(login.equals(user.login) && pass.equals(user.pass)) {
                return user;
            };
        }
        return null;
    }

    public static boolean hasUser(User u) {
        for(User user : container) {
            if(u.login.equals(user.login)) {
                return true;
            };
        }
        return false;
    }
    public static boolean hasUser(String login, String pass) {
        for(User user : container) {
            if(login.equals(user.login) && pass.equals(user.pass)) {
                return true;
            };
        }
        return false;
    }

    public static boolean push(User u) {

        if(container.size() == 0) {
            //Если пусто
            container.add(u);
        } else {
            for(User user : container) {
                if(u.login.equals(user.login)) {
                    return false;
                };
            }
            container.add(u);
            return true;
        }
        return true;
    }

}