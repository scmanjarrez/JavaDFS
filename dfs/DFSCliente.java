// Clase de cliente que proporciona acceso al servicio DFS

package dfs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class DFSCliente {
	private DFSServicio dfs_serv;
	private int tamBloque;
	private int tamCache;
	
    public DFSCliente(int tamBloque, int tamCache) throws MalformedURLException, RemoteException, NotBoundException {
		String servidor = System.getenv("SERVIDOR");
		String puerto = System.getenv("PUERTO");
		dfs_serv = (DFSServicio) Naming.lookup("//" +servidor+ ":" + puerto + "/DFS");
		this.tamBloque = tamBloque;
		this.tamCache = tamCache;
    }

	public DFSServicio getDfs_serv() {
		return dfs_serv;
	}

	public int getTamBloque() {
		return tamBloque;
	}

	public int getTamCache() {
		return tamCache;
	}
}

