/**
 * PSChar.java
 *
 * Created on 15 ao�t 2009, 22:08
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

public class PSChar extends PSString implements Comparable<Object> {
       
    private static PSBuffer _ClassBuffer = null;
    public static int MAX_UTF8_CODE = 256; // UTF-8 
    public static int MAX_UTF16_CODE = 65536; // UTF-16
    protected int _charCode = -1;
    private static PSChar[] _CharTable = null;
    private static PSDict _UTF16Cache = null;
    
    protected PSInt.Range _digitRange = null;
    protected PSInt.Range _letterRange = null;
    protected PSInt.Range _upperCaseRange = null;
    protected PSInt.Range _lowerCaseRange = null;    
    
    private PSChar( int charCode ) {
        _charCode = charCode;
    }
    
    private PSChar( char aChar ) {
        _charCode = (int )aChar;
    }
    
    // Got from Squeak Character class
    public static PSChar cr()     { return PSChar.with( 13  ); } // '\n'
    public static PSChar lf()     { return PSChar.with( 10  ); } // '\r'
    public static PSChar space()  { return PSChar.with( 32  ); }
    public static PSChar tab()    { return PSChar.with( 9   ); }
    public static PSChar dollar() { return PSChar.with( 36  ); }
    public static PSChar pound()  { return PSChar.with( 163 ); }
    public static PSChar euro()   { return PSChar.with( 219 ); }
    public static PSChar blank() { return PSChar.with( ' ' ); } //Same as space()
    
    /**
     * This method is used to return a fixed char: '�' everytime an invalid 
     * charCode is received.
     */
    private static PSChar error()  { return PSChar.with( 200 ); }
    
    public boolean isNonPrintable() {
        return equals( cr() ) || equals( lf() );
    }
    
    protected PSInt.Range digitRange() {
        if ( _digitRange == null ) {
            _digitRange = new PSInt.Range( 48, 57 );
        }
        return _digitRange;
    }

    protected PSInt.Range upperCaseRange() {
        if ( _upperCaseRange == null ) {
            _upperCaseRange = new PSInt.Range( 65, 90 );
        }
        return _upperCaseRange;
    }

    protected PSInt.Range lowerCaseRange() {
        if ( _lowerCaseRange == null ) {
            _lowerCaseRange = new PSInt.Range( 97, 122 );
        }
        return _lowerCaseRange;
    }
        
    public char asChar() { return (char )_charCode; }
    public int charCode() { return _charCode; }
    
    public static PSChar with( char aChar ) {
        return with( (int )aChar );
    }
    
    public static PSChar with( int charCode ) {
        PSChar aChar = null;
        
        if ( charCode < 0 || charCode > MAX_UTF16_CODE ) {
            logger().debug( "Invalid char code: " + charCode );
            return error();
        }
        if ( charCode < MAX_UTF8_CODE ) {
            aChar = _CharTable[charCode];
        } else {
            PSInt aKey = PSInt.with( charCode );
            aChar = (PSChar )utf16Cache().get( aKey );
            if ( aChar == null ) {
                aChar = new PSChar( charCode );
                utf16Cache().put( aKey, aChar );
            }
        }

        return aChar;
    }
    
    public static PSChar forDigit( int digit ) {
        if ( digit < 0 || digit > 9 )
            throw new IllegalArgumentException( "PSChar Invalid digit: " + digit );
        
        return PSChar.with( digit + 48 );
    }
    
    public int asDigit() {
        if ( ! isDigit() ) return -1;
        
        return charCode() - 48;
    }
    
    static {
        _initCharTable();
    }
    
    private static void _initCharTable() {
        _CharTable = new PSChar[MAX_UTF8_CODE];
        for ( int i = 0; i < MAX_UTF8_CODE; i++ ) {
            _CharTable[i] = new PSChar(i);
        }
    }    
    
    protected static PSChar[] charTable() {
        if (_CharTable == null) {
            _initCharTable();
        }
        return _CharTable;
    }
    
    private static PSDict utf16Cache() {
        if ( _UTF16Cache == null ) {
            _UTF16Cache = new PSDict();
        }
        
        return _UTF16Cache;
    }
    
     ///   H A N D L I N G   C H A R S   ////////////////////////////////////////
    public int length() { return 1; } 
    
    public PSChar charAt( int i ) {
        if ( i != 0 )
            throw new ArrayIndexOutOfBoundsException();
        else
            return this;
    }   
    
    public PSArray allChars() { return new PSArray( new Object[] { this } ); }
    
    public int charCodeAt( int i ) { return charCode(); }
    
    public PSString toLowerCase() {
        return (PSString )_toLowerCase();
    }
    
    public PSString toUpperCase() {
        return (PSString )_toUpperCase();
    }
    
     public int indexOf( PSChar aChar, int offset ) {
        if ( offset != 0 )
            throw new ArrayIndexOutOfBoundsException();
        else
            return ( equals( aChar ) ) ? 0 : -1;
    }
     
    public int lastIndexOf( PSChar aChar, int offset ) {
        return indexOf( aChar, offset );
    }
    ///   P R E D I C A T E S   /////////////////////////////////////
    public boolean isDigit()     { return digitRange().contains( charCode() ); }
    
    public PSChar _toLowerCase() {
        if ( isDelimiter() ) return this;
        if ( isUpperCase() )
            return PSChar.with( charCode() + 32 );
        else
            return this;
    }
    
    public PSChar _toUpperCase() {
        if ( isDelimiter() ) return this;
        if ( isLowerCase() )
            return PSChar.with( charCode() - 32 );
        else
            return this;
    }

    public boolean isStrictUpperCase() {
        return upperCaseRange().contains( charCode() );
    }

    public boolean isUpperCase() { 
        return isDelimiter() || isStrictUpperCase();
    }

    public boolean isStrictLowerCase() {
        return lowerCaseRange().contains( charCode() );
    }

    public boolean isLowerCase() { 
        return isDelimiter() || isStrictLowerCase();
    }
    
    public boolean isLetter()    { 
        return isUpperCase() || isLowerCase(); 
    }

    public boolean isStrictLetter()    {
        return isStrictUpperCase() || isStrictLowerCase();
    }
    
    public boolean isLineTerminator() { 
        return this.equals( lf() ) || this.equals( cr() );
    }
    
    public boolean isCurrency() {
        return equals( euro() ) || equals( pound() ) || equals( dollar() );
    }
    
    public boolean isAlphaNumeric() {
        return isLetter() || isDigit();
    }
    
    public boolean isSpace() {
        return equals( space() ) || equals( tab() );
    }
    
    public String toString() {
        PSBuffer buffer = classBuffer();
        
        buffer.append( (char)_charCode );        
        
        return buffer.toString();
    }
    
    public String toPrint() {
        if ( ! this.isNonPrintable() ) return toString();
        
        return "'c" + charCode() + "'";
    }
    
    private static PSBuffer classBuffer() {
        if ( _ClassBuffer == null ) {
            _ClassBuffer = new PSBuffer();
        } else {
            _ClassBuffer.clearContent();
        }
        
        return _ClassBuffer;
    }
    
    ///  C O M P A R I S O N   /////////////////////////////////////////////////
    public boolean equals( Object obj ) {
        return equals( (PSChar )obj );
    }
    
    public boolean equals( PSChar aChar ) {
        return this._charCode == aChar._charCode;
    }
    
    public boolean equals( char c ) {
        return _charCode == (int )c;
    }
    
    public int compareTo( PSChar another ) {
	return _charCode - another._charCode;
    }
    
    public int compareTo( Character another ) {
	return charCode() - another.charValue();
    }
    
    public int compareTo( char c ) {
	return _charCode - c;
    }    
    
    public int compareTo( Object o ) {
        if ( o instanceof PSChar )
            return compareTo( (PSChar )o );
        else
            return compareTo( (Character )o );
    }
        
    public int hashCode() {
        return charCode();
    }
    
    public int compareToIgnoreCase(PSChar c2) {
        PSChar c1 = this;
        
        if (c1 != c2) {
            c1 = c1._toUpperCase();
            c2 = c2._toUpperCase();
            if (c1 != c2) {
                c1 = c1._toLowerCase();
                c2 = c2._toLowerCase();
                if (c1 != c2) {
                    return c1.asChar() - c2.asChar();
                }
            }
        }
        
        return 0;
    }

    // a=1, b=2, ... , z=26
    public PSInt leEnumRank() {
        if ( ! isStrictLowerCase() ) return PSInt.zero();
        return PSInt.with( 1 + charCode() - 97 );
    }

    /// N E S T E D   C L A S S E S    //////////////////////////////////////////
    private static class UTF8Char extends PSChar {
        
        private int _utf8Code = -1;
        
        public UTF8Char( int utf8Code ) {
            super( utf8Code );
        }
    }
    
    private static class Set {
        
        
    }

    
}
