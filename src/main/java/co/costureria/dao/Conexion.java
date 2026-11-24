package co.costureria.dao;
import java.sql.*;
public final class Conexion {
    private Conexion() {}
    public static Connection abrir() throws SQLException {
        String usuario = System.getenv("DB_USER");
        String clave = System.getenv("DB_PASSWORD");
        if (usuario == null || clave == null) throw new SQLException("Configure DB_USER y DB_PASSWORD");
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException e) { throw new SQLException(e); }
        return DriverManager.getConnection(System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/costureria"), usuario, clave);
    }
}
