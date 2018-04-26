

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
class WaitForPost implements AsyncListener 
{
    AsyncContext ac;
    HttpServletRequest request;
    String name;
    
    
    public WaitForPost(AsyncContext ac, HttpServletRequest request)
    {
        this.ac = ac;
        this.request = request;
    }    
        
    public void newMessage()
    {
        try {
        	String msg = request.getParameter("msg");
        	String meno = request.getParameter("meno");
        	
        	for (Entry<String, WaitForMessage> entry : AsyncChat.names.entrySet())
        	{
        		if(!meno.equals(entry.getKey())) {
        			entry.getValue().newMessage(meno +": "+ msg);
        		}
        	}
        } catch (Exception e) {}
        ac.complete();
    }

    private void removeMeFromQueue()
    {
        ArrayList<WaitForPost> waiting = ChatServlet.posts;
        synchronized(waiting)
        {
            waiting.remove(this);
        }
    }    
    
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        removeMeFromQueue();
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        removeMeFromQueue();
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

@WebServlet(name = "ChatServlet", urlPatterns = {"/chatservlet"}, asyncSupported = true)
public class ChatServlet extends HttpServlet {
    
	public static ArrayList<WaitForPost> posts = new ArrayList<>();

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
    	 response.setContentType("text/html;charset=UTF-8");
    	 
         try (PrintWriter out = response.getWriter()) {
        	 out.println("<!DOCTYPE html>");
             out.println("<html>");
             out.println("<head>");
             out.println("<title>chat servlet</title>"); 
             out.println("<meta charset=\"UTF-8\">");
             out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
             out.println("<script type='text/javascript' src='chat.js?ver=\" + version + \"'></script>");
             out.println("<script src=\"http://code.jquery.com/jquery-1.10.2.js\"></script>");
             out.println("</head>");
             out.println("<body onload='startRequest()'>");
             out.println("<h3>"+ request.getParameter("meno") +"</h3>");
             out.println("<hr>");
             out.println("Message: <br> <input type='text' name='msg' onkeydown=\"sendPost()\" />");
             out.println("<input type=\"hidden\" name=\"meno\" value=\""+ request.getParameter("meno") +"\">");
             out.println("<div id = \"msgs\"></div>");
             out.println("</body>");
             out.println("</html>");
         }catch(Exception e) {
         
         }
       
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
    	AsyncContext ac = request.startAsync();

        WaitForPost waiter = new WaitForPost(ac,request);
        ac.addListener(waiter);
        posts.add(waiter);
   
                
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        
        waiter.newMessage();
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
