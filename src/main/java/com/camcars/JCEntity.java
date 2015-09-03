
package com.camcars;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JCEntity extends PSObject {

    private ContentDesc _contentDesc   = null;

    public JCEntity() {
        super();
        setContentDesc( ContentDesc.singleton() );
    }

    public int getId() {
        PSInt pKey = (PSInt )getPKey();
        return (pKey == null) ? 0 : pKey.intValue();
    }

    public void setId(int id) {
        setPKey( new PSInt( id ) );
    }

    public PSID getGID() { return (PSID )valueForKey( "gID" ); }

    public void setGID( PSID gid ) {
        takeValueForKey( gid, "gID" );
    }

    public Object getPKey() {

        PSID gid = getGID();
        return (gid == null) ? new PSInt(0) : gid.pKey();

    }

    public boolean equals( Object obj ) {
        if ( obj == null ) return false;
        if ( ! (obj instanceof JCEntity) ) return false;
        return this.getId() == ((JCEntity )obj).getId();
    }

    public int hashCode() { return getId(); }

    public void setPKey( Object pKey ) { setGID( new PSID( pKey ) ); }

    public String getName() {
        return shortName();
    }

    public String shortName() {
        return (String )valueForKey("shortName");
    }

    public void setName( String aName ) {
        setShortName( aName );
    }

    public void setShortName( String aName ) {
        //ensureNameUnicity( aName );
        takeValueForKey( aName, "shortName" );
    }

    public String fullName() {
        return (String )valueForKey("fullName");
    }

    public void setFullName( String aName ) {
        takeValueForKey( aName, "fullName" );
    }

    public String description() {
        return (String )valueForKey( "desc" );
    }

    public void setDescription( String aDesc ) {
        takeValueForKey( aDesc, "desc" );
    }

    public void prepareForCoding() {
        // TODO Auto-generated method stub

    }

    public String toString() {
        return getClass().getName() + "{" +
                " id=" + getGID() +
                " name=" + shortName() + " }";
    }

    public static SessionFactory getSessionFactory() {

        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());

        log( JCEntity.class, "Config="+configuration + " Builder="+builder + " factory="+factory );

        return factory;

    }

    public static Session getSession() {

        Session session = null;

        try {
            session = JCEntity.getSessionFactory().openSession();
        } catch (HibernateException e) {
            logger().error( "Error connecting to database server", e);
            e.printStackTrace();
        }

        return session;
    }

    public void saveEntity() {
        saveEntity( getSession() );
    }

    public void deleteEntity() {
        deleteEntity( getSession() );
    }

    public void deleteEntity( Session session ) {
        session.delete(this);
    }

    public void saveEntity(  Session session) {
        Serializable save = session.save(this);
    }

    public static JCEntity entityWithBindings( Session session, String entityName, Map bindings ) {

        List<JCEntity> entityList = entitiesWithBindings( session, entityName, bindings );
        JCEntity anEntity = ( (entityList.size() > 0) ? entityList.get(0) : null );

        return anEntity;
    }

    public static List<JCEntity> entitiesWithBindings( Session session, String entityName, Map bindings ) {

        JCFetchRequest statement;
        List<JCEntity> entityList = null;

        try {

            statement = new JCFetchRequest( session, entityName );
            statement.setPredicateBindings( bindings );
            entityList = statement.listEntities();

        } catch (Exception e) {
            logger().error( "Error fetching entity by name", e );
        }

        return entityList;

    }

    public static JCEntity entityByName( Session session, String entityName, String nameValue ) {

        JCEntity enEntity = null;
        JCFetchRequest statement;
        List<JCEntity> entityList;

        try {

            statement = new JCFetchRequest( session, entityName );
            statement.setPredicate( "ZNAME = " + "'" + nameValue + "'" );
            entityList = statement.listEntities();
            enEntity = ( (entityList.size() > 0) ? entityList.get(0) : null );

        } catch (Exception e) {
            logger().error( "Error fetching entity by name", e );
            e.printStackTrace( System.err );
        }

        return enEntity;

    }

    public static JCEntity entityById( Session session, String entityName, String entity_id ) {

        JCEntity enEntity = null;
        JCFetchRequest statement;
        List<JCEntity> entityList;

        try {

            statement = new JCFetchRequest( session, entityName );
            statement.setPredicate( "Z_PK = " + "'" + entity_id + "'" );
            entityList = statement.listEntities();
            enEntity = ( (entityList.size() > 0) ? entityList.get(0) : null );

        } catch (Exception e) {
            logger().error( "Error fetching entity by Id", e );
            e.printStackTrace( System.err );
        }

        return enEntity;

    }

    ///   C O N T E N T   D E S C R I P T I O N   ///////////////////////////////
    /**
     * This category of method describes the relationships a PSContent entertains
     * with other PSContents.
     * Whereas PSMetaContent provides data when the PSContent is constructed from
     * external files and when it is encoded and decoded.
     */
    public ContentDesc contentDesc() {
        return _contentDesc;
    }

    public void setContentDesc( ContentDesc aDesc ) {
        _contentDesc = aDesc;
    }

    public PSArray attr() { return contentDesc().attr(); }

    public PSArray toOne() { return contentDesc().toOne(); }

    public PSArray toMany() { return contentDesc().toMany(); }

    public PSArray relations() { return contentDesc().relations(); }

    public PSArray allProperties() { return contentDesc().allProperties(); }

    ///   S N A P S H O T I N G   ///////////////////////////////

    public PSDict snapshot() {

        PSDict snapshot;
        PSArray array;
        Object value = null;

        prepareForCoding();
        array = allProperties();
        snapshot = new PSDict(10);
        snapshot.put( SNAPSHOT_CLASS_KEY, getClass().getName() );

        for ( int i= 0; i < array.size(); i++ ) {
            Property aProp = (Property )array.get(i);
            value = super.valueForKey( ( aProp.key() ) );

//	            if ( this instanceof PSChapter && aProp.key().equals("bodyText") )
//	                debug( this, "Stop");

            if ( value == null || value instanceof PSPlistCoder.NullValue ) {
                continue;
            }
            if ( value instanceof Class ) {
                Class aClass = (Class )value;
                PSObject replacement = new PSCustomCoder.ClassCoder( aClass );
                value = replacement.snapshot();
            } else if ( value instanceof PSRoot ) {
                value = ((PSRoot )value).snapshot();
            } else if ( value instanceof PSArray ) {
                if ( ((PSArray )value).isEmpty() ) continue;
                value = ((PSArray )value).snapshot();
            }
            snapshot.put( aProp.key(), value );
        }

        return snapshot;
    }

    public void takePropertyDict( PSDict dict ) {
        PSArray properties = allProperties();

        for ( int i=0; i < properties.size(); i++ ) {
            Property attr = (Property )properties.get( i );
            Object value = dict.get( attr.key() );
            if ( value instanceof PSDict ) {
                value = PSRoot.objectFromSnapshot( (PSDict )value );
            }
            takeValueForKey( value, attr.key() );
        }

    }

    ///   N E S T E D   ////////////////////////////////////////////////////////
    public static class ContentDesc {

        private PSArray _Attributes = null;
        private PSArray _ToOne  = null;
        private PSArray _ToMany = null;
        private PSArray _Properties = null;
        private PSArray _Relations = null;

        private static ContentDesc _Singleton = null;

        public PSArray attr() {
            if ( _Attributes == null ) {
                _Attributes = createAttributes();
            }

            return _Attributes;
        }

        public static ContentDesc singleton() {
            if ( _Singleton == null )
                _Singleton = new ContentDesc();

            return _Singleton;
        }

        private PSArray createAttributes() {
            PSArray result = new PSArray();

            result.add( new Attr( "gID" ) );
            result.add( new Attr( "shortName" ) );
            result.add( new Attr( "fullName" ) );

            return result;
        }

        public PSArray toOne() {
            ToOne aRel;

            if ( _ToOne == null ) {
                _ToOne = createToOnes();
            }

            return _ToOne;
        }

        private PSArray createToOnes() {
            PSArray result = new PSArray();
            ToOne aRel;

//	            aRel = new ToOne( PSMetaContent.class, "metaContent" );
//	            aRel.setDefaultValue( PSMetaContent.defaultValue() );
//	            result.add( aRel );

            return result;
        }

        public void setToOne( PSArray anArray ) { _ToOne = anArray; }

        public void addToOne( ToOne aProp ) {
            if ( _ToOne == null )
                _ToOne = createToOnes();

            _ToOne.add( aProp );
        }

        public void addToOne( Class aClass, String aProp ) {
            addToOne( new ToOne( aClass, aProp ) );
        }

        public PSArray toMany() {
            if ( _ToMany == null ) {
                _ToMany = createToManys();
            }

            return _ToMany;
        }

        private PSArray createToManys() {
            PSArray result = new PSArray();

//	            result.add( new ToMany( JCEntity.class, "refs"  ) );

            return result;
        }

        public void setToMany( PSArray anArray ) { _ToMany = anArray; }

        public void addToMany( ToMany aProp ) {
            if ( _ToMany == null )
                _ToMany = createToManys();

            _ToMany.add( aProp );
        }

        public void addToMany( Class aClass, String aProp ) {
            addToMany( new ToMany( aClass, aProp ) );
        }

        public PSArray relations() {
            if ( _Relations == null ) {
                _Relations = new PSArray( toOne() );
                _Relations.addAll( toMany() );
            }

            return _Relations;
        }

        public PSArray allProperties() {
            PSArray result = null;

            if ( _Properties == null ) {
                result = new PSArray( relations() );
                result.addAll( attr() );
            }

            return result;
        }
    }

    public static class Property {

        private String _key;
        private Object _defaultValue = null;

        private Property( String aKey ) {
            setKey( aKey );
        }

        public String key() { return _key; }
        public void setKey( String aKey ) { _key = aKey; }

        public Object defaultValue() { return _defaultValue; }
        public void setDefaultValue( Object obj ) { _defaultValue = obj; }

        public String toString() {
            return getClass().getName() + "{ key=" + key() + " }";
        }

    }

    public static class Attr extends Property {

        public Attr( String aKey ) {
            super( aKey );
        }

    }

    public static class Relation extends Property {

        private Class _toClass;

        public Relation( Class aDestination, String aKey ) {
            super( aKey );
            setToClass( aDestination );
        }

        public Class toClass() { return _toClass; }
        public String toClassName() { return toClass().getName(); }
        public void setToClass( Class aClass ) {
            _toClass = aClass;
        }

        public JCEntity _defaultValue() { return (JCEntity )defaultValue(); }

        public String toString() {
            return getClass().getName() + "{ key=" + key() +
                    " dest=" + toClassName() + "}";
        }
    }

    public static class ToMany extends Relation  {

        public ToMany( Class aDestination, String aKey ) {
            super( aDestination, aKey );
        }

    }

    public static class ToOne  extends Relation  {

        public ToOne( Class aDestination, String aKey ) {
            super( aDestination, aKey );
        }

    }

}
