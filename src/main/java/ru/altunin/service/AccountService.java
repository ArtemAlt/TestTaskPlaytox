package ru.altunin.service;

import ru.altunin.entity.Account;
import ru.altunin.errors.LimitAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altunin.util.Utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public void makeTransactionalFromTo(Account sender, Account receiver, BigDecimal amount) {
        logger.info("Начата транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции - " + amount + ". В потоке - " + Thread.currentThread().getName());
        try {
            Thread.sleep(Utility.getRandomDelay());
            makeDebit(sender, receiver, amount);
            logger.info("Завершена транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". В потоке - " + Thread.currentThread().getName());
        } catch (LimitAccountException e) {
            logger.warn("Транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". Отменена");
        } catch (InterruptedException e) {
            logger.warn("Транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". Отменена по таймауту");
        }
    }

    private void makeDebit(Account sender, Account receiver, BigDecimal amount) throws LimitAccountException {
        logger.info("Начат перевод средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount);
        Account monitor = (receiver.getId().compareTo(sender.getId()) >= 0) ? sender : receiver;
        synchronized (monitor.getMoney()) {
            BigDecimal newAccountMoneyValue = sender.getMoney().subtract(amount);
            if (newAccountMoneyValue.signum() < 0) {
                logger.info("Недостаточно средств на счете " + sender.getId());
                throw new LimitAccountException("Недостаточно средств на счете " + sender.getId());
            }
            sender.setMoney(newAccountMoneyValue);
            receiver.setMoney(receiver.getMoney().add(amount));
        }
        logger.info("Завершен перевод средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " итоговый счет аккаунта отправителя - " + sender.getMoney() + " Итоговый счет аккаунта получателя - " + receiver.getMoney());
    }

    public List<Account> generateAccountList(int count) {
        List<Account> bach = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bach.add(new Account(Utility.getRandomAccountId()));
        }
        logger.info("Сгенерировано всего счетов: " + count);
        return bach;
    }

    public int countTotalAmount(List<Account> accounts) {
        int sum = accounts.stream().mapToInt(a -> a.getMoney().intValue()).sum();
        logger.info("Всего счетов - " + accounts.size() + ". Общая сумма на счетах - " + sum);
        return sum;
    }

    public void makeShuffleTransactionsWithRandomAmount(List<Account> accounts, int[] pair) {
        makeTransactionalFromTo(accounts.get(pair[0]), accounts.get(pair[1]), new BigDecimal(Utility.getRandomAmount()));
    }

}
