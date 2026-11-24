package co.costureria.web;
import co.costureria.dao.*;
import co.costureria.modelo.Prenda;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
@WebServlet("/prendas")
public class PrendaServlet extends HttpServlet {
    private final PrendaDao dao = new PrendaDao();
    private static final List<String> TIPOS=List.of("Pantalón","Camisa","Vestido","Chaqueta","Falda","Otro");
    private static final List<String> ARREGLOS=List.of("Dobladillo","Ajuste","Cambio de cremallera","Reparación","Otro");
    private static final List<String> ESTADOS=List.of("recibido","en proceso","listo","entregado");
    private static String esc(String s) { return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("'","&#39;").replace("\"","&quot;"); }
    private static String select(String nombre,List<String> opciones,String actual) {
        var b=new StringBuilder("<select class='form-select' name='"+nombre+"' required>");
        for(String v:opciones) b.append("<option"+(v.equals(actual)?" selected":"")+">"+esc(v)+"</option>");
        return b.append("</select>").toString();
    }
    private static String campos(Prenda p) {
        return "<label>Tipo de prenda"+select("tipo",TIPOS,p.tipo())+"</label><label>Arreglo"+select("arreglo",ARREGLOS,p.arreglo())+"</label><label>Estado"+select("estado",ESTADOS,p.estado())+"</label><label>Entrega estimada<input class='form-control' type='date' name='fecha' value='"+(p.estimada()==null?"":p.estimada())+"'></label>";
    }
    /** Presenta el formulario y las prendas registradas. */
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        try {
            var clientes=new ClienteDao().listar(); var prendas=dao.listar(); var out=res.getWriter();
            out.println("<!doctype html><html lang='es'><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'><title>Prendas | Nata Sam</title><link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css'><link rel='stylesheet' href='estilos.css'></head><body><aside><div class='brand'>NS <span>Nata Sam</span></div><nav><a href='./'>Inicio</a><a href='clientes'>Clientes</a><a class='active' aria-current='page' href='prendas'>Prendas</a></nav></aside><main><h1>Gestión de prendas y arreglos</h1><section class='panel'><h2>Nueva prenda</h2><form method='post' class='d-grid gap-3'><label>Cliente<select class='form-select' name='cliente' required><option value=''>Seleccionar cliente</option>");
            for(var c:clientes) out.printf("<option value='%d'>%s</option>",c.id(),esc(c.nombre()));
            out.println("</select></label>"+campos(new Prenda(0,0,"","","recibido",null))+"<button class='btn btn-dark' name='accion' value='insertar'>Registrar prenda</button></form></section><section class='panel'><h2>Prendas registradas</h2>");
            for(var p:prendas) {
                String nombre=clientes.stream().filter(c->c.id()==p.cliente()).map(c->c.nombre()).findFirst().orElse("Cliente #"+p.cliente());
                out.println("<form method='post' class='border rounded p-3 mb-3 d-grid gap-2'><h3>Prenda #"+p.id()+" — "+esc(nombre)+"</h3><input type='hidden' name='id' value='"+p.id()+"'>"+campos(p)+"<div class='d-flex gap-2'><button class='btn btn-dark' name='accion' value='actualizar'>Guardar</button><button class='btn btn-outline-danger' name='accion' value='eliminar' formnovalidate>Eliminar</button></div></form>");
            }
            out.println("</section></main></body></html>");
        } catch(SQLException e) { log("Consulta de prendas",e); res.sendError(500,"No se pudo consultar la base de datos."); }
    }
    /** Valida las categorías en el servidor antes de guardar. */
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String a=req.getParameter("accion");
            if("eliminar".equals(a)) dao.eliminar(Integer.parseInt(req.getParameter("id")));
            else if("insertar".equals(a)||"actualizar".equals(a)) {
                boolean nuevo="insertar".equals(a);
                String tipo=req.getParameter("tipo"), arreglo=req.getParameter("arreglo"), estado=req.getParameter("estado"), fecha=req.getParameter("fecha");
                if(tipo==null||arreglo==null||estado==null||!TIPOS.contains(tipo)||!ARREGLOS.contains(arreglo)||!ESTADOS.contains(estado)) throw new IllegalArgumentException();
                var p=new Prenda(nuevo?0:Integer.parseInt(req.getParameter("id")),nuevo?Integer.parseInt(req.getParameter("cliente")):0,tipo,arreglo,estado,fecha==null||fecha.isBlank()?null:LocalDate.parse(fecha));
                if(nuevo) dao.insertar(p); else dao.actualizar(p);
            } else throw new IllegalArgumentException();
            res.sendRedirect(req.getContextPath()+"/prendas");
        } catch(IllegalArgumentException|java.time.DateTimeException e) { res.sendError(400,"Datos inválidos."); }
        catch(SQLException e) { log("Escritura de prendas",e); res.sendError(409,"No se pudo guardar o eliminar la prenda."); }
    }
}
