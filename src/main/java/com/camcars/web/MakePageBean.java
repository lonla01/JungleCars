package com.camcars.web;

import com.camcars.JCCar;
import com.camcars.JCEntity;
import com.camcars.JCMake;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by patrice on 11/03/15.
 */
@ManagedBean
@ViewScoped
public class MakePageBean extends CarFinderBean {

    public String getMakeName() {
        return makeName;
    }

    public void setMakeName(String makeName) {
        logger().info( "setMakeName() makeName = " + makeName );
        this.makeName = makeName;
    }

    private String makeName = "Toyota";

    public JCMake getMake() {
        JCEntity aMake = JCEntity.entityByName( getSession(), "JCMake", getMakeName() );
        return (JCMake )aMake;
    }

    public List<JCCar> getMatchingCars() {
        return getMake().getCars();
    }
}
