
package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlElement;

public class UserApiConfiguration extends Configuration {
    private String passwordServiceIp;
    private int passwordServicePort;

    @JsonProperty
    @XmlElement
    public String getPasswordServiceIp() {
        return passwordServiceIp;
    }

    @JsonProperty
    @XmlElement
    public void setPasswordServiceIp(String passwordServiceIp) {
        this.passwordServiceIp = passwordServiceIp;
    }

    @JsonProperty
    @XmlElement
    public int getPasswordServicePort() {
        return passwordServicePort;
    }

    @JsonProperty
    @XmlElement
    public void setPasswordServicePort(int passwordServicePort) {
        this.passwordServicePort = passwordServicePort;
    }
}