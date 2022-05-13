package com.personal.cryptorankingservice.model;

import lombok.Data;

@Data
public class CoinStats {
    private int total;
    private int totalCoins;
    private int totalMarkets;
    private int totalExchanges;
    private int totalMarketCap;
    private double total24hVolume;
}
