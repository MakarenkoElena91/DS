package ie.gmit.ds;

import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private io.grpc.Server grpcServer;
    private static final Logger logger = Logger.getLogger(PasswordServiceImpl.class.getName());
    private static int PORT = 50551;

    public Server(int port) {
        this.PORT = port;
    }

    private void start() throws IOException {
        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new PasswordServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);

    }

    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(50551);
        try {
            int port = Integer.parseInt(args[0]);
            server = new Server(port);
            server.start();
        } catch (ArrayIndexOutOfBoundsException | IOException | NumberFormatException e) {
            logger.info("Something went wrong: Port number is incorrect/ Port number wasn't provided as a parameter/You're using a reserved port #. ");
            logger.info("Listening on default port 50551...");
            server = new Server(50551);
            try {
                server.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            server.blockUntilShutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
