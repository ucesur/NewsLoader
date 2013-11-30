package common;

import net.spy.memcached.MemcachedClient;

public class ContinuumCache extends Cache433 {

	@Override
	protected MemcachedClient getMemcachedClient(String key) {
		boolean isDown;
		int clientNumber;
		int control = 0;
		float index = keyGenerator(key);

		if (index >= 0 && index < 1)
			clientNumber = 1;
		else if (index >= 1 && index < 2)
			clientNumber = 2;
		else
			clientNumber = 0;
		isDown = serverStatus.get(clientNumber);

		while (isDown) {
			if (clientNumber == 0)
				clientNumber = 1;
			else if (clientNumber == 1)
				clientNumber = 2;
			else
				clientNumber = 0;

			isDown = serverStatus.get(clientNumber);
			control++;
			System.out.println(control);
			System.out.println("clientnumber: " + clientNumber);
			if (control == clients.size())
				return null;
		}

		return clients.get(clientNumber);
	}

	@Override
	protected void takeServerDown(MemcachedClient whichServer) {
		int index = clients.indexOf(whichServer);
		serverStatus.set(index);

	}
	@Override
	protected void takeServerUP(int whichServer) {
		serverStatus.clear(whichServer);
		
	}
	
	private float keyGenerator(String key) {
		float index = (float) (key.hashCode() / 137.0);
		float size = clients.size();
		while (index > size) {
			index -= size;
		}
		return index;
	}

	
	

}
