package com.sandh.billanalyzer.utility;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by hamed on 27/02/2016.
 */
public class TransformParametersTest {
    String[] transferParamsArray=null;
    @Before
    public void init(){
        String[] params={"int:0","double:10.20","string:BLACK","size:10"};
        transferParamsArray=params;
    }

    @Test
    public void testGetInt() throws Exception {
        TransformParameters transformParameters= new TransformParameters(transferParamsArray);
        int int1 = transformParameters.getInt("int");
        int size = transformParameters.getInt("size");

        Assert.assertEquals(0,int1);
        Assert.assertEquals(10,size);
    }

    @Test
    public void testGetDouble() throws Exception {
        TransformParameters transformParameters= new TransformParameters(transferParamsArray);

        double dbl1= transformParameters.getDouble("double");

        Assert.assertEquals(10.20,dbl1,0.1);
    }

    @Test
    public void testGetString() throws Exception {
        TransformParameters transformParameters= new TransformParameters(transferParamsArray);

        String str = transformParameters.getString("string");
        String strInt= transformParameters.getString("int");

        Assert.assertEquals("BLACK",str);
        Assert.assertEquals("0",strInt);

    }
}