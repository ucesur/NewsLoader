package common;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import play.Logger;
import play.Play;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public abstract class Cache433 {
	
	private static String memCachedServers = Play.configuration
			.getProperty("memcached.servers");

	protected static List<MemcachedClient> clients = new ArrayList<MemcachedClient>();
	protected static BitSet serverStatus = null;

	private static Cache433 __CSCache = new ContinuumCache();

	private AtomicInteger cacheHits = new AtomicInteger(0);
	private AtomicInteger cacheMisses = new AtomicInteger(0);
	private AtomicInteger cacheErrors = new AtomicInteger(0);
	private AtomicInteger cacheUseCounter = new AtomicInteger(0);
	
	protected abstract MemcachedClient getMemcachedClient(String key);

	protected abstract void takeServerDown(MemcachedClient whichServer);
	
	protected abstract void takeServerUP(int whichServer);


	public static Cache433 getCSInstance() {
		return __CSCache;
	}

	protected Cache433() {
		if (clients.size() > 0) {
			return;
		}

		String[] servers = memCachedServers.split(",");

		int serverCount = 0;
		for (String tmpS : servers) {
			try {
				clients.add(new MemcachedClient(AddrUtil.getAddresses(tmpS)));
				serverCount++;
			} catch (Exception exc) {
				Logger.error(exc, "Failed initiating client: %s", tmpS);
			}
		}
		serverStatus = new BitSet(serverCount);
	}
	
	public void setCacheItem(String key, Object value, int timeToLiveInSeconds) {
		MemcachedClient mClient = getMemcachedClient(key);

		Future<Boolean> f = mClient.set(key, timeToLiveInSeconds, value);

		try {
			f.get(1, TimeUnit.SECONDS);
			cacheUseCounter.incrementAndGet();
		} catch (Exception exc) {
			f.cancel(false);
			takeServerDown(mClient);
			cacheErrors.incrementAndGet();
		}
	}

	public Object getCacheItem(String key) {
		MemcachedClient mClient = getMemcachedClient(key);

		Future<Object> f = mClient.asyncGet(key);

		try {
			Object retVal = f.get(1, TimeUnit.SECONDS);
			cacheUseCounter.incrementAndGet();
			if (retVal == null) {
				cacheMisses.incrementAndGet();
			} else {
				cacheHits.incrementAndGet();
			}
			return retVal;
		} catch (Exception exc) {
			f.cancel(false);
			takeServerDown(mClient);
			cacheErrors.incrementAndGet();
		}
		return null;
	}

	public void resetCounters() {
		cacheHits.set(0);
		cacheMisses.set(0);
		cacheErrors.set(0);
		cacheUseCounter.set(0);
	}

	public String getStats() {
		return "[Hits: " + cacheHits.get() + ", Misses: " + cacheMisses.get()
				+ ", Errors: " + cacheErrors.get() + "]";
	}
}
