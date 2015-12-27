package com.sandh.billanalyzer.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.io.File.separator;

public class TransformerTests {
	public static final String OPENCV_JAVA300_DYLIB = "opencv_java300.dylib";
	public static final String TESSDATA_PREFIX = "TESSDATA_PREFIX";
	public static final String TESSDATA_PREFIX_DEFAULT_PATH = "src/test/resources/tessdata";

	private Logger LOG = LoggerFactory.getLogger(this.getClass().getName());


	@Before
	public void setup(){
		if( null == System.getProperties().getProperty(TESSDATA_PREFIX)){
			System.getProperties().setProperty(TESSDATA_PREFIX,TESSDATA_PREFIX_DEFAULT_PATH);
		}
		String javaLibPath = System.getProperties().getProperty("java.library.path");
		LOG.info("{} :: {}",Core.NATIVE_LIBRARY_NAME ,javaLibPath);
		LOG.info("TESSDATA_PREFIX:: {}", System.getProperties().getProperty(TESSDATA_PREFIX));
		TestUtility.printClassPath();
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.load(javaLibPath + separator + OPENCV_JAVA300_DYLIB);


	}



	public void testTransformersGrayScaleBlackAndWhite() {

		List<SampleReceipt> results = new ArrayList<>();
		
		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

		TestUtility.sampleRecieptTestExecuter(
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.convertToGrayScale()
						.blackAndWhiteImage()
		);

		Assert.assertTrue(true);
	}


	public void testTransformersGrayScaleBlackAndWhiteAdaptive() {

		List<SampleReceipt> results = new ArrayList<>();

		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider(1);

		TestUtility.sampleRecieptTestExecuter(
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.convertToGrayScale()
						.gaussianBlur().blackAndWhiteImageAdaptive()
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersGrayScaleBlackAndWhiteAdaptiveClearSmallBlackDots() {

		List<SampleReceipt> results = new ArrayList<>();

		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

		TestUtility.sampleRecieptTestExecuter(
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.convertToGrayScale()
						.gaussianBlur().blackAndWhiteImageAdaptive().clearSmallBlackDots(300, 0.9)
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersGrayScaleBlackAndWhiteAdaptiveFindBillClearSmallBlackDots() {

		List<SampleReceipt> results = new ArrayList<>();

		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

		TestUtility.sampleRecieptTestExecuter(
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.convertToGrayScale()
						.gaussianBlur().blackAndWhiteImageAdaptive().findBill().clearSmallBlackDots(300, 0.9)
		);

		Assert.assertTrue(true);
	}

}
