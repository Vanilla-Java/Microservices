package net.openhft.samples.microservices;

import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * Created by peter on 24/03/16.
 */
public class OrderManagerTest {

    @Test
    public void testOnOrderIdea() {
        // what we expect to happen
        OrderListener listener = createMock(OrderListener.class);
        listener.onOrder(new Order("EURUSD", Side.Buy, 1.1167, 1_000_000));
        replay(listener);

        // build our scenario
        OrderManager orderManager = new OrderManager(listener);
        SidedMarketDataCombiner combiner = new SidedMarketDataCombiner(orderManager);

        // events in
        orderManager.onOrderIdea(new OrderIdea("EURUSD", Side.Buy, 1.1180, 2e6)); // not expected to trigger

        combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789000L, Side.Sell, 1.1172, 2e6));
        combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789100L, Side.Buy, 1.1160, 2e6));

        combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789100L, Side.Buy, 1.1167, 2e6));

        orderManager.onOrderIdea(new OrderIdea("EURUSD", Side.Buy, 1.1165, 1e6)); // expected to trigger

        verify(listener);
    }
}