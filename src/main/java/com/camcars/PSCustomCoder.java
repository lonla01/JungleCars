
/**
 * PSCustomCoder.java
 *
 * Created on 27 juin 2009, 13:48
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

public abstract class PSCustomCoder extends PSObject
        implements PSCoding.CustomCoding {
    
    private Class _classForCoder;
    private Object _codingTarget;
    
    public PSCustomCoder( Class classForCoder ) {
        setClassForCoder(classForCoder);
        //fillPropertyDict();
    }
    
    public abstract void takePropertyDict( PSDict dict );
    
    public abstract void fillPropertyDict();
    
    public Object codingTarget() { return _codingTarget; }
    
    public void setCodingTarget( Object anObject ) { _codingTarget = anObject; }
    
    public Class classForCoder() { return _classForCoder; }
    
    public void setClassForCoder( Class aClass ) { _classForCoder = aClass; }

    public PSDict snapshot() {
        PSDict aDict = super.snapshot();



        if ( aDict.containsKey( "id" ) ) {
            Object objID = aDict.get( "id" );
            if ( objID instanceof PSDict ) {
                objID = PSRoot.objectFromSnapshot( (PSDict )objID );
            }
            PSID aValue = (PSID )objID;
            aDict.put( "id", aValue.snapshot() );
        }

        return aDict;
    }
    
    ///  I N N E R   C L A S S E S  ////////////////////////////////////////////    
    public static class ClassCoder extends PSCustomCoder {
        
        public ClassCoder() {
            super( PSCustomCoder.ClassCoder.class );
        }
                
        public ClassCoder( Class aClass ) {
            this();
            setCodingTarget( aClass );
            fillPropertyDict();
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( dict.get("class"), "class" );
        }
        
        public void fillPropertyDict() {
            takeValueForKey( className(), "class" );
        }
        
        public String className() { 
            return ( (Class )codingTarget() ).getName(); 
        }
        
        public Object decodeObject() {
            Class aClass = null;
            String className = null;
            
            try {
                className = (String )valueForKey( "class" );
                aClass.forName( className );
            } catch ( Exception e ) {
                logger().error( this, "Couln't decode class: " + className, e );
                aClass = null;
            }
            
            return aClass;
        }
         
    }
    
    
     
    public static class FontCoder extends PSCustomCoder implements PSCoding.CustomCoding {
        
        public FontCoder() { super(FontCoder.class); }
        
        public FontCoder( Font font ) {
            this();
            setCodingTarget( font );
            fillPropertyDict();
        }

        public void fillPropertyDict() {
            super.takeValueForKey( font().getName(), "name" );
            super.takeValueForKey( PSInt.with( font().getStyle() ), "style" );
            super.takeValueForKey( PSInt.with( font().getSize() ), "size" );
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( (String )dict.get("name"), "name" );
            super.takeValueForKey( (PSInt )dict.get("style"), "style" );
            super.takeValueForKey( (PSInt )dict.get("size"), "size" );
        }
        
        public Font decodeFont() {
            return (Font )decodeObject();
        }
        
        public Object decodeObject() {
            return new Font( ((String  )super.valueForKey("name")),
                    ( (PSInt )super.valueForKey("style") ).intValue(),
                    ( (PSInt )super.valueForKey("size" ) ).intValue() );
        }
        
        public Font font() { return (Font )codingTarget(); }
    }
    
    public static class ColorCoder extends PSCustomCoder implements PSCoding.CustomCoding {
        
        public ColorCoder() { super(ColorCoder.class); }
        
        public ColorCoder( Color color ) {
            this();
            setCodingTarget( color );
            fillPropertyDict();
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( (PSInt )dict.get("red"  ), "red"   );
            super.takeValueForKey( (PSInt )dict.get("green"), "green" );
            super.takeValueForKey( (PSInt )dict.get("blue" ), "blue"  );
        }
        
        public void fillPropertyDict() {
            super.takeValueForKey( PSInt.with( color().getRed() ), "red" );
            super.takeValueForKey( PSInt.with( color().getGreen() ), "green" );
            super.takeValueForKey( PSInt.with( color().getBlue() ), "blue" );
        }
        
        public Color decodeColor() {
            return (Color )decodeObject();
        }
        
        public Object decodeObject() {
            return new Color( ((PSInt )super.valueForKey("red")).intValue(),
                    ((PSInt )super.valueForKey("green")).intValue(),
                    ((PSInt )super.valueForKey("blue")).intValue() );
        }
        
        public Color color() { return (Color )codingTarget(); }
    }
        
    public static class JComponentCoder extends PSCustomCoder implements PSCoding.CustomCoding {
        
        public JComponentCoder() { super(JComponentCoder.class); }
        
        public JComponentCoder( JComponent aComponent ) {
            this();
            setCodingTarget( aComponent );
            fillPropertyDict();
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( (String )dict.get("class"), "class" );
            super.takeValueForKey( (Dimension )dict.get("size"), "size" );
            super.takeValueForKey( (Dimension )dict.get("minimumSize"), "minimumSize" );
            super.takeValueForKey( (Dimension )dict.get("preferredSize"), "preferredSize" );
            super.takeValueForKey( (Dimension )dict.get("maximumSize"), "maximumSize" );
            super.takeValueForKey( (Float )dict.get("alignmentX"), "alignmentX" );
            super.takeValueForKey( (Float )dict.get("alignmentY"), "alignmentY" );
            super.takeValueForKey( (String )dict.get("toolTip"), "toolTip" );
        }
        
        public void fillPropertyDict() {
            super.takeValueForKey( component().getClass().getName(), "class" );
            super.takeValueForKey( component().getSize(), "size" );
            super.takeValueForKey( component().getMinimumSize(), "minimumSize" );
            super.takeValueForKey( component().getMaximumSize(), "maximumSize" );
            super.takeValueForKey( component().getPreferredSize(), "preferredSize" );
            super.takeValueForKey( new Float( component().getAlignmentX() ), "alignmentX" );
            super.takeValueForKey( new Float( component().getAlignmentY() ), "alignmentY" );
            super.takeValueForKey( component().getToolTipText(), "toolTip" );
        }
        
        public JComponent component() { return (JComponent )codingTarget(); }
        
        public Object decodeObject() {
            JComponent retValue = null;
            String className = null;
            Class menuItemClass;
            
            try {
                className = (String )super.valueForKey("class");
                if ("javax.swing.Box$Filler".equals(className)) {
                    retValue = new Box.Filler( (Dimension )super.valueForKey("minimumSize"),
                            (Dimension )super.valueForKey("preferredSize"),
                            (Dimension )super.valueForKey("maximumSize") );
                } else {
                    menuItemClass = Class.forName(className);
                    retValue = (JComponent )menuItemClass.newInstance();
                    Dimension size = (Dimension )super.valueForKey("size");
                    if ( size != null && !size.equals( new Dimension(0,0) ) )
                        retValue.setSize( size );
                    retValue.setMinimumSize( (Dimension )super.valueForKey("minimumSize") );
                    size = (Dimension )super.valueForKey("preferredSize");
                    if ( size != null && !size.equals( new Dimension(0,0) ) )
                        retValue.setPreferredSize( size );
                    size = (Dimension )super.valueForKey("maximumSize");
                    if ( size != null && !size.equals( new Dimension(0,0) ) )
                        retValue.setMaximumSize( size );
                }
            } catch (Exception e) {
                this.logger().error( "Error instantiating a JComponent of class: "+className, e);
            }
            Float alignmentX = (Float )super.valueForKey("alignmentX");
            Float alignmentY = (Float )super.valueForKey("alignmentY");
            if (alignmentX != null)
                retValue.setAlignmentX( alignmentX.floatValue() );
            if (alignmentY != null)
                retValue.setAlignmentY( alignmentY.floatValue() );
            retValue.setToolTipText( (String )super.valueForKey("toolTip") );
            
            return retValue;
        }
    }
    
    public static class JSeparatorCoder extends PSCustomCoder implements PSCoding.CustomCoding {
        
        public JSeparatorCoder() { super( JSeparator.class ); }
        
        public JSeparatorCoder(JSeparator aSeparator) {
            this();
            setCodingTarget( aSeparator );
            fillPropertyDict();
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( (PSInt )dict.get("orientation"), "orientation" );
        }
        
        public void fillPropertyDict() {
            takeValueForKey( PSInt.with( separator().getOrientation() ), "orientation" );
        }
        
        public JSeparator separator() { return (JSeparator )codingTarget(); }
        
        public Object decodeObject() {
            PSInt orientation = (PSInt )super.valueForKey("orientation");
            
            if (orientation == null)
                orientation = PSInt.with(JSeparator.HORIZONTAL);
            
            return new JSeparator( orientation.intValue() );
        }
    }
    
    public static class JToolBarCoder extends JComponentCoder implements PSCoding.CustomCoding {
        
        public JToolBarCoder() {  }
        
        public JToolBarCoder( JToolBar aToolBar ) {
            super(aToolBar);
            super.takeValueForKey( collectionFromArray( aToolBar.getComponents() ), "components" );
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takePropertyDict( dict );
            super.takeValueForKey( (Collection )dict.get("components"), "components" );
        }
        
        public Object decodeObject() {
            JToolBar aToolBar = (JToolBar )super.decodeObject();
            Collection components = (Collection )super.valueForKey("components");
            Iterator iter = components.iterator();
            while(iter.hasNext()) {
                Object obj = iter.next();
                if (obj instanceof JComponent)
                    aToolBar.add( (JComponent)obj );
                else {
                    String className = (obj == null) ? null : obj.getClass().getName();
                    this.logger().error( "Got a "+className + " from the toolBar" );
                }
            }
            aToolBar.setPreferredSize( new Dimension( 700, 40 ) );
            
            return aToolBar;
        }
    }
    
    public static class JMenuBarCoder extends JComponentCoder implements PSCoding.CustomCoding {
        
        public JMenuBarCoder() {  }
        
        public JMenuBarCoder( JMenuBar aMenuBar ) {
            super(aMenuBar);
            super.takeValueForKey( collectionFromArray( aMenuBar.getComponents() ), "menus" );
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takePropertyDict( dict );
            super.takeValueForKey( (Collection )dict.get("menus"), "menus" );
        }
        
        public Object decodeObject() {
            JMenuBar aMenuBar = (JMenuBar )super.decodeObject();
            Collection items = (Collection )super.valueForKey("menus");
            Iterator iter = items.iterator();
            while(iter.hasNext()) {
                Object obj = iter.next();
                if (obj instanceof JMenu)
                    aMenuBar.add( (JMenu )obj );
                else
                    this.logger().error( "Got a "+obj.getClass().getName() + " from the menuBar" );
            }
            
            return aMenuBar;
        }
    }
    
     
    
    public static class Replacer {
        /////////////   C U S T O M   C O D I N G   S U P P O R T   //////////////
        
        private static PSDict _classToCoder = null;
        private static PSDict _classNameToCoder = null;

        public Replacer() { }

        protected Collection replacementObjects( Collection originals ) {
            Collection replacements = null;
            
//            replacements = (Collection )classToCoder().get( originals.getClass() );
            
            if ( replacements == null ) 
                replacements = new PSArray();
            else
                replacements.clear();
            
//            if ( originals.isEmpty() ) return originals;
            
            Iterator iter = originals.iterator();
            while(iter.hasNext()) {
                replacements.add( replacementObject( iter.next() ) );
            }

            return replacements;
        }

        protected Collection originalObjects( Collection replacements ) {
            Collection originals = new PSArray();

            Iterator iter = replacements.iterator();
            while(iter.hasNext()) {
                originals.add( originalObject( iter.next() ) );
            }

            return originals;
        }
        
        protected static void addMapping( Class orig, Class repl ) {
            Object coder;
            PSString className;
            
            try {
                coder = repl.newInstance();
                _classToCoder.put( orig, coder );
                className = PSString.with( coder.getClass().getName() );
                _classNameToCoder.put( className, coder );
            } catch (Exception ex) {
                logger().error( "PListCoder: Instantiation error", ex );
            }
        }
        
        public static PSDict classToCoder() {
            if (_classToCoder == null) {
                _classToCoder = new PSDict();
                _classNameToCoder = new PSDict();
                
                addMapping( PSString.class          , PSString.Coder.class                    );
                addMapping( PSID.class              , PSID.Coder.class                        );
                addMapping( Class.class             , PSCustomCoder.ClassCoder.class          );
                addMapping( Color.class             , PSCustomCoder.ColorCoder.class          );
                addMapping( Font.class              , PSCustomCoder.FontCoder.class           );
                addMapping( JSeparator.class        , PSCustomCoder.JSeparatorCoder.class     );
                addMapping( JMenuBar.class          , PSCustomCoder.JMenuBarCoder.class       );
                addMapping( JToolBar.class          , PSCustomCoder.JToolBarCoder.class       );
                addMapping( JComponent.class        , PSCustomCoder.JComponentCoder.class     );               
            }
            
            return _classToCoder;
        }
        
        protected static boolean isCollection( Object obj ) {
            return    (obj instanceof Collection   ) &&
                    ! (obj instanceof PSString     );
        }
        
        protected static boolean isKeyValueCoding( Object obj ) {
            return    (obj instanceof PSCoding.KeyValueCoding);
        }
         
        private static PSDict classNameToCoder() {
            if ( _classNameToCoder == null ) {
                classToCoder();
            }
            
            return _classNameToCoder;
        }
        
        public static PSCoding.CustomCoding coderForClassName( Object className ) {
            return (PSCoding.CustomCoding )classNameToCoder().get( className );
        }
        
        protected Object replacementObject( Object original ) {
            Object replacement = original;
             
            return replacement;
        }

        protected Object originalObject( Object replacement ) {
            Object original = replacement;

            if (replacement instanceof PSCoding.CustomCoding)
                original = ((PSCoding.CustomCoding )replacement).decodeObject();
            else if ( isCollection( replacement ) )
                original = originalObjects( (Collection )replacement );
            
//            logger().verbose( this, "Original: " + original + " for " + replacement );
            
            return original;
        }

    } /// End Replacer
        
} /// End PSCustomCoder


