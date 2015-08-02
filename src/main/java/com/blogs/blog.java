
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogs;

import DataBaseCredentials.DatabaseConnection;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import java.io.StringReader;
import java.util.Date;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.json.stream.JsonParser;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.xml.bind.DatatypeConverter;

/**
 * REST Web Service
 *
 * @author pankajtiwana
 */
@Stateful
@Path("blog")
public class blog {

    final static Map<String, AsyncResponse> waiters = new ConcurrentHashMap<>();
    final static ExecutorService ex = Executors.newSingleThreadExecutor();
    Connection con = null;
    int count = 0;
    Statement smt;
    ResultSet rst;
    int a1;
    String imagepath;

    String base64String;
    String sendinguserdata;
    int totalnum;
    String error;
    int a2;
    String commentimg;
    String user;

    JsonArrayBuilder array = Json.createArrayBuilder();
    JsonArrayBuilder array1 = Json.createArrayBuilder();
        JsonArrayBuilder arrayerror = Json.createArrayBuilder();
                JsonArrayBuilder commentarray = Json.createArrayBuilder();
                



    /**
     * Creates a new instance of blog
     *
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public blog() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        con = DatabaseConnection.getConnection();
    }

    /**
     * Retrieves representation of an instance of com.blogs.blog
     *
     * @param request
     * @return an instance of java.lang.String
     */
    @Path("/getcurrentuserimage")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletRequest request) {

        try {

            con = DatabaseConnection.getConnection();
            //TODO return proper representation object
            HttpSession session = request.getSession();
            user = (String) session.getAttribute("username");
            String imagequery = "SELECT image from IMAGES where username='" + user + "'";

            smt = con.createStatement();
            rst = smt.executeQuery(imagequery);

            while (rst.next()) {
                count++;

                InputStream stream = rst.getBinaryStream(1);
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                try {
                    a1 = stream.read();
                } catch (IOException ex) {
                    return "exception in stream read";
                }
                while (a1 >= 0) {
                    output.write((char) a1);
                    try {
                        a1 = stream.read();
                    } catch (IOException ex) {

                        return "exception in writing ayya";
                    }
                }
                byte[] dt = new byte[166666];
                base64String = DatatypeConverter.printBase64Binary(output.toByteArray());

            }
            if (count == 0) {
                base64String = "images/icon.png";
                JsonObjectBuilder build = Json.createObjectBuilder().add("image", imagepath);

                array.add(build);
                String img = array.build().toString();
                return "did not read image from data base";

            }

            JsonObjectBuilder build = Json.createObjectBuilder().add("myimage", base64String)
                    .add("username", user);

            array.add(build);
            sendinguserdata = array.build().toString();


        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex7) {
            String msg = ex7.getMessage();

            return msg;
        }
        finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return sendinguserdata;
    }

    /**
     * PUT method for updating or creating an instance of blog
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    /**
     * PUT method for updating or creating an instance of blog
     *
     * @param str
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Path("/global")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllData(String str)  {
        int countthis=0;
   JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> mapKeyValue = new HashMap<>();
        String key = "", val;
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    val = parser.getString();
                    mapKeyValue.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = Integer.toString(parser.getInt());
                    mapKeyValue.put(key, val);
                    break;
            }
        }
        int upperlimit=Integer.parseInt(mapKeyValue.get("upper"));
        int lowerlimit=Integer.parseInt(mapKeyValue.get("lower"));

        try {
            con = DatabaseConnection.getConnection();

            String query = "SELECT blog.blog_id,blog_text,blog_date,blog.username, image FROM blog LEFT OUTER JOIN IMAGES ON blog.username=IMAGES.username LIMIT "+lowerlimit+","+upperlimit;

               // String query="SELECT count(blog.blog_id),blog.blog_id,blog_text,blog_date,blog.username, image, comment_id,comment_text,comment_date,comments.username FROM blog INNER JOIN images ON blog.username=images.username";
            smt = con.createStatement();
            rst = smt.executeQuery(query);
            while (rst.next()) {
                countthis++;
                InputStream st = rst.getBinaryStream(5);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    a1 = st.read();
                } catch (IOException ex5) {
                    Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex5);
                }
                while (a1 >= 0) {
                    output.write((char) a1);
                    try {
                        a1 = st.read();
                    } catch (IOException ex4) {

                        return "exception in writing ayya";
                    }
                }
                byte[] dt = new byte[166666];
                base64String = DatatypeConverter.printBase64Binary(output.toByteArray());
                //int totalblogs=rst.getInt(1);
                int blogid = rst.getInt(1);
                String blogtext = rst.getString(2);
                String blogdate = rst.getString(3);
                String bloguser = rst.getString(4);

                JsonObjectBuilder build1 = Json.createObjectBuilder()
                        .add("bloguserimage", base64String)
                        .add("blogid", blogid)
                        .add("blogtext", blogtext)
                        .add("blogdate", blogdate)
                        .add("bloguser", bloguser);
                        ;

                array1.add(build1);

            }
            if(countthis==0)
            {
JsonObjectBuilder builderror = Json.createObjectBuilder()
                        .add("errorcode", "nodatafound")
                       
                        ;

                arrayerror.add(builderror); 
            error = arrayerror.build().toString();
        return error;
                //return "done";
            }
          //con.close();
  
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex) {
            

//Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
              try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String alldata = array1.build().toString();
        return alldata;
    }
    
    /**
     *
     * @param blogid
     * @return
     */
    @GET
    @Path("/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllComments(@QueryParam("blogid") String blogid) {
       
        
            try {
                con = DatabaseConnection.getConnection();
                
                String query="SELECT comment_id,comment_text, comments.username, image FROM comments INNER JOIN IMAGES ON comments.username=IMAGES.username WHERE comments.blog_id="+blogid;
            smt = con.createStatement();
            rst = smt.executeQuery(query);
            while(rst.next())
            {
                InputStream st1 = rst.getBinaryStream(4);
                ByteArrayOutputStream output1 = new ByteArrayOutputStream();
                try {
                    a2 = st1.read();
                } catch (IOException ex1) {
                    Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex1);
                }
                while (a2 >= 0) {
                    output1.write((char) a1);
                    try {
                        a2 = st1.read();
                    } catch (IOException ex1) {

                        return "exception in writing ayya";
                    }
                }
                //byte[] dt = new byte[166666];
                commentimg = DatatypeConverter.printBase64Binary(output1.toByteArray());
                String Cid=rst.getString(1);
                String commenttext=rst.getString(2);
                String comntuser=rst.getString(3);
               JsonObjectBuilder build1 = Json.createObjectBuilder()
                       .add("imageuser", commentimg).add("text",commenttext).add("user",comntuser).add("Cid",Cid);
               
               
               commentarray.add(build1);
            }
            
          // con.close();
            
            } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex3) {
                Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex3);
            }
            finally{
                  try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        
      return commentarray.build().toString();
 
    }

    @POST
    @Path("/insertComment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String insertComment(@Context HttpServletRequest req,String comment){
            try {
                JsonParser parser = Json.createParser(new StringReader(comment));
                Map<String, String> mapKeyValue = new HashMap<>();
                String key = "", val;
                while (parser.hasNext()) {
                    JsonParser.Event evt = parser.next();
                    switch (evt) {
                        case KEY_NAME:
                            key = parser.getString();
                            break;
                        case VALUE_STRING:
                            val = parser.getString();
                            mapKeyValue.put(key, val);
                            break;
                        case VALUE_NUMBER:
                            val = Integer.toString(parser.getInt());
                            mapKeyValue.put(key, val);
                            break;
                    }
                }
                HttpSession session = req.getSession();
            user = (String) session.getAttribute("username");
                
                try {
                    con = DatabaseConnection.getConnection();
                } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
                }
                int id=Integer.parseInt(mapKeyValue.get("blogid"));
                String commenttext=mapKeyValue.get("text");
                Date d=new Date();
                Calendar calendar = Calendar.getInstance();
                java.sql.Date javaSqlDate = new java.sql.Date(calendar.getTime().getTime());
                String query="INSERT INTO comments(comment_text,comment_date,username,blog_id) VALUES(?,?,?,?)";
                PreparedStatement smtc=con.prepareStatement(query);
                smtc.setString(1, commenttext);
                smtc.setDate(2,javaSqlDate);
                smtc.setString(3, user);
                smtc.setInt(4, id);
                smtc.executeUpdate();
                
            } catch (SQLException ex) {
                String msg=ex.getMessage();
                return msg;
               // Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally{
                  try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(blog.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
       return "done";
    }
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

}
