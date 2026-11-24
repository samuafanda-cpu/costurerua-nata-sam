package co.costureria.dao;
import co.costureria.modelo.Cliente;
import java.sql.*;
import java.util.*;
public class ClienteDao {
    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection c = Conexion.abrir(); PreparedStatement p = c.prepareStatement("SELECT id_cliente,nombre,telefono,correo FROM CLIENTE ORDER BY id_cliente"); ResultSet r = p.executeQuery()) {
            while (r.next()) clientes.add(new Cliente(r.getInt(1),r.getString(2),r.getString(3),r.getString(4)));
        }
        return clientes;
    }
    public void insertar(Cliente cliente) throws SQLException { guardar(cliente, false); }
    public void actualizar(Cliente cliente) throws SQLException { guardar(cliente, true); }
    private void guardar(Cliente cliente, boolean actualizar) throws SQLException {
        String sql = actualizar ? "UPDATE CLIENTE SET nombre=?,telefono=?,correo=? WHERE id_cliente=?" : "INSERT INTO CLIENTE(nombre,telefono,correo) VALUES(?,?,?)";
        try (Connection c = Conexion.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1,cliente.nombre()); p.setString(2,cliente.telefono()); p.setString(3,cliente.correo());
            if (actualizar) p.setInt(4,cliente.id());
            p.executeUpdate();
        }
    }
    public void eliminar(int id) throws SQLException {
        try (Connection c = Conexion.abrir(); PreparedStatement p = c.prepareStatement("DELETE FROM CLIENTE WHERE id_cliente=?")) { p.setInt(1,id); p.executeUpdate(); }
    }
}
