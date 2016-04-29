package net.openhft.samples.microservices.helloworld;

import net.openhft.chronicle.core.Jvm;
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
        AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis() + 1000);
        Thread thread = new Thread(() -> {
            ChronicleQueue output = SingleChronicleQueueBuilder.binary("output").build();
            MethodReader reader = output.createTailer().methodReader((HelloReplier) err::println);
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

        ChronicleQueue input = SingleChronicleQueueBuilder.binary("input").build();
        HelloWorld helloWorld = input.createAppender().methodWriter(HelloWorld.class);

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
