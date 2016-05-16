// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;


public class DFSFicheroCliente  {
	
	private DFSFicheroServ fich;
	private String nom;
	private String modo;
	
    public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
    		throws RemoteException, IOException, FileNotFoundException {
    	this.nom = nom;
		this.modo = modo;
		fich = dfs.getDfs_serv().iniciar(nom, modo);
		
	}

    public int read(byte[] b) throws RemoteException, IOException {
    	int size = b.length;
    	System.arraycopy(fich.DFSread(size), 0, b, 0, size);
 	 return size;
    }
    public void write(byte[] b) throws RemoteException, IOException {
    	fich.DFSwrite(b);
    }
    public void seek(long p) throws RemoteException, IOException {
    	fich.DFSseek(p);
    }
    public void close() throws RemoteException, IOException {
    	fich.DFSclose();
    }
}
