package org.openl.rules.testmethod;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openl.engine.OpenLSystemProperties;
import org.openl.rules.TestUtils;

/*
 * @author PTarasevich
 */

public class TestDoubleDelta {
    private static final String FILE_NAME = "test/rules/testmethod/DoubleDeltaTest.xlsx";

    @Before
    public void before() {
        System.setProperty(OpenLSystemProperties.CUSTOM_SPREADSHEET_TYPE_PROPERTY, "true");
    }

    @Test
    public void testSSTest() {
        ITestDouble instance = TestUtils.create(FILE_NAME, ITestDouble.class);
        TestUnitsResults result = instance.testSSTest();
        assertEquals(0, result.getNumberOfFailures());
    }

    @Test
    public void testDoubleTest() {
        ITestDouble instance = TestUtils.create(FILE_NAME, ITestDouble.class);
        TestUnitsResults result = instance.geTestDoubleTest();
        assertEquals(2, result.getNumberOfFailures());
    }

    @Test
    public void testDoubleTest2() {
        ITestDouble instance = TestUtils.create(FILE_NAME, ITestDouble.class);
        TestUnitsResults result = instance.geTestDoubleTest2();
        assertEquals(1, result.getNumberOfFailures());
    }

    public interface ITestDouble {
        TestUnitsResults testSSTest();

        TestUnitsResults geTestDoubleTest();

        TestUnitsResults geTestDoubleTest2();
    }
}
