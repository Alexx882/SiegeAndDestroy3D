package at.aau.gloryweapons.siegeanddestroy3d.validation;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alexander on 06.04.2018.
 */

public class ValidationHelperClassTests {
    @Test
    public void isUserNameValid_accept() throws Exception {
        Assert.assertEquals(true, ValidationHelperClass.isUserNameValid("Peter"));
        Assert.assertEquals(true, ValidationHelperClass.isUserNameValid("Hans123"));
        Assert.assertEquals(true, ValidationHelperClass.isUserNameValid("168541"));
    }

    @Test
    public void isUserNameValid_decline() throws Exception {
        Assert.assertEquals(false, ValidationHelperClass.isUserNameValid(null));
        Assert.assertEquals(false, ValidationHelperClass.isUserNameValid(""));
        Assert.assertEquals(false, ValidationHelperClass.isUserNameValid("    "));
    }

    @Test
    public void isServerIpValid_accept() throws Exception {
        Assert.assertEquals(true, ValidationHelperClass.isServerIpValid("127.0.0.1"));
        Assert.assertEquals(true, ValidationHelperClass.isServerIpValid("10.0.0.50"));
        Assert.assertEquals(true, ValidationHelperClass.isServerIpValid("192.168.0.1"));
    }

    @Test
    public void isServerIpValid_decline() throws Exception {
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid(null));
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid(""));
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("    "));

        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("256.168.0.1"));
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("192.256.0.1"));
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("192.168.256.1"));
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("192.168.0.256"));
        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("192.168.-1.1"));

        Assert.assertEquals(false, ValidationHelperClass.isServerIpValid("Peter"));

        Assert.assertFalse(ValidationHelperClass.isServerIpValid("192.168.1.1."));

        Assert.assertFalse(ValidationHelperClass.isServerIpValid("192.168.hans.1"));
        Assert.assertFalse(ValidationHelperClass.isServerIpValid("k.168.0.1"));
    }

    @Test
    public void shots_accept() throws Exception {
        Assert.assertEquals(true, ValidationHelperClass.validShots("1"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("2"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("3"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("4"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("5"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("6"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("7"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("8"));
        Assert.assertEquals(true, ValidationHelperClass.validShots("9"));

    }

    @Test
    public void shots_decline() throws Exception {
        Assert.assertEquals(false, ValidationHelperClass.validShots(""));
        Assert.assertEquals(false, ValidationHelperClass.validShots(null));
        Assert.assertEquals(false, ValidationHelperClass.validShots("0"));
        Assert.assertEquals(false, ValidationHelperClass.validShots("    "));
    }
}
