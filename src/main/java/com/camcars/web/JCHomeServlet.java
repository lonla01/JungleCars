package com.camcars.web;

import com.camcars.JCEntity;
import com.camcars.JCFetchRequest;
import com.camcars.JCModel;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.HibernateException;

/**
 * Servlet implementation class JCHomeServlet
 */
public class JCHomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JCHomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Session session = JCEntity.getSessionFactory().openSession();
		StringBuffer sb = new StringBuffer("<HTML><BODY>");
		
        sb.append("<TABLE>");
        sb.append("<TR><TH>CHOOSE A CAR MODEL:</TH></TR>");
        try {
            
        	JCFetchRequest statement = new JCFetchRequest( session, "JCModel" );
        	List<JCEntity> modelList = statement.listEntities();
        	Iterator<JCEntity> modelIter = modelList.iterator();
        	
        	sb.append( "<DIV TEXT-ALIGN=CENTER>" );
            while ( modelIter.hasNext() ){    
            	JCModel eachModel = (JCModel )modelIter.next();
                sb.append("<TR><TD>");
                sb.append("<A HREF=\"carList.html?model=" + eachModel.getName() + "\">");
                sb.append( eachModel.getDisplayName() );
                sb.append("</A>");
                sb.append("</TD></TR>");
            }
            sb.append( "</DIV>" );
            
            sb.append("</TABLE>");
            sb.append("</BODY></HTML>");
            response.getOutputStream().write( sb.toString().getBytes() );            
            
        } catch (HibernateException e) {
            System.err.println("Error connecting to database server");
            e.printStackTrace( System.err );
        } catch (Exception e) {
            System.err.println("Error processing the HTTP Request");
            e.printStackTrace( System.err );
        } finally {
			if (session != null) session.close();
		}
  
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
