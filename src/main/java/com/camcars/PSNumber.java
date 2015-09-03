/**
 * PSNumber.java
 *
 * Created on 26 ao�t 2009, 18:15
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

public abstract class PSNumber implements Comparable {
        
    public abstract int intValue();
    
    /**
     * Returns the value of this <code>Integer</code> as a
     * <code>long</code>.
     */
    public long longValue() { return (long)intValue(); }
    
    /**
     * Returns the value of this <code>Integer</code> as a
     * <code>float</code>.
     */
    public float floatValue() { return (float)intValue(); }
    
    /**
     * Returns the value of this <code>Integer</code> as a
     * <code>double</code>.
     */
    public double doubleValue() { return (double)intValue(); }
    
    public abstract String toString();
    
    ///   C O M P A R I S O N   ////////////////////////////////////////////////
    /**
     * Compares this object to the specified object.  The result is
     * <code>true</code> if and only if the argument is not
     * <code>null</code> and is an <code>Integer</code> object that
     * contains the same <code>int</code> value as this object.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof PSNumber) {
            return intValue() == ((PSNumber )obj).intValue();
        } else if (obj instanceof Integer) {
            return intValue() == ((Integer )obj).intValue();
        }
        
        return false;
    }
    
    /**
     * Compares two <code>Integer</code> objects numerically.
     *
     * @param   anotherInteger   the <code>Integer</code> to be compared.
     * @return	the value <code>0</code> if this <code>Integer</code> is
     * 		equal to the argument <code>Integer</code>; a value less than
     * 		<code>0</code> if this <code>Integer</code> is numerically less
     * 		than the argument <code>Integer</code>; and a value greater
     * 		than <code>0</code> if this <code>Integer</code> is numerically
     * 		 greater than the argument <code>Integer</code> (signed
     * 		 comparison).
     * @since   1.2
     */
    public int compareTo( PSNumber another ) {
        return PSInt.compare( intValue(), another.intValue() );
    }
    
    /**
     * Compares this <code>Integer</code> object to another object.
     * If the object is an <code>Integer</code>, this function behaves
     * like <code>compareTo(Integer)</code>.  Otherwise, it throws a
     * <code>ClassCastException</code> (as <code>Integer</code>
     * objects are only comparable to other <code>Integer</code>
     * objects).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a
     *		<code>Integer</code> numerically equal to this
     *		<code>Integer</code>; a value less than <code>0</code>
     *		if the argument is a <code>Integer</code> numerically
     *		greater than this <code>Integer</code>; and a value
     *		greater than <code>0</code> if the argument is a
     *		<code>Integer</code> numerically less than this
     *		<code>Integer</code>.
     * @exception <code>ClassCastException</code> if the argument is not an
     *		  <code>Integer</code>.
     * @see     java.lang.Comparable
     * @since   1.2
     */
    public int compareTo( Object o ) {
        return compareTo( (PSNumber )o );
    }
    
    /**
     * Returns a hash code for this <code>Integer</code>.
     *
     * @return  a hash code value for this object, equal to the
     *          primitive <code>int</code> value represented by this
     *          <code>Integer</code> object.
     */
    public int hashCode() {
        return intValue();
    }
    
    // Necessarty for a PSRoot subclass.
//    public void takePropertyDict( PSDict dict ) { }
}
