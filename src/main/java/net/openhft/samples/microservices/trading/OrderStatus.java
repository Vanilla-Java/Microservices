package net.openhft.samples.microservices.trading;

/**
 * Created by peter on 25/04/16.
 */
public class OrderStatus extends Order {
    final double quantityFilled, quantityOutstanding;

    public OrderStatus(String symbol, Side side, long orderId, double limitPrice, double quantity, double quantityFilled, double quantityOutstanding) {
        super(symbol, side, orderId, limitPrice, quantity);
        this.quantityFilled = quantityFilled;
        this.quantityOutstanding = quantityOutstanding;
    }
}
