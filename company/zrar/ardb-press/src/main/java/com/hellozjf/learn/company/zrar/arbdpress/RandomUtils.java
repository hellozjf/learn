package com.hellozjf.learn.company.zrar.arbdpress;

import java.util.List;
import java.util.Random;

/**
 * @author Jingfeng Zhou
 */
public class RandomUtils {

    /**
     * 获取来电间隔
     * @param customConfig
     * @param random
     * @return
     */
    public static int getCallInterval(CustomConfig customConfig, Random random) {
        int minCallInterval = customConfig.getMinCallInterval();
        int maxCallInterval = customConfig.getMaxCallInterval();
        return getNumberBetween(random, minCallInterval, maxCallInterval);
    }

    /**
     * 获取分机号
     * @param customConfig
     * @param random
     * @return
     */
    public static int getCsadCallNumber(CustomConfig customConfig, Random random) {
        int minCsadCallNumber = customConfig.getMinCsadCallNumber();
        int maxCsadCallNumber = customConfig.getMaxCsadCallNumber();
        return getNumberBetween(random, minCsadCallNumber, maxCsadCallNumber);
    }

    /**
     * 获取用户电话号码
     * @param customConfig
     * @param random
     * @return
     */
    public static int getCustomerCallNumber(CustomConfig customConfig, Random random) {
        int minCustomerCallNumber = customConfig.getMinCustomerCallNumber();
        int maxCustomerCallNumber = customConfig.getMaxCustomerCallNumber();
        return getNumberBetween(random, minCustomerCallNumber, maxCustomerCallNumber);
    }

    /**
     * 获取[minNumber, maxNumber]之间的某个数字
     * @param random
     * @param minNumber
     * @param maxNumber
     * @return
     */
    public static int getNumberBetween(Random random, int minNumber, int maxNumber) {
        return random.nextInt(maxNumber + 1 - minNumber) + minNumber;
    }

    /**
     * 获取发送次数
     * @param customConfig
     * @param random
     * @return
     */
    public static int getSendCount(CustomConfig customConfig, Random random) {
        int minSendCount = customConfig.getMinSendCount();
        int maxSendCount = customConfig.getMaxSendCount();
        return getNumberBetween(random, minSendCount, maxSendCount);
    }

    /**
     * 获取发送间隔
     * @param customConfig
     * @param random
     * @return
     */
    public static int getSendInterval(CustomConfig customConfig, Random random) {
        return getNumberBetween(random, customConfig.getMinSendInterval(), customConfig.getMaxSendInterval());
    }

    /**
     * 获取识别间隔
     * @param customConfig
     * @param random
     * @return
     */
    public static int getRecognizeInterval(CustomConfig customConfig, Random random) {
        return getNumberBetween(random, customConfig.getMinRecognizeInterval(), customConfig.getMaxRecognizeInterval());
    }

    /**
     * 获取识别间隔
     * @param customConfig
     * @param random
     * @return
     */
    public static int getWhoSay(CustomConfig customConfig, Random random) {
        return getNumberBetween(random, 0, 1);
    }

    /**
     * 获取随机的座席文本
     * @return
     */
    public static String getRandomCsadSentence(Random random, List<String> csadSentenceList) {
        int index = random.nextInt(csadSentenceList.size());
        return csadSentenceList.get(index);
    }

    /**
     * 获取随机的客户文本
     * @param random
     * @param customerSentenceList
     * @return
     */
    public static String getRandomCustomerSentence(Random random, List<String> customerSentenceList) {
        int index = random.nextInt(customerSentenceList.size());
        return customerSentenceList.get(index);
    }
}
