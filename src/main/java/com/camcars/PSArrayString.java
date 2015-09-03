package com.camcars;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Vector;

/**
 * PSString.java
 *
 * Created on 15 ao�t 2009, 22:52
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

public class PSArrayString extends PSString {
    
    protected Vector _array         = null;
    
    private static TranslateVisitor _TranslateVisitor = null;
    
    protected PSArrayString() {
        this( 10 ); 
    }
    
    protected PSArrayString( int capacity ) {
        _array = new Vector( capacity );
    }
    
    public PSArrayString( PSString aString ) {
        this( aString.length() );
        initWithString( aString );
    }

    public PSArrayString( String aString ) {
        this( aString.length() );
        initWithString( aString );
    }
    
    public PSArrayString( char[] chars ) {
        this( chars.length );
        initWithChars( chars );
    }
    
    /// I N I T I A L I Z A T I O N   //////////////////////////////////////////
    protected void initWithString( PSString aString ) {
        int len = aString.length();
        
        for ( int i = 0; i < len; i++ ) {
            PSChar aChar = aString.charAt( i );
            _array.add( aChar );
        }
    }
    
    protected void initWithString( String aString ) {
        int len = aString.length();
        
        for ( int i = 0; i < len; i++ ) {
            char aChar = aString.charAt( i );
            _addCharAt( aChar, i );
        }
    }
    
    protected void initWithChars( char[] chars ) {
        for ( int i = 0; i < chars.length; i++ ) {
            char aChar = chars[i];
            _addCharAt( aChar, i );
        }
    }
    
    ///   A C C E S S O R S   //////////////////////////////////////////////////
    
    public Vector array() { return _array; }
    
    public PSArray allChars() { return new PSArray( array() ); }
    
    ///   H A N D L I N G   C H A R S   ////////////////////////////////////////
    protected void _addCharAt( char aChar, int i ) {
        PSChar aPSChar = PSChar.with( (int )aChar );        
        _array.add( aPSChar );
    }
    
    protected void _addCharAt( PSChar aChar, PSInt anInt ) {
        _array.add( aChar );
    }        
    
    public int length() { return _array.size(); }
    public PSChar charAt( int i ) { return (PSChar )_array.get( i ); }
    public int charCodeAt( int i ) {
        return ( (PSChar )_array.get(i) )._charCode;
    }
    
    public PSString toUpperCase() {
        PSArrayString uCase = new PSArrayString( length() );
        
        for ( int i=0; i < length(); i++ ) {
            PSChar aChar = (PSChar )this.charAt(i);
            uCase._array.add( aChar._toUpperCase() );
        }
        
        return uCase;
    }
    
    public PSString toLowerCase() {
        PSArrayString lCase = new PSArrayString( length() );
        
        for ( int i=0; i < length(); i++ ) {
            PSChar aChar = (PSChar )this.charAt(i);
            lCase._array.add( aChar._toLowerCase() );
        }
        
        return lCase;
    }    
    
    public int indexOf( PSChar aChar, int offset ) {
        return _array.indexOf( aChar, offset );
    }
    
    /**
     * Here, offset is seen from the end of the string.
     */
    public int lastIndexOf( PSChar aChar, int offset ) {
        return _array.lastIndexOf( aChar, offset );
    }
    
    public static TranslateVisitor stringTranslateVisitor() {
        if ( _TranslateVisitor == null )
            _TranslateVisitor = new TranslateVisitor();
        else
            _TranslateVisitor.resetState();
        
        return _TranslateVisitor;
    }
    
    ///   N E S T E D   C L A S S E S   ////////////////////////////////////////
 
    public static class TranslateVisitor extends PSArray.TranslateVisitor {
        
        public TranslateVisitor() { this( null ); }
        
        public TranslateVisitor( String aKey ) {
            super( new PSArrayString(), aKey );
        }
        
        public void visitMe( Object aHost ) {
            PSTarget target = new PSTarget( aHost );
            Object returnedValue;
            
            if ( aHost instanceof PSChar ) {
                try {
                    returnedValue = target.performMethod( key() );
                    arrayResult().add( returnedValue );
                } catch (InvocationTargetException ex) {
                    this.logger().error( this, "Couln't invoque method: " + key() +
                            " Target class=" + aHost.getClass().getName() );
                }                
            }
        }
        
        public void visitMe( PSObject aHost ) {
            Object translation = aHost.valueForKey( key());
            arrayResult().add( translation );
        }
        
        public void resetState() {
            setResult( new PSArrayString() );
            setKey( null );
        }
        
    } ///   END TranslateVisitor.
     
}  ///  E N D   P S C H A R A R R A Y 
