package com.camcars.web;

import com.camcars.JCCar;
import com.camcars.JCEntity;
import com.camcars.JCModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * Created by patrice on 06/03/15.
 */

@ViewScoped
@ManagedBean
public class CarHitsBean extends  CarFinderBean {

    //@ManagedProperty("#{carSearchBean}")
    private CarSearchBean carSearchBean;
    private JCModel _model;

    @PostConstruct
    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.carSearchBean = (CarSearchBean )facesContext.getExternalContext().getSessionMap().get( "carSearchBean" );
        logger().info( String.format( "init() = %s", this.carSearchBean ) );
    }

    public CarSearchBean getCarSearchBean() {
        return carSearchBean;
    }

    public void setCarSearchBean( CarSearchBean carSearchBean ) {
        logger().info( "setSearchInfo()" + " = " + carSearchBean );
        this.carSearchBean = carSearchBean;
    }

    public JCModel defaultModel() {
        return (JCModel )getModelList().get(0);
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

    public String getSearchCriterias() {
        return getCarSearchBean().getSearchCriterias_();
    }

    public String viewMatchingCars() {
        logger().info( getSearchCriterias() );

        return "search_results";
    }

    public List<JCEntity> getMatchingCars() {
        return getCarSearchBean().getMatchingCars();
    }

}
