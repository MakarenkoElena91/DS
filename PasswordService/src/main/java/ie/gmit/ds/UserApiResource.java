package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserApiResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiResource.class);
    private final Validator validator;
    Client client = new Client("localhost", 50551);
    UserDB userDB = new UserDB();
    public UserApiResource(Validator validator) {
        this.validator = validator;
    }

    @GET
    public Response getUsers() {
        return Response.ok(userDB.getUsers()).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Integer id) {
        User user = userDB.getUser(id);
        if (user != null)
            return Response.ok(user).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    public Response createUser(User user){

            LOGGER.info("POST: createUser() - {}", user);

            HashRequest request = HashRequest.newBuilder()
                    .setUserId(user.getUserId())
                    .setPassword(user.getPassword())
                    .build();
            StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {
                User newUser;

                @Override
                public void onNext(HashResponse hashResponse) {
                    ByteString salt = hashResponse.getSalt();
                    ByteString hashedPassword = hashResponse.getHashedPassword();
                    newUser = new User (user.getUserId(), user.getUserName(), user.getEmail(), hashedPassword, salt);

                    LOGGER.info("Created new user: " + hashResponse);
                }

                @Override
                public void onError(Throwable throwable) {
//                    Status status = Status.fromThrowable(throwable);

                    LOGGER.error("RPC Error: {}", throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    userDB.updateUser(user.getUserId(), newUser);
                }
            };

            client.hashPassword(request, responseObserver);

            return Response.ok("User was added").build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUserById(@PathParam("id") Integer id, User user) {
        // validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        User e = userDB.getUser(user.getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<User> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
        }
        if (e != null) {
            user.setUserId(id);
            userDB.updateUser(id, user);
            return Response.ok(user).build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeUserById(@PathParam("id") Integer id) {
        User user = userDB.getUser(id);
        if (user != null) {
            userDB.removeUser(id);
            return Response.ok().build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }


    @POST
    @Path("/login")
    public Response login(User user) {
        User userInDB = userDB.getUser(user.getUserId());

        var pass = user.getPassword();

        ValidationRequest validationRequest = ValidationRequest.newBuilder()
                .setHashedPassword(userInDB.getHashedPassword())
                .setPassword(pass)
                .setSalt(userInDB.getSalt())
                .build();

        BoolValue boolValue = client.validatePassword(validationRequest);


        if (user != null) {
            if (boolValue.getValue()) {
                LOGGER.info("Logging: {}", user);
                return Response.ok("OK").build();
            } else return Response.status(Status.NOT_ACCEPTABLE).build();
        } else return Response.status(Status.NOT_FOUND).build();
    }
}