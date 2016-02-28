package com.sandh.billanalyzer.utility;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilityTests {

	String filePaths[] = {
			"/Users/hamed/Documents/hws/java/workspaceIJ/BillAnalyser/src/test/resources/reciepts/i1.jpg",
			"/Users/hamed/Documents/hws/java/workspaceIJ/BillAnalyser/src/test/resources/reciepts/i1.png",
			"/Users/hamed/Documents/hws/java/workspaceIJ/BillAnalyser/src/test/resources/reciepts/i1.jpg.txt"};
	String fileExtensions[] = {"i1.jpg","i1.png","i1.jpg.txt"};


	@Test
	public void testGetFileName() {



		for(int i=0;i<filePaths.length;i++){
			String fileExtension=Utility.getFileName(filePaths[i]);
			Assert.assertTrue(fileExtensions[i].equalsIgnoreCase(fileExtension));
		}
	}

	@Test
	public void testConstructFileNameForOperationOutput(){

        String expectedFileName = "countour_input_i1.jpg_output_jpg_blur";

		TraceableOperator testTraceableOp = mock(TraceableOperator.class);
		when(testTraceableOp.getFilterName()).thenReturn("blur");
		when(testTraceableOp.getOriginName()).thenReturn(filePaths[0]);

		String fileName =
				Utility.constructFileNameForOperationOutput(
						testTraceableOp,"countour","jpg");

		Assert.assertEquals("incorrect name",fileName,expectedFileName);

	}



}
