package com.sandh.billanalyzer.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

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
	public static final String TARGET_SUREFIRE_REPORTS = "target/surefire-reports";
	public static final String RECIEPTSTESTS = "recieptstests";

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

		setTempFileOutPutPaths();


	}

	private void setTempFileOutPutPaths() {
		String strPath = System.getProperty(Utility.TEST_RECEIPTS_OUTPUT);
		StringJoiner testOutPutPath = new StringJoiner(File.separator);
		testOutPutPath.add(TARGET_SUREFIRE_REPORTS);
		testOutPutPath.add(RECIEPTSTESTS);

		if( null == strPath){

			try {
				Path tpath = Paths.get(new File(".").getCanonicalPath());
				Path target =
						tpath.getFileSystem().getPath(TARGET_SUREFIRE_REPORTS, RECIEPTSTESTS);
				target.toFile().mkdirs();

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.setProperty(Utility.TEST_RECEIPTS_OUTPUT, testOutPutPath.toString());
		}
	}


	@Test
	public void testTransformersGrayScaleBlackAndWhite() {

		List<SampleReceipt> results = new ArrayList<>();
		
		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

		TestUtility.sampleRecieptTestExecuter("testOne",
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.convertToGrayScale()
						.blackAndWhiteImage()
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersGrayScaleBlackAndWhiteAdaptive() {

		List<SampleReceipt> results = new ArrayList<>();

		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider(1);

		TestUtility.sampleRecieptTestExecuter("test2",
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

		TestUtility.sampleRecieptTestExecuter("Contour",
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

		TestUtility.sampleRecieptTestExecuter("detectBoundries",
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.convertToGrayScale()
						.gaussianBlur().blackAndWhiteImageAdaptive().detectLines().clearSmallBlackDots(300, 0.9)
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersFindBill() {

		List<SampleReceipt> results = new ArrayList<>(1);

		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

		TestUtility.sampleRecieptTestExecuter("LastOne",
				sampleReceiptProvider,
				results,
				imageFilter -> imageFilter.getImageInfo().orientImage().findBill2().convertToGrayScale()
						.gaussianBlur()
						.blackAndWhiteImageAdaptive()
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testOCRQuality(){
		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider("i11");
		SampleReceipt sampleReceipt = sampleReceiptProvider.getSampleRecieptsIterator().next();
		CharacterCounter characterCounter = new CharacterCounter();
		try {
			characterCounter.countCharacterFrequency(sampleReceipt.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
