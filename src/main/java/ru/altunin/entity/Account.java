package ru.altunin.entity;

import java.math.BigDecimal;

public class Account {
    private final String id;
    private BigDecimal money;

    public Account(String id) {
        this.id = id;
        this.money = new BigDecimal(10000);
    }

    public String getId() {
        return id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Account id: " + id + " total money= " + money;
    }
}
