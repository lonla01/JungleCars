package com.camcars;

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

//import com.sun.rsasign.e;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PSArray extends Vector  {

    public  static final PSArray    EmptyArray        = new PSArray();
    private static TranslateVisitor _TranslateVisitor = null;
    private static CollectVisitor   _CollectVisitor   = null;
    private static StringBuffer     _SharedBuffer     = null;

    public PSArray() { }

    public PSArray(int initialCapacity) { super(initialCapacity); }

    public PSArray( Object[] elem ) {
        this.elementData = elem;
        this.capacityIncrement = 0;
    }
    
    public PSArray( Collection col ) {
        this();
        addAll( col );
    }

    public PSArray copy() {
        PSArray copy = new PSArray( this );

        return copy;
    }
    
    public PSArray union( Collection elem ) {
        addAll( new PSArray( elem ) );
        
        return this;
    }
        
    public PSArray union( Object[] elem ) {
        addAll( new PSArray( elem ) );
        
        return this;
    }
    
    public PSArray snapshot() {
        PSArray snapshot = new PSArray();
        
        for ( int i = 0; i < size(); i++ ) {
            Object obj = get(i);

            if ( obj == null ) {
                logger().error( this, "Null object for snapshot" );
            } else if ( obj instanceof PSRoot ) {
                snapshot.add( ( (PSRoot )obj ).snapshot() );
            } else if ( obj instanceof PSDict ) {
                snapshot.add( obj );
            } else {
                logger().error( this, "Invalid object for snapshot: " +
                        obj + " of class: " + obj.getClass().getName() );
            }
        }
        
        return snapshot;
    }
    
    public PSArray objectsFromSnapshots() {
        PSArray objects = new PSArray();
        
        for ( int i = 0; i < size(); i++ ) {
            Object obj = get(i);
            if ( ! (obj instanceof PSDict ) ) {
                PSRoot.logger().error( "Invalid snapshot" );
                return EmptyArray;
            }
            PSDict aDict = (PSDict )obj;
            objects.add( PSRoot.objectFromSnapshot( aDict ) );
       }
        
        return objects;
    }
    
    public static PSLogger logger() { return PSObject.logger(); }    
    public static PSAssert asserter() { return PSObject.asserter(); } 

    public String toPlist() {
        Object obj;
        PSPlistCoder.Writer buffer = new PSPlistCoder.Writer();

        buffer.encodeArray(this);

        return buffer.toString();
    }

    public void appendPlist( PSPlistCoder.Writer appender ) {
        appender.encodeArray(this);
    }
    
    public int entryCount() { return size(); }

    public static StringBuffer sharedBuffer() {
        if ( _SharedBuffer == null )
            _SharedBuffer = new StringBuffer();
        else
            _SharedBuffer.delete( 0, _SharedBuffer.length() );

        return _SharedBuffer;
    }
    
    ///   S U P E R   C L A S S   A L T E R A T I O N   ////////////////////////        
    public int indexOf( Object anObject, int index ) {        
        for (int i = index ; i < elementCount ; i++) {
            if ( anObject.equals( elementData[i] ) )
                return i;
        }
        
        return -1;
    }
    
    public Object get(int index) {
        return elementData[index];
    }
    
    public Object elementAt(int index) {
        return elementData[index];
    }

    public boolean add( Object element ) {
        if ( element == null ) {
            logger().error( this, "Adding null element to a PSArray" );
            return false;
        }
//        else if ( ! contains( element ) ) {
            return super.add( element );
//        }
//        return false;
    }

    ///   S M A R T   M E T H O D S   //////////////////////////////////////////
    public String appendComponents( String separator ) {
        String resultStr;
        StringBuffer aBuffer = sharedBuffer();

        if ( size() > 0 )
            aBuffer.append( firstElement() );
        for( int i = 1 ; i < size() ; i++ ) {
            aBuffer.append( separator );
            aBuffer.append( elementAt(i) );
        }
        resultStr = aBuffer.toString();

        return resultStr;
    }
    
    public PSString appendComponents( PSString separator ) {
        PSString resultStr;
        PSBuffer aBuffer = PSObject.pool().bufferInstance();

        if ( size() > 0 )
            aBuffer.append( firstElement() );
        for( int i = 1 ; i < size() ; i++ ) {
            aBuffer.append( separator );
            aBuffer.append( elementAt(i) );
        }
        resultStr = aBuffer.copy();
        
        PSObject.pool().recycleInstance( aBuffer );

        return resultStr;
    }

    public static PSArray stringComponents( String aString, String delimiters ) {
        return stringComponents( aString, delimiters, false );
    }

    /**
     * This method should rather be implemented on a String class.
     */
    public static PSArray stringComponents( String aString,
            String delimiters, boolean includeDelimiters ) {
        PSArray components = new PSArray(10);
        StringTokenizer tokenizer = new StringTokenizer( aString,
                delimiters, includeDelimiters );

        while ( tokenizer.hasMoreTokens() ) {
            String aComponent = (String )tokenizer.nextToken();
            components.add( aComponent );
        }

        return components;
    }

    /**
     * This method takes as param a method returning a number (int, Integer, Float,
     * etc. The elements of thid array should implement that method. The method may
     * thus be seen as an aatribute of the array elements.
     * The attributeName which is the method name, is used to obtain a scalar value
     * out of each element, by the mean of a method invocation through the reflection API.
     * These values are then aggregated by summing them together.
     * The total is return. If the method call does not return a Number or if an
     * exception occurs during method invocation, then -1 is returned.
     */
    public int sumWithNumAttribute( String attributeName ) {
        int aggregateValue = 0;
        PSTarget target;
        Object invocationResult = null;
//        Number invocationValue;

        try {
            for ( int i = 0; i < size(); i++ ) {
                Object element = elementAt(i);
                target = new PSTarget( element );
                invocationResult = target.performMethod( attributeName );
                if ( invocationResult instanceof Number ) {
                    Number invocationValue = (Number )invocationResult;
                    aggregateValue += invocationValue.intValue();
                } else {
                    PSNumber invocationValue = (PSNumber )invocationResult;
                    aggregateValue += invocationValue.intValue(); 
                }
            }
        } catch ( ClassCastException e ) {
            if ( invocationResult != null )
                logger().error( this, "Element class not a Number: " +
                        invocationResult.getClass().getName(), e );
            aggregateValue = -1;
        } catch ( InvocationTargetException ex ) {
            logger().error( this, "Error during invocation of: " +
                    attributeName + "()", ex );
            aggregateValue = -1;
        }
        return aggregateValue;
    }

    public Set asSet() {
        Set set = new TreeSet();
        Iterator iter = iterator();

        while ( iter.hasNext() ) {
            Object obj = iter.next();
            set.add( obj );
        }

        return set;
    }

    public PSArray asArraySet() {
        return new PSArray( this.asSet() );
    }

    public PSArray asSortedArraySet() {
        return this.asArraySet().sort();
    }
    
    public PSArray sort() {
        java.util.List elements = (java.util.List )(this);
        Collections.sort( elements );

        return this;
    }

    public PSArray subArrayFromIndex( int start ) {
        PSArray subArray = new PSArray();

        for ( int i=start; i < size(); i++ ) {
            subArray.add( get(i) );
        }

        return subArray;
    }

    public PSArray reverse() {
        PSArray reverse = new PSArray();

        for ( int i = size()-1; i >= 0; i-- ) {
            reverse.add( this.get( i ) );
        }

        return reverse;
    }

    public PSArray sortWithComparator( Comparator comparator ) {
        java.util.List elements = (java.util.List )(this);
        Collections.sort( elements, comparator );

        return this;
    }
    
    public Object receive( PSVisitor aVisitor ) {
        for ( int i = 0; i < this.elementCount; i++ ) {
            aVisitor.visitMe( elementData[i] );
        }

        return aVisitor.result();
    }
    
    public PSArray translateWithKey( String aKey, KeyVisitor aVisitor ) {
        aVisitor.setKey( aKey );

        return (PSArray )receive( aVisitor );
    }

    public PSArray translateWithKey( String aKey ) {
        TranslateVisitor aVisitor = translateVisitor();

        return translateWithKey( aKey, aVisitor );
    }

    public PSArray collectWithKey( String aKey ) {
        CollectVisitor aVisitor = PSObject.pool().collectVisitor();
        aVisitor.setKey( aKey );

        PSArray result = (PSArray )receive( aVisitor );
        
        PSObject.pool().recycleInstance( aVisitor );
        
        return result;
    }
    
     public PSObject selectWithKey( String aKey, Object aValue ) {
        Iterator iter = iterator();
        for ( int i=0; iter.hasNext() ; i++ ) {
            PSObject psObject = (PSObject )iter.next();
            Object psValue = psObject.valueForKey( aKey );
            if ( aValue.equals( psValue ) ) {
                return psObject;
            }
        }

        return null;
    }

    public static TranslateVisitor translateVisitor() {
        if ( _TranslateVisitor == null )
            _TranslateVisitor = new TranslateVisitor();
        else
            _TranslateVisitor.resetState();

        return _TranslateVisitor;
    }

    public static CollectVisitor collectVisitor() {
        if ( _CollectVisitor == null )
            _CollectVisitor = new CollectVisitor();
        else
            _CollectVisitor.resetState();

        return _CollectVisitor;
    }

    /// N E S T E D   C L A S S E S   //////////////////////////////////////////
    public abstract static class KeyVisitor extends PSVisitor {

        private String _key;

        public KeyVisitor( Object result, String aKey ) {
            super( result );
            setKey( aKey );
        }

        public String key() { return _key; }
        public void setKey(String key) { _key = key; }

        public void resetState() {
            setResult( new PSArray() );
            setKey( null );
        }
    }  ///   End KeyVisitor.

    public static class TranslateVisitor extends KeyVisitor {

        public TranslateVisitor() { this( null ); }        
        
        public TranslateVisitor( String aKey ) {
            this( new PSArray(), aKey );
        }
        
        public TranslateVisitor( Object result, String aKey ) {
            super( result, aKey );
        }
        
        public void visitMe( Object aHost ) {
            if ( aHost instanceof PSID ) {
                arrayResult().add( aHost );
            }
        }

        public void visitMe( PSObject aHost ) {
            Object translation = aHost.valueForKey( key());
            arrayResult().add( translation );
        }

    } ///   END TranslateVisitor.

    public static class CollectVisitor extends KeyVisitor {

        public CollectVisitor() { this( null );  }

        public CollectVisitor( String aKey ) {
            super( new PSArray(), aKey );
        }

        protected void collectResult( Object anObject ) {
            if ( anObject instanceof Collection )
                arrayResult().addAll( (Collection )anObject );
            else
                arrayResult().add( anObject );
        }

        public void visitMe( Object aHost ) {
            Object returnedValue;
            PSTarget target;
            
            if ( aHost == null ) return;
            target = new PSTarget( aHost );
            if ( target.respondsTo( key() ) ) {
                try {
                    returnedValue = target.performMethod( key() );
                    collectResult( returnedValue );
                } catch (InvocationTargetException ex) {
                    this.logger().error( this, "Couln't invoque method: " + key() +
                            " Target class=" + aHost.getClass().getName() );
                }
            } else {
                this.logger().warn( this, "Object don't understand: " + key() );
                collectResult( aHost );
            }
        }

        public void visitMe( PSObject aHost ) {
            Object returnedValue = aHost.valueForKey( key() );

            collectResult( returnedValue );
        }

    } ///   END CollectVisitor.

}