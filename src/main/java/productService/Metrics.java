package productService;

import io.prometheus.client.Counter;

public class Metrics {
    public static final Counter totalRequests = Counter.build()
            .name("product_service_requests_total")
            .help("Total number of requests to the product service")
            .register();
}
