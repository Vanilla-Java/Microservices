package net.openhft.samples.microservices;

import net.openhft.chronicle.core.util.NanoSampler;

/**
 * Created by peter on 02/04/16.
 */
class ServiceImpl implements Service, ServiceHandler<Service> {
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
