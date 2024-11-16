package productService.gRPC;

import productService.database.H2DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import io.grpc.stub.StreamObserver;
import productService.gRPC.ProductServiceGrpc.ProductServiceImplBase;

public class ProductServiceImpl extends ProductServiceImplBase {

    private static final Logger logger = Logger.getLogger(ProductServiceImpl.class.getName());

    private final H2DatabaseManager dbManager;

    public ProductServiceImpl() throws SQLException {
        dbManager = new H2DatabaseManager();
    }

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
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products")) {

            logger.info("Received request to fetch all products");

            try (ResultSet rs = stmt.executeQuery()) {
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

                logger.info("Fetched products: " + productList.getProductsList());
                responseObserver.onNext(productList.build());
                responseObserver.onCompleted();
            }
        } catch (SQLException e) {
            logger.severe("Error fetching products: " + e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override //WORKING
    public void addProduct(Product request, StreamObserver<ProductResponse> responseObserver) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, price, description, image) VALUES (?, ?, ?, ?)")) {

            logger.info("Attempting to add product: " + request);

            stmt.setString(1, request.getName());
            stmt.setFloat(2, request.getPrice());
            stmt.setString(3, request.getDescription());
            stmt.setString(4, request.getImage());
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                logger.info("Product added successfully: " + request);
                ProductResponse response = ProductResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Product added successfully")
                        .build();
                responseObserver.onNext(response);
            } else {
                logger.warning("Failed to add product: " + request);
                ProductResponse response = ProductResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Failed to add product")
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (SQLException e) {
            logger.severe("Error adding product: " + e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateProduct(Product request, StreamObserver<ProductResponse> responseObserver) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name = ?, price = ?, description = ?, image = ? WHERE id = ?")) {
            stmt.setString(1, request.getName());
            stmt.setFloat(2, request.getPrice());
            stmt.setString(3, request.getDescription());
            stmt.setString(4, request.getImage());
            stmt.setInt(5, request.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                ProductResponse response = ProductResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Product updated successfully")
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
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getProductById(ProductRequest request, StreamObserver<Product> responseObserver) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            stmt.setInt(1, request.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = Product.newBuilder()
                            .setId(rs.getInt("id"))
                            .setName(rs.getString("name"))
                            .setPrice(rs.getFloat("price"))
                            .setDescription(rs.getString("description"))
                            .setImage(rs.getString("image"))
                            .build();
                    responseObserver.onNext(product);
                } else {
                    responseObserver.onError(new RuntimeException("Product not found"));
                }
            }
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            stmt.setInt(1, request.getId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
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
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }
}
