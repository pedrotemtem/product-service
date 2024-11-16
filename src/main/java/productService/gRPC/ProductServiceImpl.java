package productService.gRPC;

import productService.database.H2DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import io.grpc.stub.StreamObserver;
import productService.gRPC.ProductServiceGrpc.ProductServiceImplBase;

public class ProductServiceImpl extends ProductServiceImplBase {

    private final H2DatabaseManager dbManager;

    public ProductServiceImpl() throws SQLException {
        dbManager = new H2DatabaseManager();
    }

    private final List<Product> products = new ArrayList<>();

    @Override
    public void healthCheck(Empty request, StreamObserver<HealthResponse> responseObserver) {
        HealthResponse response = HealthResponse.newBuilder()
                .setHealthy(true)
                .setMessage("Product service is healthy")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProducts(Empty request, StreamObserver<ProductList> responseObserver) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products");
             ResultSet rs = stmt.executeQuery()) {

            ProductList.Builder productList = ProductList.newBuilder();
            while (rs.next()) {
                Product product = Product.newBuilder()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setPrice(rs.getFloat("price"))
                        .setDescription(rs.getString("description"))
                        .setImage(rs.getString("image"))
                        .build();
                productList.addProducts(product);
            }

            responseObserver.onNext(productList.build());
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addProduct(Product request, StreamObserver<ProductResponse> responseObserver) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (id, name, price, description, image) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, request.getId());
            stmt.setString(2, request.getName());
            stmt.setFloat(3, request.getPrice());
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getImage());
            stmt.executeUpdate();

            ProductResponse response = ProductResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product added successfully")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateProduct(Product request, StreamObserver<ProductResponse> responseObserver) {
        Product existingProduct = products.stream()
                .filter(product -> product.getId() == request.getId())
                .findFirst()
                .orElse(null);

        if (existingProduct == null) {
            ProductResponse response = ProductResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Product not found")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        // Update product details
        int index = products.indexOf(existingProduct);
        if (index != -1) {
            products.set(index, request);
        }

        ProductResponse response = ProductResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Product updated successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(ProductRequest request, StreamObserver<Product> responseObserver) {
        Product product = products.stream()
                .filter(p -> p.getId() == request.getId())
                .findFirst()
                .orElse(null);

        if (product == null) {
            responseObserver.onError(new RuntimeException("Product not found"));
        } else {
            responseObserver.onNext(product);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = products.stream()
                .filter(p -> p.getId() == request.getId())
                .findFirst()
                .orElse(null);

        if (product != null) {
            products.remove(product);
            ProductResponse response = ProductResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product deleted successfully")
                    .build();
            responseObserver.onNext(response);
        } else {
            ProductResponse response = ProductResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Product not found")
                    .build();
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }
}
