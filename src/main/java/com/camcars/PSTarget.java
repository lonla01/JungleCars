package com.camcars;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class PSTarget extends PSObject {
    
    private Object _target;
    private PSDict _methodCache = new PSDict();

    public PSTarget(Object target) {        
        assertFalse( "Invalid target", target == null );
        setTarget( target );
    }

    public Object target() { return _target; }
    
    public void setTarget( Object target ) { _target = target; }
    
    public String targetClass() {
        return target().getClass().getName();
    }
    
    public PSDict methodCache() {
        return _methodCache;
    }

    public void setMethodCache(PSDict methodCache) {
        _methodCache = methodCache;
    }

    /**
     * If we have more than one args the resolving is base only on the first arg.
     * Interfaces are not taken into account.
     */
    protected Method resolveMethod(String methodName, Class[] argClasses) {
        Method aMethod = null;

        try {
            Class superClass = null;
            Class[] argClassesCopy;

            aMethod = _resolveMethod( methodName, argClasses );
            if (argClasses == null || argClasses.length == 0)
                return aMethod;

            // We look upward the first arg class inheritance tree to find a matching method
            superClass = argClasses[0];
            argClassesCopy = new Class[argClasses.length];
            while (aMethod == null && superClass != null) {
                superClass = superClass.getSuperclass();
                argClassesCopy[0] = superClass;
                aMethod = _resolveMethod( methodName, argClassesCopy );
            }
        }  catch (SecurityException e) {
            logger().error( this, "Could not access method: " + methodName + " on target: "+target(), e);
            aMethod = null;
        }

        return aMethod;
    }
    
    private Method _resolveMethod(String methodName, Class[] argClasses) {
        Method aMethod = null;

        try {
            if (argClasses != null && argClasses.length >= 1) {
                logger().verbose( this, "Resolving method: " + methodName + "("+argClasses[0]+")"+
                               " on target: "+targetClass() );
            } else {
                logger().verbose( this, "Resolving method: " + methodName + "()"+
                               " on target: "+targetClass() );
            }
            
            // The method cache is used only for no args invocations
            if (argClasses == null || argClasses.length == 0) 
                aMethod = (Method )methodCache().get( methodName ); 
            if (aMethod == null)
                    aMethod = target().getClass().getMethod( methodName, argClasses );
            if (aMethod != null) {
                methodCache().put( methodName, aMethod );
            }
        } catch (NoSuchMethodException e) {
            logger().error("Could not find method: " + methodName + "("+argClasses+")"+
                           " on target: " + targetClass(), e);
            aMethod = null;
        }

        return aMethod;
    }

    public boolean respondsTo(String methodName) {
        boolean b = respondsTo( methodName, null );
        logger().verbose( this, "Target="+targetClass() + " Method="+methodName + " R="+b);

        return b;
    }

    public boolean respondsTo(String methodName, Class[] argClasses) {
        Method aMethod = resolveMethod( methodName, argClasses );

        return (aMethod != null) ? true : false;
    }

    public Object performMethod(String methodName) throws InvocationTargetException {
        return performMethod( methodName, null );
    }

    public Object performMethod(String methodName, Object[] arguments) 
    throws InvocationTargetException {
        if (arguments == null)
            arguments = new Object[] {};

        return _performMethod( methodName, arguments) ;
    }
    
    /**
     * This method differs from <code>performMethod</code> in that it checks that
     * the receiver really implements the method before proceeding with the invocation. 
     */
    public Object safePerformMethod(String methodName, Object[] arguments) 
    throws InvocationTargetException {
        if (arguments == null)
            arguments = new Object[] {};

        assertTrue( "Target does not respond to method: " + methodName,
                    respondsTo( methodName, classesForObjects( arguments ) ) );

        return _performMethod( methodName, arguments) ;
    }
    
    protected Object _performMethod(String methodName, Object[] arguments) 
    throws InvocationTargetException {
        Object returnValue;
        Method aMethod;

        try {
            aMethod = resolveMethod( methodName, classesForObjects( arguments ) );
            returnValue = aMethod.invoke( target(), arguments );
            if (arguments != null && arguments.length >= 1)
                logger().verbose( this, targetClass() + "." +
                    methodName + "("+arguments[0]+") = " + returnValue );
            else
                logger().verbose( this, targetClass() + "." +
                                methodName + "() = " + returnValue );
        } catch (Exception e) {
            logger().error( this, "Error invoking method: " +
                    targetClass() + "." + methodName, e );
            throw new InvocationTargetException(e);
        }
        return returnValue;
    }
    
    public static Class[] classesForObjects( Object[] objects ) {
        Class[] classes;

        if (objects == null) return null;
        classes = new Class[objects.length];
        for (int i=0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

     /// P E R S I S T E N C E  /////////////////////////////////////////////////
    public void takePropertyDict( PSDict dict ) {}

}