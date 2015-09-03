

/**
 * <p>Title: Jungle Cars Web Application</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

package com.camcars.test;

import com.camcars.core.*;
import com.camcars.model.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

public class DBImportTestSuite extends TestSuite {
     
    static int N = 1;
    
    public DBImportTestSuite() {
        super("Test Importing Database");
        
//        addTest( new DBImportTest() );
//        addTest( new HibernateMakeCreateTest() );
//        addTest( new HibernateModelCreateTest() );
//        addTest( new HibernateCarCreateTest() );
//        addTest( new HibernateModelListTest() );
//        addTest ( new HibernateModelFetchTest() );
        addTest ( new HibernateModelPredicateTest() );
    }
    
    public static class DBImportTest extends Test {
        
        PSParser.TextParser _coder;
        
        final File PLIST_PATH =
                new File( "/Users/patrice/Library/Application Support/com.camcars.JungleCars/OSXCoreDataObjC.plist" );
        
        public String description() {
            return "Importing Database Test";
        }
                
        PSParser.TextParser createCoder() {
            return new PSParser.TextParser(PLIST_PATH);
        }
        
        PSParser.TextParser coder() {
            if ( _coder == null ) 
                _coder = createCoder();
            
            return _coder;
        }
                              
        public void test() throws Exception {            

            String loadedText = (String )coder().decodeObject();
           
            //assertEq( "Testing loaded text", text(), loadedText );
            PSPlistCoder.Reader plistDecoder = new PSPlistCoder.Reader( loadedText );
            Object decodedSnapshots = plistDecoder.decodeObject();
            logger().log( "Imported object: " + decodedSnapshots );
            PSArray makeSnapshots = (PSArray )decodedSnapshots;
            PSArray makeEntities = makeSnapshots.objectsFromSnapshots();
            logger().log( "Make entitites" + makeEntities );
            
        }        
       
    }
    
    public static JCEntity createEntity( Class classOfEntity ) {

    	JCEntity newEntity = null;

    	try {
    		PSID.Generator idGen = PSID.Generator.defaultGenerator();
    		newEntity = (JCEntity )classOfEntity.newInstance();
    		newEntity.setGID( idGen.nextIDForClass( classOfEntity ) ); 
    	} catch ( Exception e ) {
    		logger().error( "Cannot create entity of class:" + classOfEntity.getName() );
    		newEntity = null;
    	}

    	return newEntity;

    }
	    
    public static class HibernateMakeCreateTest extends Test {

    	public void test() throws Exception {

    		Configuration configuration = new Configuration().configure();
    		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
    		SessionFactory factory = configuration.buildSessionFactory(builder.build());
    		Session session = factory.openSession();
    		
    		JCMake newMake = (JCMake )createEntity( JCMake.class ); 
    		newMake.setName("Peugeot");
    		
    		session.beginTransaction();
    		session.save(newMake);
    		session.getTransaction().commit();

    	}
    	
    }
    
    public static class HibernateModelCreateTest extends Test {

    	public void test() throws Exception {

    		Session session = null;

    		try {
    			Configuration configuration = new Configuration().configure();
    			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
    			SessionFactory factory = configuration.buildSessionFactory(builder.build());

    			session = factory.openSession();

    			JCMake newMake = (JCMake )createEntity( JCMake.class );
    			newMake.setName("Jeep");
    			JCModel newModel = (JCModel )createEntity( JCModel.class );
    			newModel.setName("Cherokee");
    			newModel.setCarType("SUV");
    			newModel.setFuelType("Diesel");
    			newModel.setSeatCount("5 seats");
    			newModel.setMake( newMake );

    			session.beginTransaction();
    			session.save(newMake);  
    			session.save(newModel);
    			session.getTransaction().commit();

    		} finally {
    			if (session != null) session.close();
    		}

    	}

    }
    
    public static class HibernateCarCreateTest extends Test {

    	public void test() throws Exception {

    		Session session = null;

    		try {

    			Configuration configuration = new Configuration().configure();
    			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
    			SessionFactory factory = configuration.buildSessionFactory(builder.build());

    			session = factory.openSession();

    			JCMake newMake = (JCMake )createEntity( JCMake.class );
    			newMake.setName("Jeep");

    			JCModel newModel = (JCModel )createEntity( JCModel.class );
    			newModel.setName("Cherokee");
    			newModel.setCarType("SUV");
    			newModel.setFuelType("Diesel");
    			newModel.setSeatCount("5 seats");
    			newModel.setMake( newMake );

    			JCCar newCar = (JCCar )createEntity( JCCar.class );
    			newCar.setCapacity("2000ccm");
    			newCar.setDoorCount("5 doors");
    			newCar.setGearbox("Manual");
    			newCar.setMileage( "125000" );
    			newCar.setPower("power");
    			newCar.setPrice( "2500" );
    			newCar.setRegistration( "1234567" );
    			newCar.setModel( newModel );

    			session.beginTransaction();
    			session.save(newMake);  
    			session.save(newModel);
    			session.save(newCar); 
    			session.getTransaction().commit();

    		} finally {
    			if (session != null) session.close();
    		}

    	}

    }
    
    public static class HibernateModelListTest extends Test {

    	public void test() throws Exception {

    		Session session = null;

    		try {

    			Configuration configuration = new Configuration().configure();
    			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
    			SessionFactory factory = configuration.buildSessionFactory(builder.build());

    			session = factory.openSession();

    			Query query = (Query )session.createQuery("from JCModel");  
//    		    List<JCModel> modelList = query.list();      		      
//    		    Iterator<JCModel> itr = modelList.iterator();  
    			Iterator<JCModel> itr = query.iterate();
    		    
    		    while( itr.hasNext() ) {  
    		    	JCModel aModel = itr.next();  
    		        System.out.println( "Model Name: " + aModel.getDisplayName());  
    		          
    		        //printing cars of this model  
    		        List<JCCar> list2 = aModel.getCars_();
    		        Iterator<JCCar> itr2 = list2.iterator();  
    		        while( itr2.hasNext() ) {  
    		        	JCCar aCar = itr2.next();
    		            System.out.println( "Car Name: " + aCar.getDisplayName() );  
    		        }      		          
    		    }  
 
    		} finally {
    			if (session != null) {
    				session.flush();
    				session.close();
    			}
    		}

    	}

    }
    
    public static class HibernateModelFetchTest extends Test {
    	
    	public void test() throws Exception {
    		
    		Session session = null;
    		
            try {
                
            	session = JCEntity.getSessionFactory().openSession();
            	JCFetchRequest statement = new JCFetchRequest( session, "JCModel" );
            	List<JCEntity> modelList = statement.listEntities();
            	Iterator<JCEntity> modelIter = modelList.iterator();
            	
                while ( modelIter.hasNext() ){    
                	JCModel eachModel = (JCModel )modelIter.next();
                	System.out.println( "Model: " + eachModel.getDisplayName() );
                }
                
            } catch (Exception e) {
                System.err.println("Error connecting to database server");
                e.printStackTrace( System.err );
            } finally {
    			if (session != null) {
    				session.flush();
    				session.close();
    			}
    		}

    	}
    	
    }
    
    public static class HibernateModelPredicateTest extends Test {
    	
    	public void test() throws Exception {
    		
    		Session session = null;
    		
            try {
                            	
            	JCFetchRequest statement;
            	List<JCEntity> modelList;
            	Iterator<JCEntity> modelIter;
            	
            	session = JCEntity.getSessionFactory().openSession();
            	statement = new JCFetchRequest( session, "JCModel" );
            	statement.setPredicate( "zname = 'Avensis'" );
            	modelList = statement.listEntities();
            	modelIter = modelList.iterator();
            	
                while ( modelIter.hasNext() ){    
                	JCModel eachModel = (JCModel )modelIter.next();
                	System.out.println( "Model: " + eachModel.getName() );
                }
                
            } catch (Exception e) {
                System.err.println("Error connecting to database server");
                e.printStackTrace( System.err );
            } finally {
    			if (session != null) {
    				session.flush();
    				session.close();
    			}
    		}

    	}
    	
    }

    
}