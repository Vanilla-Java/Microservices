package net.openhft.samples.microservices.orders;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.service.ServiceWrapper;
import net.openhft.chronicle.queue.service.ServiceWrapperBuilder;

/**
 * Created by peter on 29/04/16.
 */
public class OrderManagerMain {
    static ServiceWrapper serviceWrapper;

    public static void main(String... args) {
        String input = args.length > 0 ? args[0] : OS.TMP + "/order-input";
        String output = args.length > 1 ? args[1] : OS.TMP + "/order-output";
        serviceWrapper = ServiceWrapperBuilder.serviceBuilder(input, output,
                OrderListener.class, OrderManager::new).get();
        System.out.println("Started");
    }
}
