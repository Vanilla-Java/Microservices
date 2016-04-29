package net.openhft.samples.microservices.helloworld;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.MethodReader;

/**
 * Created by peter on 29/04/16.
 */
public class HelloWorldPerfMain {
    // -verbose:gc  -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=dumponexit=true,filename=myrecording.jfr,settings=profile -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints
    public static void main(String[] args) {
        int RUNS = 1000000;
        try (ChronicleQueue input = SingleChronicleQueueBuilder.binary(OS.TMP + "/input").build();
             ChronicleQueue output = SingleChronicleQueueBuilder.binary(OS.TMP + "/output").build()) {
            HelloWorld helloWorld = input.createAppender().methodWriter(HelloWorld.class);
            MethodReader reader = output.createTailer().methodReader((HelloReplier) m -> {
            });
            for (int t = 0; t < 10; t++) {
                long start = System.nanoTime();
                for (int i = 0; i < RUNS; i++) {
                    helloWorld.hello("msg " + i);
                }

                int count = 0;
                while (count < RUNS)
                    if (reader.readOne())
                        count++;
                long time = System.nanoTime() - start;
                System.out.printf("Throughput %,d messages/sec%n", RUNS * 1_000_000_000L / time);
            }
        }
    }
}
