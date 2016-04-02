package net.openhft.samples.microservices;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;
import net.openhft.chronicle.core.util.NanoSampler;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.AbstractMarshallable;

import java.util.UUID;

/**
 * Created by peter on 01/04/16.
 */
public class MultiThreadedMain {
    private static final int THROUGHPUT = Integer.getInteger("message.throughput", 400_000);
    private static final int MESSAGE_COUNT = Integer.getInteger("message.count", THROUGHPUT * 120);
    private static final boolean ACCOUNT_FOR_COORDINATED_OMMISSION = true;

    public static void main(String[] args) {
        JLBHOptions jlbhOptions = new JLBHOptions()
                .warmUpIterations(50_000)
                .iterations(MESSAGE_COUNT)
                .throughput(THROUGHPUT)
                .runs(6)
                .recordOSJitter(true)
                .pauseAfterWarmupMS(500)
                .accountForCoordinatedOmmission(ACCOUNT_FOR_COORDINATED_OMMISSION)
                .jlbhTask(new MultiThreadedMainTask());
        new JLBH(jlbhOptions).start();
    }

    interface Service {
        void simpleCall(SimpleData data);
    }

    static class MultiThreadedMainTask implements JLBHTask {

        UUID uuid = UUID.randomUUID();
        String queueIn = OS.TMP + "/MultiThreadedMain/" + uuid + "/pathIn";
        String queue2 = OS.TMP + "/MultiThreadedMain/" + uuid + "/stage2";
        String queue3 = OS.TMP + "/MultiThreadedMain/" + uuid + "/stage3";
        String queueOut = OS.TMP + "/MultiThreadedMain/" + uuid + "/pathOut";
        int counter = 1;
        private Service serviceIn;
        private ServiceWrapper<ServiceImpl, Service> service2, service3, serviceOut;
        private SimpleData data;

        @Override
        public void init(JLBH jlbh) {

            serviceIn = SingleChronicleQueueBuilder.binary(queueIn).build().createAppender().methodWriter(Service.class);
            service2 = new ServiceWrapper<>(queueIn, queue2, new ServiceImpl(jlbh.addProbe("Service 2")));
            service3 = new ServiceWrapper<>(queue2, queue3, new ServiceImpl(jlbh.addProbe("Service 3")));
            serviceOut = new ServiceWrapper<>(queue3, queueOut, new ServiceImpl(jlbh.addProbe("Service Out"), jlbh));

            data = new SimpleData();
        }

        @Override
        public void run(long startTimeNS) {
            data.text = "Hello";
            data.number = counter++;
            data.ts0 = data.ts = System.nanoTime();
            serviceIn.simpleCall(data);
        }

        @Override
        public void complete() {
            IOTools.deleteDirWithFiles(queueIn, 2);
            IOTools.deleteDirWithFiles(queue2, 2);
            IOTools.deleteDirWithFiles(queue3, 2);
            IOTools.deleteDirWithFiles(queueOut, 2);
        }
    }

    static class ServiceImpl implements Service, ServiceHandler<Service> {
        private final NanoSampler nanoSampler;
        private final NanoSampler endToEnd;
        private Service output;

        public ServiceImpl(NanoSampler nanoSampler) {
            this(nanoSampler, t -> {
            });
        }

        public ServiceImpl(NanoSampler nanoSampler, NanoSampler endToEnd) {
            this.nanoSampler = nanoSampler;
            this.endToEnd = endToEnd;
        }

        @Override
        public void init(Service output) {
            this.output = output;
        }

        @Override
        public void simpleCall(SimpleData data) {
            data.number *= 10;

            long time = System.nanoTime();
            nanoSampler.sampleNanos(time - data.ts);
            data.ts = time;

            output.simpleCall(data);
            endToEnd.sampleNanos(System.nanoTime() - data.ts0);
        }
    }

    static class SimpleData extends AbstractMarshallable {
        String text;
        long number;
        long ts0, ts;
    }
}
