package net.openhft.samples.microservices.wrapper;

import net.openhft.chronicle.queue.service.ServiceWrapper;
import net.openhft.chronicle.queue.service.ServiceWrapperBuilder;

/**
 * Created by peter on 23/04/16.
 */
public class HelloWorldMain {
    static ServiceWrapper serviceWrapper;

    public static void main(String... args) {
        String input = args.length > 0 ? args[0] : "input";
        String output = args.length > 1 ? args[1] : "output";
        serviceWrapper = ServiceWrapperBuilder.serviceBuilder(input, output, HelloReplier.class, HelloWorldImpl::new).get();
    }
}
