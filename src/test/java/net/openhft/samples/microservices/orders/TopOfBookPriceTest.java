package net.openhft.samples.microservices.orders;

import net.openhft.chronicle.wire.Marshallable;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by peter on 24/03/16.
 */
public class TopOfBookPriceTest {
/*
    static {
        ClassAliasPool.CLASS_ALIASES.addAlias(TopOfBookPrice.class);
    }
*/

    @Test
    public void testToStringEqualsHashCode() {
        TopOfBookPrice tobp = new TopOfBookPrice("Symbol", 123456789000L, 1.2345, 1_000_000, 1.235, 2_000_000);
        assertEquals("!net.openhft.samples.microservices.orders.TopOfBookPrice {\n" +
                "  symbol: Symbol,\n" +
                "  timestamp: 123456789000,\n" +
                "  buyPrice: 1.2345,\n" +
                "  buyQuantity: 1000000.0,\n" +
                "  sellPrice: 1.235,\n" +
                "  sellQuantity: 2000000.0\n" +
                "}\n", tobp.toString());

// from string
        TopOfBookPrice topb2 = Marshallable.fromString(tobp.toString());
        assertEquals(topb2, tobp);
        assertEquals(topb2.hashCode(), tobp.hashCode());
    }

    @Ignore("Tests is expected to fail to show what happens for documentation")
    @Test
    public void testFailing() {
        TopOfBookPrice tobp = new TopOfBookPrice("Symbol", 123456789000L, 1.2345, 1_000_000, 1.235, 2_000_000);
        TopOfBookPrice tobp2 = new TopOfBookPrice("Symbol", 123456789000L, 1.2345, 1_000_000, 1.236, 2_000_000);

        assertEquals(tobp, tobp2);
    }
}