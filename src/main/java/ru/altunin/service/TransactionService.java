package ru.altunin.service;

import ru.altunin.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altunin.util.Utility;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TransactionService {
    private final AccountService service;
    private final ExecutorService threadPool;
    private final List<Account> accountList;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);


    public TransactionService(String totalAccounts, String totalThreads) {
        service = new AccountService();
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(totalThreads));
        accountList = service.generateAccountList(Integer.parseInt(totalAccounts));
    }

    private int getTotalAccounts() {
        return this.accountList.size();
    }

    public void start(String totalTransactions) {
        for (int i = 0; i < Integer.parseInt(totalTransactions); i++) {
            threadPool.execute(() -> {
                int[] accountPair = Utility.getRandomPairByLimit(getTotalAccounts());
                service.makeShuffleTransactionsWithRandomAmount(accountList, accountPair);
            });
        }
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                List<Runnable> unfurnishedTasks = threadPool.shutdownNow();
                logger.warn("Незавершенные транзакции: "+unfurnishedTasks.size());
                logger.warn("Незавершенные транзакции: "+unfurnishedTasks.stream().map(Object::toString).collect(Collectors.toList()));
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        service.countTotalAmount(accountList);
        logger.info("После проведения " + totalTransactions + " транзакций, состояние счетов:\n " + accountList.stream().map(a -> a.toString() + "\n").collect(Collectors.toList()));
        logger.info("Общая сумма на счетах: " + service.countTotalAmount(accountList));
    }

}
