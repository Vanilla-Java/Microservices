package net.openhft.samples.microservices;

import net.openhft.affinity.AffinityLock;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.MethodReader;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by Peter on 25/03/2016.
 */
@State(Scope.Thread)
public class ComponentsBenchmark {

    SidedPrice sidedPrice = new SidedPrice(null, 0, null, 0, 0);
    private File upQueuePath, downQueuePath;
    private SingleChronicleQueue upQueue, downQueue;
    private SidedMarketDataListener smdWriter;
    private MethodReader reader;
    private int counter = 0;

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, RunnerException {
//        String dump = SingleChronicleQueueBuilder.binary("C:\\Users\\peter_2\\AppData\\Local\\Temp\\target\\ComponentsBenchmark-62973098209185").build().dump();
//        System.out.println(dump);
//        if (true) System.exit(0);
        ComponentsBenchmark main = new ComponentsBenchmark();
        if (OS.isLinux())
            AffinityLock.acquireLock();

        if (Jvm.isFlightRecorder()) {
            // -verbose:gc  -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=dumponexit=true,filename=myrecording.jfr,settings=profile -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints
            System.out.println("Detected Flight Recorder");
            main.setup();
            long start = System.currentTimeMillis();
            while (start + 60e3 > System.currentTimeMillis()) {
                for (int i = 0; i < 1000; i++)
                    main.benchmarkComponents();
            }
            main.tearDown();

        } else if (Jvm.isDebug()) {
            for (int i = 0; i < 10; i++) {
                runAll(main, Setup.class);
                runAll(main, Benchmark.class);
                runAll(main, TearDown.class);
            }

        } else {
            int time = Boolean.getBoolean("longTest") ? 30 : 3;
            System.out.println("measurementTime: " + time + " secs");
            Options opt = new OptionsBuilder()
                    .include(ComponentsBenchmark.class.getSimpleName())
                    .warmupIterations(8)
                    .forks(1)
                    .mode(Mode.SampleTime)
                    .measurementTime(TimeValue.seconds(time))
                    .timeUnit(TimeUnit.MICROSECONDS)
                    .build();

            new Runner(opt).run();
        }
    }

    static void runAll(ComponentsBenchmark main, Class annotationClass) throws IllegalAccessException, InvocationTargetException {
        for (Method m : ComponentsBenchmark.class.getMethods())
            if (m.getAnnotation(annotationClass) != null)
                m.invoke(main);
    }

    @Setup
    public void setup() {
        String target = OS.TMP;
        upQueuePath = new File(target, "ComponentsBenchmark-up-" + System.nanoTime());
        upQueue = SingleChronicleQueueBuilder.binary(upQueuePath).build();
        smdWriter = upQueue.createAppender().methodWriter(SidedMarketDataListener.class);

        downQueuePath = new File(target, "ComponentsBenchmark-down-" + System.nanoTime());
        downQueue = SingleChronicleQueueBuilder.binary(downQueuePath).build();
        MarketDataListener mdWriter = downQueue.createAppender().methodWriter(MarketDataListener.class);

        SidedMarketDataCombiner combiner = new SidedMarketDataCombiner(mdWriter);

        reader = upQueue.createTailer().methodReader(combiner);
        System.out.println("up-q " + upQueuePath);
    }

    @TearDown
    public void tearDown() {
        upQueue.close();
        downQueue.close();
        IOTools.shallowDeleteDirWithFiles(upQueuePath);
        IOTools.shallowDeleteDirWithFiles(downQueuePath);
    }

    @Benchmark
    public void benchmarkComponents() {
        switch (counter++ & 3) {
            case 0:
                smdWriter.onSidedPrice(sidedPrice.init("EURUSD", 123456789000L, Side.Sell, 1.1172, 1e6));
                break;
            case 1:
                smdWriter.onSidedPrice(sidedPrice.init("EURUSD", 123456789100L, Side.Buy, 1.1160, 1e6));
                break;
            case 2:
                smdWriter.onSidedPrice(sidedPrice.init("EURUSD", 123456789000L, Side.Sell, 1.1172, 2e6));
                break;
            case 3:
                smdWriter.onSidedPrice(sidedPrice.init("EURUSD", 123456789100L, Side.Buy, 1.1160, 2e6));
                break;
        }
        assertTrue(reader.readOne());
    }
}
