package net.openhft.samples.microservices.jetty;

/**
 * Created by daniel on 26/04/2016.
 */
public interface GatewayPublisher {
    void marketData(MarketData marketData);

    void orderStatus(Order order);
}
