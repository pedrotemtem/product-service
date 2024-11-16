package productService.gRPC;

import productService.gRPC.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) {
        // Initialize channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create blocking stub
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);

        try {
            // 1. Health Check
            System.out.println("Checking Product Service Health...");
            HealthResponse healthResponse = stub.healthCheck(Empty.newBuilder().build());
            System.out.println("Health Check Response: " + healthResponse.getMessage());

            // 2. Add Product
            System.out.println("\nAdding a New Product...");
            Product newProduct = Product.newBuilder()
                    .setId(1)
                    .setName("Dog Collar Tag")
                    .setPrice(5.99f)
                    .setDescription("A stylish collar tag for dogs.")
                    .setImage("/images/dog_collar.png")
                    .build();
            ProductResponse addResponse = stub.addProduct(newProduct);
            System.out.println("Add Product Response: " + addResponse.getMessage());

            // 3. Fetch All Products
            System.out.println("\nFetching All Products...");
            ProductList productList = stub.getProducts(Empty.newBuilder().build());
            productList.getProductsList().forEach(product -> System.out.println("Product: " + product));

            // 4. Update Product
            System.out.println("\nUpdating Product...");
            Product updatedProduct = Product.newBuilder()
                    .setId(1)
                    .setName("Updated Dog Collar Tag")
                    .setPrice(7.99f)
                    .setDescription("A new and improved stylish collar tag for dogs.")
                    .setImage("/images/updated_dog_collar.png")
                    .build();
            ProductResponse updateResponse = stub.updateProduct(updatedProduct);
            System.out.println("Update Product Response: " + updateResponse.getMessage());

            // 5. Fetch Product by ID
            System.out.println("\nFetching Product by ID...");
            ProductRequest productRequest = ProductRequest.newBuilder().setId(1).build();
            Product fetchedProduct = stub.getProductById(productRequest);
            System.out.println("Fetched Product: " + fetchedProduct);

            // 6. Delete Product
            System.out.println("\nDeleting Product...");
            ProductResponse deleteResponse = stub.deleteProduct(productRequest);
            System.out.println("Delete Product Response: " + deleteResponse.getMessage());

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            // Shut down channel
            channel.shutdown();
            System.out.println("\nClient shut down.");
        }
    }
}
