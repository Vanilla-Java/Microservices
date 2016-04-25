package net.openhft.samples.microservices.trading;

import net.openhft.chronicle.wire.AbstractMarshallable;

/**
 * Created by peter on 25/04/16.
 */
public class MarketData extends AbstractMarshallable {
    private String symbol;
    private double bidPrice, bidQuantity, askPrice, askQuantity;

    public MarketData(String symbol, double bidPrice, double bidQuantity, double askPrice, double askQuantity) {
        this.symbol = symbol;
        this.bidPrice = bidPrice;
        this.bidQuantity = bidQuantity;
        this.askPrice = askPrice;
        this.askQuantity = askQuantity;
    }
}
