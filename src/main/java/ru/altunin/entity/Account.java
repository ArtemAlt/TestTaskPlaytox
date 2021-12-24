package ru.altunin.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altunin.errors.LimitAccountException;
import ru.altunin.util.Utility;

import java.math.BigDecimal;

public class Account {
    private final String id;
    private BigDecimal money;
    private Boolean isLocked;
    private final Logger logger = LoggerFactory.getLogger(Account.class);

    public Account(String id) {
        this.id = id;
        this.money = Utility.getStartAmount();
        this.isLocked = false;
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

    public synchronized void makeEnrolment(BigDecimal amount) {
        if (!this.isLocked) {
            this.lockAccount();
            this.setMoney(this.getMoney().add(amount));
            this.unlockAccount();
        } else {
            synchronized (this) {
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void makeDebit(BigDecimal amount) {
        if (!this.isLocked) {
            this.lockAccount();
            BigDecimal newAccountMoneyValue = this.getMoney().subtract(amount);
            if (newAccountMoneyValue.signum() < 0) {
                try {
                    throw new LimitAccountException("Недостаточно средств на счете " + this.getId());
                } catch (LimitAccountException e) {
                    this.unlockAccount();
                    logger.info("Недостаточно средств на счете " + this.getId());
                }
            }
            this.setMoney(this.getMoney().subtract(amount));
            this.unlockAccount();
        } else {
            synchronized (this) {
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void lockAccount() {
        this.isLocked = true;
    }

    private synchronized void unlockAccount() {
        this.isLocked = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public boolean isLock() {
        return this.isLocked;
    }

    public BigDecimal getCurrentAmount() {
        return this.money;
    }

}

