/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.service;

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
public class ServiceResponse {

    /**
     * 转换类型 1 json 2 xml
     */
    private String transformType;

    /**
     * 消息ID,32位
     */
    private String id;

    private String sessionId;

    private String serviceId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    //private boolean auth = false;

    //    /**
    //     * 服务ID, 10位
    //     */
    //    private String serviceId;

    /**
     * 结果码分类
     * 1.成功
     * 2.超时异常
     * 3.通讯异常
     * 4.服务器异常 
     * 5.消息解析异常 
     * 6.服务方异常 
     * 7.其他未知异常 
     */
    private int responseCode = -1;

    //    public boolean isAuth() {
    //        return auth;
    //    }
    //
    //    public void setAuth(boolean authFlag) {
    //        this.auth = authFlag;
    //    }

    private Object responseObject;

    private String responseMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 传递的内容类名
     */
    //private String cname;

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

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public <T> T getResponseObject() {
        return (T) responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public void setTransformType(String transformType) {
        //        if (!(transformType.equals(SocketKeys.TRANSFORM_JSON) || transformType.equals(SocketKeys.TRANSFORM_XML))) {
        //            throw new IllegalArgumentException("transformType:" + transformType + " isn't support!");
        //        }

        this.transformType = transformType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sid:").append(getServiceId()).append("id:").append(getId()).append(",transformType:")
                .append(getTransformType());
        if (getResponseObject() != null) {
            sb.append("result class:").append(getResponseObject().getClass().getName()).append(",responseObject:")
                    .append(getResponseObject());
        }
        sb.append(",responseCode:").append(getResponseCode()).append("responseMessage:")
                .append(this.getResponseMessage()).toString();

        return sb.toString();
    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 97 * hash + this.transformType.hashCode();
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + responseCode;
        hash = 97 * hash + (this.responseMessage != null ? this.responseMessage.hashCode() : 0);
        hash = 97 * hash + (this.responseObject != null ? this.responseObject.hashCode() : 0);
        return hash;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        ServiceResponse m = (ServiceResponse) obj;

        if (m.getId() == null)
            return false;

        if (this.getId().equals(m.getId()) && this.transformType == m.transformType) {
            //            if (m.getBody() == null && this.getBody() == null) {
            //                return true;
            //            } else if (m.getBody() != null) {
            //                if (m.getBody().equals(this.getBody())) {
            //                    return true;
            //                } else {
            //                    return false;
            //                }
            //
            //            } else {
            //                return false;
            //            }

            return true;
        } else
            return false;
    }

}
