package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPassword {
    private User user;
    private String password;

    public UserPassword() {
    }

    public UserPassword(String password, User user) {
        this.password = password;
        this.user = user;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserPassword{" +
                "password='" + password + '\'' +
                ", user=" + user +
                '}';
    }
}
