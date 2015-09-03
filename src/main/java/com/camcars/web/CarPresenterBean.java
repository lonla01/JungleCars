package com.camcars.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.camcars.*;

@ManagedBean
@ViewScoped
public class CarPresenterBean  extends CarFinderBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _carId = "No Id";
	
	public String getCarId() {
		return _carId;
	}
	
	public void setCarId( String carId ) {
		logger().info( "setCarId() id = " + carId );
		_carId = carId;
	}
	
	public JCCar getCar() {
		return getCarById( getCarId() );
	}
	
	public void carClicked() {
		logger().info( "carClicked() id = " + getCarId() );
	}
	
	public List<String> getGridLabels() {
		List<String> labels = new ArrayList<String>();
		labels.add( "Galleria:" );
		labels.add( "Description:" );
		return labels;
	}
		
}
