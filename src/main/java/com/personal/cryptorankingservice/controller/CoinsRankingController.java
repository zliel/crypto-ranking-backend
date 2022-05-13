package com.personal.cryptorankingservice.controller;

import com.personal.cryptorankingservice.model.CoinInfo;
import com.personal.cryptorankingservice.model.HistoryData;
import com.personal.cryptorankingservice.service.CoinsDataService;
import com.personal.cryptorankingservice.utils.ConversionUtil;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping(path = "api/v1")
public class CoinsRankingController {

    @Autowired
    private CoinsDataService service;

    @GetMapping("/coins")
    public ResponseEntity<List<CoinInfo>> getAllCoins() {
        return ResponseEntity.ok().body(service.getAllCoinsFromRedisJSON());
    }

    @GetMapping("/{symbol}/{period}")
    public List<HistoryData> getCoinHistoryPerTimePeriod(@PathVariable String symbol, @PathVariable String period) {
        List<Sample.Value> coinsTimeSeriesData = service.getCoinHistoryFromRedisByTimePeriod(symbol, period);

        return coinsTimeSeriesData.stream().map(value ->
            new HistoryData(ConversionUtil.convertUnixTimeToDate(value.getTimestamp()), ConversionUtil.round(value.getValue(), 2)
        ))
            .collect(Collectors.toList());
    }
}
