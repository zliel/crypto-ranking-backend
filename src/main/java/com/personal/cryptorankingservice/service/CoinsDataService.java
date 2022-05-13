package com.personal.cryptorankingservice.service;

import com.personal.cryptorankingservice.model.Coins;
import com.personal.cryptorankingservice.utils.HttpUtils;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CoinsDataService {

    private String get_coins_endpoint = "https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0";
    private String redis_coins_key = "coins";

    @Autowired
    private RestTemplate template;
    @Autowired
    private RedisJSON redisJSON;

    public void getCoins() {
      log.info("Getting coin data");
      ResponseEntity<Coins> response = template.exchange(get_coins_endpoint, HttpMethod.GET, HttpUtils.getHttpEntity(), Coins.class);
      storeCoins(response.getBody());
    }

    private void storeCoins(Coins coins) {
        System.out.println(coins);
        redisJSON.set(redis_coins_key, SetArgs.Builder.create(".", GsonUtils.toJson(coins)));
    }
}
