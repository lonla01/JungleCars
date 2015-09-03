
//////////////////   I N N E R   C L A S S E S   ///////////////////

/**
 * All PSObjects conforms to this protocol. It allows them to write themselves
 * In a persistent store such as a file. The format of the persistent data
 * depends of the kind of coder used to encode / decode the object.
 *
 *Was PSObject.PSCoding.
 */

package com.camcars;

import java.io.File;
import java.io.IOException;

public interface PSCoding {

    public void encodeWithCoder(Parsing coder) throws IOException;
    public Class classForCoder();
    public File savingPath();
    public void autoSave();

    //public static PSObject decodeWithCoder(PSCoding.Parsing coder)
    //        throws IOException, ClassNotFoundException;

    //////////////////   I N N E R   C L A S S E S   ///////////////////
    public interface Writing {
        public void encodeObject( Object anObject );
    }

    public interface Reading {
        public Object decodeObject();
    }

    /**
     * All concrete coders should implement this protocol. It's a generic protocol
     * geared toward implementing parsers of external files with a loosely define
     * structure. Unlike PSCosing this protocol is not intended for an object to
     * save itself to a file. But rather how on object graph could be created out
     * of a poorly structured external input.
     *
     * Was PSObject.PSCoder.
     */
    public interface Parsing {

        public void encodeObject( Object anObject );
        public Object decodeObject();
        public void initForWriting( Object data );
        public void initForReading( Object data );

    }

    /**
     * Any object conforming to this protocol can be easily be represented
     * a property list (plist) string. In such a way that the object can be
     * saved in a readable and modify-able format into a file.
     */
    public interface KeyValueCoding extends PSCoding {

        public PSDict properties();
        public void takePropertyDict( PSDict dict );
        public Object valueForKey( String aKey );
        public void takeValueForKey( Object aValue, String aKey );

    }

    /**
     * Some objects that are too complex or have specific requirements preventing them
     * to use the standard set of coders. They must provide a class specific coder
     * that complies to this protocol, in order to handle their persistence.
     */
    public static interface CustomCoding {

        public abstract Object decodeObject();
        //public Object codingTarget();
        //public void setCodingTarget( Object anObject );

    }
    
    public static interface Snapshoting {

    	public PSDict snapshot();

    	public PSObject objectFromSnapshot( PSDict aDict );        

    }

}