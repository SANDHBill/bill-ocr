package com.sandh.billanalyzer.utility;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.sandh.billanalyzer.transformers.impl.*;
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

		
		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

        List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("blackwhite",
				sampleReceiptProvider,
				imageFilter -> imageFilter
                        .apply(GrayScaleTrf.class.getName())
                        .apply(BlackAndWhiteTrf.class.getName())
						.apply(OCRTrf.class.getName())
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersGrayScaleBlackAndWhiteAdaptive() {


		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider(1);

        List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("blackwhiteBlur",
				sampleReceiptProvider,
				imageFilter -> imageFilter
                        .apply(GrayScaleTrf.class.getName())
                        .apply(BlurTrf.class.getName())
                        .apply(BlackAndWhiteTrf.class.getName())
						.apply(OCRTrf.class.getName())
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersGrayScaleBlackAndWhiteAdaptiveClearSmallBlackDots() {


		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

        List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("Contour",
				sampleReceiptProvider,
				imageFilter -> imageFilter
                        .apply(GrayScaleTrf.class.getName())
                        .apply(BlurTrf.class.getName())
                        .apply(BlackAndWhiteTrf.class.getName(), BlackAndWhiteTrf.ADAPTIVE)
                        .apply(RemoveNoiseTrf.class.getName(),
                                Param.entry(RemoveNoiseTrf.CONTOUR_SIZE,300),
                                Param.entry(RemoveNoiseTrf.THRESHOLD,0.9))
						.apply(OCRTrf.class.getName())
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersGrayScaleBlackAndWhiteAdaptiveFindBillClearSmallBlackDots() {


		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();
        List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("detectBoundries",
				sampleReceiptProvider,
				imageFilter -> imageFilter
                        .apply(GrayScaleTrf.class.getName())
                        .apply(BlurTrf.class.getName())
                        .apply(BlackAndWhiteTrf.class.getName(), BlackAndWhiteTrf.ADAPTIVE)
                        .apply(DetectLinesTrf.class.getName())
                        .apply(RemoveNoiseTrf.class.getName(),
                                Param.entry(RemoveNoiseTrf.CONTOUR_SIZE,300),
                                Param.entry(RemoveNoiseTrf.THRESHOLD,0.9))
						.apply(OCRTrf.class.getName())
		);

		Assert.assertTrue(true);
	}

	@Test
	public void testTransformersFindBill() {


		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider();

        List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("OrientAdj",
				sampleReceiptProvider,
				imageFilter -> imageFilter
                        .apply(AdjustOrientationTrf.class.getName())
                        .apply(FindBillTrf.class.getName())
                        .apply(GrayScaleTrf.class.getName())
						.apply(BlurTrf.class.getName())
						.apply(BlackAndWhiteTrf.class.getName(),BlackAndWhiteTrf.ADAPTIVE)
                        .apply(OCRTrf.class.getName())
		);

		Assert.assertTrue(true);
	}

@Test
public void testTransformersGoogle() {


	SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider("i1_low.jpg");

    List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("Google",
			sampleReceiptProvider,
			imageFilter -> imageFilter
                    .apply(AdjustOrientationTrf.class.getName())
                    .apply(GoogleTrf.class.getName())
	);

	Assert.assertTrue(true);
}

    @Test
	public void testOCRQuality(){
		SampleReceiptProvider sampleReceiptProvider = new SampleReceiptProvider("i11.");

        List<SampleReceipt> results = TestUtility.sampleRecieptTestExecuter("Google",
                sampleReceiptProvider,
                imageFilter -> imageFilter
                        .apply(AdjustOrientationTrf.class.getName())
                        .apply(FindBillTrf.class.getName())
                        .apply(GrayScaleTrf.class.getName())
                        .apply(BlurTrf.class.getName())
                        .apply(BlackAndWhiteTrf.class.getName(),BlackAndWhiteTrf.ADAPTIVE)
                        .apply(GoogleTrf.class.getName())
        );

		String subject = results.get(0).getResult();
        String refTex="";
		try {
            refTex = results.get(0).getReferenceTextString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		QualityScore charFrequencyQualityTest = new CharFrequencyQualityScore(refTex);

		QualityScoreDetails result = charFrequencyQualityTest.test(subject);

		Assert.assertTrue(result.isAtleast80percAccurate());
	}


}
