/**
 * PSTokenizer.java
 *
 * Created on 13 ao�t 2009, 20:53
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

import java.util.NoSuchElementException;
//import java.util.StringTokenizer;

public class PSTokenizer extends PSObject {
    
    private int _currentPosition;
    private int _newPosition;
    private int _maxPosition;
    private PSString _string;
    private PSString _delimiters;
    private boolean _returnDelims;
    private boolean _delimsChanged;
    
    
    /**
     * maxDelimChar stores the value of the delimiter character with the
     * highest value. It is used to optimize the detection of delimiter
     * characters.
     */
    private PSChar _maxDelimChar;
    
    
    
    /**
     * Constructs a string tokenizer for the specified string. All
     * characters in the <code>delim</code> argument are the delimiters
     * for separating tokens.
     * <p>
     * If the <code>returnDelims</code> flag is <code>true</code>, then
     * the delimiter characters are also returned as tokens. Each
     * delimiter is returned as a string of length one. If the flag is
     * <code>false</code>, the delimiter characters are skipped and only
     * serve as separators between tokens.
     * <p>
     * Note that if <tt>delim</tt> is <tt>null</tt>, this constructor does
     * not throw an exception. However, trying to invoke other methods on the
     * resulting <tt>StringTokenizer</tt> may result in a
     * <tt>NullPointerException</tt>.
     *
     * @param   str            a string to be parsed.
     * @param   delim          the delimiters.
     * @param   returnDelims   flag indicating whether to return the delimiters
     *                         as tokens.
     */
    public PSTokenizer( PSString str, PSString delim, boolean returnDelims ) {
        resetState( str, delim, returnDelims );
    }
    
    public PSTokenizer( String str, String delim, boolean returnDelims ) {
        this( PSString.with( str ), PSString.with( delim ), returnDelims );
    }
    
    /**
     * Constructs a string tokenizer for the specified string. The
     * characters in the <code>delim</code> argument are the delimiters
     * for separating tokens. Delimiter characters themselves will not
     * be treated as tokens.
     *
     * @param   str     a string to be parsed.
     * @param   delim   the delimiters.
     */
    public PSTokenizer( PSString str, PSString delim ) {
        this(str, delim, false);
    }
    
    /**
     * Constructs a string tokenizer for the specified string. The
     * tokenizer uses the default delimiter set, which is
     * <code>"&nbsp;&#92;t&#92;n&#92;r&#92;f"</code>: the space character,
     * the tab character, the newline character, the carriage-return character,
     * and the form-feed character. Delimiter characters themselves will
     * not be treated as tokens.
     *
     * @param   str   a string to be parsed.
     */
    public PSTokenizer( PSString str ) {
        this( str, new PSArrayString( " \t\n\r\f" ), false );
    }
    
    public PSTokenizer() {
        this( new PSArrayString("") );
    }
    
    public void setPSString( PSString str ) {
        _string = str;
        _maxPosition = str.length();
    }
    
    public void setDelimiters( PSString delim ) {
        _delimiters = delim;
        setMaxDelimChar();
    }
    
    /**
     * Set maxDelimChar to the highest char in the delimiter set.
     */
    private void setMaxDelimChar() {
        if (_delimiters == null) {
            _maxDelimChar = PSChar.with(0);
            return;
        }
        
        PSChar m = PSChar.with(0);
        for ( int i = 0; i < _delimiters.length(); i++ ) {
            PSChar c = _delimiters.charAt(i);
            if ( m.asChar() < c.asChar() ) m = c;
        }
        _maxDelimChar = m;
    }
    
    public void setReturnDelimiters( boolean bool ) {
        _returnDelims = bool;
    }
    
    protected void resetPositions() {
        _currentPosition = 0;
        _newPosition = -1;
    }
    
    protected void resetDelimsChanged() {
        _delimsChanged = false;
    }
    
    public void resetState( PSString str, PSString delim, boolean returnDelims ) {
        resetPositions();
        resetDelimsChanged();
        setPSString( str );
        setDelimiters( delim );
        setReturnDelimiters( returnDelims );
    }        
    
    /**
     * Skips delimiters starting from the specified position. If retDelims
     * is false, returns the index of the first non-delimiter character at or
     * after startPos. If retDelims is true, startPos is returned.
     */
    private int skipDelimiters(int startPos) {
        if (_delimiters == null)
            throw new NullPointerException();
        
        int position = startPos;
        while ( !_returnDelims && position < _maxPosition ) {
            PSChar c = _string.charAt(position);
            if ( ( c.charCode() > _maxDelimChar.charCode() ) ||
                    ( ! _delimiters.containsChar(c) ) )
                break;
            position++;
        }
        return position;
    }
    
    /**
     * Skips ahead from startPos and returns the index of the next delimiter
     * character encountered, or maxPosition if no such delimiter is found.
     */
    private int scanToken(int startPos) {
        int position = startPos;
        
        while ( position < _maxPosition ) {
            PSChar c = _string.charAt( position );
            if ( ( c.charCode() <= _maxDelimChar.charCode() ) &&
                    ( _delimiters.containsChar(c) ) )
                break;
            position++;
        }
        if ( _returnDelims && ( startPos == position ) ) {
            PSChar c = _string.charAt( position );
            if ( ( c.charCode() <= _maxDelimChar.charCode() ) &&
                    ( _delimiters.containsChar(c) ) )
                position++;
        }
        
        return position;
    }
    
    /**
     * Tests if there are more tokens available from this tokenizer's string.
     * If this method returns <tt>true</tt>, then a subsequent call to
     * <tt>nextToken</tt> with no argument will successfully return a token.
     *
     * @return  <code>true</code> if and only if there is at least one token
     *          in the string after the current position; <code>false</code>
     *          otherwise.
     */
    public boolean hasMoreTokens() {
        /*
         * Temporary store this position and use it in the following
         * nextToken() method only if the delimiters have'nt been changed in
         * that nextToken() invocation.
         */
        _newPosition = skipDelimiters( _currentPosition );
        return ( _newPosition < _maxPosition );
    }
    
//    public String nextToken() {
//        return _nextToken().toString();
//    }
    
    /**
     * Returns the next token from this string tokenizer.
     *
     * @return     the next token from this string tokenizer.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     */
    public Token nextToken() {
        /**
         * If next position already computed in hasMoreElements() and
         * delimiters have changed between the computation and this invocation,
         * then use the computed value.
         */
        
        _currentPosition = ( _newPosition >= 0 && !_delimsChanged ) ?
            _newPosition : skipDelimiters( _currentPosition );
        /* Reset these anyway */
        _delimsChanged = false;
        _newPosition = -1;
        
        if ( _currentPosition >= _maxPosition )
            throw new NoSuchElementException();
        
        int start = _currentPosition;        
        _currentPosition = scanToken( _currentPosition );
        int end = _currentPosition;        
        Token token = Token.sharedToken( _string, start, end );
        
        return token;
    }
    
    
    /**
     * Returns the next token in this string tokenizer's string. First,
     * the set of characters considered to be delimiters by this
     * <tt>StringTokenizer</tt> object is changed to be the characters in
     * the string <tt>delim</tt>. Then the next token in the string
     * after the current position is returned. The current position is
     * advanced beyond the recognized token.  The new delimiter set
     * remains the default after this call.
     *
     * @param      delim   the new delimiters.
     * @return     the next token, after switching to the new delimiter set.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     */
    public Token nextToken( PSString delim ) {
        _delimiters = delim;
        
        /* delimiter string specified, so set the appropriate flag. */
        _delimsChanged = true;        
        setMaxDelimChar();
        
        return nextToken();
    }
    
    /**
     * Returns the same value as the <code>hasMoreTokens</code>
     * method. It exists so that this class can implement the
     * <code>Enumeration</code> interface.
     *
     * @return  <code>true</code> if there are more tokens;
     *          <code>false</code> otherwise.
     * @see     java.util.Enumeration
     * @see     java.util.StringTokenizer#hasMoreTokens()
     */
    public boolean hasMoreElements() {
        return hasMoreTokens();
    }
    
    /**
     * Returns the same value as the <code>nextToken</code> method,
     * except that its declared return value is <code>Object</code> rather than
     * <code>String</code>. It exists so that this class can implement the
     * <code>Enumeration</code> interface.
     *
     * @return     the next token in the string.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     * @see        java.util.Enumeration
     * @see        java.util.StringTokenizer#nextToken()
     */
    public Object nextElement() {
        return nextToken();
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
        while ( currpos < _maxPosition ) {
            currpos = skipDelimiters( currpos );
            if ( currpos >= _maxPosition )
                break;
            currpos = scanToken( currpos );
            count++;
        }
        
        return count;
    }
    
    ///   N E S T E D   C L A S S E S   ////////////////////////////////////////
    public static class Token extends PSString {

        private PSString _string;
        private PSInt.Range _range = null;
        
        private static Token _SharedToken = null;
        private static PSArrayString.TranslateVisitor _TranslateVisitor = null;
        
        public Token( PSString string ) {
            this( string, 0, 0 );
        }
        
        public Token( PSString string, int start, int end ) {
            super();
            setRange( new PSInt.Range( start, end ) );
            setString( string );
        }
        
        public PSInt.Range range()  { return _range;                 }
        public void setRange( PSInt.Range range ) { _range = range; }
        
        public int intStart()       { return _range._start;     }        
        public int intEnd()         { return _range._end;       }               
        
        public void setStart(int start) {
           _range._start = start;
           resetCache();
        }

        public void setEnd(int end) {
            range().setEnd( end );
            resetCache();
        }
        
        public boolean contains( int element ) {
            return range().contains( element );
        }
        
        protected PSString string() { return _string; }
        protected void setString( PSString string ) { 
            _string = string; 
            resetCache();
            
//            if ( _string instanceof PSBuffer )
//                logger().warn( this, "PSBuffer found in token" );
        }
        
        /**
         * The arg i is the index inside the supporting PSString. The method 
         * returns the index relative to this Token.
         */
        protected int relativeIndex( int i ) { return i - _range._start;   }  
        
        /**
         * The arg i is the index in this token. The returned value is the index 
         * in the supporting PSString.
         */
        protected int absoluteIndex( int i ) { return i + _range._start;   }

        // TODO: CHECK THIS METHOD
        protected int absEndOffset  ( int i ) { return intEnd() - 1 - i; }
        
        /// PSString interface
        /// This method is called very very often. This is why we access the 
        /// inst-var directly. 
        public PSChar charAt( int i ) {
            return _string.charAt( _range._start + i );
        }
        
        public PSArray allChars() {
            PSArray allChars = new PSArray();
            
            for ( int i=0; i < length(); i++ ) {
                allChars.add( charAt( i ) );
            }
            
            return allChars;
        }
        
        public int charCodeAt( int i ) {
            return _string.charAt( _range._start + i )._charCode;
        }
        
        public char  _charAt( int i ) {
            return charAt(i).asChar();
        }
        
        public int indexOf( PSChar aChar ) {
            int i = string().indexOf( aChar, intStart() );
            
            return ( contains(i) ) ? relativeIndex(i) : -1;
        }
        
        public int indexOf( PSChar aChar, int o ) {
            int i = string().indexOf( aChar, absoluteIndex( o ) );
            
            return ( contains(i) ) ? relativeIndex(i) : -1;
        }
        
        public boolean containsChar( PSChar aChar ) {
            return contains( string().indexOf( aChar ) );
        }
        
        /** 
         * In this class the ending index is EXCLUSIVE. This is why we substract one.
         */
        public int lastIndexOf( PSChar aChar ) {
             int i = string().lastIndexOf( aChar, intEnd() - 1 );
             
             return ( contains(i) ? relativeIndex(i) : -1 );
        }
        
        public int lastIndexOf( PSChar aChar, int o ) {
             int i = string().lastIndexOf( aChar, absEndOffset( o ) );
             
             return ( contains(i) ? relativeIndex(i) : -1 );
        }
        
        public int length() { return ( _range._end - _range._start ); }
        
        public String toString() {
            if ( _toStringCache == null ) {
                _toStringCache = _toString();
            } 
            
            return _toStringCache;
        }
                
        ///   C O M P A R I S O N S   //////////////////////////////////////////
        
        /**
         * Unlike PSString, this class do NOT cache the hashCode. The reason
         * is that since its purpose is to transport a changing portion of another
         * string, the same hashCode is unlikely to be accessed twice. Instead, 
         * the content of the token is likely to be different between two successive
         * invocations.
         */
        public int hashCode() {
            if ( _hashCache == -1 ) {
                _hashCache = _hashCode();
            }
            
            return _hashCache;
        }               
        
        public PSString substring( int start, int end ) {
            int count = end - start;
            PSArrayString substring = new PSArrayString( end - start );
            
            for ( PSInt i = PSInt.with( start ); i.val() < end; i = i.incr() ) {
                PSChar aChar = charAt( i.val() );
                substring._addCharAt( aChar, i );
            }
            
            return (PSString )substring;
        }
        
        public PSString subToken( int start, int end ) {
            return sharedToken( _string, start + _range._start, end + _range._start );
        }

        public PSString toUpperCase() {
            PSString str =  string().substring( intStart(), intEnd() );
            
            return new Token( str.toUpperCase(), 0, length() );
        }
        
        public PSString toLowerCase() {
            PSString str =  string().substring( intStart(), intEnd() );
            
            return new Token( str.toLowerCase(), 0, length() );
        }
        
        public static Token sharedToken( PSString string ) {
            if ( _SharedToken == null ) {
                _SharedToken = new Token( string );
            } else {
                _SharedToken.setString( string );
                _SharedToken.setStart( 0 );
                _SharedToken.setEnd( 0 );
            }
            
            return _SharedToken;
        }
        
        public static Token sharedToken( PSString string, int start, int end ) {
            if ( _SharedToken == null ) {
                _SharedToken = new Token( string, start, end );
            } else {
                _SharedToken.setString( string );
                _SharedToken.setStart( start );
                _SharedToken.setEnd( end );
            }
            
            return _SharedToken;
        }
        
        public static PSArrayString.TranslateVisitor translateVisitor( Token t ) {
            if ( _TranslateVisitor == null ) {
                _TranslateVisitor = new PSArrayString.TranslateVisitor();
                _TranslateVisitor.setResult( new PSArray( t.length() ) );
            } else
                _TranslateVisitor.resetState();
            
            return _TranslateVisitor;
        }
               
    } ///   End PSTokenier.Token
    
} /// End Tokenizer
