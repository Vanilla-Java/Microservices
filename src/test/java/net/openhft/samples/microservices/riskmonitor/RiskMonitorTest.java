package net.openhft.samples.microservices.riskmonitor;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.UnsafeMemory;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.MethodReader;
import net.openhft.chronicle.wire.ValueIn;
import org.junit.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                unsafe.putByte(address, (byte) bytes.length);
                address++;
                unsafe.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, bytes.length);
                b.writeSkip(1 + 4 + 8 + 1 + bytes.length);
            });

// dump the content of the queue
            System.out.println(queue.dump());

        }

        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(path + "/trades").build()) {
            final ExcerptTailer tailer = queue.createTailer();

// reading using method calls
            RiskMonitor monitor = System.out::println;
            MethodReader reader = tailer.methodReader(monitor);
// read one message
            assertTrue(reader.readOne());

            assertTrue(tailer.readDocument(w -> w.read("trade").marshallable(
                    m -> {
                        LocalDateTime timestamp = m.read("timestamp").dateTime();
                        String symbol = m.read("symbol").text();
                        double price = m.read("price").float64();
                        double quantity = m.read("quantity").float64();
                        Side side = m.read("side").object(Side.class);
                        String trader = m.read("trader").text();
                        // do something with values.
                    })));

            assertTrue(tailer.readDocument(w -> {
                ValueIn in = w.getValueIn();
                int num = in.int32();
                long num2 = in.int64();
                String text = in.text();
                // do something with values
            }));

            assertTrue(tailer.readBytes(in -> {
                int code = in.readByte();
                int num = in.readInt();
                long num2 = in.readLong();
                String text = in.readUtf8();
                assertEquals("Hello World", text);
                // do something with values
            }));

            assertTrue(tailer.readBytes(b -> {
                long address = b.address(b.readPosition());
                Unsafe unsafe = UnsafeMemory.UNSAFE;
                int code = unsafe.getByte(address);
                address++;
                int num = unsafe.getInt(address);
                address += 4;
                long num2 = unsafe.getLong(address);
                address += 8;
                int length = unsafe.getByte(address);
                address++;
                byte[] bytes = new byte[length];
                unsafe.copyMemory(null, address, bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, bytes.length);
                String text = new String(bytes, StandardCharsets.UTF_8);
                assertEquals("Hello World", text);
                // do something with values
            }));
        }
    }

}