/**
 * PSIndexedString.java
 *
 * Created on 28 ao�t 2009, 13:24
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

public class PSIndexedString extends PSArrayString {
    
    private PSDict.StringIndex _charDict;
    
    public PSIndexedString() {
    }    
    
    protected PSIndexedString( int capacity ) {
        super( capacity );
    }

    public PSIndexedString( String aString ) {
        this( (aString == null) ? 10 : aString.length() );
        if ( aString != null )
            initWithString( aString );
    }
    
    public PSIndexedString( char[] chars ) {
        this( chars.length );
        initWithChars( chars );
    }
    
    protected void initWithString( String aString ) {
        initCharDict( aString );
        super.initWithString( aString );
    }
    
    protected void initWithChars( char[] chars ) {
        initCharDict( chars );
        super.initWithChars( chars );
    }
    
    private void initCharDict( String aString ) {
        _charDict = new PSDict.StringIndex( aString );
    }
    
    private void initCharDict( char[] chars ) {
        _charDict = new PSDict.StringIndex( chars );
    }
    
    protected void _addCharAt( char aChar, int index ) {
        PSChar aPSChar = PSChar.with( (int )aChar ); 
        
        super._addCharAt( aChar, index );
        addIndexEntry( aPSChar, index );
    }
    
    protected void _addCharAt( PSChar aChar, PSInt anInt ) {
        super._addCharAt( aChar, anInt );
        addIndexEntry( aChar, anInt.val() );
    }
    
    protected PSDict.IMap charDict() {
        PSDict.IMap result = new PSDict.IMap();
        
        for ( int i=0; i < PSChar.MAX_UTF8_CODE; i++ ) {
            PSArray array = _charDict.allIndices( PSChar.with( i ) );
            if ( array == null || array.isEmpty() ) continue;
            result.put( PSInt.with( i ), array );
        }
        
        return result;
    }
    
    protected void addIndexEntry( PSChar aChar, int anIndex ) {
        _charDict.put( aChar, anIndex );
    }
    
    protected int[] charIndices( PSChar aChar ) {
        return _charDict.allIndicesArray( aChar );
    }
    
    public int indexOf( PSChar aChar, int offset ) {
        int result = -1;
        int[] indices = charIndices( aChar );
        
        if ( indices == null ) return -1;
        for ( int i=0; i < indices.length; i++ ) {
            int anInt = indices[i];
            result = anInt;
            if ( result >= offset ) break;
        }
                
        return result;
    }
    
    /**
     * Here, offset is seen from the end of the string.
     * 
     */ 
    public int lastIndexOf( PSChar aChar, int offset ) {
        int result = -1;
        int[] indices = charIndices( aChar );
        
        for ( int i = indices.length - 1; i >= 0; i-- ) {
            result = indices[i];
            if ( result <= offset ) break;
        }
        
        return result;
    }
}
