package net.openhft.samples.microservices.jetty;

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

    public OrderStatus(Order order) {
        this.symbol = order.symbol;
        this.side = order.side;
        this.orderId = order.orderId;
        this.limitPrice = order.limitPrice;
        this.quantity = order.quantity;
        quantityFilled = 0;
        quantityOutstanding = 0;
    }
}
