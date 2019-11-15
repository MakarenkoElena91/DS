package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
public class UserPassword {
    private User user;
    private String password;

    public UserPassword() {
    }

    public UserPassword(User user, String password) {
        this.user = user;
        this.password = password;
    }

    @JsonProperty
    @XmlElement
    public String getPassword() {
        return password;
    }

    @JsonProperty
    @XmlElement
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
