
package com.camcars.web;

import com.camcars.JCEntity;
import com.camcars.JCFetchRequest;
import com.camcars.JCModel;
import com.camcars.JCCar;
import com.camcars.PSCoding;
import com.camcars.PSDict;

import java.io.IOException;
import java.util.Enumeration;
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
public class JCCarDetailsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JCCarDetailsServlet() {
        super();       
    }
    
    protected JCCar carWithID( Session session, String carID ) {
    	
    	JCFetchRequest statement;
    	List<JCEntity> carList;
    	JCCar aCar;
    	
    	session = JCEntity.getSessionFactory().openSession();
    	statement = new JCFetchRequest( session, "JCCar" );
    	statement.setPredicate( "Z_PK = " + "'" + carID + "'" );
    	carList = statement.listEntities();
    	System.out.println("carList: " + carList );
    	aCar = (JCCar) ((carList.size() > 0) ? (JCCar )carList.get(0) : null);
    	
    	return aCar;
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Session session = JCEntity.getSessionFactory().openSession();
		StringBuffer sb = new StringBuffer("<HTML><BODY>");
		
		String carID = request.getParameter( "carID" );
		JCCar currentCar = carWithID( session, carID );
		
		if ( currentCar == null ) return;
 
        sb.append("<TABLE>");
        sb.append("<TR><TH>CAR DETAILS FOR: ");
        sb.append( currentCar.getDisplayName() );
        sb.append("</TH></TR>");
        sb.append("<P></P>");
        try {
            
        	PSDict carProperties = currentCar.snapshot();
        	Iterator<String> propIter = carProperties.keySet().iterator();
        	
        	System.out.println("Snapshot: " + carProperties );
        	
        	sb.append( "<DIV TEXT-ALIGN=LEFT>" );

        	sb.append("<TR><TD><B>MODEL: </B></TD>");
        	sb.append("<TD>" + currentCar.getDisplayName() + "</TD></TR>");
        	sb.append("<TD><B>CAPACITY: </B></TD>");
        	sb.append("<TD>" + currentCar.getCapacity() + " ccm</TD></TR>");
        	sb.append("<TR><TD><B>GEARBOX: </B></TD>");
        	sb.append("<TD>" + currentCar.getGearbox() + "</TD></TR>");
        	sb.append("<TR><TD><B>DOORCOUNT: </B></TD>");
        	sb.append("<TD>" + currentCar.getDoorCount() + "</TD></TR>");
        	sb.append("<TR><TD><B>MILEAGE: </B></TD>");
        	sb.append("<TD>" + currentCar.getMileage() + " Km</TD></TR>");
        	sb.append("<TR><TD><B>PRICE: </B></TD>");
        	sb.append("<TD>" + currentCar.getPrice() + " Euros</TD></TR>");

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