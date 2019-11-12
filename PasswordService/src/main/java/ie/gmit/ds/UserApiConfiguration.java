
package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class UserApiConfiguration extends Configuration {
   // @NotEmpty
    private String passwordServiceIp;
  //  @NotEmpty
    private int passwordServicePort;

    @JsonProperty
    public String getPasswordServiceIp() {
        return passwordServiceIp;
    }

    @JsonProperty
    public void setPasswordServiceIp(String passwordServiceIp) {
        this.passwordServiceIp = passwordServiceIp;
    }

    @JsonProperty
    public int getPasswordServicePort() {
        return passwordServicePort;
    }

    @JsonProperty
    public void setPasswordServicePort(int passwordServicePort) {
        this.passwordServicePort = passwordServicePort;
    }
}