package net.openhft.samples.microservices.orders;

/**
 * Created by peter on 24/03/16.
 */
public interface OrderListener {
    void onOrder(Order order);
}
