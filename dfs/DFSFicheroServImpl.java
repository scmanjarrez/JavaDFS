// Clase de servidor que implementa el API de acceso remoto a un fichero

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DFSFicheroServImpl extends UnicastRemoteObject implements DFSFicheroServ {
	private static final String DFSDir = "DFSDir/";
	private RandomAccessFile file;
	private String nom;
	private String mode;
	private long pos;

	public DFSFicheroServImpl(String nom) throws RemoteException, FileNotFoundException {
		this.nom = nom;
	}

	public void DFSopen(String mode) throws RemoteException, FileNotFoundException {
		System.out.println("voy a abrir en modo "+mode);
		file = new RandomAccessFile(DFSDir + nom, mode);
		this.mode = mode;
		this.pos = 0;
	}

	public byte[] DFSread(int size) throws IOException {
		System.out.println("estoy en read");
		byte[] b = new byte[size];
		file.read(b);
		pos += size;
		return b;
	}

	public void DFSwrite(byte[] b) throws IOException {
		System.out.println("estoy en write");
		pos += b.length;
		file.write(b);
//		try {
//			file.write(b);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void DFSseek(long p) throws IOException {
		System.out.println("estoy en seek");
		file.seek(p);
		pos += p;
	}

	public void DFSclose() throws IOException {
		System.out.println("estoy en close");
		file.close();
	}

	public String getNom() {
		return nom;
	}

	public String getMode() {
		return mode;
	}

	public long getPos() {
		return pos;
	}

}
