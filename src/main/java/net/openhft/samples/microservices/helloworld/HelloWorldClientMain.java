package net.openhft.samples.microservices.helloworld;

import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.MethodReader;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Created by peter on 23/04/16.
 */
public class HelloWorldClientMain {
    public static void main(String[] args) {
        String input = args.length > 0 ? args[0] : OS.TMP + "/input";
        String output = args.length > 1 ? args[1] : OS.TMP + "/output";

        AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis() + 1000);
        Thread thread = new Thread(() -> {
            ChronicleQueue outputQ = SingleChronicleQueueBuilder.binary(output).build();
            MethodReader reader = outputQ.createTailer().methodReader((HelloReplier) err::println);
            while (!Thread.interrupted()) {
                if (reader.readOne()) {
                    lastUpdate.set(System.currentTimeMillis());
                } else {
                    Jvm.pause(10);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        ChronicleQueue inputQ = SingleChronicleQueueBuilder.binary(input).build();
        HelloWorld helloWorld = inputQ.createAppender().methodWriter(HelloWorld.class);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            while (System.currentTimeMillis() < lastUpdate.get() + 30)
                Thread.yield();

            out.print("Chat ");
            out.flush();
            if (!scanner.hasNextLine())
                break;
            String line = scanner.nextLine();
            helloWorld.hello(line);
            lastUpdate.set(System.currentTimeMillis());
        }
        out.print("Bye");
    }
}
