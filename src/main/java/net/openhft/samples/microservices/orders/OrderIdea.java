package net.openhft.samples.microservices.orders;

import net.openhft.chronicle.wire.AbstractMarshallable;

/**
 * Created by peter on 24/03/16.
 */
public class OrderIdea extends AbstractMarshallable {
    String strategy;
    String symbol;
    Side side;
    double limitPrice, quantity;

    public OrderIdea(String strategy, String symbol, Side side, double limitPrice, double quantity) {
        this.strategy = strategy;
        this.symbol = symbol;
        this.side = side;
        this.limitPrice = limitPrice;
        this.quantity = quantity;
    }
}
