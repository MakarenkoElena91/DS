package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;


public class User {
//    @NotEmpty
    private int userId;

    @NotEmpty
    private String userName;

    @Pattern(regexp=".+@.+\\.[a-z]+")
    private String email;

//    @NotEmpty
   // private String password;

//    @NotEmpty
    private ByteString hashedPassword;

//    @NotEmpty
    private ByteString salt;

    public User(){
    }

    public User(int userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public User(int userId, String userName, String email, ByteString hashedPassword, ByteString salt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    public ByteString getHashedPassword() {
        return hashedPassword;
    }

    public ByteString getSalt() {
        return salt;
    }

}
