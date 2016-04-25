package net.openhft.samples.microservices.trading;

public interface GUIGatewayListener {
    void market(MarketData marketData);

    void order(OrderStatus orderStatus);
}