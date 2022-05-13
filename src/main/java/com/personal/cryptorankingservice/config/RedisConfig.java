package com.personal.cryptorankingservice.config;

import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import io.github.dengliming.redismodule.redistimeseries.RedisTimeSeries;
import io.github.dengliming.redismodule.redistimeseries.client.RedisTimeSeriesClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    private static final String redisUrl = "";

    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);

        return config;
    }

    @Bean
    public RedisTimeSeriesClient redisTimeSeriesClient(Config config) {
        return new RedisTimeSeriesClient(config);
    }

    @Bean
    public RedisTimeSeries redisTimeSeries(RedisTimeSeriesClient client) {
        return client.getRedisTimeSeries();
    }

    @Bean
    public RedisJSONClient redisJSONClient(Config config) {
        return new RedisJSONClient(config);
    }

    @Bean
    public RedisJSON redisJSON(RedisJSONClient client) {
        return client.getRedisJSON();
    }
}
