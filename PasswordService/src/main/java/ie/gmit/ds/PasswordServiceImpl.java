package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

    @Override
    public void hash(HashRequest request, StreamObserver<HashResponse> responseObserver) {
        byte[] salt =  Passwords.getNextSalt();
        byte[] hashedPassword = Passwords.hash(request.getPassword().toCharArray(), salt);
        HashResponse hashResponse = HashResponse.newBuilder()
                .setUserId(request.getUserId())
                .setHashedPassword(ByteString.copyFrom(hashedPassword))
                .setSalt(ByteString.copyFrom(salt))
                .build();
        try{
            responseObserver.onNext(hashResponse);
        }catch(RuntimeException ex){
            System.out.println("Something went wrong");
        }

        responseObserver.onCompleted();
    }

    @Override
    public void validate(ValidationRequest request, StreamObserver<BoolValue> responseObserver) {
        char[] password = request.getPassword().toCharArray();
        ByteString salt = request.getSalt();
        ByteString expectedHash = request.getHashedPassword();

        boolean isExpectedPassword = Passwords.isExpectedPassword(password, salt.toByteArray(), expectedHash.toByteArray());

        try{
            responseObserver.onNext(BoolValue.newBuilder().setValue(isExpectedPassword).build());
        }catch(RuntimeException ex){
            System.out.println("Something went wrong");
        }
        responseObserver.onCompleted();
    }
}
