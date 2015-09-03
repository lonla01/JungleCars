package com.camcars;

import java.util.Vector;
import java.util.Comparator;
/**
 * PSString.java
 *
 * Created on 18 ao�t 2009, 22:26
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

public abstract class PSString extends PSRoot implements Comparable<Object>, PSRoot.Appending {
    
    protected int    _hashCache     = -1;
    protected String _toStringCache = null;
        
    public static final String NEW_LINES = "\t\n\r\f";
    public static final String DELIMITERS = " -()[]{}.,:;";
    public static final PSString EmptyPSString = PSString.with( "" );
    private static CaseInsensitiveComparator _CaseInsensitiveComparator = null;
    private static PSTokenizer.Token _SharedToken = null;
    private static PSBuffer _ClassBuffer = null;
    private static PSTokenizer _ClassTokenizer = null;
    private PSRomanNumRecognizer _recognizer = null;
    
    protected void resetCache() {
        _toStringCache = null;
        _hashCache = -1;
    }
    
    public static PSLogger logger() { return PSObject.logger(); }

    public PSRomanNumRecognizer recognizer() {
            if (_recognizer == null) {
                _recognizer = new PSRomanNumRecognizer();
            }

            return _recognizer;
        }

    ///   H A N D L I N G   C H A R S   ////////////////////////////////////////
    public abstract int length();
    
    public abstract PSChar charAt( int i );
    
    public PSArray allChars() {
        logger().error( this, "AbstractMethodInvocation: allChars()" +
                " class=" + getClass().getName() );
        
        return PSArray.EmptyArray;
    }
    
    public char _charAt  ( int i ) { return charAt( i ).asChar(); }
    
    public abstract int charCodeAt( int i );

    // Defined in PSRoot
    public void takePropertyDict( PSDict dict ) { }
            
    ///   S T R I N G   M A N I P U L A T I O N   ///////////////////////////////
    public abstract PSString toUpperCase();
    
    public abstract PSString toLowerCase();    
    
    public PSString substring( int start, int end ) {
        int count = end - start;
        char[] chars = new char[ count ];
        
        for ( int i = start; i < end; i++ ) {
            PSChar aChar = charAt( i );
            chars[i - start] = aChar.asChar();
        }
        
        return new PSArrayString( chars );
    }
    
    /**
     * WARNING: We should not use the _ClassBuffer in this method because, toString()
     * uses that as well. And toString() is being called inside the append().
     * So that both calls interfere.
     */
    
    public PSString concat( PSString anoStr ) {
        PSBuffer buffer = PSObject.pool().bufferInstance();
        
        buffer.append( this ); 
        buffer.append( anoStr );        
        
        PSString result = buffer.copy();
        PSObject.pool().recycleInstance( buffer );
        
        return result;
    }
    
    public PSString subToken( int start, int end ) {
        return sharedToken( this, start, end );
    }
    
    public PSString trim() {
        int len = length();
        int start = 0;
        
        while ( ( start < len ) && ( this._charAt( start ) <= ' ' ) ) {
            start++;
        }
        while ( ( start < len ) && ( this._charAt( len - 1 ) <= ' ' ) ) {
            len--;
        }
        
        return ( (start > 0) || (len < length()) ) ?
            substring( start, len ) : this;
    }

    public PSString trimTail( PSString aTail ) {
        int end_1, end_2;
        int len_1 = length();
        int len_2 = aTail.length();
        

        if ( len_2 > len_1 ) {
            error( this, "Tail too long: " + aTail.toPrint() );
            return this;
        }
        if ( ! endsWith( aTail ) ) {
            error( this, "Wrong tail of string: " + aTail.toPrint() );
            return this;
        }
        for ( end_1 = len_1; end_1 > len_1 - len_2; end_1-- ) {
            PSChar aChar = charAt( end_1-1 );
            end_2 = (end_1 - 1) - (len_1 - len_2); 
            PSChar tailChar = aTail.charAt( end_2 );
            if ( ! aChar.equals( tailChar ) ) break;
        }
        return substring( 0, end_1 );
    }
    
    public PSString trimDelimiters() {
        int len = length();
        int start = 0;
        
        while ( ( start < len ) && ( charAt( start ).isDelimiter() ) ) {
            start++;
        }
        while ( ( start < len ) && ( charAt( len - 1 ).isDelimiter() ) ) {
            len--;
        }
        
        return ( (start > 0) || (len < length()) ) ?
            substring( start, len ) : this;
    } 
    
    public PSString stripDelimiters() {
        PSString stripped;
        int len = length();
        PSBuffer buffer = pool().bufferInstance();
        
        for ( int i=0; i < len; i++ ) {
            PSChar aChar = charAt( i );
            if ( ! aChar.isDelimiter() )
                buffer.append( aChar );
        }
            
        stripped = buffer.copy();
        pool().recycleInstance( buffer );
        
        return stripped;
    }
    
    /**
     * Trim blanks around a given char.
     */
    protected PSString trimBlanksArroundChar( PSString aChar ) {
        PSArray parts = splitWithDelimiters( aChar, false );
        
        parts = parts.collectWithKey( "trim" );
        PSString result = parts.appendComponents( aChar );       
        
        return result;
    }
    
    /**
     * Trim blanks around the range separator ('-')
     */
    protected PSString __trimBlanksArroundMinus() {
        PSArray parts = splitWithDelimiters( PSVerse.RANGE_SEPARATOR, false );
        
        parts = parts.collectWithKey( "trim" );
        PSString result = parts.appendComponents( PSVerse.RANGE_SEPARATOR );       
        
        return result;
    }

    protected PSString trimAllBlanks() {
        PSArray parts = splitWithDelimiters( PSChar.space(), false );

        parts = parts.collectWithKey( "trim" );
        PSString result = parts.appendComponents( emptyString() );

        return result;
    }

    public static String trimAllBlanks( String aString ) {
        PSString psString = PSString.with( aString );
        PSString trimmed = psString.trimAllBlanks();

        return trimmed.toString();
    }
    
    /**
     * Trim blanks around the range separator (':')
     */
    protected PSString __trimBlanksArroundColon() {
        PSArray parts = splitWithDelimiters( PSVerse.CHAP_SEPARATOR, false );
        
        parts = parts.collectWithKey( "trim" );
        PSString result = parts.appendComponents( PSVerse.CHAP_SEPARATOR );       
        
        return result;
    }
    
    /**
     * Trim blanks around the range separator (',')
     */
    protected PSString __trimBlanksArroundComma() {
        PSArray parts = splitWithDelimiters( PSVerse.VERSE_SEPARATOR, false );
        
        parts = parts.collectWithKey( "trim" );
        PSString result = parts.appendComponents( PSVerse.VERSE_SEPARATOR );       
        
        return result;
    }
    
    public PSString prepateForParsing() {
        PSString result, result1, result2, result3, result4;
        
        result = trimDelimiters();
        result1 = result.trimBlanksArroundChar( PSChar.with("-") );
        result2 = result1.trimBlanksArroundChar( PSChar.with(":") );
        result3 = result2.trimBlanksArroundChar( PSChar.with(",") );
        result4 = result3.trimBlanksArroundChar( PSChar.with(" ") );
                
        return result4;
    }
            
    public int lastLetterIndex() {
        int len = length();
        int start = 0;
        
        while ( ( start < len ) && ( charAt( start ).isStrictLetter() ) ) {
            start++;
        }
        
        // start is the number of leading letters.
        // So the last letter index = start - 1.
        return start - 1 ;
    }
    
    /**
     * Turns:
     *     1. - "1 Chr15:25"  into "1 Chr 15:25"  and
     *     2. - "2 Ki9:10"    into "2 Ki9:10"     and
     *     3. - "Num33:34,35" into "Num 33:34,35"
     */
    public PSString insertBookSeparator() {
        PSString result = this;
        PSString one = PSString.with( "1" );
        PSString two = PSString.with( "2" );
        PSString one_ = PSString.with( "1 " );
        PSString two_ = PSString.with( "2 " );
        PSString[] strings = new PSString[] { one_, two_, one, two  };
        PSString substring, leading;
        int len = length();
        int prefixLen;
        PSBuffer buffer = PSObject.pool().bufferInstance( this );
        int index = lastLetterIndex();
                
        // This loop takes care of case 1. and 2.
        for ( int i=0; i < strings.length; i++ ) {
            leading = strings[i];
            prefixLen = leading.length();
            if ( startsWith( leading ) ) {                
                substring = substring( prefixLen, len ) ;
                return leading.concat( substring.insertBookSeparator() );
            }
        }
        
        // This takes care of case 3.
        PSChar aChar = charAt( index + 1 );
        if ( index >= 0 && ! aChar.isSpace() ) {
            buffer.insert( index + 1, PSVerse.BOOK_SEPARATOR.charAt(0) );
            result = buffer.copy();
        }
        PSObject.pool().recycleInstance( buffer );
        
        return result;
    }
    
    /**
     * Turns:
     *     1. - "1 Chr15:25"  returns true ("1 Chr 15:25")  and
     *     2. - "2 Ki9:10"    returns true ("2 Ki 9:10"    ) and
     *     3. - "Num33:34,35" returns true ("Num 33:34,35")
     */
    public boolean needsBookSeparator() {
        int len = length();
        int index = lastLetterIndex();
        int prefixLen;
        
        PSString one = PSString.with( "1" );
        PSString two = PSString.with( "2" );
        PSString one_ = PSString.with( "1 " );
        PSString two_ = PSString.with( "2 " );
        PSString[] strings = new PSString[] { one_, two_, one, two  };
        PSString substring, leading;
        
        // This loop takes care of case 1. and 2.
        for ( int i=0; i < strings.length; i++ ) {
            leading = strings[i];
            prefixLen = leading.length();
            if ( startsWith( leading ) ) {                
                substring = substring( prefixLen, len ) ;
                return substring.needsBookSeparator();
            }
        }
                
        if ( index >= 0 && ( (index + 1) < len ) ) {
            PSChar aChar = charAt( index + 1 );
            if ( ! aChar.isSpace() ) return true;
        }
        
        return false;
    }
    
    ///  S T R I N G   Q U E R Y   /////////////////////////////////////////////
    public int indexOf( PSChar aChar ) {
//        return indexOf( aChar, PSInt.zero() );
        return indexOf( aChar, 0 );
    }
    
    public abstract int indexOf( PSChar aChar, int offset );
    
    public int indexOf( PSString str, int offset ) {
        int result = -1;
        PSChar firstChar;
        int fCharIndex = -1;
        int len = str.length();
        int off = offset;
        
        if ( len == 0 || len > length() ) return result;
        firstChar = str.charAt(0);
        fCharIndex = indexOf( firstChar, off );
        if ( fCharIndex >= 0 ) {
            if ( startsWith( str, fCharIndex ) )
                result = fCharIndex;
            else
                result = indexOf( str, fCharIndex + 1);
        }
        
        return result;
    }
    
    public int indexOf ( PSString str ) {
        return indexOf( str, 0 );
    }
    
    public int indexOf ( String str ) {
        return indexOf( str, 0 );
    }
    
    public int indexOf ( String str, int offset ) {
        return indexOf( new PSIndexedString( str ), offset );
    }
    
    public boolean containsChar( PSChar aChar ) {
        return indexOf( aChar, 0 ) >= 0;
    }
    
    public int lastIndexOf( PSChar aChar ) {
        return lastIndexOf( aChar, length() - 1 );
    }
    
    /**
     * Here, offset is seen from the end of the string.
     */
    public abstract int lastIndexOf( PSChar aChar, int offset );
    
    public boolean startsWith( PSString prefix, int offset ) {
        int start = offset;
        int end   = offset + prefix.length();
        
        if ( length() < end ) return false;
        for ( int i = start; i < end; i++ ) {
            PSChar prChar = prefix.charAt( i - start );
            PSChar thChar = this.charAt( i );
            if ( ! prChar.equals( thChar ) ) return false;
        }
        
        return true;
    }
    
    public boolean startsWith( PSString prefix ) {
        return startsWith( prefix, 0 );
    }
    
    public boolean startsWith( String prefix ) {
        return startsWith( PSString.with( prefix ) );
    }
    
    public boolean endsWith( PSString suffix ) {
        int offset = length() - suffix.length();
        
        return startsWith( suffix, offset );
    }
    
    public boolean endsWith( String suffix ) {
        return endsWith( PSString.with( suffix ) );
    }
    
    ///  P R I N T I N G   /////////////////////////////////////////////////////
    public void appendTo( PSBuffer buffer ) {
        int len = length();
        
        for ( int i=0; i < len; i++ ) {
            PSChar aChar = charAt( i );
            buffer.append( aChar.asChar() );
        }
    }
    
    public void appendTo( StringBuffer buffer ) {
        int len = length();
        
        for ( int i=0; i < len; i++ ) {
            PSChar aChar = charAt( i );
            buffer.append( aChar.asChar() );
        }
    }
    
    public PSString copy() {
        return new PSArrayString( this );
    }
    
    public String toString() {
        if ( _toStringCache == null ) {
            _toStringCache = _toString();
        }
        
        return _toStringCache;
    }
    
    public String _toString() {
        PSBuffer buffer = classBuffer();
        
        appendTo( buffer );
        
        return buffer.toString();
    }
    
    public String toPrint() {
        String result;
        PSArray allChars;
        
        allChars = allChars();
        allChars = allChars.collectWithKey( "toPrint" );
        result = allChars.appendComponents( "" );
        
        return result;
    }
    
    ///   E Q U A L I T Y   //////////////////////////////////////////////
    /**
     * Compares this string to the specified object.
     * The result is <code>true</code> if and only if the argument is not
     * <code>null</code> and is a <code>String</code> object that represents
     * the same sequence of characters as this object.
     *
     * @param   anObject   the object to compare this <code>String</code>
     *                     against.
     * @return  <code>true</code> if the <code>String </code>are equal;
     *          <code>false</code> otherwise.
     * @see     java.lang.String#compareTo(java.lang.String)
     * @see     java.lang.String#equalsIgnoreCase(java.lang.String)
     */
    public boolean equals(Object anObject) {
        if ( this == anObject )  return true;
        if ( anObject instanceof PSString ){
            PSString anoStr = (PSString )anObject;
            return _equals( this, anoStr );
        } else if ( anObject instanceof String ) {
            String anoStr = (String )anObject;
            return _equals( this, anoStr );
        } else
            return false;
    }
    
    protected boolean _equals( PSString first, PSString second ) {
        int n;
        
        n = first.length();
        if ( n != second.length() ) return false;
//        while (n-- != 0) {
//            if ( ! first.charAt(i++).equals( second.charAt(j++) ) )
//                return false;
//        }
        for ( int i = 0; i < n; i++ ) {
            PSChar aChar1 = (PSChar )first.charAt(i);
            PSChar aChar2 = (PSChar )second.charAt(i);
            if ( ! aChar1.equals( aChar2 ) )
                return false;
        }
        
        return true;
    }
    
    protected boolean _equals( PSString first, String second ) {
        int n, i = 0, j = 0;
        
        n = first.length();
        if ( n != second.length() ) return false;
        while (n-- != 0) {
            if ( ! first.charAt(i++).equals( second.charAt(j++) ) )
                return false;
        }
        
        return true;
    }
    
    public boolean equalsIgnoreCase(Object anObject) {
            if ( this == anObject )  return true;
            if ( anObject instanceof PSString ){
                PSString anoStr = (PSString )anObject;
                return _equalsIgnoreCase( this, anoStr );
            } else if ( anObject instanceof String ) {
                String anoStr = (String )anObject;
                return _equalsIgnoreCase( this, anoStr );
            } else
                return false;
        }
        
        protected boolean _equalsIgnoreCase( PSString first, PSString second ) {
            return _equals( first.toUpperCase(), second.toUpperCase() );
        }
        
        protected boolean _equalsIgnoreCase( PSString first, String second ) {
            return _equals( first.toUpperCase(), second.toUpperCase() );
        }
    
    public int hashCode() {
        if ( _hashCache == -1 ) {
            _hashCache = _hashCode();
        }
        
        return _hashCache;
    }
    
    /**
     * Returns a hash code for this string. The hash code for a
     * <code>String</code> object is computed as
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     * using <code>int</code> arithmetic, where <code>s[i]</code> is the
     * <i>i</i>th character of the string, <code>n</code> is the length of
     * the string, and <code>^</code> indicates exponentiation.
     * (The hash value of the empty string is zero.)
     *
     * @return  a hash code value for this object.
     */
    public int _hashCode() {
        int hash = 0;
        int len = length();
        
        for ( int i = 0; i < len; i++ ) {
            hash = 31 * hash + charCodeAt( i );
        }
        
        return hash;
    }
    
    ///   C O M P A R I S O N S   //////////////////////////////////////////////
    /**
     * Compares two strings lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the strings. The character sequence represented by this
     * <code>String</code> object is compared lexicographically to the
     * character sequence represented by the argument string. The result is
     * a negative integer if this <code>String</code> object
     * lexicographically precedes the argument string. The result is a
     * positive integer if this <code>String</code> object lexicographically
     * follows the argument string. The result is zero if the strings
     * are equal; <code>compareTo</code> returns <code>0</code> exactly when
     * the {@link #equals(Object)} method would return <code>true</code>.
     * <p>
     * This is the definition of lexicographic ordering. If two strings are
     * different, then either they have different characters at some index
     * that is a valid index for both strings, or their lengths are different,
     * or both. If they have different characters at one or more index
     * positions, let <i>k</i> be the smallest such index; then the string
     * whose character at position <i>k</i> has the smaller value, as
     * determined by using the &lt; operator, lexicographically precedes the
     * other string. In this case, <code>compareTo</code> returns the
     * difference of the two character values at position <code>k</code> in
     * the two string -- that is, the value:
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     * If there is no index position at which they differ, then the shorter
     * string lexicographically precedes the longer string. In this case,
     * <code>compareTo</code> returns the difference of the lengths of the
     * strings -- that is, the value:
     * <blockquote><pre>
     * this.length()-anotherString.length()
     * </pre></blockquote>
     *
     * @param   anotherString   the <code>String</code> to be compared.
     * @return  the value <code>0</code> if the argument string is equal to
     *          this string; a value less than <code>0</code> if this string
     *          is lexicographically less than the string argument; and a
     *          value greater than <code>0</code> if this string is
     *          lexicographically greater than the string argument.
     */
    public int compareTo( PSString anoStr ) {
        int len1 = length();
        int len2 = anoStr.length();
        int n = Math.min(len1, len2);
        int i = 0;
        int j = 0;
        
        if (i == j) {
            int k = i;
            int lim = n + i;
            while (k < lim) {
                char c1 = _charAt(k);
                char c2 = anoStr._charAt(k);
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
        } else {
            while (n-- != 0) {
                char c1 = _charAt(i++);
                char c2 = anoStr._charAt(j++);
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
        }
        return len1 - len2;
    }
    
    public int compareTo( String anoStr ) {
        int len1 = length();
        int len2 = anoStr.length();
        int n = Math.min(len1, len2);
        int i = 0;
        int j = 0;
        
        if (i == j) {
            int k = i;
            int lim = n + i;
            while (k < lim) {
                char c1 = _charAt(k);
                char c2 = anoStr.charAt(k);
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
        } else {
            while (n-- != 0) {
                char c1 = _charAt(i++);
                char c2 = anoStr.charAt(j++);
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
        }
        return len1 - len2;
    }
    
    /**
     * Compares this String to another Object.  If the Object is a String,
     * this function behaves like <code>compareTo(String)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code> (as Strings are comparable
     * only to other Strings).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a string
     *		lexicographically equal to this string; a value less than
     *		<code>0</code> if the argument is a string lexicographically
     *		greater than this string; and a value greater than
     *		<code>0</code> if the argument is a string lexicographically
     *		less than this string.
     * @exception <code>ClassCastException</code> if the argument is not a
     *		  <code>String</code>.
     * @see     java.lang.Comparable
     * @since   1.2
     */
    public int compareTo( Object o ) {
        if ( o instanceof PSString )
            return compareTo( (PSString )o );
        else
            return compareTo( (String )o );
    }
    
    /**
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling <code>compareTo</code> with normalized versions of the strings
     * where case differences have been eliminated by calling
     * <code>Character.toLowerCase(Character.toUpperCase(character))</code> on
     * each character.
     * <p>
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>collators</em> to allow
     * locale-sensitive ordering.
     *
     * @param   str   the <code>String</code> to be compared.
     * @return  a negative integer, zero, or a positive integer as the
     *		the specified String is greater than, equal to, or less
     *		than this String, ignoring case considerations.
     * @see     java.text.Collator#compare(String, String)
     * @since   1.2
     */
    public int compareToIgnoreCase( PSString str ) {
        return caseInsensitiveComparator().compare( this, str );
    }

    public PSInt leEnumRank() {
        if ( length() != 1 ) return PSInt.zero();
        return charAt(0).leEnumRank();
    }

    public PSInt rnEnumRank() {
        PSRomanNumRecognizer recognizer = PSRomanNumRecognizer.singleton();
        if ( ! recognizer.isRomanNumber(this) ) return PSInt.zero();
        return recognizer.decimalValue(this);
    }

    ///   S M A R T   M E T H O D S   //////////////////////////////////////////
    public static String _newLines() { return NEW_LINES; }
    public static PSString newLines() { return PSString.with( NEW_LINES ); }
    
    public static String _delimiters() { return DELIMITERS; }
    public static PSString delimiters() { return PSString.with( DELIMITERS ); }

    public static PSString separators() { return PSString.with( NEW_LINES + DELIMITERS ); }

    public static PSString _specialChars() {
        return PSString.with( _newLines() + _delimiters() );
    }
    
    public static PSArray specialChars() {
        PSArray result = new PSArray();
        
        for ( int i=0; i < _specialChars().length(); i++ ) {
            PSChar aChar = _specialChars().charAt( i );
            result.add( aChar );
        }
        
        return result;
    }

    public PSChar firstChar() { return (PSChar )this.charAt(0); }
    public static PSString emptyString() { return with( "" ); }
    public boolean isEmpty() { return length() == 0; }
    public boolean isBlank() { return trim().isEmpty(); }
//    public boolean isUpperCase() { return equals( toUpperCase() ); }

    public boolean isUpperCase() {
        for ( int i=0; i < length(); i++ ) {
            PSChar aChar = charAt( i );
            if ( ! aChar.isUpperCase() )
                return false;
        }

        return true;
    }

    public boolean isStrictUpperCase() {
        for ( int i=0; i < length(); i++ ) {
            PSChar aChar = charAt( i );
            if ( ! aChar.isStrictUpperCase() )
                return false;
        }

        return true;
    }

    public boolean hasUpperCase() { return ! isLowerCase(); }
//    public boolean isLowerCase() { return equals( toLowerCase() ); }
    public boolean isLowerCase() {
        for ( int i=0; i < length(); i++ ) {
            PSChar aChar = charAt( i );
            if ( ! aChar.isLowerCase() )
                return false;
        }

        return true;
    }

    public boolean isStrictLowerCase() {
        for ( int i=0; i < length(); i++ ) {
            PSChar aChar = charAt( i );
            if ( ! aChar.isStrictLowerCase() )
                return false;
        }

        return true;
    }
    public boolean hasLowerCase() { return ! isUpperCase(); }
    public boolean isNewLineChar() {
        return (length() == 1) && firstChar().isNonPrintable();
    }
    public boolean isEmptyToken() {
        return isEmpty() || isNewLineChar();
    }

    public boolean isAllDigit() {
        int len = length();
        
        if ( isEmpty() ) return false;
        for( int i = 0; i < len; i++ ) {
            PSChar aChar = charAt(i);
            if ( ! aChar.isDigit() ) return false;
        }
        
        return true;
    }
    
    public static boolean isAllDigit( String aToken ) {
        if ( aToken == null || aToken.length() == 0 ) return false;
        for( int i = 0; i < aToken.length(); i++ ) {
            char aChar = aToken.charAt(i);
            if ( !Character.isDigit(aChar) ) return false;
        }
        
        return true;
    }
    
    public boolean isAllLetter() {
        int len = length();
        
        if ( isEmpty() ) return false;
        for( int i = 0; i < len; i++ ) {
            PSChar aChar = charAt(i);
            if ( ! aChar.isLetter() ) return false;
        }
        
        return true;
    }

    public boolean isRomanNumber() {
        return recognizer().isRomanNumber( this );
    }
    
    public boolean hasDigit() {
        int len = length();
        
        for( int i = 0; i < len; i++ ) {
            PSChar aChar = charAt(i);
            if ( aChar.isDigit() ) return true;
        }
        
        return false;
    }
    
    public boolean hasLetter() {
        int len = length();
        
        for( int i = 0; i < len; i++ ) {
            PSChar aChar = charAt(i);
            if ( aChar.isLetter() ) return true;
        }
        
        return false;
    }
    
    public boolean isDelimiter() {
        return delimiters().indexOf( this ) >= 0;
    }

    public boolean isSpecialChar() {
        return _specialChars().indexOf( this ) >= 0;
    }
    
    public boolean isAllDelimiter() {
        int len = length();
        
        if ( isEmpty() ) return true;
        for( int i = 0; i < len; i++ ) {
            PSChar aChar = charAt(i);
            if ( ! aChar.isDelimiter() ) return false;
        }
        
        return true;
    }
        
    public PSArray splitWithDelimiters( PSString delimiters ) {
        return splitWithDelimiters( delimiters, false );
    }
    
    /**
     * Two copy() calls are necessary. One at Tokenizer creation and another 
     * before adding the token in the result PSArray. If not, bad things happen!
     */
    public PSArray splitWithDelimiters( PSString delimiters, boolean includeDelimiters ) {
        PSArray components = new PSArray(10);
        PSTokenizer tokenizer = classTokenizer( copy(), delimiters, includeDelimiters );

        while ( tokenizer.hasMoreTokens() ) {
            PSString aComponent = tokenizer.nextToken();
            components.add( aComponent.copy() );
        }

        return components;
    }
    
    
    public PSDict snapshot() {
        PSObject replacement = new Coder( this );
        PSDict snapshot = replacement.snapshot();
        
        return snapshot;
    }
    
    public static Object objectFromSnapshot( PSDict aDict ) {
        Coder replacement = new Coder();
        
        replacement.takePropertyDict( aDict );
        
        return replacement.decodeString();
    }
    
    ///   C L A S S    M E T H O D S   /////////////////////////////////////////
    public static PSString with( String str ) { 
        return new PSArrayString( str ); 
    }
    
    public static PSArray with( String[] str ) { 
        PSArray result = new PSArray( str.length );
        
        for ( int i=0; i < str.length; i++ ) {
            result.add( new PSArrayString( str[i] ) );
        }
        
        return result; 
    }
    
    public static PSTokenizer.Token sharedToken( PSString string, int start, int end ) {
        if ( _SharedToken == null ) {
            _SharedToken = new PSTokenizer.Token( string, start, end );
        } else {
            _SharedToken.setString( string );
            _SharedToken.setStart( start );
            _SharedToken.setEnd( end );
        }
        
        return _SharedToken;
    }
    
    private static PSBuffer classBuffer() {
        if ( _ClassBuffer == null ) {
            _ClassBuffer = new PSBuffer();
        } else {
            _ClassBuffer.clearContent();
        }
        
        return _ClassBuffer;
    }
    
    private static PSTokenizer classTokenizer( PSString s, PSString d, boolean rd ) {
        if ( _ClassTokenizer == null ) {
            _ClassTokenizer = new PSTokenizer( s, d, rd );
        } else {
            _ClassTokenizer.resetState( s, d, rd );
        }
        
        return _ClassTokenizer;
    }
    
    public static Comparator caseInsensitiveComparator() {
        if ( _CaseInsensitiveComparator == null ) {
            _CaseInsensitiveComparator = new CaseInsensitiveComparator();
        }
        
        return _CaseInsensitiveComparator;
    }
        
    ///   N E S T E D   C L A S S E S   ////////////////////////////////////////
    private static class CaseInsensitiveComparator
            implements Comparator {
        
        public int compare(Object o1, Object o2) {
            PSString s1 = (PSString ) o1;
            PSString s2 = (PSString ) o2;
            int n1 = s1.length();
            int n2 = s2.length();
            
            for ( int i1=0, i2=0; i1<n1 && i2<n2; i1++, i2++ ) {
                PSChar c1 = s1.charAt(i1);
                PSChar c2 = s2.charAt(i2);
                int comp = c1.compareToIgnoreCase( c2 );
                if ( comp != 0 ) return comp;
            }
            
            return n1 - n2;
        }
        
    } /// E N D CaseInsensitiveComparator
    
    ///   N E S T E D  C L A S S E S   /////////////////////////////////////////
    public static class Coder extends PSCustomCoder {
        
        public Coder() { super( PSString.Coder.class ); }
        
        public Coder( PSString aString ) {
            this();
            setCodingTarget( aString );
            fillPropertyDict();
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( PSString.with( (String )dict.get( "s" ) ), "s" );
        }
        
        public void fillPropertyDict() {
            super.takeValueForKey( string().toString(), "s" );
        }
        
        public PSString string() { return (PSString )codingTarget(); }
        
        public PSString decodeString() {
            return (PSString )decodeObject();
        }
        
        public Object decodeObject() {
            PSString aString;
            
            aString = PSString.with( (String  )super.valueForKey( "s" ) );
            
            return  aString;
        }
    } // End Coder.
        
    public static abstract class Filter extends PSObject {
        
        public static Filter _UpperCase = null;
        public static Filter _LowerCase = null;
        public static Filter _MixedCase = null;

        public abstract boolean matchString( PSString aString );

        public static Filter upperCase() {
            if ( _UpperCase == null )
                _UpperCase = new UpperCase();

            return _UpperCase;
        }

        public static Filter lowerCase() {
            if ( _LowerCase == null )
                _LowerCase = new LowerCase();

            return _LowerCase;
        }

        public static Filter mixedCase() {
            if ( _MixedCase == null )
                _MixedCase = new MixedCase();

            return _MixedCase;
        }
        ///   N E S T E D   C L A S S E S   //////////////////////////////////////////

        public static class Not extends Filter {

            private Filter _original;

            public Not( Filter filter ) {
                _original = filter;
            }

            public boolean matchString( PSString aString ) {
                return ! _original.matchString( aString );
            }

        }

        public static class StrictUpperCase extends Filter {

            // Returns true if the String is all Upper Case           
            public boolean matchString( PSString aString ) {
                return aString.isStrictUpperCase();
            }

        }

        public static class UpperCase extends Filter {

            // Returns true if the String is all Upper Case
            public boolean matchString( PSString aString ) {
                return aString.isAllLetter() && ! aString.hasLowerCase();
            }
        }

        public static class LowerCase extends Filter {

            // Returns true if the String is all Lower Case
            public boolean matchString( PSString aString ) {
                return aString.isAllLetter() && ! aString.hasUpperCase();
            }
        }

        public static class MixedCase extends Filter {

            // Returns true if the String starts with an Upper Case, but is NOT all
            // Upper Case.
            public boolean matchString( PSString aString ) {
                PSString remaining;
                PSString.Filter upperCaseFilter = PSString.Filter.upperCase();

                if ( aString.isEmpty() ) return false;
                if ( aString.firstChar().isLowerCase() ) return false;
                remaining = aString.substring( 1, aString.length() );
                return ! upperCase().matchString( remaining );
            }

        }

    }

}
