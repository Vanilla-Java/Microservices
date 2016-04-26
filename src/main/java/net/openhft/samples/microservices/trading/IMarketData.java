package net.openhft.samples.microservices.trading;

/**
 * Created by daniel on 26/04/2016.
 */
public interface IMarketData {
    void symbol(String symbol);
    void bidPrice(double bidPrice);
    void bidQuantity(double bidQuantity);
    void askPrice(double askPrice);
    void askQuantity(double askQuantity);
}
