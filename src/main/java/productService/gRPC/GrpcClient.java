package productService.gRPC;

import productService.gRPC.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class GrpcClient {
    public static void main(String[] args) {
        // Initialize channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create blocking stub
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Health Check");
            System.out.println("2. Add Product");
            System.out.println("3. Fetch All Products");
            System.out.println("4. Update Product");
            System.out.println("5. Fetch Product by ID");
            System.out.println("6. Delete Product");
            System.out.println("7. Run Preset Sequence");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.println("Checking Product Service Health...");
                        HealthResponse healthResponse = stub.healthCheck(Empty.newBuilder().build());
                        System.out.println("Health Check Response: " + healthResponse.getMessage());
                        break;
                    case 2:
                        System.out.println("Adding a New Product...");
                        Product newProduct = Product.newBuilder()
                                .setId(1)
                                .setName("Dog Collar Tag")
                                .setPrice(5.99f)
                                .setDescription("A stylish collar tag for dogs.")
                                .setImage("/images/dog_collar.png")
                                .build();
                        ProductResponse addResponse = stub.addProduct(newProduct);
                        System.out.println("Add Product Response: " + addResponse.getMessage());
                        break;
                    case 3:
                        System.out.println("Fetching All Products...");
                        ProductList productList = stub.getProducts(Empty.newBuilder().build());
                        productList.getProductsList().forEach(product -> System.out.println("Product: " + product));
                        break;
                    case 4:
                        System.out.println("Updating Product...");
                        System.out.print("Enter Product ID: ");
                        int updateProductId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter new name (leave blank to keep current): ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new price (leave blank to keep current): ");
                        String newPriceStr = scanner.nextLine();
                        System.out.print("Enter new description (leave blank to keep current): ");
                        String newDescription = scanner.nextLine();
                        System.out.print("Enter new image URL (leave blank to keep current): ");
                        String newImage = scanner.nextLine();

                        Product.Builder productBuilder = Product.newBuilder().setId(updateProductId);
                        if (!newName.isEmpty()) {
                            productBuilder.setName(newName);
                        }
                        if (!newPriceStr.isEmpty()) {
                            productBuilder.setPrice(Float.parseFloat(newPriceStr));
                        }
                        if (!newDescription.isEmpty()) {
                            productBuilder.setDescription(newDescription);
                        }
                        if (!newImage.isEmpty()) {
                            productBuilder.setImage(newImage);
                        }

                        Product updatedProduct = productBuilder.build();
                        ProductResponse updateResponse = stub.updateProduct(updatedProduct);
                        System.out.println("Update Product Response: " + updateResponse.getMessage());
                        break;
                    case 5:
                        System.out.println("Fetching Product by ID...");
                        System.out.print("Enter Product ID: ");
                        int productId = scanner.nextInt();
                        ProductRequest productRequest = ProductRequest.newBuilder().setId(productId).build();
                        Product fetchedProduct = stub.getProductById(productRequest);
                        System.out.println("Fetched Product: " + fetchedProduct);
                        break;
                    case 6:
                        System.out.println("Deleting Product...");
                        System.out.print("Enter Product ID: ");
                        int deleteProductId = scanner.nextInt();
                        ProductRequest deleteRequest = ProductRequest.newBuilder().setId(deleteProductId).build();
                        ProductResponse deleteResponse = stub.deleteProduct(deleteRequest);
                        System.out.println("Delete Product Response: " + deleteResponse.getMessage());
                        break;
                    case 7:
                        runPresetSequence(stub);
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        channel.shutdown();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void runPresetSequence(ProductServiceGrpc.ProductServiceBlockingStub stub) {
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
        }
    }
}