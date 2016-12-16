import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Intermediario {

	ApiRequests encargadoPeticiones;

	Scanner teclado;
	Connection con;

	private String[][] ejecutaQuery(String query) {
		String sMatrixRes[][] = null;
		try {

			Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			rset.last();
			int f = rset.getRow();
			rset.beforeFirst();
			ResultSetMetaData rsmd = rset.getMetaData();
			int c = rsmd.getColumnCount();
			int i = 0;
			sMatrixRes = new String[f][c];
			while (rset.next()) {
				for (int j = 0; j < c; j++) {
					sMatrixRes[i][j] = rset.getString(j + 1);
				}
				i++;
			}
			rset.close();
			stmt.close();
		} catch (SQLException s) {
			s.printStackTrace();
		}
		return sMatrixRes;
	}

	public void crearConexion() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://localhost/listaespera", "root", "");
			System.out.println("Conecta con basse de datos");
		} catch (SQLException ex) {
			ex.printStackTrace();
			System.out.println("SQLException: " + ex.getMessage());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Intermediario() {
		teclado = new Scanner(System.in); // Para leer las opciones de teclado
		encargadoPeticiones = new ApiRequests();
	}

	public void ejecucion() {
		int op = 0; // Opcion
		boolean salir = false;

		while (!salir) { // Estructura que repite el algoritmo del menu
							// principal hasta que se la condicion sea falsa
			// Se muestra el menu principal
			System.out.println(".......................... \n" + ".  0 Salir \n" + ".  1 Leer consultas abiertas  \n"
					+ ".  2 Iniciar consulta \n" + ".  3 Cerrar consulta \n"

					+ "..........................");
			try {
				op = teclado.nextInt(); // Se le da a la variable op el valor
										// del teclado
				System.out.println("OPCION SELECCIONADA:" + op);
				switch (op) {
				case 0:
					System.out.println("Adios");
					System.exit(0);
				case 1://
					leerConsultas();
					break;
				case 2://
					insertar();
					break;
				case 3://
					borrar();
					break;
				default:// No valido
					System.out.println("Opcion invalida: marque un numero de 1 a 3");
					break;
				}
			} catch (Exception e) {
				System.out.println("Excepcion por opcion invalida: marque un numero de 1 a 3");
				// flushing scanner
				// e.printStackTrace();
				teclado.next();
			}
		}

		// teclado.close();

	}

	private void leerConsultas() {

		try {
			System.out.println("Lanzamos peticion JSON para consultas abiertas");

			String url = "http://localhost/listaEspera/vertablajson.php";

			System.out.println("La url a la que lanzamos la petición es " + url);

			String response = encargadoPeticiones.getRequest(url);

			JSONParser jsonParser = new JSONParser();
			JSONArray jsonarr = (JSONArray) jsonParser.parse(response);

			JSONObject jsonObject;

			for (int i = 0; i < jsonarr.size(); i++) {
				jsonObject = (JSONObject) jsonarr.get(i);
				System.out.println("La id de la peticion: " + jsonObject.get("idPeticion"));
				System.out.println("Usuario: " + jsonObject.get("usuario"));
				System.out.println("Duda: " + jsonObject.get("texto"));
				System.out.println("Fecha inicio: " + jsonObject.get("fechaInicio"));
				System.out.println("------------");
			}

			// System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Ha ocurrido un error en la busqueda de datos");
			System.out.println(e.getMessage());
			System.exit(-1);
		}

	}

	@SuppressWarnings("unchecked")
	private void insertar() throws IOException {

		String url = "http://localhost/listaEspera/anadir.php";

		Scanner sc = new Scanner(System.in);

		System.out.println("Usuario:");
		String usuario = sc.nextLine();

		System.out.println("Duda:");
		String duda = sc.nextLine();

		JSONObject json = new JSONObject();
		json.put("usuario", usuario);
		json.put("pregunta", duda);

		String jsonString = json.toJSONString();
		String response = encargadoPeticiones.postRequestWithParams(url, jsonString);

		// System.out.println(response);
		// System.out.println(json.toJSONString());

	}

	private void borrar() throws IOException {

		Scanner sc = new Scanner(System.in);

		System.out.println("Escribe la id de la pregunta que quieres borrar:");
		int id = sc.nextInt();

		String url = "http://localhost/listaEspera/borrar.php";

		JSONObject json = new JSONObject();
		json.put("id", id);

		String jsonString = json.toJSONString();
		String response = encargadoPeticiones.postRequestWithParams(url, jsonString);

		System.out.println(response);
		System.out.println(json.toJSONString());

	}
}