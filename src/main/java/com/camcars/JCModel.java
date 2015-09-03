
package com.camcars;

import java.util.*;

public class JCModel extends JCEntity {

	public JCModel() {
        super();
        setContentDesc( ContentDesc.singleton() );
    }
	
    public String getCarType() {
        return (String )valueForKey("carType");
    }

    public void setCarType( String value ) {
        takeValueForKey( value, "carType" );
    }

    public String getFuelType() {
        return (String )valueForKey("fuelType");
    }

    public void setFuelType( String value ) {
        takeValueForKey( value, "fuelType" );
    }
    
    public String getSeatCount() {
        return (String )valueForKey("seatCount");
    }

    public void setSeatCount( String value ) {
        takeValueForKey( value, "seatCount" );
    }

    public JCMake getMake() {
        return (JCMake )valueForKey("make");
    }

    public void setMake( JCMake value ) {
        takeValueForKey( value, "make" );
    }

    @SuppressWarnings("unchecked")
	public Set<JCCar> getCars() {

        Set<JCCar> cars = (Set<JCCar >)valueForKey("cars");

        if ( cars == null ) cars = new HashSet<JCCar>();

        return cars;
    }

    public List<JCCar> getCars_() {
        List<JCCar> carList = new ArrayList<JCCar>();
        carList.addAll( getCars() );

        return carList;
    }

    public void setCars( Set<JCCar> cars ) {
        logger().debug( this, "setCars() = " + cars );
        takeValueForKey(cars, "cars");
    }

    public void addCars( Set<JCCar> cars ) {
        getCars().addAll( cars );
    }
    
    /// D E R I V E D  P R O P E R T I E S  //////////////////////
    public String getDisplayName() {
    	JCMake aMake = getMake();
    	String makeName = (aMake == null) ? "" : aMake.getName();
    	return makeName  + " " + getName() + " " + getFuelType();
    }
    
    
///   N E S T E D   ////////////////////////////////////////////////////////
	
    public static class ContentDesc extends JCEntity.ContentDesc {
        
        private PSArray _Attributes = null;
        private PSArray _ToOne = null;
        private PSArray _ToMany = null;
        private static ContentDesc _Singleton = null;

        public PSArray attr() {
            if ( _Attributes == null ) {
                _Attributes = new PSArray( super.attr() );
                _Attributes.add( new Attr( "carType" ) );
                _Attributes.add( new Attr( "fuelType" ) );
                _Attributes.add( new Attr( "seatCount" ) );
                
            }
            
            return _Attributes;
        }
        
        public PSArray toOne() {
            if ( _ToOne == null ) {
                _ToOne = new PSArray( super.toOne() );
                _ToOne.add( new ToOne( JCMake.class, "make" ) );
            }
            
            return _ToOne;
        }

        public PSArray toMany() {
            if ( _ToMany == null ) {
                _ToMany = new PSArray( super.toMany() );
                _ToMany.add( new ToMany( JCCar.class, "cars" ) );
            }
            
            return _ToMany;
        }
        
        public static JCEntity.ContentDesc singleton() {
            if ( _Singleton == null )
                _Singleton = new ContentDesc();
            
            return _Singleton;
        }
        
   }

}
