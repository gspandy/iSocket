/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.WriteResult;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.impl.SafeFutureImpl;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.ssl.SSLFilter;
import org.glassfish.grizzly.utils.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.Assert;
import org.young.icore.util.ClassLoaderUtils;
import org.young.isocket.filter.ClientAuthFilter;
import org.young.isocket.filter.ClientFutureFilter;
import org.young.isocket.filter.ClientSSLFilter;
import org.young.isocket.filter.ClientStringDataFilter;
import org.young.isocket.service.ServiceRequest;
import org.young.isocket.service.ServiceResponse;
import org.young.isocket.util.SocketKeys;

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
public class NIOSocketClient {
	private static final Logger logger = LoggerFactory
			.getLogger(NIOSocketClient.class);

	private TCPNIOTransportBuilder tcpNIOTransportBuilder;
	private TCPNIOTransport transport;
	private Connection connection;

	private ClientFutureFilter clientFutureFilter;

	private ClientSSLFilter clientSSLFilter;

	private FilterChainBuilder filterChainBuilder;
	private String userName;
	private String password;

	private boolean needSSL;
	private boolean needAuth;

	public boolean isNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}

	private String trustStoreFile;
	private String trustStorePassword;
	private String keyStoreFile;
	private String keyStorePassword;

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

	public NIOSocketClient() {
		this(null, null);
	}

	public NIOSocketClient(String userName, String password) {

		this.userName = userName;
		this.password = password;

		tcpNIOTransportBuilder = TCPNIOTransportBuilder.newInstance();

	}

	private void init() {
		clientFutureFilter = new ClientFutureFilter();
		// Create a FilterChain using FilterChainBuilder
		filterChainBuilder = FilterChainBuilder.stateless();
		// Add TransportFilter, which is responsible
		// for reading and writing data to the connection
		filterChainBuilder.add(new TransportFilter());

		SSLFilter sslFilter = null;
		if (isNeedSSL()) {
			// Initialize and add SSLFilter
			final SSLEngineConfigurator serverConfig = initializeSSL();
			final SSLEngineConfigurator clientConfig = serverConfig.copy()
					.setClientMode(true);

			sslFilter = new SSLFilter(serverConfig, clientConfig);
			filterChainBuilder.add(sslFilter);
		}

		filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8"),
				SocketKeys.STRING_TERMINATING_SYMB));

		filterChainBuilder.add(new ClientStringDataFilter());

		if (isNeedAuth()) {

			Assert.notNull(this.userName, "user name must not be null");
			Assert.notNull(this.password, "password must not be null");
			filterChainBuilder.add(new ClientAuthFilter(this.userName,
					this.password));

		}

		if (isNeedSSL()) {
			clientSSLFilter = new ClientSSLFilter(sslFilter);
			filterChainBuilder.add(clientSSLFilter);
		}

		filterChainBuilder.add(clientFutureFilter);

	}

	/**
	 * 
	 * <p>
	 * 描述:设置客户端超时时间 ,单位milliseconds
	 * </p>
	 * 
	 * @param
	 * @return
	 * @throws
	 * @see
	 * @since %I%
	 */
	public void setSoTimeout(int clientSocketSoTimeout) {
		tcpNIOTransportBuilder.setClientSocketSoTimeout(clientSocketSoTimeout);
	}

	public int getSoTimeout() {
		return tcpNIOTransportBuilder.getClientSocketSoTimeout();
	}

	/**
	 * 
	 * <p>
	 * 描述:建立连接的超时时间
	 * </p>
	 * 
	 * @param
	 * @return
	 * @throws
	 * @see
	 * @since %I%
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		tcpNIOTransportBuilder.setConnectionTimeout(connectionTimeout);
	}

	/**
	 * 
	 * <p>
	 * 描述:当 SO_KEEPALIVE 选项为 true 时, 表示底层的TCP 实现会监视该连接是否有效.
	 * 当连接处于空闲状态(连接的两端没有互相传送数据) 超过了 2 小时时, 本地的TCP 实现 会发送一个数据包给远程的 Socket.
	 * 如果远程Socket 没有发回响应, TCP实现就会持续 尝试 11 分钟, 直到接收到响应为止. 如果在 12 分钟内未收到响应, TCP
	 * 实现就会自动 关闭本地Socket, 断开连接. 在不同的网络平台上, TCP实现尝试与远程Socket 对话的时限有所差别.
	 * </p>
	 * 
	 * @param
	 * @return
	 * @throws
	 * @see
	 * @since %I%
	 */
	public void setKeepAlive(boolean keepAlive) {
		tcpNIOTransportBuilder.setKeepAlive(keepAlive);
	}

	/**
	 * 
	 * <p>
	 * 描述:那么执行Socket 的 close() 方法, 该方法不会立即返回, 而是进入阻塞状态. 同时, 底层的 Socket
	 * 会尝试发送剩余的数据. 只有满足以下两个条件之一, close() 方法才返回: ⑴ 底层的 Socket 已经发送完所有的剩余数据; ⑵
	 * 尽管底层的 Socket 还没有发送完所有的剩余数据, 但已经阻塞了 x 秒(注意这里是秒, 而非毫秒), close() 方法的阻塞时间超过
	 * 3600 秒, 也会返回, 剩余未发送的数据被丢弃. 值得注意的是, 在以上两种情况内, 当close() 方法返回后, 底层的 Socket
	 * 会被关闭, 断开连接. 此外, setSoLinger(boolean on, int seconds) 方法中的 seconds
	 * 参数以秒为单位, 而不是以毫秒为单位. 单位:秒
	 * </p>
	 * 
	 * @param
	 * @return
	 * @throws
	 * @see
	 * @since %I%
	 */
	public void setLinger(int linger) {
		tcpNIOTransportBuilder.setLinger(linger);
	}

	/**
	 * 
	 * <p>
	 * 描述:默认情况下, 发送数据采用Negale 算法. Negale 算法是指发送方发送的数据不会立即发出, 而是先放在缓冲区,
	 * 等缓存区满了再发出. 发送完一批数据后, 会等待接收方对这批数据的回应, 然后再发送下一批数据. Negale
	 * 算法适用于发送方需要发送大批量数据, 并且接收方会及时作出 回应的场合, 这种算法通过减少传输数据的次数来提高通信效率.
	 * </p>
	 * 
	 * @param
	 * @return
	 * @throws
	 * @see
	 * @since %I%
	 */
	public void setTcpNoDelay(boolean tcpNoDelay) {
		tcpNIOTransportBuilder.setTcpNoDelay(tcpNoDelay);
	}

	public void setReadBuffer(int readBufferSize) {
		tcpNIOTransportBuilder.setReadBufferSize(readBufferSize);
	}

	public void setWriteBuffer(int writeBufferSize) {
		tcpNIOTransportBuilder.setWriteBufferSize(writeBufferSize);
	}

	public void add(Filter filter) {
		filterChainBuilder.addLast(filter);
	}

	public void connect(final String host, final int port) throws IOException {
		// init
		init();

		// Create TCP NIO transport
		transport = tcpNIOTransportBuilder.build();

		transport.setProcessor(filterChainBuilder.build());

		// start transport
		try {
			transport.start();
			Future<Connection> future = transport.connect(host, port);
			connection = future.get(10, TimeUnit.SECONDS);

			if (isNeedSSL()) {
				HandshakeStatus handshakeStatus = clientSSLFilter
						.getHandshakeStatus(10, TimeUnit.SECONDS);
				if (handshakeStatus == null
						|| handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
					throw new IOException(String.format(
							"SSL handshake status:%s error!", handshakeStatus));
				}
			}

			// return connection;

		} catch (IOException e) {
			logger.error("client connect error!", e);
			throw e;
		} catch (TimeoutException e) {
			logger.error("client connect error!", e);
			throw new IOException(e);
		} catch (InterruptedException e) {
			logger.error("client connect error!", e);
			throw new IOException(e);
		} catch (ExecutionException e) {
			logger.error("client connect error!", e);
			throw new IOException(e);
		}

	}

	// public HandshakeStatus getSSLHandshakeStatus(long timeout, TimeUnit unit)
	// throws IOException {
	// try {
	// HandshakeStatus r = clientSSLFilter.getHandshakeStatus(timeout, unit);
	// return r;
	// } catch (IOException e) {
	// logger.error("getServiceResponse error.", e);
	// throw e;
	// }
	// }
	//
	// public HandshakeStatus getSSLHandshakeStatus() throws IOException {
	// try {
	// HandshakeStatus r = clientSSLFilter.getHandshakeStatus();
	// return r;
	// } catch (IOException e) {
	// logger.error("getServiceResponse error.", e);
	// throw e;
	// }
	// }

	public ServiceResponse getServiceResponse(long timeout, TimeUnit unit)
			throws IOException {
		try {
			ServiceResponse r = clientFutureFilter.getServiceResponse(timeout,
					unit);
			return r;
		} catch (IOException e) {
			logger.error("getServiceResponse error.", e);
			throw e;
		}
	}

	public ServiceResponse getServiceResponse() throws IOException {
		try {
			ServiceResponse r = clientFutureFilter.getServiceResponse();
			return r;
		} catch (IOException e) {
			logger.error("getServiceResponse error.", e);
			throw e;
		}
	}

	public void write(ServiceRequest request) throws IOException {
		try {
			SafeFutureImpl<ServiceResponse> resultFuture = SafeFutureImpl
					.create();
			clientFutureFilter.setFuture(resultFuture);
			GrizzlyFuture<WriteResult<ServiceRequest, SocketAddress>> write = connection
					.write(request);
		} catch (IOException e) {
			logger.error("write service request error.", e);
			throw e;
		}
	}

	public void close() throws IOException {
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				logger.error("client close connection error!", e);
				throw e;
			}
		}

		if (transport != null) {
			try {
				transport.stop();
				transport = null;
			} catch (IOException e) {
				logger.error("client stop transport error!", e);
				throw e;
			}

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
		URL cacertsUrl = cl.getResource(getTrustStoreFile());// getTrustStoreFile()
																// serverstore.jks
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
		return new SSLEngineConfigurator(sslContextConfig.createSSLContext(),
				false, false, false);
	}

	public static void main(String[] args) throws Exception {
		// NIOSocketClient client = new NIOSocketClient();
		NIOSocketClient client = new NIOSocketClient("EAUSER123", "aaaaa888");
		client.setNeedSSL(false);
		client.setNeedAuth(false);
		client.setSoTimeout(1);
		client.setKeepAlive(true);
		client.setLinger(30);
		client.setTcpNoDelay(false);

		// client.setNeedSSL(true);
		// client.setTrustStoreFile("ssltest-cacerts.jks");
		// client.setTrustStorePassword("changeit");
		// client.setKeyStoreFile("ssltest-keystore.jks");
		// client.setKeyStorePassword("changeit");

		// final FutureImpl<ServiceResponse> resultMessageFuture =
		// SafeFutureImpl.create();
		// client.add(new ClientFutureFilter(resultMessageFuture));
		client.connect("localhost", 7777);

		ServiceRequest message1 = new ServiceRequest();
		message1.setId("11111111111111111111111111111122");
		message1.setRequestObject("test string");
		message1.setServiceId("1000010001");
		message1.setTransformType(SocketKeys.TRANSFORM_JSON);
		logger.debug("write message:");
		client.write(message1);
		ServiceResponse rcvMessage = client.getServiceResponse(10,
				TimeUnit.SECONDS);
		// ServiceResponse rcvMessage = client.getServiceResponse();
		if (rcvMessage != null)
			logger.info(rcvMessage.toString());
		else
			logger.info("receiveMessage is null!");
		
		ServiceRequest message2 = new ServiceRequest();
		message2.setId("11111111111111111111111111111122");
		message2.setRequestObject("test string");
		message2.setServiceId("1000010003");
		message2.setTransformType(SocketKeys.TRANSFORM_JSON);
		logger.debug("write message:");
		client.write(message2);
		
		client.close();
		
		
		
		
	}
}
