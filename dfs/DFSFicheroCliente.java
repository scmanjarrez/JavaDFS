// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

public class DFSFicheroCliente {

	private DFSFicheroServ fich;
	private DFSCliente dfs;
	private String nom;
	private String modo;
	private long pos;
	private Cache cache;
	private int tamBloq;
	private int tamCache;
	private long fecha;
	private boolean open;
	private boolean activada;
	private boolean dentro = false;
	private DFSFicheroCallback callback;

	public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
			throws RemoteException, IOException, FileNotFoundException {
		this.nom = nom;
		this.modo = modo;
		this.dfs = dfs;
		this.tamBloq = dfs.getTamBloque();
		this.tamCache = dfs.getTamCache();
		this.pos = 0;
		this.callback = new DFSFicheroCallbackImpl(this);
		
		//System.out.println("hago peticion de abrir fichero");
		FicheroInfo aux = dfs.getDfs_serv().iniciar(nom, modo, tamBloq, callback);
		//System.out.println("recibo respuesta de peticion de abrir");
		cache = dfs.getCache(nom);
		if (aux.getFecha() > cache.obtenerFecha()) {
			//System.out.println("voy a vaciar");
			cache.vaciar();
		}
		this.fecha = aux.getFecha();
		fich = aux.getDfs_fich();
		open = true;
		activada = aux.getUsarCache();
	}

	public int read(byte[] b) throws RemoteException, IOException {
		//System.out.println("entro en cliente read?");
		if (!open) {
			throw new IOException();
		}

		int nBloq = b.length / tamBloq;
		Bloque b_serv;
		Bloque bloq;
		Bloque dirty;
		if (activada) {
			for (int i = 0, offset = 0; i < nBloq; i++, pos += tamBloq, offset += tamBloq) {
				bloq = cache.getBloque(pos);
				if (bloq == null) {
					b_serv = fich.DFSread(pos);

					bloq = new Bloque(b_serv.obtenerId(), b_serv.obtenerContenido());

					dirty = cache.putBloque(bloq);
					if (dirty != null && cache.preguntarMod(dirty)) {
						fich.DFSwrite(dirty);
						cache.desactivarMod(dirty);
					}
				}

				System.arraycopy(bloq.obtenerContenido(), 0, b, offset, tamBloq);
			}
		} else {
			for (int i = 0, offset = 0; i < nBloq; i++, pos += tamBloq, offset += tamBloq) {
				b_serv = fich.DFSread(pos);
				bloq = new Bloque(b_serv.obtenerId(), b_serv.obtenerContenido());
				System.arraycopy(bloq.obtenerContenido(), 0, b, offset, tamBloq);
			}
		}

		return b.length;
	}

	public void write(byte[] b) throws RemoteException, IOException {
		//System.out.println("entro en cliente write?");

		if (!open) {
			throw new IOException();
		}

		if (!modo.equals("rw")) {
			throw new IOException();
		}
		int nBloq = b.length / tamBloq;
		byte[] baux = new byte[tamBloq];
		Bloque bloq;
		Bloque dirty;
		if (activada) {
			for (int i = 0, offset = 0; i < nBloq; i++, pos += tamBloq, offset = tamBloq) {
				System.arraycopy(b, offset, baux, 0, tamBloq);
				bloq = new Bloque(pos, baux);
				cache.activarMod(bloq);
				dirty = cache.putBloque(bloq);
				if (dirty != null && cache.preguntarMod(dirty)) {
					fich.DFSwrite(dirty);
					cache.desactivarMod(dirty);
				}
				fecha++;
			}
		} else {
			for (int i = 0, offset = 0; i < nBloq; i++, pos += tamBloq, offset = tamBloq) {
				System.arraycopy(b, offset, baux, 0, tamBloq);
				bloq = new Bloque(pos, baux);
				fich.DFSwrite(bloq);
			}
		}
	}

	public void seek(long p) throws RemoteException, IOException {
		//System.out.println("entro en cliente seek?");

		if (!open) {
			throw new IOException();
		}

		pos = p;
	}

	public void close() throws RemoteException, IOException {
		//System.out.println("entro en cliente close?");

		if (!open) {
			throw new IOException();
		}

		if (activada) {
			List<Bloque> modificados = cache.listaMod();
			Iterator<Bloque> i = modificados.iterator();
			Bloque aux;

			while (i.hasNext()) {
				aux = i.next();
				fich.DFSwrite(aux);
				cache.desactivarMod(aux);
			}
			cache.fijarFecha(fecha);
		}
		open = false;
		//System.out.println("voy a enviar peticion de close a servidor");
		fich.DFSclose();
		//System.out.println("acabo de recibir el resultado del close");
	}

	public synchronized void invalidacionCache() throws IOException {
		//System.out.println("me van a desalojar, pero que cojones???!!");
		while (dentro) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dentro = true;
		//System.out.println("ahora si me desalojan t_t, stop deshaucios!!!");
		activada = false;
		if (modo.equals("rw")) {
			List<Bloque> modificados = cache.listaMod();
			Iterator<Bloque> i = modificados.iterator();
			Bloque aux;

			while (i.hasNext()) {
				aux = i.next();
				fich.DFSwrite(aux);
				cache.desactivarMod(aux);
			}
		}
		cache.vaciar();
		dentro = false;
		notifyAll();
	}

}
