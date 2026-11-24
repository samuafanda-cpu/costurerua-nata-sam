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
            out.println("<!doctype html><html lang='es'><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'><title>Clientes | Nata Sam</title><link rel='stylesheet' href='estilos.css'><script src='app.js' defer></script></head><body><aside><div class='brand'>NS <span>Nata Sam<small>ATELIER · GESTIÓN</small></span></div><nav><a href='./'>Inicio</a><a class='active' href='clientes' aria-current='page'>Clientes</a><a href='prendas'>Prendas</a></nav><p class='side-note'>Módulo de gestión de clientes</p></aside><main><header><span class='eyebrow'>ADMINISTRACIÓN / CLIENTES</span><h1>Clientes</h1><p>Organiza los contactos de tu costurería en un solo lugar.</p></header>");
            out.printf("<div class='metric'><span>Clientes registrados</span><strong>%d</strong></div>", clientes.size());
            out.println("<section class='panel'><h2>Nuevo cliente</h2><p>Completa los datos para registrar un contacto.</p><form class='register' method='post'><input type='hidden' name='accion' value='insertar'><label>Nombre<input name='nombre' required maxlength='100' placeholder='Nombre completo'></label><label>Teléfono<input name='telefono' required maxlength='20' placeholder='Número de contacto'></label><label>Correo<input name='correo' type='email' required maxlength='120' placeholder='correo@ejemplo.com'></label><button>Registrar cliente</button></form></section><section class='panel'><div class='panel-head'><h2>Directorio de clientes</h2><label>Buscar clientes<input id='buscar' type='search' placeholder='Nombre, teléfono o correo'></label></div><p id='resultado' aria-live='polite'></p><div class='table-wrap'><table><thead><tr><th>ID</th><th>Nombre</th><th>Teléfono</th><th>Correo</th><th>Acciones</th></tr></thead><tbody>");
            for (Cliente c : clientes) {
                out.printf("<tr class='client-row'><td>#%d<form id='cliente-%d' method='post'><input type='hidden' name='id' value='%d'></form></td><td><input aria-label='Nombre' form='cliente-%d' name='nombre' required maxlength='100' value='%s'></td><td><input aria-label='Teléfono' form='cliente-%d' name='telefono' required maxlength='20' value='%s'></td><td><input aria-label='Correo' form='cliente-%d' name='correo' type='email' required maxlength='120' value='%s'></td><td class='actions'><button form='cliente-%d' name='accion' value='actualizar' class='secondary'>Guardar</button><button form='cliente-%d' name='accion' value='eliminar' formnovalidate class='danger'>Eliminar</button></td></tr>",c.id(),c.id(),c.id(),c.id(),escapar(c.nombre()),c.id(),escapar(c.telefono()),c.id(),escapar(c.correo()),c.id(),c.id());
            }
            out.println("</tbody></table></div></section><footer>La Costurería Nata Sam · Gestión de clientes</footer></main></body></html>");
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
