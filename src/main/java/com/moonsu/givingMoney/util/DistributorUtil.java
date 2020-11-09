package com.moonsu.givingMoney.util;

import java.util.ArrayList;
import java.util.List;

public class DistributorUtil {

    public static List<Integer> distribute(int receiveCount, int money) {

        List<Integer> result = new ArrayList(receiveCount);
        if (receiveCount > money) {
            while (result.size()<money) {
                result.add(1);
            }
            for (int i=result.size(); i<receiveCount; i++) {
                result.add(0);
            }
            return result;
        }


        result.add((money % receiveCount) + (money / receiveCount));

        for (int i = 1; i < receiveCount; i++) {
            result.add(money / receiveCount);
        }

        return result;
    }

}
