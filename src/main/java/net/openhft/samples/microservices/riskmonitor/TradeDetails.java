package net.openhft.samples.microservices.riskmonitor;

import net.openhft.chronicle.wire.AbstractMarshallable;

import java.time.LocalDateTime;

/**
 * Created by peter on 13/07/16.
 */
public class TradeDetails extends AbstractMarshallable {
    LocalDateTime timestamp;
    String symbol;
    double price;
    double quantity;
    Side side;
    String trader;

    public TradeDetails(LocalDateTime timestamp, String symbol, double price, double quantity, Side side, String trader) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.trader = trader;
    }
}
