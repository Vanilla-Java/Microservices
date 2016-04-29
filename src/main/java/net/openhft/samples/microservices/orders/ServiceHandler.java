package net.openhft.samples.microservices.orders;

/**
 * Created by peter on 01/04/16.
 */
public interface ServiceHandler<O> {
    void init(O output);
}
