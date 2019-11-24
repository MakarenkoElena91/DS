package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/users")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UserApiResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiResource.class);
    private final Validator validator;
    private final Client client;
    private final UserDB userDB = new UserDB();

    public UserApiResource(Validator validator, UserApiConfiguration c) {
        this.validator = validator;
        this.client = new Client(c.getPasswordServiceIp(), c.getPasswordServicePort());
    }

    @GET
    public Response getUsers() {
        // https://stackoverflow.com/a/18240578/5322506
        GenericEntity<List<User>> gList = new GenericEntity<>(userDB.getUsers()) {
        };
        return Response.ok(gList).build();
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
    public Response createUser(UserPassword userPassword) {
        LOGGER.info("POST: createUser() - {}", userPassword);

        User e = userDB.getUser(userPassword.getUser().getUserId());
        if (e == null) {
            HashRequest request = HashRequest.newBuilder()
                    .setUserId(userPassword.getUser().getUserId())
                    .setPassword(userPassword.getPassword())
                    .build();
            StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {
                User newUser;

                @Override
                public void onNext(HashResponse hashResponse) {
                    ByteString salt = hashResponse.getSalt();
                    ByteString hashedPassword = hashResponse.getHashedPassword();
                    newUser = new User(userPassword.getUser().getUserId(), userPassword.getUser().getUserName(), userPassword.getUser().getEmail(), hashedPassword, salt);
                    userDB.updateUser(newUser.getUserId(), newUser);
                    LOGGER.info("Created new user: " + hashResponse);
                }

                @Override
                public void onError(Throwable throwable) {
                    LOGGER.error("RPC Error: {}", throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    userDB.updateUser(userPassword.getUser().getUserId(), newUser);
                }
            };

            client.hashPassword(request, responseObserver);

            return Response.ok("User was added").build();
        } else {
            return Response.status(Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUserById(@PathParam("id") Integer id, UserPassword userPassword) {

        Set<ConstraintViolation<UserPassword>> violations = validator.validate(userPassword);
        User e = userDB.getUser(userPassword.getUser().getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<UserPassword> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
        }
        if (e != null) {
            HashRequest request = HashRequest.newBuilder()
                    .setUserId(userPassword.getUser().getUserId())
                    .setPassword(userPassword.getPassword())
                    .build();
            StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {
                User newUser;

                @Override
                public void onNext(HashResponse hashResponse) {
                    ByteString salt = hashResponse.getSalt();
                    ByteString hashedPassword = hashResponse.getHashedPassword();
                    newUser = new User(userPassword.getUser().getUserId(), userPassword.getUser().getUserName(), userPassword.getUser().getEmail(), hashedPassword, salt);

                    LOGGER.info("Updated new user: " + hashResponse);
                }

                @Override
                public void onError(Throwable throwable) {
                    LOGGER.error("RPC Error: {}", throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    userDB.updateUser(userPassword.getUser().getUserId(), newUser);
                }
            };

            client.hashPassword(request, responseObserver);

            userPassword.getUser().setUserId(id);
            userDB.updateUser(id, userPassword.getUser());
            return Response.ok("User was updated: {}").build();
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
    public Response login(UserPassword userPassword) {
        Response response;
        try {
            User userInDB = userDB.getUser(userPassword.getUser().getUserId());
            String pass = userPassword.getPassword();

            ValidationRequest validationRequest = ValidationRequest.newBuilder()
                    .setHashedPassword(userInDB.getHashedPassword())
                    .setPassword(pass)
                    .setSalt(userInDB.getSalt())
                    .build();
            BoolValue boolValue = client.validatePassword(validationRequest);

            if (userPassword != null) {
                if (boolValue.getValue()) {
                    LOGGER.info("Logging: {}", userPassword);
                    response = Response.ok("OK").build();
                } else response = Response.status(Status.NOT_ACCEPTABLE).build();
            } else response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
            response = Response.status(Status.SERVICE_UNAVAILABLE).build();
        }
        return response;
    }
}