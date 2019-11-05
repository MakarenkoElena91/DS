package ie.gmit.ds;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App extends Application<Configuration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Override
    public void initialize(Bootstrap<Configuration> b) {
    }

    @Override
    public void run(Configuration c, Environment e) throws Exception {
        LOGGER.info("Registering REST resources");
        e.jersey().register(new UserApiResource(e.getValidator()));

        final BasicHealthCheck basicHealthCheck = new BasicHealthCheck();
        e.healthChecks().register("example", basicHealthCheck);
    }

    public static void main(String[] args) throws Exception {
        //new App().run(args);
        new App().run(new String[] {"server", "config.yml"});
    }
}