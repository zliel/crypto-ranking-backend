package com.personal.cryptorankingservice.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoinData {
    private CoinStats stats;
    private List<CoinInfo> coins = new ArrayList<>();
}
