
/**
 * PSBuffer.java
 *
 * Created on 28 ao�t 2009, 13:41
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */
package com.camcars;

public class PSBuffer extends PSArrayString {
    
    boolean _verbose = false;

    public PSBuffer() { super(); }
    
    protected PSBuffer( int capacity ) {
        super( capacity );
    }

    public PSBuffer( String aString ) {
        super( aString );
    }
    
    public PSBuffer( PSString aString ) {
        this();
        append( aString );
    }
    
    public PSBuffer( char[] chars ) {
        super( chars );
    }
    
    protected void setCharAt( char aChar, int index ) {
        PSChar aPSChar = PSChar.with( aChar );
        array().set( index, aPSChar );
    }
    
    protected void setCharAt( PSChar aPSChar, int index ) {
        array().set( index, aPSChar );
    }
            
    /// E D I T I N G   ////////////////////////////////////////////////////////
    public void insert( int index, PSChar aChar ) {
        _array.insertElementAt( aChar, index );
    }
    
//    public void insert( int index, String str ) {
//        long t1 = 0, t2 = 0;
//        int len = str.length();
//        
//        if ( str.length() > 360000 ) {
//            _verbose = true;
//            t1 = System.currentTimeMillis();
//            array().ensureCapacity( len );
//            t2 = System.currentTimeMillis();
//            PSObject.logger().trace( this, "ensureCapacity: in " + (t2-t1) + "ms" );
//        }
//                
//        for ( int i = len-1; i >= 0 ; i-- ) {                  
//            PSChar aChar = PSChar.with( str.charAt(i) );
//            insert( index, aChar );
//            if ( _verbose && ( (i % 10000) == 0 ) ) {
//                t2 = System.currentTimeMillis();
//                PSObject.logger().trace( this, "Inserted: " + i + " chars in " +
//                        (t2-t1) + "ms" );
//                t1 = System.currentTimeMillis();
//            }
//        }
//    }
    
    public void insert( int index, String str ) {
        long t1 = 0, t2 = 0;
        int i=0;
        int len = str.length();
        PSString aCopy = copy();
        
//        if ( str.length() > 360000 ) {
//            _verbose = true;            
//        }
        PSAssert.asserter().assertTrue( "String Index out of bounds: " +
                index + " > " + length(), index <= length() );
        this.clearContent();
        for ( i = 0; i < index; i++ ) {
            PSChar aChar = (PSChar )aCopy.charAt( i );
            _array.add( aChar );
        }
        for ( i = index; i < index + len; i++ ) {
            PSChar aChar = PSChar.with( str.charAt( i - index ) );
            _array.add( aChar );
            if ( _verbose && ( (i % 10000) == 0 ) ) {
                t2 = System.currentTimeMillis();
                PSObject.logger().trace( this, "Inserted: " + i + " chars in " +
                        (t2-t1) + "ms" );
                t1 = System.currentTimeMillis();
            }
        }
        for ( i = index; i < aCopy.length(); i++ ) {
            PSChar aChar = (PSChar )aCopy.charAt( i );
            _array.add( aChar );
        }        
    }
    
    ///   D E L E T I N G   ////////////////////////////////////////////////////
    public PSBuffer delete( int start, int end ) {
	for ( int i = end - 1; i >= start ; i-- ) {
            array().remove( i );
        }
        
        return this;
    }
    
    public PSBuffer deleteCharAt( int index ) {
        array().remove( index );
        
        return this;
    }
    
    public PSBuffer clearContent() {
        array().clear();
        
        return this;
    }
    
    public String toString() {              
        int len = length();
        char[] chars = new char[ len ]; 
        
        for ( int i=0; i < len; i++ ) {
            PSChar aChar = charAt( i );
            chars[i] = aChar.asChar();
        }
        
        return new String( chars );
    }
       
//    public String toString() {              
//        int len = length();
//        StringBuffer buffer =  new StringBuffer();
//        
//        for ( int i=0; i < len; i++ ) {
//            PSChar aChar = charAt( i );
//            buffer.append( aChar.asChar() );
//        }
//        
//        return new String( buffer );
//    }
    
    ///   A P P E N D I N G   //////////////////////////////////////////////////
    public PSBuffer append( PSChar aChar ) {
        _array.add( aChar );
        
        return this;
    }
    
    public PSBuffer append( int i ) {
        PSInt anInt = PSInt.with(i);
        
        anInt.appendTo( this );
        
        return this;
    }        
    
    public PSBuffer append( char aChar ) { 
//        if ( (int )aChar > PSChar.MAX_UTF8_CODE ) {
////            System.out.println( "Invalid char: " + aChar + " Code=" + (int )aChar );
//            append( PSChar.error() );
//            return this;
//        }
                        
        PSChar aPSChar = PSChar.with( (int )aChar );        
        array().add( aPSChar );
        
        return this;
    }
    
    public PSBuffer append( boolean b ) { 
        if ( b ) {
            append( "true" );
        } else {
            append( "false" );
        }
        
        return this;
    }
    
    public PSBuffer append( String str ) {
//        try {
            append( str.toCharArray() );
//        } catch ( Throwable t ) {
//            PSObject.logger().error( this, "Error appending str[" + str + "]" +
//                    " len=" + length() );
//        }
        
        return this;
    }
    
    public PSBuffer append( Object obj ) {
        if ( obj == null )
            append( "null" );
        else
            append( obj.toString() );
        
        return this;
    }
    
    public PSBuffer append( StringBuffer sb ) {
        append( sb.toString() );
        
        return this;
    }
    
    public PSBuffer append( PSBuffer sb ) {
        for ( int i=0; i < sb.length(); i++ ) {
            PSChar aChar = (PSChar )sb.charAt(i);
            append( aChar );
        }
        
        return this;
    }
    
    public PSBuffer append( char str[] ) {
        for ( int i=0; i < str.length; i++ ) {
            append( str[i] );
        }
        
        return this;
    }
    
}
