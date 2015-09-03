
package com.camcars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.Set;

import org.apache.commons.io.FileUtils;

import org.json.*;

public class JCCar extends JCEntity {

    public static String _DirtyChars = "%C3%";
    public static String _CleanChars = "_C3_";
    public static String _DirtyImgNameChars = "_$_";
    public static String _CleanImgNameChars = "___";

    public static String _CarListingRoot = "/Users/patrice/Developer/Library/JCCars";
    public static String _CarListingPath =  _CarListingRoot + "/cars";

	public JCCar() {
        super();
        setContentDesc( ContentDesc.singleton() );
    }
	
    public String getCapacity() {
        return (String )valueForKey("capacity");
    }

    public void setCapacity( String value ) {
        takeValueForKey( value, "capacity" );
    }

    public String getDoorCount() {
        return (String )valueForKey("doorCount");
    }

    public void setDoorCount( String value ) {
        takeValueForKey( value, "doorCount" );
    }
    
    public String getGearbox() {
        return (String )valueForKey("gearbox");
    }

    public void setGearbox( String value ) {
        takeValueForKey( value, "gearbox" );
    }
    
    public String getMileage() {
        return (String )valueForKey("mileage");
    }

    public void setMileage( String value ) {
        takeValueForKey( value, "mileage" );
    }
    
    public String getPower() {
        return (String )valueForKey("power");
    }

    public void setPower( String value ) {
        takeValueForKey( value, "power" );
    }
    
    public String getRegistration() {
        return (String )valueForKey("registration");
    }

    public void setRegistration( String value ) {
        takeValueForKey( value, "registration" );
    }
    
    public String getPrice() {
        return (String )valueForKey("price");
    }

    public void setPrice( String value ) {
        takeValueForKey( value, "price" );
    }

    public JCModel getModel() {
        return (JCModel )valueForKey("model");
    }

    public void setModel( JCModel value ) {
        takeValueForKey( value, "model" );
    }
    
    public String getTitle() {
        return (String )valueForKey("title");
    }
    
    public String getShortTitle() {
    	
        String title = getTitle();
        int endIndex;
        String shortTitle = "No Title Provided";
        
        if (title != null) {
        	endIndex = Integer.min(  title.length(), 20 );
        	shortTitle = title.substring( 0, endIndex );
        }
        
        return shortTitle;
    }

    public void setTitle( String value ) {
        takeValueForKey( value, "title" );
    }
    
    public String getListing() {
        return (String )valueForKey("listing");
    }

    public void setListing( String value ) {
        takeValueForKey( value, "listing" );
    }
    
    // Returns a list of image file names encoded as a JSON array
    public String getImageList() {
        return (String )valueForKey("imageList");
    }

    public void setImageList( String value ) {
        takeValueForKey( value, "imageList" );
    }
    
    public List<String> getImageNames() {
    	
    	JSONArray jsonArray =  new JSONArray( getImageList() );
    	ArrayList<String> imageNames = new ArrayList<String>();
    	
    	for (int i = 0; i < jsonArray.length(); i++) {
    	    String aName = jsonArray.getString(i);
    	    imageNames.add( imagePathForName( aName ) );
    	}
    	
    	return imageNames;
    }

    // The resulting relative path has the form: cars/<listing>/image_$_nn.JPG
    String imagePathForName( String imageName ) {
    	if ( imageName == null ) {
    		return new File( "cars", "default_car_img_$_27.JPG" ).getPath();
    	} else {
    		File listing = new File( getListing(), imageName );
    		return new File( new File( "cars" ), listing.getPath() ).getPath();
    	}
    }

    public boolean cleanListing() {

        boolean result = true;

        // Do filename cleanup in the database
        String imgList = getListing();

        if (imgList == null) return false;

        imgList = cleanDirtyListing(imgList);
        setListing(imgList);

        return result;
    }

    public boolean renameListing() {

        boolean result;

        // Do filename cleanup in the database
        String oldFileName = getListing();
        if (oldFileName == null) return false;

        // We try to get the path of the dirty folder even though the listing property in DB might have been cleaned already.
        oldFileName = oldFileName.replaceAll( _CleanChars, _DirtyChars );
        // If successful do it on disk also
        String newFileName = cleanDirtyListing(oldFileName);
        if ( newFileName.equals( oldFileName ) ) return true; // No need to rename listing folder

        File newFile = new File( _CarListingPath, newFileName );
        File oldFile = new File( _CarListingPath, oldFileName );

        try {
            FileUtils.moveDirectory( oldFile, newFile );
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public boolean cleanImageList() {

        boolean result = true;

        // Do filename cleanup in the database
        String imgList = getImageList();

        if (imgList == null) return false;

        imgList = cleanDirtyImageName(imgList);
        setImageList(imgList);

        return result;
    }

    public boolean renameImageNames() {

        boolean result = true;

        // Do filename cleanup in the database
        List<String> oldFileNames = getImageNames();
        Iterator<String> imgIter = oldFileNames.iterator();

        if (oldFileNames == null) return false;

        while( imgIter.hasNext() ) {
            String oldFileName = imgIter.next();

            // We try to get the path of the dirty folder even though the listing property in DB might have been cleaned already.
            oldFileName = oldFileName.replaceAll( _CleanImgNameChars, _DirtyImgNameChars );

            // If successful do it on disk also
            String newFileName = cleanDirtyImageName( oldFileName );
            if ( newFileName.equals( oldFileName ) ) return true; // No need to rename listing folder

            File newFile = new File( _CarListingRoot, newFileName );
            File oldFile = new File( _CarListingRoot, oldFileName );

            try {
                FileUtils.moveFile( oldFile, newFile );
                result = true;
            } catch (IOException e) {
                System.err.print( e.getMessage() );
                result = true; // not false to keep going
            }
        }

        return result;
    }

    public static String cleanDirtyImageName( String dirty ) {
        return dirty.replaceAll( "_\\$_", _CleanImgNameChars );
    }

    public static String cleanDirtyListing( String dirty ) {
        return dirty.replaceAll( _DirtyChars, _CleanChars );
    }
    
    // The image names stored in the DB in imageList properties end with $_27.JPG
    // But on disk, for each image we have 2 other sizes of the same image with suffixes: $_1.JPG, $_14.JPG
    // From the smallest to the biggest, the rank like: $_14.JPG, $_1.JPG and $_27.JPG
    public String getThumbnail() {
    	
    	List<String> imgNames = getImageNames();
    	String thumbnail = (imgNames.size() > 0) ? imgNames.get(0) : null;
    	
    	// log( this, "Thumbnail:["+thumbnail+"]" );
    	
    	return thumbnail;
    }

    File getCatalinaBase() {
    	String appName = System.getProperty( "app.name", "JungleCarPF" );
    	File relFile = new File( new File( "webapps" ), appName );
    	File appFile = new File( new File( System.getProperty( "catalina.base" ) ), relFile.getPath() );
    	File catalinaBase = appFile.getAbsoluteFile();
    	return catalinaBase;
    }

    
    public boolean hasThumbnail() {
    	
    	String thumbnail = getThumbnail();
    	File thumbFile = null;
    	boolean hasT;
    	
    	if ( thumbnail != null && thumbnail.length() > 0 ) {
    		// thumbFile = new File( getCatalinaBase(), thumbnail );
    		thumbFile = new File( thumbnail );
    	}
    	
    	hasT = thumbFile != null && thumbFile.exists() && thumbFile.isFile();
    	if ( thumbFile != null && hasT == false ) {
    		log( this, "Thumbnail:["+thumbFile.getPath()+"]" + " exists:["+hasT+"]" );
    	}
    	
    	return hasT;
    }
    
  /// D E R I V E D  P R O P E R T I E S  //////////////////////
    public String getDisplayName() {
    	return getModel().getDisplayName() + " " + getCapacity() + " " + getGearbox() + " " + getMileage() + " " + getPrice();
    }


///   N E S T E D   ////////////////////////////////////////////////////////
    public static class ContentDesc extends JCEntity.ContentDesc {
        
        private PSArray _Attributes = null;
        private PSArray _ToOne = null;
        private static ContentDesc _Singleton = null;

        public PSArray attr() {
            if ( _Attributes == null ) {
                _Attributes = new PSArray( super.attr() );
                _Attributes.add( new Attr( "capacity" ) );
                _Attributes.add( new Attr( "doorCount" ) );
                _Attributes.add( new Attr( "gearbox" ) );   
                _Attributes.add( new Attr( "mileage" ) ); 
                _Attributes.add( new Attr( "power" ) ); 
                _Attributes.add( new Attr( "price" ) ); 
                _Attributes.add( new Attr( "registration" ) ); 
            }
            
            return _Attributes;
        }
        
        public PSArray toOne() {
            if ( _ToOne == null ) {
                _ToOne = new PSArray( super.toOne() );
                _ToOne.add( new ToOne( JCModel.class, "model" ) );
            }
            
            return _ToOne;
        }

        public static JCEntity.ContentDesc singleton() {
            if ( _Singleton == null )
                _Singleton = new ContentDesc();
            
            return _Singleton;
        }
        
   }

}
