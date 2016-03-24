package net.openhft.samples.microservices;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.MethodReader;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Test;

import java.io.File;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testWithQueue() {
        File queuePath = new File(OS.TARGET, "testWithQueue-" + System.nanoTime());
        try {
            try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
                OrderIdeaListener orderManager = queue.createAppender().methodWriter(OrderIdeaListener.class, MarketDataListener.class);
                SidedMarketDataCombiner combiner = new SidedMarketDataCombiner((MarketDataListener) orderManager);

                // events in
                orderManager.onOrderIdea(new OrderIdea("EURUSD", Side.Buy, 1.1180, 2e6)); // not expected to trigger

                combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789000L, Side.Sell, 1.1172, 2e6));
                combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789100L, Side.Buy, 1.1160, 2e6));

                combiner.onSidedPrice(new SidedPrice("EURUSD", 123456789100L, Side.Buy, 1.1167, 2e6));

                orderManager.onOrderIdea(new OrderIdea("EURUSD", Side.Buy, 1.1165, 1e6)); // expected to trigger
            }

// what we expect to happen
            OrderListener listener = createMock(OrderListener.class);
            listener.onOrder(new Order("EURUSD", Side.Buy, 1.1167, 1_000_000));
            replay(listener);

            try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
                // build our scenario
                OrderManager orderManager = new OrderManager(listener);
                MethodReader reader = queue.createTailer().methodReader(orderManager);
                for (int i = 0; i < 5; i++)
                    assertTrue(reader.readOne());

                assertFalse(reader.readOne());
                System.out.println(queue.dump());
            }

            verify(listener);
        } finally {
            IOTools.shallowDeleteDirWithFiles(queuePath);
        }
    }
}