/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class JobThreshold {
    private Map<String, Integer> thresholdPool = new HashMap<String, Integer>();
    private int defaultThreshold;

    public Map<String, Integer> getThresholdPool() {
        return thresholdPool;
    }

    public void setThresholdPool(Map<String, Integer> thresholdPool) {
        this.thresholdPool = thresholdPool;
    }

    public int getDefaultThreshold() {
        return defaultThreshold;
    }

    public void setDefaultThreshold(int defaultThreshold) {
        this.defaultThreshold = defaultThreshold;
    }
}
