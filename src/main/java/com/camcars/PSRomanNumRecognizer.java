package com.camcars;

import java.util.Set;

/**
 * PSRomanNumRecognizer.java
 *
 * Created on 09 Juillet 2009, 15:35
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

// </editor-fold>
    

public class PSRomanNumRecognizer {

    private static PSRomanNumRecognizer _Singleton = null;
    private static PSDict _romanNumbers = null;
    private PSInt _memorisedValue = null;
            
    private PSDict romanDict() {
        if (_romanNumbers == null) {
            _romanNumbers = new PSDict();
            _romanNumbers.put( "I", PSInt.with(1) );
            _romanNumbers.put( "V", PSInt.with(5) );
            _romanNumbers.put( "X", PSInt.with(10) );
            _romanNumbers.put( "L", PSInt.with(50) );
            _romanNumbers.put( "C", PSInt.with(100) );
            _romanNumbers.put( "D", PSInt.with(500) );
            _romanNumbers.put( "M", PSInt.with(1000) );
        }
        return _romanNumbers;
    }
    
    private void _checkArgument(String romanNumber) {
        if (romanNumber == null || romanNumber.length() == 0)
            throw new IllegalArgumentException("Empty argument.");
        if (romanNumber.length() == 1) {
            if ( isRomanAtomicChar( romanNumber ) ) {
                return;
            }
            throw new IllegalArgumentException("Invalid roman number.");
        }
        for (int i = 0; i < romanNumber.length(); i++) {
            String curChar = romanNumber.substring(i, i+1);
            PSInt decValue = decimalValue( curChar );
            if (decValue == null)
                throw new IllegalArgumentException("Invalid roman number.");
        }
    }
    
    public boolean isRomanAtomicChar( String romanChar ) {
        if ( romanChar.length() > 1 ) return false;
        String upCaseNum = romanChar.toUpperCase();
        
        Set romanSet = romanDict().keySet();
        if ( romanSet.contains(upCaseNum) ) return true;
        else return false;
    }
    
    /**
     * Checks if a given string represent or not a roman number.
     */
    
    public boolean isRomanNumber( String aRomanString ) {
        try {
            _checkArgument( aRomanString );
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    
    // TODO: This method may not create a new String.
    public boolean isRomanNumber( PSString aRomanString ) {
        return isRomanNumber( aRomanString.toString() );
    }
    
    /**
     * Takes a single char string as argument and returns its decimal equivalent.
     */
    protected PSInt _atomicDecimalValue(String romanNumber) {
        if (romanNumber == null || romanNumber.length() != 1)
            throw new IllegalArgumentException("Argument length should be one.");
        return (PSInt) romanDict().get( romanNumber );
    }  
    
    protected PSInt _biAtomicDecimalValue(String romanNumber) {
        if (romanNumber == null || romanNumber.length() != 2)
            throw new IllegalArgumentException("Argument length should be two.");
        
        String leftChar = romanNumber.substring(0, 1);
        String rightChar = romanNumber.substring(1, 2);
        
        PSInt leftVal = _atomicDecimalValue( leftChar );
        PSInt rightVal = _atomicDecimalValue( rightChar );
        
        if ( leftVal.lt( rightVal ) ) 
            return rightVal.sub( leftVal );
        else
            return rightVal.add( leftVal );
    }
    
    /**
     * Takes a string representing a roman number and compute its decimal 
     * equivalent. The method throws an IllegalArgumentException if the 
     * string is not a valid representation of a roman number.
     */
    public PSInt decimalValue(String aRomanNumber) {
        String upCaseNum = aRomanNumber.toUpperCase();
        String remaining = null,  lastTwo = null;
        PSInt retVal = null;
        int len = upCaseNum.length();
        
        _checkArgument( upCaseNum );
        if ( len == 1 ) {
            return _atomicDecimalValue( upCaseNum );
        }
        if ( len == 2 ) {
            return _biAtomicDecimalValue( upCaseNum );
        }
        lastTwo = upCaseNum.substring( len-2, len );
        remaining = upCaseNum.substring( 0, len-2 );           
        retVal = _biAtomicDecimalValue( lastTwo ).add( decimalValue( remaining ) );
        
        return retVal;
    }
    
    public PSInt decimalValue( PSString aRomanNumber ) {
        return decimalValue( aRomanNumber.toString() );
    }

    public static PSRomanNumRecognizer singleton() {
        if ( _Singleton == null ) {
            _Singleton = new PSRomanNumRecognizer();
        }

        return _Singleton;
    }
    
}