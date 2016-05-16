// Interfaz del servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DFSServicio extends Remote {

	FicheroInfo iniciar(String nom, String mode, int tamBloq, DFSFicheroCallback callback)
			throws RemoteException, FileNotFoundException, IOException;

}
