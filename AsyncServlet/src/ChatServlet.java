

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author petrovic
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/chatservlet"})
public class ChatServlet extends HttpServlet {

    private enum OperationType { LOGIN, SEND, CLEAR } 
    
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
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Chat Servlet</title>");            
            //prevent javascript to be cached
            long version = java.util.Calendar.getInstance().getTimeInMillis();
            out.println("<script type='text/javascript' src='chat.js?ver=" + version + "'></script>");
            out.println("</head>");
            out.println("<body onload='startRequest()'>");
            out.println("<h1>Chat Servlet at " + request.getContextPath() + "</h1>");
            
            String nickname = (String) request.getSession().getAttribute("nickname");            
            ArrayList<String> msgs = (ArrayList<String>) request.getServletContext().getAttribute("msgs");
            if (msgs == null) 
            {
                msgs = new ArrayList<String>();
                request.getServletContext().setAttribute("msgs", msgs);
            }
            String submit = request.getParameter("submit");
            OperationType operation = OperationType.LOGIN;
            
            if (submit.equals("login")) operation = OperationType.LOGIN;
            else if (submit.equals("send")) operation = OperationType.SEND;
            else if (submit.equals("clear")) operation = OperationType.CLEAR;
            
            switch (operation)
            {              
                case LOGIN:
                    nickname = request.getParameter("nickname");            
                    out.println("Welcome " + nickname + "!<br /><br />");
                    request.getSession().setAttribute("nickname", nickname);
                    msgs.add("// " + nickname + " arrived");
                    break;
                case SEND:
                    msgs.add(nickname + ": " + request.getParameter("msg"));
                    break;
                case CLEAR:
                    msgs.clear();
                    break;
            }

            ArrayList<WaitForMessage> waiting = (ArrayList<WaitForMessage>) request.getServletContext().getAttribute("waiting");                    
            if (waiting != null)  
            {                
                ArrayList<WaitForMessage> copy = new ArrayList<WaitForMessage>(waiting);                
                for (WaitForMessage ac: copy)
                    ac.newMessage();                
            }
            
            out.println("<div id='msgs'>");
            
            for (String m: msgs)
                out.println(m + "<br />");
            
            out.println("</div><br /><br />");
           
            
            out.println("<form action='chatservlet' method='POST'>");
            out.println("<input type='text' name='msg' />");
            out.println("<input type='submit' name='submit' value='send' />");
            out.println("<input type='submit' name='submit' value='clear' />");
            out.println("</form>");
        }
        catch (Exception e)
        {
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        finally 
        {
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
