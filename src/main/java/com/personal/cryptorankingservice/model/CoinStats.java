package com.personal.cryptorankingservice.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoinStats {
    private float total;
    private float referenceCurrencyRate;
    private float totalCoins;
    private float totalMarkets;
    private float totalExchanges;
    private String totalMarketCap;
    private String total24hVolume;
}
