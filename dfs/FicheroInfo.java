// Esta clase representa información de un fichero.
// El enunciado explica más en detalle el posible uso de esta clase.
// Al ser serializable, puede usarse en las transferencias entre cliente
// y servidor.

package dfs;

import java.io.Serializable;

public class FicheroInfo implements Serializable {
	private long fecha;
	private DFSFicheroServ dfs_fich;
	private boolean usarCache;

	public FicheroInfo(long fecha, DFSFicheroServ dfs_fich, boolean usarCache) {
		this.fecha = fecha;
		this.dfs_fich = dfs_fich;
		this.usarCache = usarCache;
	}

	public long getFecha() {
		return fecha;
	}

	public DFSFicheroServ getDfs_fich() {
		return dfs_fich;
	}

	public boolean getUsarCache() {
		return usarCache;
	}

}
