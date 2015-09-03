

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

package com.camcars;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.net.URL;

/**
 * Some of the behaviour of this class can be moved up in a PSRoot superclass.
 * In order to factorise common members with PSID.
 */
public abstract class PSObject extends PSRoot implements PSCoding.KeyValueCoding {

    private PSDict _properties = null;
    private static PSCoding.Writing _defaultWriter = null;
    private static PSCoding.Reading _defaultReader = null;
    private static PSCoding.Parsing _defaultCoder  = null;
    private static PSBuffer         _sharedBuffer  = null;    
    private static String _dataStoreDir = null;

    // For convenience, we use the same store both for test and prod.
    public static final String DEFAULT_STORE_DIR = "TestStore/";

    public PSDict snapshot() {
        PSDict aDict = super.snapshot();

        aDict.putAll( properties() );

        return aDict;
    }
    
    public static String dataStoreDir() {
        if ( _dataStoreDir == null ) {
            _dataStoreDir = DEFAULT_STORE_DIR;
        }
        return _dataStoreDir;
    }        

    public static void setDataStoreDir( String aDir ) {
        _dataStoreDir = aDir;
    }

    ///   PSCoding   /////////////////////////////////////////////////////////////////////////
    public Class classForCoder() {  return this.getClass(); }

    public File savingPath() {
        debug( this, "savingPath  class="+getClass() );
        return savingPathForClass( getClass() );
    }

    public static File savingPathForClass( Class aClass ) {
        //logger().debug( "dataStoreDir="+dataStoreDir() + " classN="+aClass.getName() );
        return new File( dataStoreDir() + aClass.getName() + ".plist" );
    }

    public void encodeWithCoder(PSCoding.Parsing coder) throws IOException {
        coder.encodeObject(this);
    }

    protected static PSObject decodeWithCoder(PSCoding.Parsing coder)
    throws IOException, ClassNotFoundException {
        Object retVal = null;
        PSObject psObject = null;
        
        try {
            retVal = coder.decodeObject();
            psObject = (PSObject )retVal;
        } catch ( ClassCastException e) {
            logger().error( "PSObject: Decoded Object not PSObject: " + retVal.getClass().getName() );
        }
        
        return psObject;
    }

    /// TOFIX should not catch IOException but throw it!
    public void autoSave() {
        try {
            encodeWithCoder( defaultCoder( savingPath() ) );
        } catch (IOException io) {
            logger().error( this, "IOException while saving object:", io);
        }
    }

    protected static PSCoding.Parsing coderForClass( Class aClass ) {
        return new PSPlistCoder( savingPathForClass( aClass ) );
    }

    protected static PSCoding.Parsing defaultCoder( File path ) {
        if ( _defaultCoder == null ) {
            _defaultCoder = new PSPlistCoder( path );
        } else {
            ( (PSPlistCoder )_defaultCoder ).resetCoding();
            ( (PSPlistCoder )_defaultCoder ).setFilePath( path );
        }

        return _defaultCoder;
    }

    ///  PSCoding.KeyValueCoding   /////////////////////////////////////////////
    public PSDict properties() {
        if (_properties == null) {
            _properties = new PSDict(5);
        }
        return _properties;
    }

    /**
     * Sub-classes should make sure they override this method in order to
     * ensure their state is properly updated with unarchived value.
     */
    public void takePropertyDict( PSDict dict ) { 
        error( this, "takePropertyDict() is abstract !" );
    }

    public Object valueForKey( String aKey ) {
        return properties().get( aKey );
    }

    public void takeValueForKey( Object aValue, String aKey ) {
        properties().put( aKey, aValue );
    }


//    ///   C O N V E N I E N C E S    ///////////////////////////////////////////
//    protected static PSFactory factory() { return PSFactory.factory(); }    
//
//    public String toPlist() {
//        PSCoding.Writing writer = defaultWriter();
//        writer.encodeObject(this);
//        String result = writer.toString();
//        ( (PSPlistCoder.Writer )writer ).finishedWriting();
//
//        return result;
//    }
//
//    public static PSCoding.Writing defaultWriter() {
//        if ( _defaultWriter == null ) {
//            PSPlistCoder.Writer writer = new PSPlistCoder.Writer();
//            writer.setIndentationEnabled(true);
//            writer.setIndentThreshold(999);
//            setDefaultWriter( writer );
//        } else {
//            ( (PSPlistCoder.Writer )_defaultWriter ).resetWriting();
//            ( (PSPlistCoder.Writer )_defaultWriter ).recycleAllInstances();
//            pool().store().debugStats();
//        }
//
//        return _defaultWriter;
//    }

    public static PSCoding.Reading defaultReader() {
        if ( _defaultReader == null ) {
            _defaultReader = new PSPlistCoder.Reader();
//            _defaultReader = new PSStringCoder.Reader();
        } else {
            ( (PSPlistCoder.Reader )_defaultReader ).resetReading();
        }

        return _defaultReader;
    }

    public static void setDefaultWriter( PSCoding.Writing writer ) {
        _defaultWriter = writer;
    }

    public static void setDefaultReader( PSCoding.Reading reader ) {
        _defaultReader = reader;
    }

    public static PSBuffer sharedBuffer() {
        if ( _sharedBuffer == null )
            _sharedBuffer = new PSBuffer();
        else
            _sharedBuffer.clearContent();

        return _sharedBuffer;
    }
    
    public static Collection collectionFromArray( Object[] objects ) {
//        Collection collection = new PSArray(objects.length);
//        for(int i=0; i < objects.length; i++) {
//            collection.add( objects[i] );
//        }

        return new PSArray( objects );
    }    
    
    public static void invalidArg( String msg ) {
        throw new IllegalArgumentException( msg );
    }

    /**
     * Deprecated. Rather create a coder and call decodeWithCoder.
     */
    public static String stringFromFile( File path ) throws IOException {
        String result;
        PSBuffer loadedString = pool().bufferInstance();

        try {
            Reader in = new FileReader(path);
            char[] buff = new char[4096];
            int nch;
            while ((nch = in.read(buff, 0, buff.length)) != -1) {
                loadedString = loadedString.append( new String(buff, 0, nch) );
            }
        } catch (IOException io_e) {
            logger().error( io_e.toString(), io_e );
        }

        result = loadedString.toString();
        pool().recycleInstance( loadedString );

        return result;
    }

    public static Object objectFromFile( File path ) {
        PSPlistCoder coder;
        Object fileContent = null;

        try {
            coder = new PSPlistCoder( path );
            fileContent = coder.decodeObject();
        } catch ( Throwable e ) {
            error( "PSObject: Exception while reading file: " + path.getAbsolutePath(), e );
            fileContent = null;
        }

        return fileContent;
    }

    public static void saveObject( Object anObject, File aFile ) throws Exception {
        PSPlistCoder coder;

        try {
            coder = new PSPlistCoder( aFile );
            coder.encodeObject( anObject );
        } catch ( Exception e ) {
            error( "PSObject: Exception while saving file: " + aFile.getAbsolutePath(), e );
            throw e;
        }

    }
    
    ///   N E S T E D   C L A S S E S   ////////////////////////////////////////

}