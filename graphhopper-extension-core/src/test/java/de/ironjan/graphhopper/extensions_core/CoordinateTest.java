package de.ironjan.graphhopper.extensions_core;


import org.junit.Assert;
import org.junit.Test;


public class CoordinateTest {

    @Test
    public void serializationTest(){
Assert.assertEquals("0.000000,0.000000,0.000000",new Coordinate(0,0,0).asString());
        Assert.assertEquals(new Coordinate(12.34,-5.67890,0.0).asString(), "12.340000,-5.678900,0.000000");
    }

    @Test
    public void deserializationTest() {
        Coordinate c1 = new Coordinate(0,0,0);
        Coordinate c2 = new Coordinate(12.34,-5.67890,0.0);

        Assert.assertEquals(c1, Coordinate.fromString(c1.asString()));
        Assert.assertEquals(c2, Coordinate.fromString(c2.asString()));
    }
}