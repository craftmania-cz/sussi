package cz.wake.sussi.metrics;

import cz.wake.sussi.metrics.collectors.BotCollector;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Metrics {

    private static final Logger logger = LoggerFactory.getLogger(Metrics.class);

    private static Metrics instance;

    private final HTTPServer server;

    public static void setup() {
        logger.info("Setup metrics {}!", instance().toString());
    }

    public static Metrics instance() {
        if (instance == null)
            instance = new Metrics();
        return instance;
    }

    public Metrics() {
        DefaultExports.initialize();

        new BotCollector(new BotMetrics()).register();
        try {
            server = new HTTPServer(9181);
            logger.info("Setup HTTPServer for Metrics");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to set up HTTPServer for Metrics", e);
        }
    }

    public HTTPServer getServer() {
        return server;
    }


    public static final Gauge channelMessages = Gauge.build()
            .name("channel_messages")
            .help("Message activity in channels")
            .labelNames("channel")
            .register();
}