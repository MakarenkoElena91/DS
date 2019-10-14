package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.logging.Logger;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

    @Override
    public void hash(HashRequest request, StreamObserver<HashResponse> responseObserver) {
        byte[] salt =  Passwords.getNextSalt();
        byte[] hash = Passwords.hash(request.getPassword().toCharArray(), salt);
        HashResponse loginResponse = HashResponse.newBuilder()
                .setUserId(request.getUserId())
                .setHash(ByteString.copyFrom(hash))
                .setSalt(ByteString.copyFrom(salt))
                .build();
        responseObserver.onNext(loginResponse);
        responseObserver.onCompleted();

    }

    @Override
    public void validate(ValidationRequest request, StreamObserver<BoolValue> responseObserver) {
        char[] password = request.getPassword().toCharArray();
        ByteString salt = request.getSalt();
        ByteString expectedHash = request.getHash();

        boolean isExpectedPassword = Passwords.isExpectedPassword(password, salt.toByteArray(), expectedHash.toByteArray());
        responseObserver.onNext(BoolValue.newBuilder().setValue(isExpectedPassword).build());
        responseObserver.onCompleted();
    }
}
