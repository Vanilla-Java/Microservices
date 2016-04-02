package net.openhft.samples.microservices;

import net.openhft.chronicle.wire.AbstractMarshallable;

/**
 * Created by peter on 02/04/16.
 */
class SimpleData extends AbstractMarshallable {
    String text;
    long number;
    long ts0, ts;
}
