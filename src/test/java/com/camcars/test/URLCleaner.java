package com.camcars.test;

import com.camcars.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

public class URLCleaner extends TestCase {

    private static String _DirtyListingCarPK = "24";
    private static String _DirtyImageListCarPK = "15";

    private static Log _Logger = LogFactory.getLog(TestCase.class);
    private Map<String, Integer> _MakeTable;

    private Session _session = null;
    private Transaction _transaction = null;
    private boolean _shouldCommit = true;

    public Log logger() {
        return _Logger;
    }

    public void init() {
        _MakeTable = new HashMap<String, Integer>();
        _MakeTable.put( "Mercedes", new Integer(31) );
        _MakeTable.put( "Toyota", new Integer(13) );
        _MakeTable.put( "Suzuki", new Integer(5) );
        _MakeTable.put( "Honda", new Integer(1) );
        _MakeTable.put( "Mitsubishi", new Integer(7) );
        _MakeTable.put( "Nissan", new Integer(3) );
        _MakeTable.put( "BMW", new Integer(0) );
        _MakeTable.put( "Audi", new Integer(0) );

        _session = JCEntity.getSession();

    }

    public void setUp() throws Exception {
        super.setUp();
        init();
        _transaction = _session.beginTransaction();
    }

    public void tearDown() throws Exception {
        if (_shouldCommit) {
            logger().info("Commit transaction...");
            _transaction.commit();
            logger().info("Commit successful");
        } else {
            logger().info("Rollback transaction...");
            _transaction.rollback();
            logger().info("Rollback successful");
        }
        super.tearDown();
    }

    protected Session getSession() {
        return _session;
    }

    public void finalize() {
        if ( _session != null ) _session.close();
    }

    public void testCanonicalModels() {

        Iterator<String> keyIter = _MakeTable.keySet().iterator();

        try {
            while ( keyIter.hasNext() ) {
                String makeName = (String )keyIter.next();

                logger().info( "Processing " + makeName );
                _testCanonicalModelsForMakeName(makeName);

            }
        } catch (Exception e) {
            _shouldCommit = false;
            e.printStackTrace();
        }
    }

    public void _testCanonicalModelsForMakeName( String makeName ) throws Exception {

        Session session = getSession();
        JCMake aMake = (JCMake )JCEntity.entityByName( getSession(), "JCMake", makeName);
        assertNotNull( "Couldn't find make with name = " + makeName, aMake);

        // Get the Make entity
        Collection<JCModel> models = aMake.getModels();

        // Create canonical models form the files in models subfolder
        // Save those in the database
        Collection<JCModel> canonicalModels = aMake.getCanonicalModels( getSession() );
        assertEquals( "Wrong number of cannonical models for make: " + aMake.getName(),
                _MakeTable.get( aMake.getName() ), new Integer( canonicalModels.size() ) );

        // Loop on all the models and make sure their canonical representation is present
        models.forEach(
                (JCModel aModel) -> {
                    Collection<JCCar> cars = aModel.getCars();
                    JCModel canonicalModel = aMake.getCanonicalModelByName( getSession(), aModel.getName() );
                    assertNotNull( "Couldn't find canonical model for make " + aMake.getName(), canonicalModel);
                }
        );

        // Loop on all the models and transfer their car into the canonical representation
        transferCarsToCanonicalModel( aMake );

    }

    protected void transferCarsToCanonicalModel( JCMake aMake ) {

        try {

            Collection<JCModel> models = aMake.getCanonicalModels( getSession() );
            logger().info( "Transfering " + aMake.getName() + "'s " + String.valueOf( models.size() ) +
                    " models to canonical form" );
            models.forEach(
                    (JCModel aModel) -> {
                        Collection<JCCar> cars = aModel.getCars();
                        JCModel canonicalModel = aMake.getCanonicalModelByName( getSession(), aModel.getName() );
                        assertNotNull( "Couldn't find canonical model for make " + aMake.getName(), canonicalModel );
                        /* Transfer all cars from the model to its canonical representation */
                        canonicalModel.addCars( aModel.getCars() );
                        aModel.deleteEntity( getSession() );
                    }
            );


        } catch (RuntimeException e) {
            _shouldCommit = false;
            e.printStackTrace(); // or display error message
        }

    }

    //////    C L E A N I N G    C A R   L I S T I N G    ////////////////////////////////////////
    public void testBatchCleanCarListing() {

        JCFetchRequest dirtyCarsRequest = new JCFetchRequest( getSession(), "JCCar" );

        dirtyCarsRequest.setPredicate( "zlisting like '" + JCCar._DirtyChars + "'" );
        Collection<JCEntity> dirtyCars = dirtyCarsRequest.listEntities();
        assertTrue(
                "Wrong number of dirty cars: " + String.valueOf( dirtyCars.size() ),
                dirtyCars.size() == 249
        );
        _batchCleanCarListing(dirtyCars);

    }

    protected void _batchCleanCarListing(Collection<JCEntity> cars) {

        Iterator<JCEntity> carIter;

        if ( cars == null ) return;

        carIter = cars.iterator();
        while ( carIter.hasNext() ) {
            JCCar aCar = (JCCar )carIter.next();

            if ( aCar == null ) continue;
//          aCar.cleanListing();
//          aCar.renameListing();
        }

    }

    public void testCleanCarListing() {

        try {

            JCCar aCar = (JCCar )JCEntity.entityById( getSession(), "JCCar", _DirtyListingCarPK );
            assertTrue( "The car should be dirty", aCar.getListing().contains( JCCar._DirtyImgNameChars ) );
            boolean success = aCar.cleanListing();
            assertTrue( "The car listing renaming failed", success );
            assertTrue( "The car listing has no clean chars after cleaning", aCar.getListing().contains( JCCar._CleanImgNameChars ) );
            assertFalse( "The car is still dirty after cleaning", aCar.getListing().contains( JCCar._DirtyImgNameChars ) );

            // Re-fetch the car from the DB an test that its listing is now clean
            aCar = (JCCar )JCEntity.entityById( getSession(), "JCCar", _DirtyListingCarPK );
            assertFalse( "The car should be dirty", aCar.getListing().contains( JCCar._DirtyImgNameChars ) );

        } catch (Exception e) {
            _shouldCommit = false;
            e.printStackTrace();
        }

    }

    ////// C L E A N I N G    C A R   I M A G E   N A M E S    /////////////////////////////
    public void testBatchCleanCarImageNames() {

        JCFetchRequest dirtyCarsRequest = new JCFetchRequest( getSession(), "JCCar" );

        dirtyCarsRequest.setPredicate( "imageList like '%" + "_$_" + "%'" );
        Collection<JCEntity> dirtyCars = dirtyCarsRequest.listEntities();
        assertTrue(
                "Wrong number of dirty cars: " + String.valueOf( dirtyCars.size() ),
                dirtyCars.size() == 901
        );
        _batchCleanCarImageNames(dirtyCars);

    }

    protected void _batchCleanCarImageNames(Collection<JCEntity> cars) {

        Iterator<JCEntity> carIter;

        if ( cars == null ) return;

        carIter = cars.iterator();
        while ( carIter.hasNext() ) {
            JCCar aCar = (JCCar )carIter.next();

            if ( aCar == null ) continue;
            aCar.cleanImageList();
//            aCar.renameImageNames();

        }

    }

    public void testCleanCarImageNames() {

        try {

            JCCar aCar = (JCCar )JCEntity.entityById( getSession(), "JCCar", _DirtyImageListCarPK );
            assertTrue( "The car should be dirty", aCar.getImageList().contains( "_$_" ) );
            boolean success = aCar.cleanImageList();
            assertTrue( "The car listing renaming failed", success );
            assertTrue( "The car listing has no clean chars after cleaning", aCar.getImageList().contains( JCCar._CleanImgNameChars ) );
            assertFalse( "The car is still dirty after cleaning", aCar.getImageList().contains( JCCar._DirtyImgNameChars ) );

            // Re-fetch the car from the DB an test that its listing is now clean
            aCar = (JCCar )JCEntity.entityById( getSession(), "JCCar", _DirtyImageListCarPK );
            assertFalse( "The car should be dirty", aCar.getImageList().contains( "_$_" ) );

        } catch (Exception e) {
            _shouldCommit = false;
            e.printStackTrace();
        }

    }

}