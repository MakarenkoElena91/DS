package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

    @Override
    public void hash(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        byte[] salt =  Passwords.getNextSalt();
        byte[] hashedPassword = Passwords.hash(request.getPassword().toCharArray(), salt);
        LoginResponse loginResponse = LoginResponse.newBuilder()
                .setUserId(request.getUserId())
                .setHashedPassword(String.valueOf(hashedPassword))
                .setSalt(String.valueOf(salt))
                .build();
        responseObserver.onNext(loginResponse);
        responseObserver.onCompleted();

    }

    @Override
    public void validate(ValidationRequest request, StreamObserver<BoolValue> responseObserver) {
        char[] password = request.getPassword().toCharArray();
        byte[] salt = request.getSalt().getBytes();
        byte[] expectedHash = request.getHashedPassword().getBytes();

        boolean isExpectedPassword = Passwords.isExpectedPassword(password, salt, expectedHash);
        responseObserver.onNext(BoolValue.newBuilder().setValue(isExpectedPassword).build());
        responseObserver.onCompleted();
    }
}
