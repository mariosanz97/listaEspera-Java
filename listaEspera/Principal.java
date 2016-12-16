
public class Principal {

	public static void main(String[] args) {
		System.out.println("Inicio Ejecucion - Cliente Lista Espera");
		
		Intermediario intermediario = new Intermediario();
		
		intermediario.ejecucion();
				
		System.out.println("Fin Ejecucion - Cliente Lista Espera");		
		
	}

}
