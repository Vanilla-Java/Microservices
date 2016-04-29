package net.openhft.samples.microservices.jetty;

public interface GUIGateway {
    void enableMarketData(boolean enabled);

    void newOrder(Order order);
}