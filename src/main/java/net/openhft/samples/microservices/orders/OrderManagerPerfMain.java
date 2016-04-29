package net.openhft.samples.microservices.orders;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.MethodReader;

/**
 * Created by peter on 29/04/16.
 */
public class OrderManagerPerfMain {
    // -verbose:gc  -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=dumponexit=true,filename=myrecording.jfr,settings=profile -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints
    public static void main(String[] args) {
        int RUNS = 1000000;
        try (ChronicleQueue input = SingleChronicleQueueBuilder.binary(OS.TMP + "/order-input").build();
             ChronicleQueue output = SingleChronicleQueueBuilder.binary(OS.TMP + "/order-output").build()) {
            OrderIdeaListener ideaListener = input.createAppender().methodWriter(OrderIdeaListener.class);
            MethodReader reader = output.createTailer().methodReader((OrderListener) m -> {
            });
            for (int t = 0; t < 10; t++) {
                long start = System.nanoTime();
                OrderIdea orderIdea = new OrderIdea("strategy1", "EUR/USD", null, 0.0, 0.0);
                for (int i = 0; i < RUNS; i++) {
                    orderIdea.limitPrice = 1.234;
                    orderIdea.quantity = 100e6;
                    orderIdea.side = i % 2 == 0 ? Side.Buy : Side.Sell;
                    ideaListener.onOrderIdea(orderIdea);
                }

                int count = 0;
                while (reader.readOne())
                    count++;
                long time = System.nanoTime() - start;
                System.out.printf("Throughput %,d messages/sec%n", RUNS * 1_000_000_000L / time);
            }
        }
    }
}
