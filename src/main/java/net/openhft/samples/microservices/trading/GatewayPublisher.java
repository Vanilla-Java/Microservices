package net.openhft.samples.microservices.trading;

/**
 * Created by daniel on 26/04/2016.
 */
public interface GatewayPublisher {
    void marketData(MarketData marketData);

    void orderStatus(Order order);
}
