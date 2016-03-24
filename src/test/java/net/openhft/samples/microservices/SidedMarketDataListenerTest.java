package net.openhft.samples.microservices;

import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * Created by peter on 24/03/16.
 */
public class SidedMarketDataListenerTest {
    @Test
    public void testOnSidedPrice() {
// what we expect to happen
        SidedPrice sp = new SidedPrice("Symbol", 123456789000L, Side.Buy, 1.2345, 1_000_000);
        SidedMarketDataListener listener = createMock(SidedMarketDataListener.class);
        listener.onSidedPrice(sp);
        replay(listener);

// what happens
        listener.onSidedPrice(sp);

// verify we got everything we expected.
        verify(listener);
    }
}
