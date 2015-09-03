package com.camcars.web;

import com.camcars.JCEntity;
import com.camcars.JCFetchRequest;
import com.camcars.JCModel;
import com.camcars.JCCar;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.HibernateException;

/**
 * Servlet implementation class JCCarListServlet
 */
public class JCCarListServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JCCarListServlet() {
        super();       
    }
    
    protected JCModel modelNamed( Session session, String modelName ) {
    	
    	JCFetchRequest statement;
    	List<JCEntity> modelList;
    	JCModel aModel;
    	
    	session = JCEntity.getSessionFactory().openSession();
    	statement = new JCFetchRequest( session, "JCModel" );
    	statement.setPredicate( "zname = " + "'" + modelName + "'" );
    	modelList = statement.listEntities();
    	System.out.println("modelList: " + modelList );
    	aModel = (JCModel) ((modelList.size() > 0) ? (JCModel )modelList.get(0) : null);
    	
    	return aModel;
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Session session = JCEntity.getSessionFactory().openSession();
		StringBuffer sb = new StringBuffer("<HTML><BODY>");
		
		String modelName = request.getParameter( "model" );
		JCModel currentModel = modelNamed( session, modelName );
		
		if ( currentModel == null ) return;
 
        sb.append("<TABLE>");
        sb.append("<TR><TH>CURRENTLY AVAILABLE CARS FOR:");
        sb.append( currentModel.getDisplayName() );
        sb.append("</TH></TR>");
        try {
            
        	List<JCCar> carList = currentModel.getCars_();
        	Iterator<JCCar> modelIter = carList.iterator();
        	
        	sb.append( "<DIV TEXT-ALIGN=CENTER>" );
            while ( modelIter.hasNext() ){    
            	JCCar eachCar = (JCCar )modelIter.next();
                sb.append("<TR><TD>");
//                sb.append("<A HREF=\"carList.html?model=" + eachModel.getName() + "\">");
                sb.append("<A HREF=\"carDetails.html?carID=" + eachCar.getPKey().toString() + "\">");
                sb.append( eachCar.getDisplayName() );
                sb.append("</A>");
                sb.append("</TD></TR>");
            }
            sb.append( "</DIV>" );
            
            sb.append("</TABLE>");
            sb.append("</BODY></HTML>");
            response.getOutputStream().write(sb.toString().getBytes()); 
            
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