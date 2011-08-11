/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.ssl.SSLFilter;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
import org.glassfish.grizzly.utils.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.annotation.PropertiesAnnotation;
import org.young.icore.util.ClassLoaderUtils;
import org.young.icore.util.PropertiesLoaderUtils;
import org.young.isokcet.filter.ServerAuthFilter;
import org.young.isokcet.filter.ServerStringDataFilter;
import org.young.isokcet.filter.ThreadPoolFilter;
import org.young.isokcet.threadpool.JobDispatcher;
import org.young.isokcet.util.SocketKeys;

/**
 * <p>
 * 描述:非阻塞的Socket Server
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class NIOSocketServer extends AbstractLifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(NIOSocketServer.class);

    private final Object _joinLock = new Object();

    //TODO 修改为properties文件读取.
    @PropertiesAnnotation(name = "serverhost", resource = "isocket-server.properties")
    private String host = "localhost";

    @PropertiesAnnotation(name = "serverport", resource = "isocket-server.properties")
    private int port = 7777;

    @PropertiesAnnotation(name = "serverkeepalive", resource = "isocket-server.properties")
    private boolean keepAlive;

    @PropertiesAnnotation(name = "serverlinger", resource = "isocket-server.properties")
    private int linger = 0;

    @PropertiesAnnotation(name = "serverreuseaddress", resource = "isocket-server.properties")
    private boolean reuseAddress;

    @PropertiesAnnotation(name = "serverbacklog", resource = "isocket-server.properties")
    private int backLog = 0;

    @PropertiesAnnotation(name = "servertimeout", resource = "isocket-server.properties")
    private int serverSocketTimeout;

    @PropertiesAnnotation(name = "servertcpnodelay", resource = "isocket-server.properties")
    private boolean tcpNoDelay = false;

    @PropertiesAnnotation(name = "needauth", resource = "isocket-server.properties")
    private boolean needAuth = true;

    @PropertiesAnnotation(name = "needssl", resource = "isocket-server.properties")
    private boolean needSSL = false;

    @PropertiesAnnotation(name = "ssltruststorefile", resource = "isocket-server.properties")
    private String trustStoreFile;

    @PropertiesAnnotation(name = "ssltruststorepassword", resource = "isocket-server.properties")
    private String trustStorePassword;

    @PropertiesAnnotation(name = "sslkeystorefile", resource = "isocket-server.properties")
    private String keyStoreFile;

    @PropertiesAnnotation(name = "sslkeystorepassword", resource = "isocket-server.properties")
    private String keyStorePassword;

    @PropertiesAnnotation(name = "sslneedclientmode", resource = "isocket-server.properties")
    private boolean needClientMode = false;

    @PropertiesAnnotation(name = "sslwantclientmode", resource = "isocket-server.properties")
    private boolean wantClientMode = false;

    @PropertiesAnnotation(name = "serverreadbuffer", resource = "isocket-server.properties")
    private int readBufferSize;

    @PropertiesAnnotation(name = "serverwritebuffer", resource = "isocket-server.properties")
    private int writeBufferSize;

    private static NIOSocketServer nioSocketServer = new NIOSocketServer();

    private TCPNIOTransport transport;

    private JobDispatcher jobDispatcher;

    //private FilterChainBuilder filterChainBuilder;

    private TCPNIOTransportBuilder tcpNIOTransportBuilder;

    public boolean isNeedClientMode() {
        return needClientMode;
    }

    public void setNeedClientMode(boolean needClientMode) {
        this.needClientMode = needClientMode;
    }

    public boolean isWantClientMode() {
        return wantClientMode;
    }

    public void setWantClientMode(boolean wantClientMode) {
        this.wantClientMode = wantClientMode;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public boolean isNeedSSL() {
        return needSSL;
    }

    public void setNeedSSL(boolean needSSL) {
        this.needSSL = needSSL;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }

    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getLinger() {
        return linger;
    }

    public void setLinger(int linger) {
        this.linger = linger;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public int getBackLog() {
        return backLog;
    }

    public void setBackLog(int backLog) {
        this.backLog = backLog;
    }

    public int getServerSocketTimeout() {
        return serverSocketTimeout;
    }

    public void setServerSocketTimeout(int serverSocketTimeout) {
        this.serverSocketTimeout = serverSocketTimeout;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    public static NIOSocketServer getInstance() {
        return nioSocketServer;
    }

    private NIOSocketServer() {

        //init attribute from properties
        PropertiesLoaderUtils.setPropertiesFields(this);

        // Create TCP transport
        this.tcpNIOTransportBuilder = TCPNIOTransportBuilder.newInstance();

        this.tcpNIOTransportBuilder.setKeepAlive(keepAlive);

        if (this.linger > 0)
            this.tcpNIOTransportBuilder.setLinger(this.linger);

        this.tcpNIOTransportBuilder.setReuseAddress(reuseAddress);

        if (this.backLog > 0)
            this.tcpNIOTransportBuilder.setServerConnectionBackLog(this.backLog);

        if (this.serverSocketTimeout > 0)
            this.tcpNIOTransportBuilder.setServerSocketSoTimeout(this.serverSocketTimeout);

        this.tcpNIOTransportBuilder.setTcpNoDelay(tcpNoDelay);

        if (readBufferSize > 0)
            this.tcpNIOTransportBuilder.setReadBufferSize(readBufferSize);

        if (writeBufferSize > 0)
            this.tcpNIOTransportBuilder.setReadBufferSize(writeBufferSize);

    }

    @Override
    public void doStart() throws Exception {

        if (transport != null && isRunning()) {
            logger.error("Server is running now!");
            return;
        }

        try {
            initLog();

            transport = buildTCPNIOTransport();
            transport.setWorkerThreadPoolConfig(null);//custom work thread pool

            // start the transport
            transport.start();

            join();//不退出jvm

            logger.info("server exit!");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Server start error!", e);
            throw e;
        }
    }

    private void initLog() throws IOException {
        java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();
        InputStream is = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("log4j.properties");
        logManager.readConfiguration(is);
    }

    private TCPNIOTransport buildTCPNIOTransport() throws IOException {
        final TCPNIOTransport transport = tcpNIOTransportBuilder.build();
        transport.configureBlocking(false);

        // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());

        if (needSSL) {
            // Initialize and add SSLFilter
            final SSLEngineConfigurator serverConfig = initializeSSL();
            final SSLEngineConfigurator clientConfig = serverConfig.copy().setClientMode(true);

            filterChainBuilder.add(new SSLFilter(serverConfig, clientConfig));
        }

        // StringFilter is responsible for Buffer <-> String conversion
        filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8"), SocketKeys.STRING_TERMINATING_SYMB));

        filterChainBuilder.add(new ServerStringDataFilter());

        if (needAuth) {
            filterChainBuilder.add(new ServerAuthFilter());
        }

        this.jobDispatcher = new JobDispatcher();

        // EchoFilter is responsible for echoing received messages
        filterChainBuilder.add(new ThreadPoolFilter(this.jobDispatcher));

        transport.setProcessor(filterChainBuilder.build());
        //不使用grizzly默认的线程池，而是使用与应用相关的线程池,
        //优点：根据业务信息来使用线程池
        //缺点：SelectorRunner的生命周期变长.造成处理能力下降,可以通过区分accept和read/write来
        //降低这个影响程度。

        transport.setIOStrategy(SameThreadIOStrategy.getInstance());

        // binding transport to start listen on certain host and port
        logger.debug("server host:{},port:{}", new Object[] { host, port });
        transport.bind(host, port);

        return transport;
    }

    public void join() throws InterruptedException {
        synchronized (_joinLock) {
            while (isRunning())
                _joinLock.wait();
        }

        while (isStopping())
            Thread.sleep(1);
    }

    @Override
    public void doStop() throws Exception {
        try {

            if (this.transport != null) {
                this.transport.stop();
            } else {
                logger.error("transport is null, Server is " + getState());
                return;
            }

            if (this.jobDispatcher != null) {
                this.jobDispatcher.stopDispatcher();
            }

            synchronized (_joinLock) {
                _joinLock.notifyAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Server stop error!", e);
        }
    }

    /**
     * Initialize server side SSL configuration.
     * 
     * @return server side {@link SSLEngineConfigurator}.
     */
    private SSLEngineConfigurator initializeSSL() {
        // Initialize SSLContext configuration
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();

        // Set key store
        ClassLoader cl = ClassLoaderUtils.getDefaultClassLoader();
        URL cacertsUrl = cl.getResource(getTrustStoreFile());
        if (cacertsUrl != null) {
            sslContextConfig.setTrustStoreFile(cacertsUrl.getFile());
            sslContextConfig.setTrustStorePass(getTrustStorePassword());
        }

        // Set trust store
        URL keystoreUrl = cl.getResource(getKeyStoreFile());
        if (keystoreUrl != null) {
            sslContextConfig.setKeyStoreFile(keystoreUrl.getFile());
            sslContextConfig.setKeyStorePass(getKeyStorePassword());
        }

        // Create SSLEngine configurator
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(), false, isNeedClientMode(),
                isWantClientMode());
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("start server:  java -cp isocket.jar org.young.isokcet.server.NIOSocketServer start");
            System.out.println("stop server:   java -cp isocket.jar org.young.isokcet.server.NIOSocketServer stop");
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (logger.isDebugEnabled()) {
                System.setProperty("javax.net.debug", "ssl,handshake");
            }
            NIOSocketServer server = NIOSocketServer.getInstance();
            server.start();
        } else if (args[0].equalsIgnoreCase("stop")) {
            NIOSocketServer.getInstance().stop();
        } else {
            logger.error("command parameter error.");
            System.out.println("start server:  java -cp isocket.jar org.young.isokcet.server.NIOSocketServer start");
            System.out.println("stop server:   java -cp isocket.jar org.young.isokcet.server.NIOSocketServer stop");
        }
    }
}
