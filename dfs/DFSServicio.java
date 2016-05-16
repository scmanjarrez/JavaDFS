// Interfaz del servicio DFS

package dfs;
import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DFSServicio extends Remote {

    DFSFicheroServ iniciar(String nom, String mode) throws RemoteException, FileNotFoundException;
    
}       
