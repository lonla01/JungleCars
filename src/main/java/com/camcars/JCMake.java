
package com.camcars;

import org.hibernate.Session;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JCMake extends JCEntity {

    private static int _ModelPrimaryKey = 5000;

	public JCMake() {
        super();
        setContentDesc( ContentDesc.singleton() );
    }
	
    @SuppressWarnings("unchecked")
	public Set<JCModel> getModels() {

        return (Set<JCModel>) valueForKey("models");
    }

    public List<JCModel> getModels_() {
        List<JCModel> modelList = new ArrayList<JCModel>();
        modelList.addAll( getModels() );

        return modelList;
    }

    public void setModels( Set<JCModel> models ) {
        takeValueForKey( models, "models" );
    }

    public List<JCCar> getCars() {

        List<JCCar> cars = new ArrayList<JCCar>();
        List<JCModel> models = getModels_();

        for (int i = 0; i < models.size(); i++) {
            JCModel aModel = models.get(i);
            cars.addAll( aModel.getCars() );
        }

        return cars;
    }

    public Collection<String> getCanonicalModelNames() {

        String modelName = "/Users/patrice/Developer/Library/JCCars/models/" + getName() + ".txt";
        Charset encoding = Charset.forName( "UTF-8" );
        Collection lines = null;

        try {
            lines = Files.readAllLines( Paths.get( modelName ), encoding);
        } catch (IOException e) {
            // return silently an empty list
            lines = new ArrayList<String>();
        }

        return lines;
    }

    public Collection<String> getLowerCaseCanonicalModelNames() {

        Collection<String> modelNames_lowerCase = new ArrayList<String>();
        getCanonicalModelNames().forEach(
                (String aName) -> modelNames_lowerCase.add( aName.toLowerCase() )
        );

        return modelNames_lowerCase;

    }

    public JCModel getCanonicalModelByName( Session session, String rawModelName ) {

        String canonicalName = canonicalModelNameForRowName(rawModelName);
        JCModel aModel = null;

        aModel = (JCModel )JCEntity.entityByName( session, "JCModel", canonicalName );
        if ( aModel == null ) {
            aModel = new JCModel();
            aModel.setMake( this );
            aModel.setName( canonicalName );
            aModel.setId( _ModelPrimaryKey++ );
            aModel.saveEntity( session );
        }

        return aModel;
    }

    public String canonicalModelNameForRowName(String rawModelName) {

        Iterator nameIter = getCanonicalModelNames().iterator();
        while( nameIter.hasNext() ) {
            String canonicalName = (String )nameIter.next();
            canonicalName = canonicalName.toLowerCase();
            if ( canonicalName.equals( rawModelName ) ) return canonicalName;
            String[] parts = canonicalName.split("-");
            int matches = 0;
            for( int i=0; i < parts.length; i++ ) {
                String canonicalPart = parts[i];
                boolean foundInRaw = rawModelName.contains(canonicalPart);
                if ( ! foundInRaw ) {
                    break; // Out of for loop
                }
                matches++;
            }
            if ( matches == parts.length ) return canonicalName;
        }

        // No Canonical name found
        logger().error( this, "Couldn't find canonical name for: " + rawModelName );
        return rawModelName;

    }

    public Collection<JCModel> getCanonicalModels( Session session ) {

        Collection<JCModel> models = new ArrayList<JCModel>();

        getCanonicalModelNames().forEach(
                (modelName) -> {
                    models.add( getCanonicalModelByName( session, modelName ) );
                }
        );

        return models;
    }

	
///   N E S T E D   ////////////////////////////////////////////////////////
	
    public static class ContentDesc extends JCEntity.ContentDesc {
        
        private PSArray _ToMany = null;
        private static ContentDesc _Singleton = null;
                
        public PSArray toMany() {
            if ( _ToMany == null ) {
                _ToMany = new PSArray( super.toMany() );
                _ToMany.add( new ToMany( JCModel.class, "models" ) );
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
