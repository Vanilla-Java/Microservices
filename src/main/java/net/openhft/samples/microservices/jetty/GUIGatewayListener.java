package net.openhft.samples.microservices.jetty;

public interface GUIGatewayListener {
    void market(MarketData marketData);

    void order(OrderStatus orderStatus);
}