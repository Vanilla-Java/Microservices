package net.openhft.samples.microservices.riskmonitor;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.UnsafeMemory;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Created by peter on 13/07/16.
 */
public class RiskMonitorTest {
    @Test
    public void trade() throws IOException {
        String path = OS.TARGET + "/deleteme-" + System.nanoTime();

        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(path + "/trades").build()) {
            final ExcerptAppender appender = queue.acquireAppender();

            // using the method writer interface.
            RiskMonitor riskMonitor = appender.methodWriter(RiskMonitor.class);
            final LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
            riskMonitor.trade(new TradeDetails(now, "GBPUSD", 1.3095, 10e6, Side.Buy, "peter"));

            // writing a self describing message
            appender.writeDocument(w -> w.write("trade").marshallable(
                    m -> m.write("timestamp").dateTime(now)
                            .write("symbol").text("EURUSD")
                            .write("price").float64(1.1101)
                            .write("quantity").float64(15e6)
                            .write("side").object(Side.class, Side.Sell)
                            .write("trader").text("peter")));

            // writing just data
            appender.writeDocument(w -> w
                    .getValueOut().int32(0x123456)
                    .getValueOut().int64(0x999000999000L)
                    .getValueOut().text("Hello World"));

            // writing raw data
            appender.writeBytes(b -> b
                    .writeByte((byte) 0x12)
                    .writeInt(0x345678)
                    .writeLong(0x999000999000L)
                    .writeUtf8("Hello World"));

            // Unsafe low level
            appender.writeBytes(b -> {
                long address = b.address(b.writePosition());
                Unsafe unsafe = UnsafeMemory.UNSAFE;
                unsafe.putByte(address, (byte) 0x12);
                address += 1;
                unsafe.putInt(address, 0x345678);
                address += 4;
                unsafe.putLong(address, 0x999000999000L);
                address += 8;
                byte[] bytes = "Hello World".getBytes(StandardCharsets.ISO_8859_1);
                unsafe.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, bytes.length);
                b.writeSkip(1 + 4 + 8 + bytes.length);
            });

            // dump the content of the queue
            System.out.println(queue.dump());
        }
    }

}