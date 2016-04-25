package net.openhft.samples.microservices.trading;

public interface GUIGateway {
    void enableMarketData(boolean enabled);

    void newOrder(Order order);
}