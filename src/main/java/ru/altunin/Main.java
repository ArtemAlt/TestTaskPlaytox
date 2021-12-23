package ru.altunin;

import ru.altunin.service.TransactionService;
import ru.altunin.util.ApplicationProperties;

import java.util.Properties;


public class Main {

    public static void main(String[] args)  {
        Properties appProps = ApplicationProperties.getAppProps();
        TransactionService transactionService = new TransactionService(appProps.getProperty("accounts"),appProps.getProperty("threads"));
        transactionService.start(appProps.getProperty("transactions"));
    }
}
