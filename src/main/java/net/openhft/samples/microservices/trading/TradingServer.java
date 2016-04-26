package net.openhft.samples.microservices.trading;

import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.websocket.jetty.JettyWebSocketServer;

import java.util.Random;

/**
 * Created by daniel on 26/04/2016.
 */
public class TradingServer {
    private final JettyWebSocketServer server;

    public TradingServer(int port) {
        this.server = new JettyWebSocketServer(port);
        server.addService("/*", IMarketData.class, GUIGatewayPublisher::new);
        server.start();
    }

    static class GUIGatewayPublisher implements GUIGateway{

        private IMarketData md;
        private volatile boolean isStopped = false;

        GUIGatewayPublisher(IMarketData md) {
            this.md = md;
            System.out.println("New connection");
        }

        public void enableMarketData(boolean enable) {
            if(!enable)
                isStopped = true;
            else {
                isStopped = false;
                new Thread(() -> {
                    while (!isStopped) {
                        Random r = new Random();
                        int[] fourRandomNumbers = r.ints(4, 0, 1001).toArray();
                        md.symbol("GBP/USD");
                        md.bidPrice(fourRandomNumbers[0]);
                        md.bidQuantity(fourRandomNumbers[1]);
                        md.askPrice(fourRandomNumbers[2]);
                        md.askQuantity(fourRandomNumbers[3]);

                        System.out.println("Message sent " + md);
                        Jvm.pause(1000);

                    }
                }).start();
            }
        }

        @Override
        public void newOrder(Order order) {
            System.out.println("New order received " + order);
        }
    }

    public static void main(String[] args) {
        new TradingServer(7001);
        System.out.println("Server started");
    }
}
