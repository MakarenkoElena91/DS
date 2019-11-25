package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
    private int userId;

    @NotEmpty
    private String userName;

    @Pattern(regexp=".+@.+\\.[a-z]+")
    private String email;

    private String hashedPassword;

    private String salt;

    public User(){
    }

    public User(int userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public User(int userId, String userName, String email, String hashedPassword, String salt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    @JsonProperty
    @XmlElement
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    @XmlElement
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    @XmlElement
    public String getEmail() {
        return email;
    }

    @JsonProperty
    @XmlElement
    public String getHashedPassword() {
        return hashedPassword;
    }

    @JsonProperty
    @XmlElement
    public String getSalt() {
        return salt;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
