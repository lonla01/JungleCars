package com.camcars.web;

import com.camcars.core.PSObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

//@Controller
//import javax.faces.bean.RequestScoped;

@ManagedBean
@ViewScoped
public class CarFinderBean extends PSObject implements Serializable {
	
	private static final long serialVersionUID = -7888415453276475667L;
	//private static Log _Logger = LogFactory.getLog(CarFinderBean.class);
	private String _selectedCarType = "Off-road Vehicle/Pickup Truck/Used Vehicle";
//    private String _selectedCarType = "EstateCar/Used Vehicle";
	private Session _session = null;
	
//	public Log logger() {
//		return _Logger;
//	}
	
	public CarFinderBean() {
		try {
			_session = JCEntity.getSessionFactory().openSession();
		} catch (HibernateException e) {
			logger().error( "Error connecting to database server", e);
			e.printStackTrace();
		}
	}
	
	public Session getSession() {
		return _session;
	}
	
	public void finalize() {
		if ( _session != null) _session.close();
	}
	
	public List<JCEntity> getMakeList() {

		List<JCEntity> carMakeList = new ArrayList<JCEntity>();
		Session session = getSession();
		JCFetchRequest statement;

		try {

			statement = new JCFetchRequest( session, "JCMake" );
			carMakeList = statement.listEntities();   
			logger().info( "Entering getMakeList: modelObject = " + carMakeList );

		} catch (Exception e) {
			logger().error("Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return carMakeList;

	}
	
	public List<JCEntity>getModelList() {

		List<JCEntity> carModelList = new ArrayList<JCEntity>();
		Session session = null;
		JCFetchRequest statement;

		try {

			session = getSession();
			statement = new JCFetchRequest( session, "JCModel" );
			carModelList = getEntityList( statement );   
			//logger().info( "Entering homePage: getModelList = " + carModelList );

		} catch (Exception e) {
			logger().error("Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return carModelList;

	}
	
	public String getSelectedCarType() { return _selectedCarType; }
	
	public void setSelectedCarType( String selectedCarType ) {
		logger().info( "setSelectedCarType() = [" + selectedCarType + "]");
		_selectedCarType = selectedCarType;
	}
	
	public Set<JCCar> getSelectedCars() {
		logger().info( "getSelectedCars() for " + getSelectedCarType() );
		return getCarsForType( getSelectedCarType() );
	}
	
	public Set<JCCar> getSUVs() {
		return getCarsForType( "Off-road Vehicle/Pickup Truck/Used Vehicle" );
	}
	
	public Set<JCCar> getSaloons() {
		return getCarsForType( "Saloon/Used Vehicle" );
	}
	
	public Set<JCCar> getCoupes() {
		return getCarsForType( "SportsCar/Coupe/Used Vehicle" );
	}
	
	public Set<JCCar> getEstates() {
		return getCarsForType( "EstateCar/Used Vehicle" );
	}
	
	public Set<JCCar> getSmallCars() {
		return getCarsForType( "SmallCar/Used Vehicle" );
	}
	
	public Set<JCCar> getVans() {
		return getCarsForType( "Van/Minibus/Used Vehicle" );
	}
	
	public Set<JCCar> getCarsForType( String carType ) {

		List<JCEntity> carModelList = new ArrayList<JCEntity>();
		Set<JCCar> carList = new HashSet<JCCar>();
		Set<JCCar> filteredCarList = new HashSet<JCCar>();
		Session session = null;
		JCFetchRequest statement;
		
		try {

			session = getSession();
			statement = new JCFetchRequest( session, "JCModel" );
			statement.setPredicate( " ZCARTYPE = '" + carType + "'");
			statement.setFirstResult( new Integer(0) );
			statement.setMaxResults( new Integer(30) );
			carModelList = getEntityList( statement );  
			
			// For all the models we retrieved, collect all the cars			
			carModelList.forEach(
                    (aModel) -> carList.addAll( ((JCModel )aModel).getCars() )
            );
			
			// Filtered out all cars with no valid thumbnail
			carList.forEach( (aCar) -> {
                if ( aCar != null && aCar.hasThumbnail() ) {
                    filteredCarList.add(aCar);
                }
            } );
			
			//logger().info( "Entering homePage: getSUVs = " + carList );

		} catch (Exception e) {
			logger().error("Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return filteredCarList;

	}
	
	public List<JCEntity> getModelListForCarType( String carType ) {

		List<JCEntity> carModelList = new ArrayList<JCEntity>();
		Session session = null;
		JCFetchRequest statement;

		try {

			session = getSession();
			statement = new JCFetchRequest( session, "JCCar" );
			//statement.setPredicate( "WHERE ZCARTYPE = " + carType );
			statement.setFirstResult( new Integer(0) );
			statement.setMaxResults( new Integer(60) );
			carModelList = getEntityList( statement );   
			//logger().info( "Entering homePage: getModelList = " + carModelList );

		} catch (Exception e) {
			logger().error( "Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		}

		return carModelList;

	}
	
	public List<Object> getAllCarTypes() {
		
		List<Object> carTypes = new ArrayList<Object>();
		Session session = null;
		JCSQLFetchRequest statement;
		String sql = "SELECT DISTINCT ZCARTYPE FROM ZJCMODEL";
		
		try {

			session = getSession();
			statement = new JCSQLFetchRequest( session, "JCModel", sql );
			carTypes = (List<Object> )statement.listJDBCResults();   
			//logger().info( "getAllCarTypes() carTypes = " + carTypes );

		} catch (Exception e) {
			logger().error( "Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return carTypes;
	}
	
	public List<JCEntity> getEntityList( JCFetchRequest statement ) {

		List<JCEntity> carModelList = new ArrayList<JCEntity>();

		try {
			
			carModelList = statement.listEntities();   

		} catch (Exception e) {
			logger().error( "Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return carModelList;

	}
	
	public JCCar getCarById( String carID ) {

		Session session = null;
		JCCar aCar = null;
		JCFetchRequest statement;
		List<JCEntity> carList;
		
		try {
			
			session = getSession();
			statement = new JCFetchRequest( session, "JCCar" );
			statement.setPredicate( "Z_PK = " + "'" + carID + "'" );
			carList = statement.listEntities();
			//logger().info( "Entering carWithID: carList = " + carList );
			aCar = (JCCar) ((carList.size() > 0) ? (JCCar )carList.get(0) : null);
			
		} catch (Exception e) {
			logger().error( "Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return aCar;

	}
	
	public JCEntity getEntityByName( String entityName, String nameValue ) {

		Session session = null;
		JCEntity enEntity = null;
		JCFetchRequest statement;
		List<JCEntity> entityList;
		
		try {
			
			session = getSession();
			statement = new JCFetchRequest( session, entityName );
			statement.setPredicate( "ZNAME = " + "'" + nameValue + "'" );
			entityList = statement.listEntities();
			logger().info( "Entering carWithID: entityList = " + entityList );
			enEntity = (JCEntity ) ( (entityList.size() > 0) ? (JCEntity )entityList.get(0) : null );
			
		} catch (Exception e) {
			logger().error( "Error processing the HTTP Request", e );
			e.printStackTrace( System.err );
		} 

		return enEntity;

	}

	//@RequestMapping(value = "/hello.html", method = RequestMethod.GET )                              
	public ModelAndView helloPage() {        
		
		Map<String, Object> model = new HashMap<String, Object>();    
		
		model.put("message", "Welcome to Jungle Cars For Africa !");    
		logger().info( "Entering helloPage: model = " + model );
		
		return new ModelAndView("hello", model);                                
	}
	
	//@RequestMapping(value = "/home.html", method = RequestMethod.GET )
	public ModelAndView homePage() {	
		List<JCEntity> model = getModelList();
		logger().info( "Entering Car Model List Page: Models = " + model );
        return new ModelAndView( "car_list", "modelObject", model );
	}
	
	
	//@RequestMapping(value = "/car_details.html")  
    public ModelAndView carDetailsPage(@RequestParam(value="carID", required=true) String carID, HttpServletRequest request,  
            HttpServletResponse response) {   
		 
		JCCar aCar = getCarById( carID );
		logger().info( "Entering Car Details Page: Car = " + aCar );
        return new ModelAndView( "car_details", "currentCar", aCar );  
    }  
	
}
