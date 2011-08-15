/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.service;

import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.isocket.exception.ConfigException;
import org.young.isocket.parse.AbstractXmlConfigParser;

/**
 * <p>
 * 服务工厂类
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class ServiceFactory extends AbstractXmlConfigParser<IService> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

    private final static ServiceFactory sf = new ServiceFactory();

    private final static String SERVICE_CONFIG = "services.xml";

    private final static String TAG_SERVICE = "service";

    private final static String ATTRIBUTE_ID = "id";

    private final static String ATTRIBUTE_CLASS = "class";

    private final static String ATTRIBUTE_DESC = "desc";

    private Map<String, IService> serviceMap;

    private ServiceFactory() {
        this.serviceMap = initServices();
    }

    public static ServiceFactory getInstance() {
        return sf;
    }

    public Map<String, IService> initServices() {
        return super.parse(SERVICE_CONFIG);
    }

    @Override
    protected void doStartElement(XMLStreamReader r, Map<String, IService> m) {
        if (r.getLocalName().equals(TAG_SERVICE)) {
            String id = r.getAttributeValue(null, ATTRIBUTE_ID);
            String className = r.getAttributeValue(null, ATTRIBUTE_CLASS);
            String desc = r.getAttributeValue(null, ATTRIBUTE_DESC);

            Class<?> claz = null;
            try {
                claz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new ConfigException(e.getMessage(), e);
            }

            IService service = null;
            try {
                service = (IService) claz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                throw new ConfigException(e.getMessage(), e);
            }

            if (logger.isDebugEnabled()) {
                logger.debug(
                        new StringBuffer().append("loaded service,id:{}").append(",classname:{}").append(",desc:{}")
                                .toString(), new Object[] { id, className, desc });
            }

            m.put(id, service);

        }

    }

    @Override
    protected void doEndElement(XMLStreamReader r, Map<String, IService> m) {
        super.dummy();
    }

    public IService getService(String servceId) {
    	IService service = serviceMap.get(servceId);
        if (service == null)
            throw new NullPointerException("no service match id:" + servceId);

        return service;
    }

    public boolean isExist(String serviceId) {
        return serviceMap.containsKey(serviceId);
    }

}
