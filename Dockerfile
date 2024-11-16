FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim

# Copy the built .jar file from the Maven build stage to the runtime stage
COPY --from=build /app/target/productService-gRPC-1.0-SNAPSHOT.jar /app/productService-gRPC.jar

# Set the working directory in the container for runtime
WORKDIR /app

# Command to run the application
CMD ["java", "-cp", "productService-gRPC.jar", "productService.gRPC.GrpcServer"]
