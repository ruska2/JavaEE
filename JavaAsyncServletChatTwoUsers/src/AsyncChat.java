

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


class WaitForMessage implements AsyncListener 
{
    AsyncContext ac;
    
    public WaitForMessage(AsyncContext ac)
    {
        this.ac = ac;
    }    
        
    public void newMessage(String msg)
    {
        try {
            PrintWriter out = ac.getResponse().getWriter();
            out.println("<msgs>");
            out.println("<line>" + msg + "</line>");
            out.println("</msgs>");
        } catch (Exception e) {}
        ac.complete();
    }

    private void removeMeFromQueue()
    {
    	HashMap<String, WaitForMessage> waiting = AsyncChat.names;
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
        newMessage("");
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

@WebServlet(name = "AsyncChat", urlPatterns = {"/asyncchat"}, asyncSupported = true)
public class AsyncChat extends HttpServlet {
	
	 //public static ArrayList<WaitForMessage> waiting = new ArrayList<WaitForMessage>();
	 public static HashMap<String, WaitForMessage> names = new HashMap<>();
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
        AsyncContext ac = request.startAsync();

        WaitForMessage waiter = new WaitForMessage(ac);
        ac.addListener(waiter);
        String all = request.getQueryString();
        String meno = all.split("=")[1];
        names.put(meno, waiter);
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
