package ie.gmit.ds;

import com.codahale.metrics.health.HealthCheck;

public class BasicHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
            return Result.healthy();
    }
}