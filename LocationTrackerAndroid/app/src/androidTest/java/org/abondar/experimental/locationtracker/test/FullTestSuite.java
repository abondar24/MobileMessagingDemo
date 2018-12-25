package org.abondar.experimental.locationtracker.test;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TrackerTest.class})
public class FullTestSuite extends TestSuite {


    public FullTestSuite() {
        super();
    }
}