/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import DataBaseCredentials.DatabaseConnection;
import credentials.UserCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author pankajtiwana
 */
@WebServlet(urlPatterns = {"/imageupload"})
@MultipartConfig(maxFileSize = 16177215) 
public class imageupload extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    int count=0;
    Connection con = null;
    Statement smt;
    ResultSet result;
    DatabaseConnection connection = new DatabaseConnection();

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
        //response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            con = DatabaseConnection.getConnection();
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
           out.print("dfbfgnn"); 
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
        processRequest(request, response);
   HttpSession session=request.getSession();
        processRequest(request, response);

        PrintWriter out = response.getWriter();
        String username = request.getParameter("name");
        String password = request.getParameter("pass");
        //out.print(username);
InputStream inputStream = null; // input stream of the upload file
         
        // obtains the upload file part in this multipart request
        Part filePart = request.getPart("image");
        if (filePart != null) {
        try {
            // prints out some information for debugging
           
             
            // obtains input stream of the upload file
            inputStream = filePart.getInputStream();
            try {
                smt = con.createStatement();
                
            } catch (SQLException ex) {
                out.print("not in this");
                Logger.getLogger(UserCredentials.class.getName()).log(Level.SEVERE, null, ex);
            }
            String checkdup="Select username FROM USERS where username='"+username+"'";
            
            ResultSet rst=smt.executeQuery(checkdup);
            if(rst.next())
            {
                out.write("ARY");
                
            }
            else{
            String sql = "INSERT INTO IMAGES(image, tag, username,date_uploaded) values (?, ?, ?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setBlob(1, inputStream);
            statement.setString(2, "Profile");
            statement.setString(3, username);
            Date d=new Date();
            Calendar calendar = Calendar.getInstance();
            java.sql.Date javaSqlDate = new java.sql.Date(calendar.getTime().getTime());
            statement.setDate(4,javaSqlDate);
            
            
            // sends the statement to the database server
            statement.executeUpdate();
            String usertab="INSERT INTO USERS VALUES(?,?)";
            PreparedStatement usstmt = con.prepareStatement(usertab);
            usstmt.setString(1, username);
            usstmt.setString(2, password);
            usstmt.executeUpdate();

            out.write("done");
            }
        } catch (SQLException ex) {
            String rs=ex.getMessage();
            out.printf(rs);
            Logger.getLogger(imageupload.class.getName()).log(Level.SEVERE, null, ex);
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