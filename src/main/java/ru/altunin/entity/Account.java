package ru.altunin.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altunin.errors.LimitAccountException;
import ru.altunin.util.Utility;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String id;
    private BigDecimal money;
    private final Lock lock;
    private final Logger logger = LoggerFactory.getLogger(Account.class);

    public Account(String id) {
        this.id = id;
        this.money = Utility.getStartAmount();
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

    public void makeEnrolment(BigDecimal amount) throws InterruptedException {
        Thread.sleep(Utility.getRandomDelay());
        this.setMoney(this.getMoney().add(amount));
    }

    public void makeDebit(BigDecimal amount) throws LimitAccountException, InterruptedException {
        BigDecimal newAccountMoneyValue = this.getMoney().subtract(amount);
        Thread.sleep(Utility.getRandomDelay());
        if (newAccountMoneyValue.signum() < 0) {
            logger.info("Недостаточно средств на счете " + this.getId());
            throw new LimitAccountException("Недостаточно средств на счете " + this.getId());
        } else {
            this.setMoney(this.getMoney().subtract(amount));
        }
    }

    public BigDecimal getCurrentAmount() {
        return this.money;
    }

    public void lockAccount() {
        lock.lock();
    }

    public void unlockAccount() {
        lock.unlock();
    }
}

