package net.openhft.samples.microservices.orders;

import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * Created by peter on 24/03/16.
 */
public class SidedMarketDataCombinerTest {
    @Test
    public void testOnSidedPrice() {
        MarketDataListener listener = createMock(MarketDataListener.class);
        listener.onTopOfBookPrice(new TopOfBookPrice("EURUSD", 123456789000L, 1.1167, 1_000_000, Double.NaN, 0));
        listener.onTopOfBookPrice(new TopOfBookPrice("EURUSD", 123456789100L, 1.1167, 1_000_000, 1.1172, 2_000_000));
        replay(listener);

        SidedMarketDataListener combiner = new SidedMarketDataCombiner(listener);
        combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789000L, Side.Buy, 1.1167, 1e6));
        combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789100L, Side.Sell, 1.1172, 2e6));

        verify(listener);
    }
}