// Clase de cliente que implementa el servicio de callback de DFS

package dfs;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DFSFicheroCallbackImpl extends UnicastRemoteObject implements DFSFicheroCallback {
	private DFSFicheroCliente dfsCliente;

	public DFSFicheroCallbackImpl(DFSFicheroCliente dfsCliente) throws RemoteException {
		this.dfsCliente = dfsCliente;
	}

	public void invalidarCache() throws IOException {
		dfsCliente.invalidacionCache();
	}
}
