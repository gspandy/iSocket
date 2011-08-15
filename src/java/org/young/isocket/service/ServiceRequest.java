/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.service;

import org.young.icore.util.TransactionUtils;

/**
 * <p>
 * 描述:消息对象
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class ServiceRequest {

    /**
     * 转换类型 1 json 2 xml
     */
    private String transformType;

    /**
     * 消息ID,32位
     */
    private String id;

    /**
     * 服务ID, 10位
     */
    private String serviceId;

    /**
     * sessionid
     */
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    /**
     * 请求的内容
     */
    private Object requestObject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransformType() {
        return transformType;

    }

    //    public TransformType getTransformTypeDesc() {
    //        if (transformType == (byte) 1) {
    //            return TransformType.JSON;
    //        } else if (transformType == (byte) 2) {
    //            return TransformType.XML;
    //        } else {
    //            throw new IllegalArgumentException("no support!");
    //        }
    //    }

    public String getServiceId() {
        return serviceId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public <T> T getRequestObject() {
        return (T) requestObject;
    }

    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }

    public void setTransformType(String transformType) {
        //        if (!(transformType.equals(SocketKeys.TRANSFORM_JSON) || transformType.equals(SocketKeys.TRANSFORM_XML))) {
        //            throw new IllegalArgumentException("transformType:" + transformType + " isn't support!");
        //        }

        this.transformType = transformType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("id:").append(getId()).append(",transformType:")
                .append(getTransformType());

        if (getRequestObject() != null) {
            sb.append(",requestObject class:").append(getRequestObject().getClass().getName())
                    .append(",requestObject:").append(getRequestObject());
        }

        return sb.append(",serviceId:").append(getServiceId()).toString();
    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 97 * hash + this.transformType.hashCode();
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.serviceId != null ? this.serviceId.hashCode() : 0);
        hash = 97 * hash + (this.requestObject != null ? this.requestObject.hashCode() : 0);
        return hash;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        ServiceRequest m = (ServiceRequest) obj;

        if (m.getId() == null)
            return false;

        if (this.getId().equals(m.getId()) && this.transformType == m.transformType) {

            return true;
        } else
            return false;
    }

    public ServiceRequest() {
        this.id = TransactionUtils.generateID();
    }

}
