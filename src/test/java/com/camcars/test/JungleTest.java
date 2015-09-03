
package com.camcars.test;

import com.camcars.*;
import com.camcars.web.CarFinderBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.*;
import org.hibernate.Session;

import java.util.*;

public class JungleTest extends TestCase {

    private CarFinderBean _carFinder = null;
    private static String _CarId = "321";
    private static String _CarMakeName = "Suzuki";
    private static String _CarModelName = "Swift";
    private static String _MercedesModelDirty = "mercedes-benz-200-clk-coupe-200-kompressor-chemnitz-ot-r%C3%B6hrsd";
    private static String _MercedesModelCano = "mercedes-benz-200-clk-coupe";
    private static String _MercedesModelCleaned = "mercedes-benz-200-clk-coupe-200-kompressor-chemnitz-ot-r_C3_B6hrsd";
    private static String _MercedesModel2 = "Mercedes-Benz-CLK-200";
    private static String _MercedesModelCleaned2 = "Mercedes-Benz-CLK-200";
    private Session _session = null;
    private static Log _Logger = LogFactory.getLog(TestCase.class);

    public Log logger() {
        return _Logger;
    }

    public JungleTest(String testMethodName) {
        super(testMethodName);
    }

    protected Session getSession() {
        return _session;
    }

    protected void setUp() {
        _session = JCEntity.getSession();
        _carFinder = new CarFinderBean();
    }

    public void testGetSUVs() {
        Collection suvs = _carFinder.getSUVs();
        assertEquals("Wrong count of SUVs " + String.valueOf(suvs.size()), 23, suvs.size());
    }

    public void testGetSaloons() {
        Collection cars = _carFinder.getSaloons();
        assertEquals("Wrong count of SUVs " + String.valueOf(cars.size()), 24, cars.size());
    }

    public void testGetSmallCars() {
        Collection cars = _carFinder.getSmallCars();
        assertEquals("Wrong count of SUVs " + String.valueOf(cars.size()), 43, cars.size());
    }

    // TODO: Fix this test
//    public void testGetAllCarTypes() {
//        Collection carTypes = _carFinder.getAllCarTypes();
//        assertEquals( "Wrong count of car types " + String.valueOf( carTypes.size() ), 6, carTypes.size() );
//    }

    public void testFetchByName() throws Exception {

        JCMake aMake = (JCMake) _carFinder.getEntityByName("JCMake", _CarMakeName);
        assertNotNull("Couldn't find make with name = " + _CarMakeName, aMake);

        JCModel aModel = aMake.getModels_().get(1);
        assertNotNull("Couldn't find make's models for make with name = " + _CarMakeName, aModel);

        //logger().info( "Got model: " + aModel );
        String modelName = aModel.getName();
        assertNotNull("Invalid name for model " + aModel.getId(), modelName);

//		String fuelType = aModel.getFuelType();
//		assertNotNull( "Invalid fuel type for model " + aModel.getId(), fuelType );
//        assertTrue("Invalid fuel type property", fuelType.equals("Petrol") || fuelType.equals("Diesel"));

        List<JCCar> cars = aModel.getCars_();
        logger().info("Cars: " + cars);
        if (cars.size() > 0) {
            JCCar aCar = cars.get(0);
            assertNotNull("couldn't find a car for model " + aModel.getName(), aCar);
        }

    }

    public void testFetchWithBindings() throws Exception {

        Map bindings = new TreeMap();

        JCMake aMake1 = (JCMake) JCEntity.entityByName(getSession(), "JCMake", _CarMakeName);
        bindings.put("name", _CarMakeName);
        JCMake aMake2 = (JCMake) JCEntity.entityWithBindings(getSession(), "JCMake", bindings);
        assertNotNull("Couldn't find make with name = " + _CarMakeName, aMake2);
        assertEquals("Got a different make using bindings " + aMake2.toString(), aMake1, aMake2);

        bindings.clear();
        bindings.put("price", "5,450 EUR");
        bindings.put("id", "282");
        JCCar aCar = (JCCar) JCEntity.entityWithBindings(getSession(), "JCCar", bindings);
        assertNotNull("Couldn't find car with bindings = " + bindings, aCar);
        assertEquals("Wrong id from fetched car " + aCar, String.valueOf(aCar.getId()), bindings.get("id"));
        assertEquals("Wrong price from fetched car " + aCar, aCar.getPrice(), bindings.get("price"));
    }

    public void testFetchPriceRange() throws Exception {

        Map bindings = new TreeMap();

        bindings.put("price", new PSRange("5,000 EUR", "6,000 EUR"));
        List<JCEntity> cars = (List<JCEntity>) JCEntity.entitiesWithBindings(getSession(), "JCCar", bindings);
        assertNotNull("Couldn't find car with bindings = " + bindings, cars);
        assertEquals("Wrong number from fetched car " + String.valueOf(cars.size()), cars.size(), 68);

    }

    public void testFetchById() throws Exception {

        JCCar aCar = _carFinder.getCarById(_CarId);
        assertNotNull("Couldn't find car with id = " + _CarId, aCar);

        JCModel aModel = aCar.getModel();
        assertNotNull("Couldn't find car's model for car with id = " + _CarId, aModel);

        logger().info("Got model: " + aModel);
        String modelName = aModel.getName();
        assertNotNull("Invalid name for model" + aModel.getId(), modelName);

        String fuelType = aModel.getFuelType();
        assertNotNull("Invalid fuel type for model" + aModel.getId(), fuelType);

    }


    public void testCanonicalModels() throws Exception {
        _testCanonicalModelCount(_CarMakeName, 5);
        _testCanonicalModelCount("Mercedes", 31);
        _testCanonicalModel(_CarMakeName, _CarModelName, _CarModelName);
        _testCanonicalModel("Mercedes", _MercedesModelDirty, _MercedesModelCano);
    }

    protected void _testCanonicalModelCount(String makeName, int modelCount) throws Exception {

        JCMake aMake = (JCMake) JCEntity.entityByName(getSession(), "JCMake", makeName);
        assertNotNull("Couldn't find make with name = " + makeName, aMake);

        Collection<String> modelNames_lowerCase = aMake.getLowerCaseCanonicalModelNames();
        assertEquals("Wrong number of cannonical model names", modelCount, modelNames_lowerCase.size());

        Collection models = aMake.getCanonicalModels(getSession());
        assertEquals("Wrong number of cannonical models", modelCount, models.size());

    }

    protected void _testCanonicalModel(String makeName, String modelName, String expectedModelName) throws Exception {

        JCMake aMake = (JCMake) JCEntity.entityByName(getSession(), "JCMake", makeName);
        Collection<String> modelNames_lowerCase = aMake.getLowerCaseCanonicalModelNames();

        String canonicalName = aMake.canonicalModelNameForRowName(modelName.toLowerCase());
        assertTrue("Couldn't find model " + canonicalName, modelNames_lowerCase.contains(canonicalName));

        JCModel aModel = (JCModel) aMake.getCanonicalModelByName(getSession(), modelName);
        assertNotNull("Couldn't find model with name = " + makeName, aModel);
        assertEquals("Couldn't find model for " + modelName, expectedModelName.toLowerCase(), aModel.getName().toLowerCase());

    }

    public void testCleanString() {
        String oldString = _MercedesModelDirty;
        String newString = _MercedesModelCleaned;
        assertEquals("Clean string operation failed", newString, JCCar.cleanDirtyListing(oldString));
    }

    public void testEntityRead() {

        int carId = Integer.parseInt(_CarId);
        getSession().beginTransaction();
        JCCar aCar = (JCCar) getSession().get(JCCar.class, carId);
        logger().info("testEntityRead() = " + aCar);
        getSession().getTransaction().commit();

    }


    static int _SerialNum = 1;

    private JCCar createCar() {

        JCCar aCar = new JCCar();
        aCar.setListing("Ferrari Testarossa " + String.valueOf( _SerialNum++ ) );
        aCar.setTitle( aCar.getListing() );
        aCar.setPrice("90,000 EUR");
        aCar.setDoorCount("2");
        aCar.setMileage("56000 Km");

        getSession().save(aCar);

        return aCar;
    }

    private JCMake createMake() {

        JCMake aMake = new JCMake();
        aMake.setName("Ferrari " + _SerialNum );
        getSession().save(aMake);

        return aMake;
    }

    private JCModel createModel() {

        JCModel aModel = new JCModel();
        aModel.setName("Testarossa " + _SerialNum );
        aModel.setFuelType( "Petrol" );
        aModel.setCarType("Coupé");
        getSession().save(aModel);

        return aModel;
    }

    public void testCarCreate() {

        getSession().beginTransaction();
        JCCar aCar = createCar();
        getSession().getTransaction().commit();
        logger().info("testEntityCreate() = " + aCar);

        aCar = (JCCar )getSession().get(JCCar.class, aCar.getId());
        assertNotNull( aCar );
    }

    public void testCarUpdate() {

        String newPrice = "80,000 EUR";
        String newMileage = "76000km";

        getSession().beginTransaction();
        JCCar aCar = createCar();
        aCar = (JCCar) getSession().load(JCCar.class, aCar.getId() );
        aCar.setPrice( newPrice );
        aCar.setMileage(newMileage);
        getSession().update(aCar);
        getSession().getTransaction().commit();

        aCar = (JCCar )getSession().get(JCCar.class, aCar.getId());
        assertEquals( newPrice, aCar.getPrice() );
        assertEquals( newMileage, aCar.getMileage() );

    }

    public void testCarDelete() {

        getSession().beginTransaction();
        JCCar aCar = createCar();
        aCar = (JCCar) getSession().load(JCCar.class, aCar.getId() );
        logger().info("testEntityDelete() = " + aCar);
        getSession().delete(aCar);
        getSession().getTransaction().commit();

        getSession().beginTransaction();
        aCar = (JCCar )getSession().get(JCCar.class, aCar.getId());
        assertNull("After deleting the car, the car should be null", aCar);
        getSession().getTransaction().commit();

    }

    public void testGraphCreate() {

        getSession().beginTransaction();
        // Creating the graph
        JCMake aMake = createMake();
        JCModel aModel = createModel();
        JCCar aCar = createCar();
        aModel.setMake(aMake);
        aCar.setModel(aModel);
        getSession().getTransaction().commit();

        // Deleting the graph
        getSession().beginTransaction();
        logger().info("Deleting make with id = " + aMake.getId());
        getSession().delete( aMake );
        aMake = (JCMake )getSession().get( JCMake.class, aMake.getId() );
        assertNull("After deleting the graph, the make should be null", aMake);
        getSession().getTransaction().commit();

    }

}