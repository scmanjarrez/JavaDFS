// Interfaz del API de acceso remoto a un fichero

package dfs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DFSFicheroServ extends Remote  {
	
	public void DFSopen(String mode)throws RemoteException, FileNotFoundException;
	
    public byte[] DFSread(int size) throws IOException;

	public void DFSwrite(byte[] b) throws IOException;
    
    public void DFSseek(long p) throws IOException;
    
    public void DFSclose() throws IOException;
}
