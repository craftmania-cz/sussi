package cz.wake.sussi.metrics.collectors;

import cz.wake.sussi.metrics.BotMetrics;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BotCollector extends Collector {

    private final BotMetrics botMetrics;

    public BotCollector(BotMetrics botMetrics) {
        this.botMetrics = botMetrics;
    }

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> familySamples = new ArrayList<>();

        GaugeMetricFamily discordEntities = new GaugeMetricFamily("discord_entities", "Amount of Discord entities",
                Collections.singletonList("entity"));

        familySamples.add(discordEntities);


        if (botMetrics.count()) {
            discordEntities.addMetric(Collections.singletonList("member_count"), botMetrics.getMemberCount());
            discordEntities.addMetric(Collections.singletonList("member_count_online"), botMetrics.getMemberOnlineCount());
            discordEntities.addMetric(Collections.singletonList("ping"), botMetrics.getPing());
        }

        return familySamples;
    }
}