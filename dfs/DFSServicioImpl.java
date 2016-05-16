// Clase de servidor que implementa el servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
	ArrayList<DFSFicheroServ> servicio;
	
    public DFSServicioImpl() throws RemoteException {
    	servicio = new ArrayList<DFSFicheroServ>();
    }
    
    public DFSFicheroServ iniciar(String nom, String mode) throws RemoteException, FileNotFoundException{
		Iterator<DFSFicheroServ> i = servicio.iterator();
		DFSFicheroServImpl aux;
		while (i.hasNext()) {
			aux = (DFSFicheroServImpl)i.next();
			if (aux.getNom().equals(nom)) {
				aux.DFSopen(mode);
				return aux;
			}
		}

		DFSFicheroServImpl newFile = new DFSFicheroServImpl(nom);
		servicio.add(newFile);
		newFile.DFSopen(mode);
		return newFile;
    }
}
