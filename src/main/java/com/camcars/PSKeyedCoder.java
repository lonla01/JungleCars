package com.camcars;

import java.io.File;
import java.util.Collection;
/**
 * PSKeyedCoder.java
 *
 * Created on 13 septembre 2009, 13:22
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

public class PSKeyedCoder extends PSStringCoder {
       
    public PSKeyedCoder( File path ) {
        super( path );
    }
    
    ///   N E S T E D   C L A S S E S   ////////////////////////////////////////    
    public static class Reader extends PSStringCoder.Reader {
        
        protected PSCoding.KeyValueCoding instanceForClassName( Object classToken ) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
            PSCoding.KeyValueCoding psobject;
            Class psObjectClass;
            
            psObjectClass = Class.forName( classToken.toString() );
            psobject = (PSCoding.KeyValueCoding )psObjectClass.newInstance();
            
            return psobject;
        }

        protected void psObjectEnd() {
            Object stackElement, object;
            
            if (_stack.isEmpty())
                throw new PlistException("Found object end while stack is empty!");
            
            object = _stack.pop();
            if (_stack.isEmpty()) {
                setReturnValue( originalObject(object) );
                return;
            }
            stackElement = _stack.peek();
            if ( isCollection( stackElement ) ) {
                ((Collection )stackElement).add( originalObject(object) );
            } else
                _stack.push( originalObject(object) );
            
//            String className = stackElement.getClass().getName();
//            logger().verbose( this, "Finished building object: "+ className );
        }
    
    } /// End Reader.
}
