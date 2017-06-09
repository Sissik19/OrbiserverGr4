/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.orbisgis.orbisserver.ITTests;

import org.junit.Assert;
import org.junit.Test;
import org.wisdom.api.http.Result;
import org.wisdom.test.parents.Action;
import org.wisdom.test.parents.Invocation;
import org.wisdom.test.parents.WisdomTest;

import org.orbisgis.orbisserver.control.web.*;
import org.orbisgis.orbisserver.control.wps.*;

import javax.inject.Inject;

import static org.wisdom.test.parents.Action.action;

/**
 * An in-container test checking the application while it's executing.
 */
public class InContainerIT extends WisdomTest {
    /**
     * The @Inject annotation is able to inject (in tests)
     * the bundle context, controllers, services and
     * templates.
     */
    @Inject
    GetCapabilitiesController getCapabilitiesController;

    @Inject
    WelcomeController welcomeController;

    @Inject
    ExecuteController executeController;

    @Inject
    WpsOperationController wpsOperationController;

    /**
     * Checks that the index page content is good.
     */
    @Test
    public void testIndexPageContent() {
        // Call the action method as follows
        Action.ActionResult result = action(new Invocation() {
            @Override
            public Result invoke() throws Throwable {
                return getCapabilitiesController.getCapabilities();
            }
        }).invoke();

        //It returns a redirection to the index.html page
        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("OrbisServer"));
        Assert.assertTrue(toString(result).contains("Welcome"));
        Assert.assertTrue(toString(result).contains("Welcome to OrbisServer"));

        Assert.assertTrue(toString(result).contains("Variable distance buffer"));
        Assert.assertTrue(toString(result).contains("Fixed distance buffer"));
        Assert.assertTrue(toString(result).contains("Extract center"));
        Assert.assertTrue(toString(result).contains("Create a grid of points"));
        Assert.assertTrue(toString(result).contains("Create a grid of polygons"));
        Assert.assertTrue(toString(result).contains("Fixed extrude polygons"));
        Assert.assertTrue(toString(result).contains("Variable extrude polygons"));
        Assert.assertTrue(toString(result).contains("Geometry properties"));
        Assert.assertTrue(toString(result).contains("Reproject geometries"));
        Assert.assertTrue(toString(result).contains("Point table from CSV"));
        Assert.assertTrue(toString(result).contains("Import a CSV file"));
        Assert.assertTrue(toString(result).contains("Import a DBF file"));
        Assert.assertTrue(toString(result).contains("Import a GPX file"));
        Assert.assertTrue(toString(result).contains("Import a GeoJSON file"));
        Assert.assertTrue(toString(result).contains("Import a OSM file"));
        Assert.assertTrue(toString(result).contains("Import a shapeFile"));
        Assert.assertTrue(toString(result).contains("Import a TSV file"));
        Assert.assertTrue(toString(result).contains("Create a graph"));
        Assert.assertTrue(toString(result).contains("Delete columns"));
        Assert.assertTrue(toString(result).contains("Delete rows"));
        Assert.assertTrue(toString(result).contains("Describe columns"));
        Assert.assertTrue(toString(result).contains("Insert values in a table"));
        Assert.assertTrue(toString(result).contains("Tables join"));

        Assert.assertTrue(toString(result).contains("Please select an operation"));
        Assert.assertTrue(toString(result).contains("GetCapabilities"));
        Assert.assertTrue(toString(result).contains("Create an execute method"));

    }

    /**
     * Checks that the welcome page content is good.
     */
    @Test
    public void testWelcomePageContent() {
        // Call the action method as follows
        Action.ActionResult result = action(new Invocation() {
            @Override
            public Result invoke() throws Throwable {
                return welcomeController.welcome();
            }
        }).invoke();

        //It returns a redirection to the welcome.html page
        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("Welcome to Orbis Server"));
        Assert.assertTrue(toString(result).contains("Please"));
        Assert.assertTrue(toString(result).contains("login"));
    }

    /**
     * Checks that the form page content is good.
     */
    @Test
    public void testFormPageContent() {
        // Call the action method as follows
        Action.ActionResult result = action(new Invocation() {
            @Override
            public Result invoke() throws Throwable {
                return executeController.execute();
            }
        }).invoke();

        //It returns a redirection to the welcome.html page
        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("Execute Parameters"));
        Assert.assertTrue(toString(result).contains("Identifier"));
        Assert.assertTrue(toString(result).contains("Response"));
        Assert.assertTrue(toString(result).contains("Mode"));
        Assert.assertTrue(toString(result).contains("Input"));
        Assert.assertTrue(toString(result).contains("Output"));
    }

    /**
     * Checks that the WpsOperationController is returning OK,
     * and returning the good response corresponding to the GetCapabilities method.
     */
    @Test
    public void testGetCapabilitiesRequest() throws Exception {

        // Test of GetCapabilities with the correct parameters
        Action.ActionResult result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0", "GetCapabilities", null);
            }
        }).invoke();

        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("net.opengis.wps._2_0.WPSCapabilitiesType@"));

        // Test of  GetCapabilities, when the service parameter is missing
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("", "2.0.0", "GetCapabilities",null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("You need to enter a service to do queries, it should be wps here"));

        // Test of GetCapabilities, when the service parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("WP", "2.0.0", "GetCapabilities",null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("The service was not properly written, it should be wps here"));

        // Test of GetCapabilities, when the version parameter is missing
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "", "GetCapabilities",null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("You need to enter the version of wps to get the corresponding xml file"));

        // Test of GetCapabilities, when the version parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.1", "GetCapabilities",null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("Please enter a good version of wps, it should be 2.0.0"));

        // Test of GetCapabilities, when the request parameter is missing
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0", "",null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("You need to enter the request to get the corresponding xml file"));

        // Test of GetCapabilities, when the request parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0", "GetCapabilites",null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("This request does not exist," +
                " please try something else like GetCapabilities."));

        // Test of GetCapabilities, when the request parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0",
                        "GetCapabilities", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("GetCapabilities does not need identifier, so don't write it."));

    }

    /**
     * Checks that the WpsOperationController is returning OK,
     * and returning the good response corresponding to the DescribeProcess method.
     */
    @Test
    public void testDescribeProcessRequest() throws Exception {

        // Test of DescribeProcess with the correct parameters
        Action.ActionResult result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0",
                        "DescribeProcess", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("net.opengis.wps._2_0.ProcessOfferings@"));

        // Test of DescribeProcess with the correct parameters
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0", "DescribeProcess",
                        "file:/C:/Users/mande/AppData/Local/Temp/csvToPointsTable.groovy");
            }
        }).invoke();

        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("net.opengis.wps._2_0.ProcessOfferings@"));

        // Test of  DescribeProcess, when the service parameter is missing
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("", "2.0.0",
                        "DescribeProcess", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("You need to enter a service to do queries, it should be wps here"));

        // Test of DescribeProcess, when the service parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("WP", "2.0.0",
                        "DescribeProcess", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("The service was not properly written, it should be wps here"));

        // Test of DescribeProcess, when the version parameter is missing
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "",
                        "DescribeProcess", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("You need to enter the version of wps to get the corresponding xml file"));

        // Test of DescribeProcess, when the version parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.1",
                        "DescribeProcess", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("Please enter a good version of wps, it should be 2.0.0"));

        // Test of DescribeProcess, when the request parameter is missing
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0", "", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("You need to enter the request to get the corresponding xml file"));

        // Test of DescribeProcess, when the request parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0",
                        "DescribeProces", "orbisgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("This request does not exist," +
                " please try something else like GetCapabilities."));

        // Test of DescribeProcess, when the request parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0", "DescribeProcess","");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("An Identifier is missing."));

        // Test of DescribeProcess, when the request parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXML("wps", "2.0.0",
                        "DescribeProcess", "orbgis:wps:official:deleteRows");
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("No process has this identifier, please be more accurate."));
    }

    /**
     * Checks that the WpsOperationController is returning OK,
     * and returning the good response corresponding to the Execute method.
     */
    @Test
    public void testExecuteRequest() throws Exception {

        // Test of Execute with the correct parameters
        Action.ActionResult result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXMLForExecute("orbisgis:wps:official:deleteRows",
                        "document", "auto", null, null);
            }
        }).invoke();

        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("net.opengis.wps._2_0.StatusInfo@"));

        // Test of Execute with the correct parameters
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXMLForExecute("file:/C:/Users/mande/AppData/Local/Temp/csvToPointsTable.groovy",
                        "document", "auto", "toto&tata;titi&identifiant&30&25&true&true&test", "ErrorMessage");
            }
        }).invoke();

        Assert.assertEquals(status(result), OK);
        Assert.assertTrue(toString(result).contains("net.opengis.wps._2_0.StatusInfo@"));

        // Test of  Execute, when the identifier parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXMLForExecute("orbisis:wps:official:deleteRows",
                        "document", "auto", null, null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("No process has this identifier, please be more accurate."));

        // Test of Execute, when the response parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXMLForExecute("orbisgis:wps:official:deleteRows",
                        "docment", "auto", null, null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("The desired response format is incorrect, " +
                "please set it to document or raw."));

        // Test of Execute, when the mode parameter is wrong
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXMLForExecute("orbisgis:wps:official:deleteRows",
                        "document", "aut", null, null);
            }
        }).invoke();

        Assert.assertEquals(status(result), 400);
        Assert.assertTrue(toString(result).contains("The desired execution method is incorrect, " +
                "please set it to auto, sync or async."));

        // Test of Execute with the correct parameters
        result = action(new Invocation(){
            @Override
            public Result invoke() throws Throwable {
                return wpsOperationController.displayXMLForExecute("file:/C:/Users/mande/AppData/Local/Temp/csvToPointsTable.groovy",
                        "document", "auto", "toto&tata;titi&identifiant&30&25&true&true&test&test2", "ErrorMessage");
            }
        }).invoke();

        Assert.assertEquals(status(result), 500);
    }
}
