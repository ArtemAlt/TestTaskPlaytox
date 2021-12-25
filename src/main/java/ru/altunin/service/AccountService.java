package ru.altunin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altunin.entity.Account;
import ru.altunin.errors.BadAmountException;
import ru.altunin.errors.LimitAccountException;
import ru.altunin.errors.MakeDebitException;
import ru.altunin.util.Utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AccountService {
    private final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final Comparator<Account> comparator = Comparator.comparing(Account::getId);

    public void makeTransactionalFromTo(Account sender, Account receiver, BigDecimal amount) {
        logger.info("Начата транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции - " + amount + ". В потоке - " + Thread.currentThread().getName());
        try {
            if (amount == null && amount.signum() < 0) throw new BadAmountException("Неверная сумма перевода");
            makeDebit(sender, receiver, amount);
            logger.info("Завершена транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". В потоке - " + Thread.currentThread().getName());
        } catch (LimitAccountException e) {
            logger.warn("Транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". Отменена. Не достаточно средств на счете отправителя");
        } catch (BadAmountException e) {
            logger.warn("Транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". Отменена. Некорректная сумма перевода");
        }
    }

    private void makeDebit(Account sender, Account receiver, BigDecimal amount) throws LimitAccountException {
        logger.info("Начат перевод средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount);
        List<Account> accounts = new ArrayList<>();
        accounts.add(receiver);
        accounts.add(sender);
        accounts.sort(comparator);
        try {
            accounts.get(0).lockAccount();
            accounts.get(1).lockAccount();
            sender.makeDebit(amount);
            receiver.makeEnrolment(amount);
        } catch (MakeDebitException e) {
            try {
                sender.makeEnrolment(amount);
            } catch (InterruptedException ex) {
                logger.error("Транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". Отменена." +
                        " Фатальная ошибка перевода. Средства в объеме - " + amount + ". Переведены на счет - " + sender.getId());
            }
        } catch (InterruptedException e) {
            logger.error("Транзакция перевода средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " сумма транзакции  - " + amount + ". Отменена." +
                    " Фатальная ошибка перевода. Средства в объеме - " + amount + ". Переведены на счет - " + sender.getId());
        } finally {
            accounts.get(0).unlockAccount();
            accounts.get(1).unlockAccount();
        }
        logger.info("Завершен перевод средств с аккаунта " + sender.getId() + " на аккаунт " + receiver.getId() + " итоговый счет аккаунта отправителя - " + sender.getCurrentAmount() + " Итоговый счет аккаунта получателя - " + receiver.getCurrentAmount());
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
        return accounts.stream().mapToInt(a -> a.getCurrentAmount().intValue()).sum();
    }

    public void makeShuffleTransactionsWithRandomAmount(List<Account> accounts, int[] pair) {
        makeTransactionalFromTo(accounts.get(pair[0]), accounts.get(pair[1]), new BigDecimal(Utility.getRandomAmount()));
    }

}
