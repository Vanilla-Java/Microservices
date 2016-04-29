package net.openhft.samples.microservices.jetty;

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
        server.addService("/*", GatewayPublisher.class, GUIGatewayPublisher::new);
        server.start();
    }

    public static void main(String[] args) {
        new TradingServer(7001);
        System.out.println("Server started");
    }

    static class GUIGatewayPublisher implements GUIGateway{

        private GatewayPublisher gatewayPublisher;
        private volatile boolean isStopped = false;

        GUIGatewayPublisher(GatewayPublisher gatewayPublisher) {
            this.gatewayPublisher = gatewayPublisher;
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

                        MarketData md  = new MarketData("GBP/USD",
                                fourRandomNumbers[0],
                                fourRandomNumbers[1],
                                fourRandomNumbers[2],
                                fourRandomNumbers[3]);

                        gatewayPublisher.marketData(md);

                        System.out.println(md);
                        Jvm.pause(1000);
                    }
                }).start();
            }
        }

        @Override
        public void newOrder(Order order) {
            OrderStatus orderStatus = new OrderStatus(order);
            System.out.println("New orderStatus received " + orderStatus);
            gatewayPublisher.orderStatus(orderStatus);
        }
    }
}
