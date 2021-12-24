package ru.altunin.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altunin.util.Utility;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String id;
    private BigDecimal money;
    private Boolean isLocked;
    private final Lock lock;
    private final Logger logger = LoggerFactory.getLogger(Account.class);

    public Account(String id) {
        this.id = id;
        this.money = Utility.getStartAmount();
        this.isLocked = false;
        this.lock = new ReentrantLock();
    }

    public String getId() {
        return id;
    }

    private BigDecimal getMoney() {
        return money;
    }

    private void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Account id: " + id + " total money= " + money;
    }

    public void makeEnrolment(BigDecimal amount) {
        try {
            if (lock.tryLock()) {
                this.isLocked = true;
                this.setMoney(this.getMoney().add(amount));
            }
        } finally {
            this.isLocked = false;
            lock.unlock();
        }
    }

    public BigDecimal makeDebit(BigDecimal amount) {
        BigDecimal result = BigDecimal.valueOf(0);
        try {
            if (lock.tryLock()) {
                BigDecimal newAccountMoneyValue = this.getMoney().subtract(amount);
                if (newAccountMoneyValue.signum() < 0) {
                    this.isLocked = false;
                    logger.info("Недостаточно средств на счете " + this.getId());
                } else {
                    this.setMoney(this.getMoney().subtract(amount));
                    result= amount;
                }
            }
        } finally {
            this.isLocked = false;
            lock.unlock();
        }
        return result;
    }

    public boolean isLock() {
        return this.isLocked;
    }

    public BigDecimal getCurrentAmount() {
        return this.money;
    }

}

