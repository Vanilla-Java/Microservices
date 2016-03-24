package net.openhft.samples.microservices;

import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.wire.Marshallable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by peter on 24/03/16.
 */
public class SidedPriceTest {
    static {
        ClassAliasPool.CLASS_ALIASES.addAlias(Side.class);
        ClassAliasPool.CLASS_ALIASES.addAlias(SidedPrice.class);
    }

    @Test
    public void testToStringEqualsHashCode() {
        SidedPrice sp = new SidedPrice("Symbol", 123456789000L, Side.Buy, 1.2345, 1_000_000);
        assertEquals("!SidedPrice {\n" +
                "  symbol: Symbol,\n" +
                "  timestamp: 123456789000,\n" +
                "  side: Buy,\n" +
                "  price: 1.2345,\n" +
                "  quantity: 1000000.0\n" +
                "}\n", sp.toString());

// from string
        SidedPrice sp2 = Marshallable.fromString(sp.toString());
        assertEquals(sp2, sp);
        assertEquals(sp2.hashCode(), sp.hashCode());
    }
}