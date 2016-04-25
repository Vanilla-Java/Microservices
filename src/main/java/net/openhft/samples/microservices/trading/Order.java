package net.openhft.samples.microservices.trading;

import net.openhft.chronicle.wire.AbstractMarshallable;

/**
 * Created by peter on 24/03/16.
 */
public class Order extends AbstractMarshallable {
    final String symbol;
    final Side side;
    final long orderId;
    final double limitPrice, quantity;

    public Order(String symbol, Side side, long orderId, double limitPrice, double quantity) {
        this.symbol = symbol;
        this.side = side;
        this.orderId = orderId;
        this.limitPrice = limitPrice;
        this.quantity = quantity;
    }
}
