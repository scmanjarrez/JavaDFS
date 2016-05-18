// Interfaz del API de acceso remoto a un fichero

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DFSFicheroServ extends Remote {

	public boolean DFSopen(String mode, DFSFicheroCallback callback)
			throws RemoteException, FileNotFoundException, IOException;

	public Bloque DFSread(long pos) throws IOException;

	public void DFSwrite(Bloque bloq, long pos) throws IOException;

	public void DFSclose(String mmode) throws IOException;
}
