package com.camcars.web;

import java.io.FileOutputStream;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.component.UIOutput;

import com.camcars.*;

@ManagedBean
@SessionScoped
public class CarSearchBean extends CarFinderBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public final String ANY_STR = "Any";
	JCMake _make;
	JCModel _model;
	String _priceMin = ANY_STR;
	String _priceMax = ANY_STR;
	String _mileageMin = ANY_STR;
	String _mileageMax = ANY_STR;
	String _yearMin = ANY_STR;
	String _yearMax = ANY_STR;
	String _fuelType = ANY_STR;
	String _gearbox = ANY_STR;
	
	public CarSearchBean() {
		super();
		_make = defaultMake();
		_model = defaultModel();
		logger().info( "CarSearchBean Instantiation" );
	}
	
	public JCMake defaultMake() {
		return (JCMake )getMakeList().get(0);
	}
	
	public JCModel defaultModel() {
		return (JCModel )getModelList().get(0);
	}
	
	public JCMake getMake() { 
		
		if ( _make == null ) {
			_make = defaultMake();
		}
		
		return _make; 
	}
	
	public void setMake( JCMake make ) {
		logger().info( "Chosen make is " + make );
		_make = make;
	}
	
	public String getMakeName() {
		return getMake().getName();
	}
	
	public void setMakeName( String makeName ) {
		logger().info( "setMakeName() Chosen make is " + makeName );
		JCMake aMake = (JCMake )getEntityByName( "JCMake", makeName );
		setMake( aMake );
	}
	
	public JCModel getModel() { 
		
		if ( _model == null ) {
			_model = defaultModel();
		}
		
		return _model; 
	}
	
	public void setModel( JCModel model ) {
		_model = model;
	}
	
	public String getModelName() {
		return getModel().getName();
	}
	
	public void setModelName( String modelName ) {
		JCModel aModel = (JCModel )getEntityByName( "JCModel", modelName );
		setModel( aModel );
	}
	
	public String getPriceMax() { return _priceMax; }
	public void setPriceMax( String price ) {
		logger().info( "Chosen price max is " + price );
		_priceMax = price;
	}
	public String getPriceMin() { return _priceMin; }
	public void setPriceMin( String price ) {
		logger().info( "Chosen price min is " + price );
		_priceMin = price;
	}
	
	public List<JCEntity> getAllMakes() {
		return getMakeList();
	}
	
	public List<String> getAllMakeNames() {
		
		List<JCEntity> makes = getMakeList();
		List<String> names = new ArrayList<String>();
		
		makes.forEach( (aMake) -> { if (aMake != null && aMake.getName() != null ) names.add( (String )aMake.getName() ); } );
		
		return names;
	}
	
	public List<JCModel> getAllModels() {
		return getMake().getModels_();
	}
	
	public List<String> getAllModelNames() {
		
		List<JCModel> models = getMake().getModels_();
		List<String> names = new ArrayList<String>();
		
		models.forEach( (aModel) -> { if (aModel != null && aModel.getName() != null ) names.add( aModel.getName() ); } );
		
		return names;
	}
	
	public List<String> getPriceList() {
		String[] values = { ANY_STR, "1,000 EUR", "2,000 EUR", "3,000 EUR", "4,000 EUR", "5,000 EUR",
				"6,000 EUR", "7,000 EUR", "8,000 EUR", "9,000 EUR", "10,000 EUR" };
		return Arrays.asList( values );
	}
	
	public String getMileageMax() { return _mileageMax; }
	public void setMileageMin( String value ) {
		_mileageMin = value;
	}
	public String getMileageMin() { return _mileageMin; }
	public void setMileageMax( String value ) {
		_mileageMax = value;
	}
	
	public List<String> getMileageList() {
		String[] values = { ANY_STR, "50,000km", "60,000km", "70,000km", "80,000km", "90,000km", "100,000km", "110,000km", "120,000km",
				"130,000km", "140,000km", "150,000km", "160,000km", "170,000km", "180,000km", "190,000km", "200,000km" };
		return Arrays.asList( values );
	}
	
	public String getYearMin() { return _yearMin; }
	public void setYearMin( String value ) {
		_yearMin = value;
	}
	public String getYearMax() { return _yearMax; }
	public void setYearMax( String value ) {
		_yearMax = value;
	}
	
	public List<String> getYearList() {
		String[] values = { ANY_STR, "2000", "2001", "2002", "2003", "2004",
				"2005", "2006", "2007", "2008", "2009", "2010" };
		return Arrays.asList( values );
	}
	
	public String getFuelType() { return _fuelType; }
	public void setFuelType( String value ) {
		_fuelType = value;
	}
	public String getGearbox() { return _gearbox; }
	public void setGearbox( String value ) {
		_gearbox = value;
	}
	
	public List<String> getFuelTypeList() {
		String[] values = { ANY_STR, "Petrol", "Diesel" };
		return Arrays.asList( values );
	}
	
	public List<String> getGearboxList() {
		String[] values = { ANY_STR, "Manual", "Automatic" };
		return Arrays.asList( values );
	}

    public boolean hasPrice() {
        return ! ( getPriceMin().equals( ANY_STR ) || getPriceMax().equals( ANY_STR ) );
    }

    public boolean hasMileage() {
        return ! ( getMileageMin().equals( ANY_STR ) || getMileageMax().equals( ANY_STR ) );
    }

    public boolean hasYear() {
        return ! ( getYearMin().equals( ANY_STR ) || getYearMax().equals( ANY_STR ) );
    }

    public boolean hasFuelType() {
        return ! ( getFuelType().equals( ANY_STR ) );
    }

    public boolean hasGearbox() {
        return ! ( getGearbox().equals( ANY_STR ) );
    }
	
	public String getSearchCriterias_() {

        StringBuilder sb = new StringBuilder();

        sb.append( getMake().getName() + " " + getModel().getName() );
        if ( hasPrice() )       sb.append( " [" + getPriceMin() + " - " + getPriceMax() + "]" );
        if ( hasMileage() )     sb.append( " [" + getMileageMin() + " - " + getMileageMax() + "]" );
        if ( hasYear() )        sb.append( " [" + getYearMin() + " - " + getYearMax() + "]" );
        if ( hasFuelType() )    sb.append( " " + getFuelType() );
        if ( hasGearbox() )     sb.append( " " + getGearbox() );

		return sb.toString();
	}

    public Map getSearchCriteria() {

        Map criteria = new TreeMap<String, Object>();

        if ( hasPrice() )      criteria.put( "price", new PSRange( getPriceMin(), getPriceMax() ) );
        if ( hasMileage() )    criteria.put( "mileage", new PSRange( getMileageMin(), getMileageMax() ) );
        if ( hasYear() )       criteria.put( "year", new PSRange( getYearMin(), getYearMax() ) );
        if ( hasFuelType() )   criteria.put( "fuelType", getFuelType() );
        if ( hasGearbox() )    criteria.put( "year", getGearbox() );

        return criteria;
    }
	
	public void performSearch() {
        logger().info( getSearchCriterias_() );
	}
	
	public String viewMatchingCars() {
		logger().info( getSearchCriterias_() );
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("carSearchBean", this);
        return "search_results";
    }
	
	public List<JCEntity> getMatchingCars() {

        Map bindings = getSearchCriteria();
        List<JCEntity> cars = JCEntity.entitiesWithBindings( getSession(), "JCCar", bindings );

        return cars;

    }
	
	public void handleMakeChange( AjaxBehaviorEvent event ) {
		logger().info( "handleMakeChange() Current make is [" + getMake().getName() + "]");
		logger().info( "handleMakeChange() Event make is [" + ( (UIOutput )event.getSource() ).getValue() + "]");
	}
	
}
