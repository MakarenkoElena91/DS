package ie.gmit.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDB.class);

    public static HashMap<Integer, User> users = new HashMap<>();
    static{
        users.put(1, new User(1, "Lena", "example@example.com", "Russia"));
        users.put(2, new User(2, "John", "example2@example.com", "USA"));
        users.put(3, new User(3, "Jack", "example3@example.com", "Ireland"));
    }

    public  List<User> getUsers(){
        return new ArrayList<User>(users.values());
    }

    public  User getUser(Integer id){
        return users.get(id);
    }

    public  void updateUser(Integer id, User user){
        LOGGER.info("Updating: {}", user);
        users.put(id, user);
    }

    public  void removeUser(Integer id){
        users.remove(id);
    }
}
