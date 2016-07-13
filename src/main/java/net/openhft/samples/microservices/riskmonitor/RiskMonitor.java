package net.openhft.samples.microservices.riskmonitor;

/**
 * Created by peter on 13/07/16.
 */
public interface RiskMonitor {
    void trade(TradeDetails tradeDetails);
}
