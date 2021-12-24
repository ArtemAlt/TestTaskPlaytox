package ru.altunin.util;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;

public class Utility {
    private static final Properties appProps = ApplicationProperties.getAppProps();

    public static Integer getRandomDelay() {
        return Integer.parseInt(appProps.getProperty("min_delay")) + (int) (Math.random() * (Integer.parseInt(appProps.getProperty("max_delay"))
                - Integer.parseInt(appProps.getProperty("min_delay"))) + 1);
    }

    public static Integer getRandomAmount() {
        return Integer.parseInt(appProps.getProperty("min_amount")) + (int) (Math.random() * (Integer.parseInt(appProps.getProperty("max_amount"))
                - Integer.parseInt(appProps.getProperty("min_amount"))) + 1);
    }

    public static int[] getRandomPairByLimit(int limit) {
        int[] result = new int[2];
        int first = (int) (Math.random() * limit);
        int second = (int) (Math.random() * limit);
        if (first == second) {
            result = getRandomPairByLimit(limit);
        } else {
            result[0] = first;
            result[1] = second;
        }
        return result;
    }

    public static String getRandomAccountId() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static BigDecimal getStartAmount(){
        return new BigDecimal(Integer.parseInt(appProps.getProperty("start_amount")));
    }

}
