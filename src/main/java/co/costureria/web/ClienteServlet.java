package co.costureria.web;
import co.costureria.dao.ClienteDao;
import co.costureria.modelo.Cliente;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {
    private final ClienteDao dao = new ClienteDao();
    private static String escapar(String s) { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); }
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        try {
            var clientes = dao.listar();
            PrintWriter out = res.getWriter();
            out.println("<!doctype html><html lang='es'><meta charset='utf-8'><title>Clientes | Nata Sam</title><link rel='stylesheet' href='estilos.css'><main><h1>La Costurería Nata Sam</h1><h2>Gestión de clientes</h2><form method='post'><input type='hidden' name='accion' value='insertar'><label>Nombre <input name='nombre' required maxlength='100'></label><label>Teléfono <input name='telefono' required maxlength='20'></label><label>Correo <input name='correo' type='email' required maxlength='120'></label><button>Registrar cliente</button></form><h2>Clientes registrados</h2>");
            for (Cliente c : clientes) {
                out.printf("<section><form method='post'><input type='hidden' name='id' value='%d'><label>Nombre <input name='nombre' required maxlength='100' value='%s'></label><label>Teléfono <input name='telefono' required maxlength='20' value='%s'></label><label>Correo <input name='correo' type='email' required maxlength='120' value='%s'></label><button name='accion' value='actualizar'>Actualizar</button><button name='accion' value='eliminar' formnovalidate onclick=\"return confirm('¿Eliminar cliente?')\">Eliminar</button></form></section>",c.id(),escapar(c.nombre()),escapar(c.telefono()),escapar(c.correo()));
            }
            out.println("</main></html>");
        } catch (SQLException e) { log("Error de consulta",e); res.sendError(500,"No se pudo consultar la base de datos."); }
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String accion = req.getParameter("accion");
            if ("eliminar".equals(accion)) dao.eliminar(Integer.parseInt(req.getParameter("id")));
            else if ("insertar".equals(accion) || "actualizar".equals(accion)) {
                String nombre = campo(req,"nombre",100), telefono = campo(req,"telefono",20), correo = campo(req,"correo",120);
                if (!correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw new IllegalArgumentException();
                boolean editar = "actualizar".equals(accion);
                Cliente c = new Cliente(editar ? Integer.parseInt(req.getParameter("id")) : 0,nombre,telefono,correo);
                if (editar) dao.actualizar(c); else dao.insertar(c);
            } else throw new IllegalArgumentException();
            res.sendRedirect(req.getContextPath()+"/clientes");
        } catch (IllegalArgumentException e) { res.sendError(400,"Datos inválidos."); }
        catch (SQLException e) { log("Error de escritura",e); res.sendError(409,"No se pudo guardar o eliminar. Verifique la conexión y las órdenes asociadas."); }
    }
    private static String campo(HttpServletRequest req,String nombre,int max) {
        String valor = req.getParameter(nombre);
        if (valor == null || valor.isBlank() || valor.trim().length()>max) throw new IllegalArgumentException();
        return valor.trim();
    }
}
