/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Longpolling;

import DataBaseCredentials.DatabaseConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author pankajtiwana
 */
@WebServlet(name = "AshychronousPolling", urlPatterns = {"/AshychronousPolling"}, asyncSupported=true)
public class AshychronousPolling extends HttpServlet {
    private List<AsyncContext> contexts = new LinkedList<>();
        JsonArrayBuilder array = Json.createArrayBuilder();

Connection con=null;
PreparedStatement smt;
ResultSet rst;
String blogtext;
int a1;
String base64String;
String sendinguserdata;
int blogid;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AshychronousPolling</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AshychronousPolling at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
     
    
        
        final AsyncContext ashync=request.startAsync(request, response);
        ashync.setTimeout(3000);
        contexts.add(ashync);
       // PrintWriter out=response.getWriter();
       // out.write("adding");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
     
         HttpSession session=request.getSession();
         String username=(String)session.getAttribute("username");
        //processRequest(request, response);
        String blog=request.getParameter("blog");
           try {
            con = DatabaseConnection.getConnection();
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            
        }
           
           String query="INSERT INTO blog(blog_text,blog_date,username) VALUES(?,?,?)";
           
           try{
               
                Date d=new Date();
            Calendar calendar = Calendar.getInstance();
            java.sql.Date javaSqlDate = new java.sql.Date(calendar.getTime().getTime());
                        smt = con.prepareStatement(query);
                       
                       smt.setString(1,blog);
                       smt.setDate(2, javaSqlDate);
                       smt.setString(3, username);
                       
                       smt.executeUpdate();
                       
                    

              String selectquery="SELECT blog_id,blog_text,image  FROM blog INNER JOIN IMAGES ON blog.username=IMAGES.username ORDER BY blog_id DESC LIMIT 1";
              
              smt=con.prepareStatement(selectquery);
              //smt.setString(1, username);
              rst=smt.executeQuery();
              while(rst.next())
              {
                  blogid=rst.getInt(1);
                 blogtext=rst.getString(2);
                  InputStream stream = rst.getBinaryStream(3);
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                try {
                    a1 = stream.read();
                } catch (IOException ex) {
                    //return "exception in stream read";
                }
                while (a1 >= 0) {
                    output.write((char) a1);
                    try {
                        a1 = stream.read();
                    } catch (IOException ex) {

                       // return "exception in writing ayya";
                    }
                }
                byte[] dt = new byte[166666];
                base64String = DatatypeConverter.printBase64Binary(output.toByteArray());
   JsonObjectBuilder build = Json.createObjectBuilder().add("blog", blogtext)
                    .add("image", base64String).add("user", username).add("blogid", blogid);

            array.add(build);
            sendinguserdata = array.build().toString();
              }
           }
           catch(SQLException ex)
           {
                              PrintWriter out=response.getWriter();

               out.println(ex.getMessage());
           }
                List<AsyncContext> asyncContexts = new ArrayList<>(this.contexts);
        this.contexts.clear();
for (AsyncContext asyncContext : asyncContexts) {
            try (PrintWriter writer = asyncContext.getResponse().getWriter()) {
                writer.println(sendinguserdata);
                //writer.write(asyncContexts);
                writer.flush();
                asyncContext.complete();
                con.close();
            } catch (Exception ex) {
            }
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
