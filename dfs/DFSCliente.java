// Clase de cliente que proporciona acceso al servicio DFS

package dfs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

public class DFSCliente {
	private DFSServicio dfs_serv;
	private int tamBloque;
	private int tamCache;
	private HashMap<String, Cache> listaCaches;

	public DFSCliente(int tamBloque, int tamCache) throws MalformedURLException, RemoteException, NotBoundException {
		String servidor = System.getenv("SERVIDOR");
		String puerto = System.getenv("PUERTO");
		dfs_serv = (DFSServicio) Naming.lookup("//" + servidor + ":" + puerto + "/DFS");
		listaCaches = new HashMap<String, Cache>();
		this.tamBloque = tamBloque;
		this.tamCache = tamCache;
	}

	public DFSServicio getDfs_serv() {
		return dfs_serv;
	}

	public Cache getCache(String nomFich) {
		Cache cache = listaCaches.get(nomFich);
		if (cache != null) {
			return cache;
		}
		cache = new Cache(tamCache);
		listaCaches.put(nomFich, cache);
		return cache;
	}

	public int getTamBloque() {
		return tamBloque;
	}

	public int getTamCache() {
		return tamCache;
	}
}
