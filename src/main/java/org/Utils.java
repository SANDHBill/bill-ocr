package org;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Vector;

public class Utils {
	public static Image mat2Image(Mat frame) {
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	public static Mat matify(Image fxImage) {
		return matify(convertToAwtImage(fxImage));
	}

	private static BufferedImage convertToAwtImage(Image fxImage) {
		return SwingFXUtils.fromFXImage(fxImage, null);
	}

	private static Mat matify(BufferedImage in) {
		Mat out;
		byte[] data;
		

		int widthSize = in.getWidth();
		int heightSize = in.getHeight();

		if (in.getType() == BufferedImage.TYPE_INT_RGB) {
			out = new Mat(heightSize, widthSize, CvType.CV_8UC3);
			data = new byte[heightSize * widthSize * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, widthSize, heightSize, null, 0, widthSize);
			for (int i = 0; i < dataBuff.length; i++) {
				data[i * 3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
				data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
				data[i * 3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
			}
		} else {
			 BufferedImage convertedImg = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
			 convertedImg.getGraphics().drawImage(in, 0, 0, null);
			 return matify(convertedImg);
			
			 /*int r, g, b;
		    out = new Mat(heightSize, widthSize, CvType.CV_8UC1);
			data = new byte[widthSize * heightSize * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, widthSize, heightSize, null, 0, widthSize);
			for (int i = 0; i < dataBuff.length; i++) {
				r = (byte) ((dataBuff[i] >> 16) & 0xFF);
				g = (byte) ((dataBuff[i] >> 8) & 0xFF);
				b = (byte) ((dataBuff[i] >> 0) & 0xFF);
				data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b)); // luminosity
			}*/
		}
		out.put(0, 0, data);
		return out;
	}
	private static Mat HomomorphicFilter(String filename)
	{
		float lower, upper, threshold;
		Mat img = Imgcodecs.imread(filename, Imgcodecs.CV_LOAD_IMAGE_COLOR);
		
		int width = img.cols();
		int height = img.rows();
		int channel = img.channels();
		img.convertTo(img, CvType.CV_32FC3, 1.0 / 255.0);
		
		lower = 0.5f;
		upper = 2.0f;
		threshold = 7.5f;
	
		Vector<Mat> chs = new Vector<>();
		Vector<Mat> spc = new Vector<>();
		for(int i = 0; i < channel; i++) spc.addElement(new Mat(height, width, CvType.CV_32FC1));	
		Core.split(img, chs);
		for(int c=0; c<channel; c++) {
			Core.dct(chs.get(c), spc.get(c));
			hef(spc.get(c), spc.get(c), lower, upper, threshold);
			Core.idct(spc.get(c), chs.get(c));
		}
		Mat out = new Mat();
		Core.merge(chs, out);
		out.convertTo(out, CvType.CV_8UC3, 255.0);
		return out;
	}
	
	static void hef(Mat input, Mat output, float lower, float upper, float threshold) {
		int width = input.cols();
		int height = input.rows();
		int channel = input.channels();

		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				float r = (float) Math.sqrt((float)(x*x + y*y));
				double coeff = (1.0 - 1.0 / (1.0 + Math.exp(r - threshold))) * (upper - lower) + lower;
				for(int c=0; c<channel; c++) {
					input.get(y, x*channel+c);
					//output.put(y, x*channel+c,coeff * input.get(y, x*channel+c));					
				}
			}
		}
	}

}
