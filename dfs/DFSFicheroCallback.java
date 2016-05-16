// Interfaz del servicio de callback de DFS

package dfs;

import java.io.IOException;
import java.rmi.Remote;

public interface DFSFicheroCallback extends Remote {

	public void invalidarCache() throws IOException;

}
