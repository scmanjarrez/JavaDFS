// Clase de servidor que implementa el servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
	ArrayList<DFSFicheroServ> servicio;
	/*private boolean dentro;*/

	public DFSServicioImpl() throws RemoteException {
		servicio = new ArrayList<DFSFicheroServ>();
		/*dentro = false;*/
	}

	public synchronized FicheroInfo iniciar(String nom, String mode, int tamBloq, DFSFicheroCallback callback)
			throws RemoteException, FileNotFoundException, IOException {
		/*System.out.println("recibo 1 iniciar, dentro="+dentro);*/
		/*while (dentro) {
			try {
				//System.out.println("antes del open, dentro="+dentro);
				wait();
				//System.out.println("despues del open, dentro="+dentro);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//System.out.println("ha saltado excepcion?=");
				e.printStackTrace();
			}
		}
		//System.out.println("he pasado el bucle?");
		dentro = true;*/
		Iterator<DFSFicheroServ> i = servicio.iterator();
		DFSFicheroServImpl aux;
		boolean usarCache=false;
		while (i.hasNext()) {
			aux = (DFSFicheroServImpl) i.next();
			if (aux.getNom().equals(nom)) {
				//System.out.println("antes del open");
				try {
					usarCache = aux.DFSopen(mode, callback);
				} catch (IOException e) {
					/*dentro = false;
					notifyAll();*/
					throw new IOException();
				}
				//System.out.println("despues del open");
				/*dentro = false;
				notifyAll();*/
				return new FicheroInfo(aux.getFecha(), aux, usarCache);
			}
		}

		DFSFicheroServImpl newFile = new DFSFicheroServImpl(nom, tamBloq, this);
		servicio.add(newFile);
		try {
			usarCache = newFile.DFSopen(mode, callback);
		} catch (IOException e) {
			/*dentro = false;
			notifyAll();*/
			throw new IOException();
		}
		/*dentro = false;
		notifyAll();*/
		//System.out.println("envio 1 iniciar");
		return new FicheroInfo(newFile.getFecha(), newFile, usarCache);
	}

	public synchronized void eliminarFichero(String nom) throws RemoteException{
		Iterator<DFSFicheroServ> i = servicio.iterator();
		DFSFicheroServImpl aux;
		while (i.hasNext()) {
			aux = (DFSFicheroServImpl) i.next();
			if (aux.getNom().equals(nom)) {
				i.remove();
				//return;
			}
		}
	}

}
