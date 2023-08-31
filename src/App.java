import java.sql.*;
import java.util.*;

class Program {
    public static abstract class SQLManager {
        private Connection currentConnection;

        public void connect() {
            String jdbcUrl = "jdbc:mysql://localhost:3306/books";
            String user = "root";
            String password = "password";

            try {
                this.currentConnection = DriverManager.getConnection(jdbcUrl, user, password);

            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Error al intentar conectar con la base de datos.");
            }
        }

        public Connection getConnection() {
            return currentConnection;
        }

        public abstract HashMap<String, String> query(String consult, String parameter);

        public void close() {
            try {
                currentConnection.close();

            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Error al intentar cerrar la conexión con la base de datos.");
            }
        }
    }

    public static class BookManager extends SQLManager {
        @Override
        public HashMap<String, String> query(String consult, String parameter) {
            try {
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(consult);
                statement.setString(1, parameter);

                ResultSet result = statement.executeQuery();

                HashMap<String, String> hashMap = new HashMap<>();

                while(result.next()) {
                    hashMap.put(result.getString("nombre"), result.getString("informacion"));
                }

                return hashMap;

            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Error al intentar conseguir contenido de la base de datos.");

                return new HashMap<>();
            }
        }

        public static void ask(BookManager currentManager) {
            Scanner scan = new Scanner(System.in);

            System.out.print("Nombre del libro: ");
            String name = scan.next();

            HashMap<String, String> books = currentManager.query("SELECT * FROM books WHERE nombre = ?", name);

            for(String key : books.keySet()) {
                System.out.println("\nNombre: " + key + "\nInformación: " + books.get(key));
            }

            scan.close();
        }
    }

    public static void main(String[] args) {
        BookManager newManager = new BookManager();
        newManager.connect();

        try {
            BookManager.ask(newManager);

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error al intentar ejecutar el programa");

        } finally {
            newManager.close();
        }
    }
}