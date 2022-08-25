package mx.com.spring.app.controllers.util.paginator;

public class PageItem {
	
	private int numero;
	private boolean actual;
	public PageItem(int numero, boolean actual) {
		super();
		this.numero = numero;
		this.actual = actual;
	}
	public int getNumero() {
		return numero;
	}
	public boolean isActual() {
		return actual;
	}
	
	

}
