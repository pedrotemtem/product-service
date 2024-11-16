package productService.gRPC;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.sql.SQLException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new ProductServiceImpl())
                .build();

        System.out.println("Server started, listening on 8080");
        server.start();
        server.awaitTermination();
    }
}