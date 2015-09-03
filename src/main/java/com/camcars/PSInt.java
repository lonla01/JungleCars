


/**
 * PSInt.java
 *
 * Created on 23 juin 2009, 15:35
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */
package com.camcars;

import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;

public class PSInt extends PSNumber implements PSRoot.Appending {
        
    private static final PSInt ZERO = PSInt.with(0);
    private static final PSInt ONE  = PSInt.with(1);
    private static PSBuffer _ClassBuffer = null;
    
    protected String  _toStringCache = null;
    protected int     _intValue  = -1;
    private int     _sign      = 1; // -1 is negative
    private Trio    _billions  = null;
    private Trio    _millions  = null;
    private Trio    _thousands = null;
    private Trio    _modulo    = null;
    
    private PSInt() { }
    
    public PSInt( Trio[] trios ) {
        setValues( trios );
        intValue();
    }
    
    public PSInt( int value ) {
        if ( value < 0 )
            PSObject.invalidArg( "Cannot create PSInt with: " + value );
        
        parseValues( value );
        intValue();
    }
    
    private PSInt( int value, int sign ) {
        parseValues( value );
        _sign = sign;
        intValue();
    }
    
    private void parseValues( int value ) {
        Trio[] values = new Trio[4];
        int v = value;
        int n;
        
        for ( int i = 3; i >= 1; i-- ) {
            n = v % 1000;  v = (v - n) / 1000;
            setValue( Trio.trio( n ), i );
            if ( v == 0 ) break;
        }
        if ( v > 0 ) {
            setValue( Trio.trio( v ), 0 );
        }
    }
    
    private Trio billions()  { return _billions;  }
    private Trio millions()  { return _millions;  }
    private Trio thousands() { return _thousands; }
    private Trio modulo()    { return _modulo;    }
    
    private void setBillions ( Trio t ) { _billions = t;  }
    private void setMillions ( Trio t ) { _millions = t;  }
    private void setThousands( Trio t ) { _thousands = t; }
    private void setModulo   ( Trio t ) { _modulo = t;    }
    
    private void setSign( int sign ) { _sign = sign; }
    
    private int _billions() {
        return (_billions == null) ? 0 :_billions._intValue;
    }
    
    private int _millions() {
        return (_millions == null) ? 0 : _millions._intValue;
    }
    
    private int _thousands() {
        return (_thousands == null) ? 0 : _thousands._intValue;
    }
    
    private int _modulo() {
        return (_modulo == null) ? 0 : _modulo._intValue;
    }
    
    private void setValues( Trio[] trios ) {
        int i = 0;
        
        if ( trios.length < 4 )
            PSObject.invalidArg( "PSInt: Can't create instance with: " + trios.length + " values" );
        
        setValue( trios[i], i++ );
        setValue( trios[i], i++ );
        setValue( trios[i], i++ );
        setValue( trios[i], i++ );
    }
    
    private void setValue( Trio trio, int position ) {
        if ( trio == null ) return;
        
        switch( position ) {
            case 0: _billions   = trio; break;
            case 1: _millions   = trio; break;
            case 2: _thousands = trio; break;
            case 3: _modulo   = trio; break;
        }
    }
    
    public static PSInt with( int n ) {
        return (n < 0) ? new PSInt( -n, -1 ) : new PSInt(n);
    }
    
    public static PSInt with( Object value ) {
        if ( value == null ) {
//            PSRoot.error( "PSInt: Cannot create PSInt from null" );
            return null;
        } else if ( value instanceof PSInt )
            return with( (PSInt )value );
        else if ( value instanceof PSString )
            return with( (PSString )value );
        else if ( value instanceof String )
            return with( (String )value );
        else {
            PSRoot.error( "PSInt: Unrecognized class for PSInt: " +
                    value.getClass().getName() );
            return null;
        }
    }

    public static PSInt with( PSInt value ) {
        return value;
    }
    
    public static PSInt with( String value ) {
        return parsePSInt( value );
    }
    
    public static PSInt with( PSString value ) {
        return parsePSInt( value );
    }
    
    public static PSArray intsWith( int[] ints ) {
        PSArray result = new PSArray( ints.length );
        
        for ( int i = 0; i < ints.length; i++ ) {
            result.add( PSInt.with( ints[i] ) );
        }
        
        return result;
    }
    
    ///////////////////   P U B L I C   I N T E R F A C E   /////////////////
    public static PSInt zero() { return ZERO; }
    public static PSInt one()  { return ONE; }
    
    public int val() { return _intValue; }   
    
    public int intValue() {
        if ( _intValue == -1 ) {
                _intValue = _billions() * 1000000000 + _millions() * 1000000
                        + _thousands() * 1000 + _modulo.intValue();
                _intValue *= _sign;          
        }
        
        return _intValue;
    }
      
    public static int parseInt( String s ) throws NumberFormatException {
        return parsePSInt(s).intValue();
    }
    
    public static int parseInt( PSString s ) throws NumberFormatException {
        return parsePSInt(s).intValue();
    }
    
    public static PSInt parsePSInt( String aString ) throws NumberFormatException {
        if ( aString == null ) return null;
        
        String str = aString;
        int len = 4;
        PSInt result = new PSInt();
        Tokenizer tokenizer;
        int length = str.length();
        
        if ( length == 0 )
            throw new NumberFormatException( "Empty string: " + str );
        if ( str.charAt(0) == '-' ) {
            result.setSign( -1 );
            str = str.substring( 1, str.length() );
        }
        if ( length == 0 || ( ! ( PSString.isAllDigit( str ) ) ) ) 
            throw new NumberFormatException( "Empty or non-digit: " + str );
        
        
        tokenizer = Tokenizer.sharedTokenizer( str );
        for ( int i = len-1; i >= 0 && tokenizer.hasMoreTrios() ; i-- ) {
            result.setValue( tokenizer.nextTrio(), i );
        }        
        result.intValue();
        
        return result;
    }
    
    public static PSInt parsePSInt( PSString aString ) throws NumberFormatException {
        if ( aString == null ) return null;
        
        PSString str = aString;
        int len = 4;
        PSInt result = new PSInt();
        Tokenizer tokenizer;
        
        if ( str.isBlank() )
            throw new NumberFormatException( "Empty string: " + str );
        if ( str._charAt(0) == '-' ) {
            result.setSign( -1 );
            str = str.subToken( 1, str.length() );
        }
        
        tokenizer = Tokenizer.sharedTokenizer( str );
        for ( int i = len-1; i >= 0 && tokenizer.hasMoreTrios() ; i-- ) {
            result.setValue( tokenizer.nextTrio(), i );
        }      
        
        if ( result == null )
            throw new NumberFormatException( "Invalid string: " + str );
        
        try {
            result.intValue();
        } catch (Exception e) {
                throw new NumberFormatException( "Invalid string: " + str );
        }
        
        return result;
    }
    
    public void appendTo( PSBuffer buffer ) {
        if ( isNegative() ) buffer.append( '-' );
        if ( _billions() != 0 )
            buffer.append( _intToString( null       , billions()  ) );
        if ( _millions() != 0 )
            buffer.append( _intToString( billions() , millions()  ) );
        if ( _thousands() != 0 )
            buffer.append( _intToString( millions() , thousands() ) );
        buffer.append( _intToString( thousands(), modulo()    ) );
    }
    
    /**
     * Returns a <code>String</code> object representing this
     * <code>Integer</code>'s value. The value is converted to signed
     * decimal representation and returned as a string, exactly as if
     * the integer value were given as an argument to the {@link
     * java.lang.Integer#toString(int)} method.
     *
     * @return  a string representation of the value of this object in
     *          base&nbsp;10.
     */
    protected String _toString() {
        PSBuffer buffer = classBuffer();
        
        appendTo( buffer );
        
        return buffer.toString();
    }
    
    public String toString() {
        if ( _toStringCache == null ) {
            _toStringCache = _toString();
        }
        
        return _toStringCache;
    }
    
    private String _intToString( Trio previous, Trio next ) {
        String retVal;
        
        if ( next == null ) return "";
        if ( previous != null && previous.intValue() != 0 ) {
            retVal = next.prependZeros();
        } else {
            retVal = next.toString();
        }
        
        return retVal;
    }
    
    public boolean isNegative() { return (_sign == -1); }
    
    private static PSBuffer classBuffer() {
        if ( _ClassBuffer == null ) {
            _ClassBuffer = new PSBuffer();
        } else {
            _ClassBuffer.clearContent();
        }
        
        return _ClassBuffer;
    }
    
    public static int compare( int i, int j ) {
        return (i < j) ? -1 : ( (i == j) ? 0 : 1 );
    }
    
    ///   S M A R T   M E T H O D S    ////////////////////////////////////////
    public PSInt squared() {
        return PSInt.with( _intValue * _intValue );
    }
    
    public PSInt mean( PSInt another ) {
        return PSInt.with( ( _intValue + another._intValue )/2 );
    }
    
    // TOFIX Some unknown problem with negate()
    public PSInt negated() {
        return (_intValue == 0) ? this : PSInt.with( -_intValue );
    }
    
    public PSInt max( PSInt that ) { return ( _intValue >= that._intValue ) ? this : that; }
    
    public PSInt incr() { return PSInt.with( _intValue + 1 ); }
    
    public PSInt decr() { return PSInt.with( _intValue - 1 ); }
    
    public PSInt add( PSInt another ) {
        return PSInt.with( _intValue + another._intValue );
    }
    
    public PSInt sub( PSInt another ) {
        return PSInt.with( _intValue - another._intValue );
    }
    
    public PSInt div( PSInt another ) {
        return PSInt.with( _intValue / another._intValue );
    }
    
    public PSInt mod( PSInt another ) {
        return PSInt.with( _intValue % another._intValue );
    }
    
    public boolean lt( PSInt another ) {
        return _intValue < another._intValue;
    }
    
    public boolean le( PSInt another ) {
        return _intValue <= another._intValue;
    }
    
    public boolean gt( PSInt another ) {
        return _intValue > another._intValue;
    }
    
    public boolean ge( PSInt another ) {
        return _intValue >= another._intValue;
    }
    
    ///  N E S T E D  C L A S S E S   //////////////////////////////////////////
    protected static class Trio extends PSInt {
        
        private static PSBuffer _ClassBuffer = null;
        private static final String[] ZEROS = new String[] { "", "0", "00" };
        private static Trio[]  _trioCache       = null;
        private static Map  _stringToInt = null;
        
        private PSChar _hundreds = null;
        private PSChar _tens = null;
        private PSChar _units = null;
        
//        private String _toStringCache = null;
        private PSString _toPSStringCache = null;
//        private int    _intValue      = -1;
        
        public Trio( int value ) {
            int v = value;
            int n;
            
            if ( v < 0 || v >= 1000 )
                PSObject.invalidArg( "Cannot create Trio with: " + value );
            
            for ( int i = 2; i >= 1; i-- ) {
                n = v % 10;  v = (v - n) / 10;
                setValue( PSChar.forDigit(n), i );
                if ( v == 0 ) break;
            }
            if ( v > 0 ) {
                setValue( PSChar.forDigit(v), 0 );
            }
            intValue();
        }
        
        public Trio( PSChar h, PSChar t, PSChar u ) {
            _hundreds = h;
            _tens = t;
            _units = u;
            intValue();
        }
        
        private static PSBuffer classBuffer() {            
            if ( _ClassBuffer == null ) {
                _ClassBuffer = new PSBuffer();
            } else {
                _ClassBuffer.clearContent();
            }
            
            return _ClassBuffer;
        }
        
        public void setValues( PSChar[] chars ) {
            int i = 0;
            
            if ( chars.length < 3 )
                PSObject.invalidArg( "Trio: Not enough values" );
            
            setValue( chars[i], i++ );
            setValue( chars[i], i++ );
            setValue( chars[i], i++ );
        }
        
        private void setValue( PSChar aChar, int position ) {
            if ( aChar == null ) return;
            if (! aChar.isDigit() )
                throw new NumberFormatException( "Non digit arg: " + aChar );
            
            switch( position ) {
                case 0: _hundreds = aChar; break;
                case 1: _tens     = aChar; break;
                case 2: _units    = aChar; break;
            }
        }
        
        public PSChar hundreds() { return _hundreds; }
        public PSChar tens()     { return _tens;     }
        public PSChar units()    { return _units;    }
        
        public int _hundreds()   {
            return (_hundreds == null) ? 0 : _hundreds.asDigit();
        }
        
        public int _tens()       {
            return (_tens == null) ? 0 : _tens.asDigit();
        }
        
        public int _units()      { return _units.asDigit();    }
        
        private void setHundreds( PSChar c ) { _hundreds = c; }
        private void setTens    ( PSChar c ) { _tens = c;     }
        private void setUnits   ( PSChar c ) { _units = c;    }
        
        public int intValue() {
            if ( _intValue == -1 ) {
                _intValue = _hundreds()*100 + _tens()*10 + _units();
            }
            
            return _intValue;
        }
        
        protected PSString _toPSString() {
            PSBuffer buffer = classBuffer();
            
            if ( _hundreds() != 0 )
                buffer.append( hundreds().asChar() );
            if ( _hundreds() != 0 || _tens() != 0 )
                buffer.append( tens().asChar() );
            buffer.append( units().asChar() );
            
            return buffer.copy();
        }
        
        public PSString toPSString() {
            if ( _toPSStringCache == null ) {
                _toPSStringCache = _toPSString();
            }
            
            return _toPSStringCache;
        }
        
        protected String _toString() {
            return toPSString().toString();
        }
        
        public String prependZeros() {            
            String str = toString(); // classBuffer() should be called after this !
            int len = str.length();
            PSBuffer buffer = classBuffer();
            
            buffer.append( ZEROS[3-len] ).append( str );
            
            return buffer.toString();
        }
        
        public PSString _prependZeros() {            
            PSString str = toPSString(); // classBuffer() should be called after this !
            int len = str.length();
            PSBuffer buffer = classBuffer();
            
            buffer.append( ZEROS[3-len] ).append( str );
            
            return buffer.copy();
        }
        
        public String toString() {
            if ( _toStringCache == null ) {
                _toStringCache = _toString();
            }
            
            return _toStringCache;
        }
        
        public static Trio parseTrio( char[] chars ) {
            return parseTrio( new String( chars ) );
        }
        
        public static Trio parseTrio( String aString ) {
            int len = aString.length();
            
            if ( len == 0 || len > 3 )
                PSObject.invalidArg( "Trio length is invalid: " + len );
            
            Trio trio = (Trio )_stringToInt.get( aString );
            
            return trio;
        }
        
        public static Trio parseTrio( PSString aString ) {
            int len = aString.length();
            
            if ( len == 0 || len > 3 )
                PSObject.invalidArg( "Trio length is invalid: " + len );
            
            Trio trio = (Trio )_stringToInt.get( aString );
            
            return trio;
        }
        
        public static int parseInt( String aString ) {
            Trio trio = (Trio )parseTrio( aString );
            
            return trio.intValue();
        }
        
        static {
            _initTrioCache();
            _initStringToInt();
        }
        
        private static void _initTrioCache() {
            _trioCache = new Trio [1000];
            for ( int i = 0; i < 1000; i++ ) {
                _trioCache[i] = new Trio(i);
            }
        }
        
        private static void _initStringToInt() {
            Trio trio;
            PSString toString;
            _stringToInt = new Hashtable(1100);
            
            for ( int i = 0; i < 1000; i++ ) {
                trio = _trioCache[i];
//                toString = trio.toString();
                toString = trio.toPSString();
                _stringToInt.put( toString, trio );
                if ( toString.length() < 3 ) {
                    toString = trio._prependZeros();
                    _stringToInt.put( toString, trio );
                }
            }
        }
        
        protected static Trio[] cache() {
            if (_trioCache == null) {
                _initTrioCache();
            }
            
            return _trioCache;
        }
        
        public static Trio trio( int i ) {
            return _trioCache[i];
        }
        
    } /// End Trio
    
    public static class Range extends PSObject {
        
        protected int _start;
        protected int _end;
        
        public Range( int start, int end ) {
            setStart( start );
            setEnd( end );
        }
        
        public Range( PSInt start, PSInt end ) {
            this( start.intValue(), end.intValue() );
        }
        
        public boolean contains( int element ) {
//            ensureIntegrity();
            return element >= intStart() && element <= intEnd();
        }
        
        public boolean contains( PSInt element ) {
            return contains( element.intValue() );
        }
        
        public PSInt integerStart() { return PSInt.with( intStart() ); }
        
        public PSInt integerEnd() { return PSInt .with( intEnd() ); }
        
        public int intStart() { return _start; }
        public void setStart( int start ) { _start = start; }
        
        public int intEnd() { return _end; }
        public void setEnd( int end ) { _end = end; }
        
        public int size() { 
            ensureIntegrity();
            return intEnd() - intStart() + 1; 
        }
        
        protected void ensureIntegrity() {
            if ( intStart() > intEnd() )
                throw new IllegalArgumentException(
                        "start=[" + intStart() + "] > end=[" + intStart() + "]" );
        }
        
        public String toString() {
            PSBuffer buffer = pool().bufferInstance();
            
            buffer.append("[").append(_start);
            buffer.append(" , ").append(_end).append("]");
            String result = buffer.toString();
            pool().recycleInstance( buffer );
            
            return result;
        }
        
    } /// End Range
    
    public static class Tokenizer extends PSObject {
        
        private static Tokenizer _SharedTokenizer  = null;
        private static Tokenizer _SharedSTokenizer = null;
        private static PSTokenizer.Token _SharedToken    = null;
        private int _currentPosition;
        private int _newPosition;
        private int _minPosition;
        private Object _string;
        
        public Tokenizer( Object str ) {
            resetState( str );
        }
        
        public void resetState( Object str ) {
            setString( str );            
            _newPosition = -1;
            _minPosition = 0;
        } 
              
        public void setString( Object str ) {
            _string = str; 
            if ( str instanceof String )
                _currentPosition = ( (String   )str ).length();
            else
                _currentPosition = ( (PSString )str ).length();
        }
        
        public PSString psString() { return (PSString )_string; }
        public String string() { return (String )_string; }
        
        public static Tokenizer sharedTokenizer( PSString str ) {
            if ( _SharedTokenizer == null ) {
                _SharedTokenizer = new Tokenizer( str );
            } else {
                _SharedTokenizer.resetState( str );
            }
            
            return _SharedTokenizer;
        }
        
        public static Tokenizer sharedTokenizer( String str ) {
            if ( _SharedSTokenizer == null ) {
                _SharedSTokenizer = new STokenizer( str );
            } else {
                _SharedSTokenizer.resetState( str );
            }
            
            return _SharedSTokenizer;
        }
        
        private static PSTokenizer.Token sharedToken( PSString string, int start, int end ) {
            if ( _SharedToken == null ) {
                _SharedToken = new PSTokenizer.Token( string, start, end );
            } else {
                _SharedToken.setString( string );
                _SharedToken.setStart( start );
                _SharedToken.setEnd( end );
            }
            
            return _SharedToken;
        }
        
        private int scanToken( int startPos ) {
            return (startPos >= 3) ? (startPos - 3) : _minPosition;
        }
        
        public boolean hasMoreTrios() {
        /*
         * Temporary store this position and use it in the following
         * nextToken() method only if the delimiters have'nt been changed in
         * that nextToken() invocation.
         */
            _newPosition = scanToken( _currentPosition );
            return ( _currentPosition - _newPosition > 0 );
        }
        
        protected Object nextToken() {
            int start, end;
            /**
             * If next position already computed in hasMoreElements() and
             * delimiters have changed between the computation and this invocation,
             * then use the computed value.
             */
            end = _currentPosition;
            _currentPosition = ( _newPosition >= 0 ) ?
                _newPosition : scanToken( _currentPosition );
            /* Reset these anyway */
            _newPosition = -1;
                                   
            start = _currentPosition;            
            
            return createToken( start, end );
        }
        
        protected Object createToken( int start, int end ) {            
            return sharedToken( psString(), start, end );            
        }        
        
        public Trio nextTrio() {
            return Trio.parseTrio( (PSString )nextToken() );
        }
                
        /**
         * Calculates the number of times that this tokenizer's
         * <code>nextToken</code> method can be called before it generates an
         * exception. The current position is not advanced.
         *
         * @return  the number of tokens remaining in the string using the current
         *          delimiter set.
         * @see     java.util.StringTokenizer#nextToken()
         */
        public int countTokens() {
            int count = 0;
            int currpos = _currentPosition;
            while ( currpos >= _minPosition ) {
                currpos = scanToken( currpos );
                count++;
            }
            
            return count;
        }
        
    } /// End Tokenizer
    
    public static class STokenizer extends Tokenizer {
        
        public STokenizer( String str ) {
            super( str );
        }
        
        protected Object createToken( int start, int end ) {
            return string().substring( start, end );
        }
        
        public Trio nextTrio() {
            return Trio.parseTrio( (String )nextToken() );
        }
    }
    
} /// End PSInt
