package co.costureria.dao;
import co.costureria.modelo.Prenda;
import java.sql.*;
import java.util.*;
public class PrendaDao {
    public List<Prenda> listar() throws SQLException {
        var lista = new ArrayList<Prenda>();
        String sql = "SELECT p.id_prenda,o.id_cliente,p.tipo,p.descripcion,p.estado,p.fecha_estimada FROM PRENDA p JOIN ORDEN o ON o.id_orden=p.id_orden ORDER BY p.id_prenda DESC";
        try (var c=Conexion.abrir(); var s=c.prepareStatement(sql); var r=s.executeQuery()) {
            while(r.next()) lista.add(new Prenda(r.getInt(1),r.getInt(2),r.getString(3),r.getString(4),r.getString(5),r.getObject(6,java.time.LocalDate.class)));
        }
        return lista;
    }
    /** Crea la orden y la prenda en una sola transacción. */
    public void insertar(Prenda p) throws SQLException {
        try(var c=Conexion.abrir()) {
            c.setAutoCommit(false);
            try(var s=c.prepareStatement("INSERT INTO ORDEN(id_cliente,fecha_recepcion) VALUES (?,NOW())",Statement.RETURN_GENERATED_KEYS)) {
                s.setInt(1,p.cliente()); s.executeUpdate();
                try(var r=s.getGeneratedKeys()) {
                    if(!r.next()) throw new SQLException("Orden sin identificador");
                    try(var t=c.prepareStatement("INSERT INTO PRENDA(id_orden,tipo,descripcion,estado,fecha_estimada,fecha_entrega) VALUES (?,?,?,?,?,IF(?='entregado',CURRENT_DATE,NULL))")) {
                        t.setInt(1,r.getInt(1)); datos(t,p,2); t.setString(6,p.estado()); t.executeUpdate();
                    }
                }
                c.commit();
            } catch(SQLException e) { c.rollback(); throw e; }
        }
    }
    private void datos(PreparedStatement s,Prenda p,int i) throws SQLException {
        s.setString(i,p.tipo()); s.setString(i+1,p.arreglo()); s.setString(i+2,p.estado()); s.setObject(i+3,p.estimada());
    }
    public void actualizar(Prenda p) throws SQLException {
        try(var c=Conexion.abrir(); var s=c.prepareStatement("UPDATE PRENDA SET tipo=?,descripcion=?,estado=?,fecha_estimada=?,fecha_entrega=IF(?='entregado',COALESCE(fecha_entrega,CURRENT_DATE),NULL) WHERE id_prenda=?")) {
            datos(s,p,1); s.setString(5,p.estado()); s.setInt(6,p.id()); s.executeUpdate();
        }
    }
    /** Conserva la orden como registro histórico de recepción. */
    public void eliminar(int id) throws SQLException {
        try(var c=Conexion.abrir(); var s=c.prepareStatement("DELETE FROM PRENDA WHERE id_prenda=?")) { s.setInt(1,id); s.executeUpdate(); }
    }
}
