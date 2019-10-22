package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger =
            Logger.getLogger(Client.class.getName());
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;
    private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordService;

    public Client(String host, int port) {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
        asyncPasswordService = PasswordServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public Optional<HashResponse> hashPassword(HashRequest hashRequest) {
        logger.info("Hashing password... " + hashRequest);
        Optional <HashResponse> hashResponse = Optional.empty();
        try {
            hashResponse = Optional.of(syncPasswordService.hash(hashRequest));
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return hashResponse;
        }
        return hashResponse;
    }


    public BoolValue validatePassword(ValidationRequest validationRequest) {
        logger.info("Validating password... " + validationRequest);
        BoolValue isValid;
        try {
            isValid = syncPasswordService.validate(validationRequest);
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return null;
        }
        if (isValid.getValue()) {
            logger.info("Validation Successful " + validationRequest);
        } else {
            logger.warning("Failed to validate password");
        }
        return isValid;
    }
    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 50551);
        String pass = Passwords.generateRandomPassword(8);
        HashRequest password = HashRequest.newBuilder()
                .setUserId(1)
                .setPassword(pass)
                .build();


        try {
            HashResponse hashResponse = client.hashPassword(password).orElseThrow(()-> new RuntimeException("It is null"));
            ValidationRequest validationRequest = ValidationRequest.newBuilder()
                    .setHashedPassword(hashResponse.getHashedPassword())
                    .setPassword(pass)
                    .setSalt(hashResponse.getSalt())
                    .build();

            BoolValue boolValue = client.validatePassword(validationRequest);
            System.out.println("Is valid? " + (boolValue.getValue() ? "valid" : "not valid"));
        } finally {
            // Don't stop process, keep alive to receive async response
            Thread.currentThread().join();
        }}
}
