/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.util;

/**
 * <p>
 * 
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class SocketKeys {

    public final static String TRANSFORM_JSON = "1";
    public final static String TRANSFORM_XML = "2";
    public final static String CHARSET_NAME_UTF8 = "UTF-8";

    public static final int SIZE_SERVICE_ID = 10;
    public static final int SIZE_TRANSACTION_ID = 32;
    public static final int SIZE_TRANSFORM_TYPE = 1;
    public static final int SIZE_CONTENT = 10;
    public static final int SIZE_RESPONSE_CODE = 4;
    public static final int SIZE_SESSION_ID = 36;

    public static final int OBJECT_SERIALIZE_SIZE = 1024 * 1024 * 100;

    public final static String SERVICE_ID_AUTH = "0000000001";

    public final static String PARAMETER_USERNAME = "userName";
    public final static String PARAMETER_PASSWORD = "password";

    public static final int RESPONSE_CODE_SUCCESS = 0;

    public static final int RESPONSE_CODE_TIMEOUT = 1;

    public static final int RESPONSE_CODE_COMMUNICATIONERROR = 2;

    public static final int RESPONSE_CODE_SERVERERROR = 3;

    public static final int RESPONSE_CODE_MESSAGEERROR = 4;

    public static final int RESPONSE_CODE_SERVICEERROR = 5;

    public static final int RESPONSE_CODE_AUTHENTICATIONERROR = 6;

    public static final int RESPONSE_CODE_UNKNOWERROR = 7;

    public final static String MESSAGE_AUTH_ERROR = "client request authenticate failure!";
    public final static String MESSAGE_AUTH_DELETED = "auththentication info is deleted!";
    public final static String MESSAGE_SERVICE_ERROR = "tid:%s sid:%s authentication service error,message:%s";

    public final static String STRING_TERMINATING_SYMB = "#@#";

    //

}
