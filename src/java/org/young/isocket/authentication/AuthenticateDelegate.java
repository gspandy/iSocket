/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.authentication;

import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netscape.ldap.LDAPCache;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.util.ConnectionPool;

import org.young.icore.annotation.PropertiesAnnotation;
import org.young.icore.util.PropertiesLoaderUtils;

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
public class AuthenticateDelegate {

	private final static String[] RETURN_DEFAULT = new String[] { "uid" };

	@PropertiesAnnotation(name = "ldapuserfilterkey", resource = "isocket-server.properties")
	private String userFilterKey;// "(&(uid=%u)(pafaisactive=true)(objectclass=person))";

	@PropertiesAnnotation(name = "ldapprincipal", resource = "isocket-server.properties")
	private String principal;
	@PropertiesAnnotation(name = "ldapcredential", resource = "isocket-server.properties")
	private String credential;

	@PropertiesAnnotation(name = "ldapport", resource = "isocket-server.properties")
	private int port;
	@PropertiesAnnotation(name = "ldahost", resource = "isocket-server.properties")
	private String host;
	@PropertiesAnnotation(name = "ldappoolmin", resource = "isocket-server.properties")
	private int min = 1;
	@PropertiesAnnotation(name = "ldappoolmax", resource = "isocket-server.properties")
	private int max = 150;

	@PropertiesAnnotation(name = "ldapuserbasedn", resource = "isocket-server.properties")
	private String userBaseDN = "ou=people,o=paic.com.cn";

	@PropertiesAnnotation(name = "ldapcachetimeout", resource = "isocket-server.properties")
	private int cacheTimeout = 60;

	@PropertiesAnnotation(name = "ldapcachesize", resource = "isocket-server.properties")
	private int cacheSize = 10 * 1024 * 1024;

	private ConnectionPool pool;

	private ConnectionPool cachePool;

	private LDAPCache cache;

	private Matcher userFilter;

	private final ReentrantLock lock = new ReentrantLock();

	private boolean initFlag = false;

	public AuthenticateDelegate() {

	}

	private void init() throws LDAPException {
		if (!initFlag) {
			lock.lock();
			try {
				PropertiesLoaderUtils.setPropertiesFields(this);
				System.out.println("min:" + min + ",max:" + max + ",host:"
						+ host + ",port:" + port + ",principal:" + principal
						+ ",credential:" + credential);
				pool = new ConnectionPool(min, max, host, port, principal,
						credential);
				cachePool = new ConnectionPool(min, max, host, port, principal,
						credential);

				userFilter = Pattern.compile("%u", Pattern.CASE_INSENSITIVE)
						.matcher(userFilterKey);
				cache = new LDAPCache(cacheTimeout, cacheSize);
			} catch (LDAPException e) {
				throw e;
			} finally {
				lock.unlock();
			}
		}
	}

	public String getUserFilterKey() {
		return userFilterKey;
	}

	public void setUserFilterKey(String userFilterKey) {
		this.userFilterKey = userFilterKey;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getUserBaseDN() {
		return userBaseDN;
	}

	public void setUserBaseDN(String userBaseDN) {
		this.userBaseDN = userBaseDN;
	}

	public int getCacheTimeout() {
		return cacheTimeout;
	}

	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public ConnectionPool getPool() {
		return pool;
	}

	public void setPool(ConnectionPool pool) {
		this.pool = pool;
	}

	public ConnectionPool getCachePool() {
		return cachePool;
	}

	public void setCachePool(ConnectionPool cachePool) {
		this.cachePool = cachePool;
	}

	public LDAPCache getCache() {
		return cache;
	}

	public void setCache(LDAPCache cache) {
		this.cache = cache;
	}

	public Matcher getUserFilter() {
		return userFilter;
	}

	public void setUserFilter(Matcher userFilter) {
		this.userFilter = userFilter;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public boolean authenticate(String userName, String password)
			throws LDAPException {

		init();
		String dn = getUserDN(userName);
		boolean result = false;
		LDAPConnection conn = getConnFromPool();

		try {
			// LDAPAttribute attr = new LDAPAttribute("userpassword", password);
			// result = conn.compare(dn, attr);
			conn.bind(dn, password);
			result = true;
		} catch (LDAPException e) {
			result = false;
			throw e;
		} finally {
			closeConn(conn);
		}

		return result;
	}

	/**
	 * 无cache的连接
	 * 
	 * @return
	 */
	private LDAPConnection getConnFromPool() {
		LDAPConnection conn = pool.getConnection();

		return conn;
	}

	private LDAPConnection getCachedConnFromPool() {
		LDAPConnection conn = cachePool.getConnection();

		if (conn.getCache() == null) {
			conn.setCache(cache);
		}

		return conn;
	}

	/**
	 * 根据uid取到用户的DN
	 * 
	 * @param uid
	 * @return
	 * @throws LDAPException
	 */
	private String getUserDN(String uid) throws LDAPException {
		// logger.log(Level.FINEST, "Begin getUserDN...");

		String result = null;
		LDAPConnection conn = getConnFromPool();

		try {
			LDAPSearchResults searchResults = conn.search(userBaseDN,
					LDAPConnection.SCOPE_SUB, getUserFilter(uid),
					RETURN_DEFAULT, false);

			if (searchResults.hasMoreElements()) {
				LDAPEntry entry = searchResults.next();
				result = entry.getDN();
			}
		} catch (LDAPException e) {
			throw e;
		} finally {
			closeConn(conn);
		}

		// logger.log(Level.FINEST, "End getUserDN.");

		return result;
	}

	/**
	 * 
	 * <p>
	 * 描述: 线程不安全
	 * </p>
	 * 
	 * @param
	 * @return
	 * @throws
	 * @see
	 * @since %I%
	 */
	private synchronized String getUserFilter(String uid) {
		lock.lock();
		try {
			userFilter.reset();
			return userFilter.replaceFirst(uid);
		} finally {
			lock.unlock();
		}

	}

	private void closeConn(LDAPConnection conn) {
		try {
			if (conn != null) {
				pool.close(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		AuthenticateDelegate ad = new AuthenticateDelegate();
		boolean r = ad.authenticate("EAUSER123", "aaaaa888");
		System.out.println(r);
	}
}
