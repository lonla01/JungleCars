/**
 * PSRange.java
 *
 * Created on 27 juillet 2009, 19:35
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

public class PSRange extends PSObject {
    
    private Comparable _start;
    private Comparable _end;
    
    public PSRange() {
        this( PSInt.with(0), PSInt.with(0) );
    }
    
    public PSRange( Comparable start, Comparable end ) {
        setStart(start);
        setEnd(end);
        ensureIntegrity();
    }
    
    public boolean contains( Comparable element ) {
        ensureIntegrity();
        return element.compareTo( start() ) >= 0 &&
               element.compareTo( end()   ) <= 0;         
    }
    
    protected void ensureIntegrity() {
        if ( start().compareTo( end() ) > 0 )
            throw new IllegalArgumentException( 
                    "start=[" + start() + "] > end=[" + end() + "]" );
    }

    public Comparable start() { return _start; }

    public void setStart(Comparable start) { 
        this._start = start; 
    }

    public Comparable end() { return _end; }

    public void setEnd(Comparable end) { 
        this._end = end;  
    }
    
    public String toString() {
        PSBuffer buffer = pool().bufferInstance();
        
        buffer.append("[").append( start() );
        buffer.append(" , ").append(  end() ).append("]");
        String result = buffer.toString();
        pool().recycleInstance( buffer );
        
        return result;
    }
        
    public boolean equals( Object other ) {
        if (other == null) return false;
        if ( ! this.getClass().equals( other.getClass() ) )  return false;
        PSRange another = (PSRange )other;
        
        return this.start().equals( another.start() ) &&
               this.end().equals  ( another.end()   ) ;
    }

}
