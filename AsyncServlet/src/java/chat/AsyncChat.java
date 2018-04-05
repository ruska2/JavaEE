/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        
    public void newMessage()
    {
        ArrayList<String> msgs = (ArrayList<String>) ac.getRequest().getServletContext().getAttribute("msgs");
        try {
            PrintWriter out = ac.getResponse().getWriter();
            out.println("<msgs>");
            for (String m: msgs)       
                out.println("<line>" + m + "</line>");
            out.println("</msgs>");
        } catch (Exception e) {}
        ac.complete();
    }

    private void removeMeFromQueue()
    {
        ArrayList<WaitForMessage> waiting = (ArrayList<WaitForMessage>) ac.getRequest().getServletContext().getAttribute("waiting");
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
        newMessage();
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
        ArrayList<WaitForMessage> waiting = (ArrayList<WaitForMessage>) request.getServletContext().getAttribute("waiting");
        
        if (waiting == null)
        {
            waiting = new ArrayList<WaitForMessage>();
            request.getServletContext().setAttribute("waiting", waiting);
        }
        WaitForMessage waiter = new WaitForMessage(ac);
        ac.addListener(waiter);
        waiting.add(waiter);
                
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
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
