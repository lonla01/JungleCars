package com.camcars;

import java.lang.reflect.InvocationTargetException;
/**
 * PSAction.java
 *
 * Created on 11 juillet 2009, 21:16
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

public class PSAction extends PSObject {
    
    private Object _owner;
    private PSTarget _target = null;
    private Object _subject = null;
    private String _targetKey;
    private String _actionKey;
    private String _subjectKey;
    
    public PSAction( Object owner, String targetKey, 
            String actionKey, String subjectKey ) {
        super();
        _owner = owner;
        assertTrue( "Illegal owner for generic action", _owner != null );
        _targetKey = targetKey;
        _actionKey = actionKey;
        _subjectKey = subjectKey;
    }
    
    /**
     * Returns the object against which the action will be perfomed. In general
     * the object returned is obtained by the call: owner().targetKey().
     */
    protected PSTarget target() {
        if (_target == null) {
            _target = (_targetKey == null) ? new PSTarget( _owner )
            : new PSTarget( _valueForKey( _owner, _targetKey ) );
        }
        return _target;
    }
    
    /**
     * Returns the argument of the action method. In general
     * the object returned is obtained by the call: owner().subjectKey().
     */
    protected Object subject() {
        if (_subject == null) {
            _subject = (_subjectKey == null) ? null : _valueForKey( _owner, _subjectKey );
        }
        return _subject;
    }
    
    /**
     * Calls the actionKey method on the target object. The result of this call
     * is like doing owner().targetKey().actionKey( owner().subjectKey() ).
     */
    public void performAction() throws InvocationTargetException {
        logger().trace( this, "Target="+target().target().getClass().getName() +
                " action="+_actionKey + " subject="+_subject );
        target().performMethod( _actionKey, argumentObjects() );
    }

    /**
     * Calls the actionKey method on the target object. The result of this call
     * is like doing owner().targetKey().actionKey( owner().subjectKey() ).
     * This method differs from <code>performAction</code> in that it checks
     * that the receiver responds to the method before attempting the invocation.
     */
    public void safePerformAction() throws InvocationTargetException {
        logger().trace( this, "Target="+target().target().getClass().getName() +
                " action="+_actionKey + " subject="+_subject );
        if ( target().respondsTo( _actionKey, argumentClasses() ) ) {
            target().performMethod( _actionKey, argumentObjects() );
        } else {
            logger().warn( this, "Target does not respond to action: " + _actionKey );
        }
    }
    
    protected Object[] argumentObjects() {
        return (subject() == null) ? null : new Object[] { subject() };
    }
    
    protected Class[] argumentClasses() {
        return (subject() == null) ? null : 
            PSTarget.classesForObjects( argumentObjects() );
    }
    
    protected String prependGet( String aKey ) {
        assertTrue( "Null key", aKey != null);
        assertTrue( "Invalid key: " + aKey, aKey.length() >= 1);
        String firstChar = aKey.substring(0, 1);
        String remainingChars = aKey.substring(1, aKey.length());
        
        String s = "get" + firstChar.toUpperCase() + remainingChars;
        logger().debug( this, "getter="+s );
        
        return s;
    }
    
    protected Object _valueForKey( Object target, String aKey ) {
        Object returnValue = null;
        PSTarget invocationTarget = new PSTarget( target );
        try {
            
            if (invocationTarget.respondsTo( aKey )) {
                returnValue = invocationTarget.performMethod( aKey );
            } else if (invocationTarget.respondsTo( prependGet(aKey) )) {
                logger().debug( this, "invocationTarget.performMethod" );
                returnValue = invocationTarget.performMethod( prependGet(aKey) );
            }
        } catch (InvocationTargetException ex) {
            logger().error( this, "Error while evaluating value for key:[" + aKey +
                    "] Returning null", ex );
            returnValue = null;
        }
        
        logger().trace( this, "valueForKey: target="+target.getClass().getName() +
                " Key="+aKey + " Value="+returnValue );
        
        return returnValue;
    }
    
     /// P E R S I S T E N C E  /////////////////////////////////////////////////
    public void takePropertyDict( PSDict dict ) {}
} /// End GeneriAction