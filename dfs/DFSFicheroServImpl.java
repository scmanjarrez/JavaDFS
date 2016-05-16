// Clase de servidor que implementa el API de acceso remoto a un fichero

package dfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class DFSFicheroServImpl extends UnicastRemoteObject implements DFSFicheroServ {
	private static final String DFSDir = "DFSDir/";
	private long fecha;
	private RandomAccessFile file;
	private String nom;
	private String mode;
	private int nLectores = 0;
	private int nEscritores = 0;
	private int tamBloq;
	private boolean dentro;
	private boolean existe;
	private ArrayList<DFSFicheroCallback> usandoCache;

	public DFSFicheroServImpl(String nom, int tamBloq) throws RemoteException, FileNotFoundException {
		this.nom = nom;
		this.fecha = 0;
		this.tamBloq = tamBloq;
		this.dentro = false;
		this.existe = true;
		this.usandoCache = new ArrayList<DFSFicheroCallback>();
	}

	public synchronized boolean DFSopen(String mode, DFSFicheroCallback callback) throws IOException {
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;

		if (nLectores == 0 && nEscritores == 0) {
			File f = new File(nom);
			if ((!f.exists() || f.isDirectory()) && mode.equals("r")) {
				existe = false;
			}
			//System.out.println("voy a abrir en modo " + mode);
			file = new RandomAccessFile(DFSDir + nom, "rw");
			this.mode = mode;
		}
		if (mode.equals("r")) {
			nLectores++;
		}
		if (mode.equals("rw")) {
			nEscritores++;
			existe = true;
		}

		dentro = false;
		notifyAll();
		return puedeUsarCache(mode, callback);
	}

	public synchronized Bloque DFSread(long pos) throws IOException {
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;
		//System.out.println("estoy en read");
		if (!existe) {
			throw new IOException();
		}

		byte[] b = new byte[tamBloq];
		file.seek(pos);
		file.read(b);
		Bloque bloq = new Bloque(pos, b);
		dentro = false;
		//System.out.println("??");
		notifyAll();
		return bloq;
	}

	public synchronized void DFSwrite(Bloque bloq) throws IOException {
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;
		//System.out.println("estoy en write");

		file.seek(bloq.obtenerId());
		byte[] b = bloq.obtenerContenido();
		file.write(b);
		fecha++;
		dentro = false;
		notifyAll();
	}

	public synchronized void DFSclose() throws IOException {
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;
		//System.out.println("estoy en close");

		if (mode.equals("r")) {
			nLectores--;
		}
		if (mode.equals("rw")) {
			nEscritores--;
		}

		//System.out.println("lectores= " + nLectores + " escritores= " + nEscritores);
		if (nLectores == 0 && nEscritores == 0) {
			//System.out.println("voy a cerrar de verdad");
			file.close();
		}
		dentro = false;
		notifyAll();
	}

	public String getNom() {
		return nom;
	}

	public String getMode() {
		return mode;
	}

	public int getTamBloq() {
		return tamBloq;
	}

	public long getFecha() {
		return fecha;
	}

	private boolean puedeUsarCache(String mode, DFSFicheroCallback callback) throws IOException {
		if (nEscritores == 0 && mode.equals("r")) {
			usandoCache.add(callback);
			//System.out.println("puede usar cache");
			return true;
		}
		if (nLectores != 0 && mode.equals("rw")) {
			//System.out.println("no puede usar cache, desalojando...");
			Iterator<DFSFicheroCallback> i = usandoCache.iterator();
			while (i.hasNext()) {
				i.next().invalidarCache();
			}
			usandoCache.clear();
			return false;
		}
		if (nEscritores == 1 && mode.equals("r")) {
			//System.out.println("no puede usar cache, desalojando...");
			usandoCache.get(0).invalidarCache();
			usandoCache.remove(0);
			if (!usandoCache.isEmpty()) {
				//System.out.println("error, deberia estar vacia la lista de callbacks, ya que solo 'habia' 1 escritor");
			}
			return false;
		}

		return false;
	}

}
