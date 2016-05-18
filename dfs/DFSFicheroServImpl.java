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
	private RandomAccessFile file;
	private String nom;
	private String mode;
	private int nLectores = 0;
	private int nEscritores = 0;
	private int tamBloq;
	private boolean dentro;
	//private boolean existe;
	private ArrayList<DFSFicheroCallback> usandoCache;
	private DFSServicio servicio;

	public DFSFicheroServImpl(String nom, int tamBloq, DFSServicio servicio) throws RemoteException, FileNotFoundException {
		this.nom = nom;
		this.tamBloq = tamBloq;
		this.dentro = false;
		//this.existe = true;
		this.servicio = servicio;
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
		boolean puede = puedeUsarCache(mode, callback);
		System.out.println("paso?");
		if (nLectores == 0 && nEscritores == 0) {
			File f = new File(DFSDir + nom);
			if ((!f.exists() || f.isDirectory()) && mode.equals("r")) {
				// System.out.println("entro al existe=false");
				//existe = false;
				dentro = false;
				System.out.println("libero el cerrojo? dentro="+dentro);
				notifyAll();
				throw new IOException();
			}
			// System.out.println("voy a abrir en modo " + mode);
			file = new RandomAccessFile(DFSDir + nom, "rw");
			this.mode = mode;
		}
		System.out.println("deberia estar aqui");
		if (mode.equals("r")) {
			nLectores++;
		}
		if (mode.equals("rw")) {
			nEscritores++;
			//existe = true;
		}

		dentro = false;
		notifyAll();
		return puede;
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
		// System.out.println("estoy en read");
		// System.out.println("existe="+existe);
		//if (!existe) {
		//	dentro = false;
		//	notifyAll();
		//	throw new IOException();
		//}

		byte[] b = new byte[tamBloq];
		file.seek(pos);
		int leido = file.read(b);
		if (leido == -1) {
			dentro = false;
			notifyAll();
			return null;
		}
		Bloque bloq = new Bloque(pos, b);
		dentro = false;
		// System.out.println("??");
		notifyAll();
		return bloq;
	}

	public synchronized void DFSwrite(Bloque bloq, long pos) throws IOException {
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;
		// System.out.println("estoy en write");

		file.seek(pos);
		byte[] b = bloq.obtenerContenido();
		file.write(b);
		dentro = false;
		notifyAll();
	}

	public synchronized void DFSclose(String mmode) throws IOException {
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;
		// System.out.println("estoy en close");

		//System.out.println("nEsc= " + nEscritores + " nLect= " + nLectores);
		if (mmode.equals("r")) {
			nLectores--;
		}
		if (mmode.equals("rw")) {
			nEscritores--;
		}

		// System.out.println("lectores= " + nLectores + " escritores= " +
		// nEscritores);
		if (nLectores == 0 && nEscritores == 0) {
			// System.out.println("voy a cerrar de verdad");
			file.close();
			servicio.eliminarFichero(nom);
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

	public synchronized long getFecha() {
		File f = new File(DFSDir + nom);
		return f.lastModified();
	}

	private boolean puedeUsarCache(String mode, DFSFicheroCallback callback) throws IOException {
		
		//si hay lectores o escritores y llega escritor, se desactiva
		// o si hay escritores y llega lector, se desactiva
		if((nEscritores > 0 || nLectores > 0)&&mode.equals("rw")||(nEscritores>0 && mode.equals("r"))){
			System.out.println("se va a desactivar cache, nLec="+nLectores+" nEsc="+nEscritores+" modo="+mode);
			if(!usandoCache.isEmpty()){
				Iterator<DFSFicheroCallback> i = usandoCache.iterator();
				while (i.hasNext()) {
					i.next().invalidarCache();
				}
				usandoCache.clear();
			}
			return false;
		}
		System.out.println("se permite cache, nLec="+nLectores+" nEsc="+nEscritores+" modo="+mode);
		usandoCache.add(callback);
		return true;
	}

}
