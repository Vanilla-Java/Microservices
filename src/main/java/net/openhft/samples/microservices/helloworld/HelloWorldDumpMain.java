package net.openhft.samples.microservices.helloworld;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

/**
 * Created by peter on 23/04/16.
 */
public class HelloWorldDumpMain {
    public static void main(String... args) {
        String input = args.length > 0 ? args[0] : OS.TMP + "/input";
        String output = args.length > 1 ? args[1] : OS.TMP + "/output";
        try (ChronicleQueue inputQ = SingleChronicleQueueBuilder.binary(input).build();
             ChronicleQueue outputQ = SingleChronicleQueueBuilder.binary(output).build()) {
            System.out.println(inputQ.dump());
            System.out.println(outputQ.dump());
        }
    }
}
