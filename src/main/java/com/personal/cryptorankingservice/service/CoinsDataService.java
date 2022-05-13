package com.personal.cryptorankingservice.service;

import com.personal.cryptorankingservice.model.*;
import com.personal.cryptorankingservice.utils.HttpUtils;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import io.github.dengliming.redismodule.redistimeseries.DuplicatePolicy;
import io.github.dengliming.redismodule.redistimeseries.RedisTimeSeries;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import io.github.dengliming.redismodule.redistimeseries.TimeSeriesOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoinsDataService {

    private String get_all_coins_endpoint = "https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0";
    private String redis_coins_key = "coins";
    private String base_coin_endpoint = "https://coinranking1.p.rapidapi.com/coin/";
    private String history_endpoint_addon = "/history?timePeriod=";
    private List<String> timePeriods = List.of("24h", "7d", "30d", "3m", "1y", "3y", "5y");

    @Autowired
    private RestTemplate template;
    @Autowired
    private RedisJSON redisJSON;
    @Autowired
    private RedisTimeSeries redisTimeSeries;

    public void getCoins() {
      log.info("Getting coin data");
      ResponseEntity<Coins> response = template.exchange(get_all_coins_endpoint, HttpMethod.GET, HttpUtils.getHttpEntity(), Coins.class);
      storeCoins(response.getBody());
    }

    public void getCoinHistory() {
        log.info("Fetching Coin History");
        List<CoinInfo> allCoins = getAllCoinsFromRedisJSON();
        allCoins.forEach(coin -> {
            timePeriods.forEach(period -> {
                getCoinHistoryByTimePeriod(coin, period);
            });
        });
    }

    public void getCoinHistoryByTimePeriod(CoinInfo coinInfo, String period) {
        log.info("Fetching History of " + coinInfo.getName() + " for Time Period " + period);
        String fullUrl = base_coin_endpoint + coinInfo.getUuid() + history_endpoint_addon + period;
        ResponseEntity<CoinPriceHistory> coinPriceHistoryEntity = template.exchange(fullUrl, HttpMethod.GET, HttpUtils.getHttpEntity(), CoinPriceHistory.class);
        storeCoinToTimeSeries(coinPriceHistoryEntity.getBody(), coinInfo.getSymbol(), period);
    }

    public void storeCoinToTimeSeries(CoinPriceHistory coinPriceHistory, String symbol, String period) {
        List<CoinPriceHistoryExchangeRate> coinExchangeRateHistory = coinPriceHistory.getData().getHistory();

        coinExchangeRateHistory.stream().filter(rate -> rate.getPrice() != null && rate.getTimestamp() != null)
                .forEach(rate -> redisTimeSeries.add(new Sample(symbol + ":" + period, Sample.Value.of(Long.parseLong(rate.getTimestamp()),
                        Double.parseDouble(rate.getPrice()))),
                        new TimeSeriesOptions()
                                .unCompressed()
                                .duplicatePolicy(DuplicatePolicy.LAST)));
    }

    public List<CoinInfo> getAllCoinsFromRedisJSON() {
        CoinData coinData = redisJSON.get(redis_coins_key, CoinData.class, new GetArgs().path(".data").indent("\t").newLine("\n").space(" "));
        return coinData.getCoins();
    }

    private void storeCoins(Coins coins) {
        System.out.println(coins);
        redisJSON.set(redis_coins_key, SetArgs.Builder.create(".", GsonUtils.toJson(coins)));
    }

    public List<Sample.Value> getCoinHistoryFromRedisByTimePeriod(String symbol, String timePeriod) {
        Map<String, Object> timeSeriesInfo = redisTimeSeries.info(symbol + ":" + timePeriod);
        Long firstTimeStamp = Long.valueOf(timeSeriesInfo.get("firstTimeStamp").toString());
        Long lastTimeStamp = Long.valueOf(timeSeriesInfo.get("lastTimeStamp").toString());

        List<Sample.Value> coinTimeSeriesData = getTimeSeriesDataForCoin(symbol, timePeriod, firstTimeStamp, lastTimeStamp);
        return coinTimeSeriesData;
    }

    private List<Sample.Value> getTimeSeriesDataForCoin(String symbol, String timePeriod, Long firstTimeStamp, Long lastTimeStamp) {
        String key = symbol + ":" + timePeriod;
        return redisTimeSeries.range(key, firstTimeStamp, lastTimeStamp);

    }
}
