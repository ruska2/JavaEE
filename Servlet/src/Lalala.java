/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author petrovic16
 */
@WebServlet(name = "Lalala", urlPatterns = {"/Lalala"})
public class Lalala extends HttpServlet {
	
	HashMap<String,Long> times = new HashMap<>();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            ServletContext ctx = request.getServletContext();
            String zoznamLudi;
          
            String meno = request.getParameter("meno");
		    
            zoznamLudi = (String)ctx.getAttribute("zoznam");
                if (zoznamLudi == null)
                    zoznamLudi = meno;
                else
                	if(!zoznamLudi.contains(meno))
                    zoznamLudi += ", " + meno;
                ctx.setAttribute("zoznam", zoznamLudi);
                
            times.put(meno, System.currentTimeMillis());
                
            for(Entry<String, Long> c: times.entrySet()) {
            	if(System.currentTimeMillis() - c.getValue() > 15000){
            			zoznamLudi = zoznamLudi.replace(", " + c.getKey(),"");
            			zoznamLudi = zoznamLudi.replace(c.getKey() + ",", "");
            			ctx.setAttribute("zoznam", zoznamLudi);
    			}
    		}
            
            ArrayList<String> spravy = (ArrayList<String>)ctx.getAttribute("spravy");
            if (spravy == null)
            {
                spravy = new ArrayList<String>();
                ctx.setAttribute("spravy", spravy);      
            }
            
            if (request.getParameter("logout") != null)
            {
                 zoznamLudi = zoznamLudi.replace(meno+",", "");
                 zoznamLudi = zoznamLudi.replace(", "+meno, "");
                 ctx.setAttribute("zoznam", zoznamLudi);
         		 spravy.add(meno + ": " + "odhlasil sa.");
         		 
         		out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Login</title>"); 
                out.println("</head>");
                out.println("<body>");
                
                out.println("<div>Zadaj svoje meno:</div>");
                out.println("<form action=\"Lalala\" method=\"POST\"> <input type=\"text\" name=\"meno\" size=\"30\"><input type=\"submit\" name=\"login\" value=\"login\"></form>");
                out.println("</form></body>");
                out.println("</html>");
                return;
                
            }
            
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            
            out.println("<title>Rozhovor</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Rozhovor</h1>");
            out.println("Volas sa " + meno);
            out.println("<br>Zoznam: " + zoznamLudi);
            
            
            String sprava = request.getParameter("sprava");
            if (sprava == null)
            {
                sprava = "prihlasil sa.";
            }
            if (request.getParameter("vymaz") != null)
            {
                spravy.clear();
            }
            else if (request.getParameter("aktualizuj") != null)
            {
                // Lalala
            }
            else if(!sprava.equals("")) spravy.add(meno + ": " + sprava);
            
            out.println("<hr>");
            for (String s: spravy)
            {
                out.println(s);
                out.println("<br>");
            }
            out.println("<hr>");
            out.println("Napis spravu:<br>");
            out.println("<form action=\"Lalala\" method=\"POST\">");
            out.println("<input type=\"hidden\" name=\"meno\" value=\""+ meno +"\">");
            out.println("<input type=\"text\" name=\"sprava\" size=\"100\">");
            out.println("<input type=\"submit\" name=\"odosli\" value=\"Odosli\">");
            out.println("<input type=\"submit\" name=\"vymaz\" value=\"Vymaz\">");
            out.println("<input type=\"submit\" name=\"aktualizuj\" value=\"Aktualizuj\">");
            out.println("<input type=\"submit\" name=\"logout\" value=\"Logout\">");
            
            out.println("</form></body>");
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
        processRequest(request, response);
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
