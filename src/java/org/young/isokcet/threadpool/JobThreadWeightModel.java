/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import com.thoughtworks.xstream.XStream;

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
public class JobThreadWeightModel {

    /**
     * 预留模式，整个线程池将预留设置数量的线程仅仅用于某一类请求使用
     */
    public static final String WEIGHT_MODEL_LEAVE = "leave";
    /**
     * 限制模式，这类请求使用默认线程池，但不能超过设置的最大值
     */
    public static final String WEIGHT_MODEL_LIMIT = "limit";

    private String key;
    private String type;
    private int value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static void main(String[] args) throws Exception {

        JobThreadWeightModel m = new JobThreadWeightModel();
        m.setKey("key1");
        m.setValue(11);
        m.setType("leave");
        XStream xstream = new XStream();
        xstream.alias("threadweightmodel", JobThreadWeightModel.class);
        String xml = xstream.toXML(m);
        System.out.println(xml);
    }
}
