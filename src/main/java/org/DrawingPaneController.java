package org;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.lept.*;
import org.bytedeco.javacpp.tesseract;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.core.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
//import javafx.geometry.Point2D;
//import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
//import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DrawingPaneController {
	Image currentImage;
	private WritableImage originalImage;
	private FileChooser fileChooser;
	Stage stage;
	ImageView im;
	private Mat imageMat;
	Polygon poly;
	Map<Integer, Circle> mapCircles = new HashMap<Integer, Circle>();
	private double dragDeltaX, dragDeltaY;
	
	class Point2Dim
	{
		double x;
		double y;
		public Point2Dim(double x, double y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private Point2Dim point1;
	private Point2Dim point2;
	private Point2Dim point3;
	private Point2Dim point4;

	public DrawingPaneController() {
		init();
	}
	protected void init() {
		this.fileChooser = new FileChooser();
		// imageMat = new Mat();
		imageMat = null;
	}
	
	private WritableImage copyImage(Image src,int minX,int minY,int width,int height) {
		PixelReader pixelReader = src.getPixelReader();
		// Copy from source to destination pixel by pixel
		WritableImage retImg = new WritableImage(width, height);
		PixelWriter pixelWriter = retImg.getPixelWriter();
		for (int y = minY; y < height; y++) {
			for (int x = minX; x < width; x++) {
				Color color = pixelReader.getColor(x, y);
				pixelWriter.setColor(x, y, color);
			}
		}
		return retImg;
	}
	
	private WritableImage copyImage(Image src) {
		int width = (int) src.getWidth();
		int height = (int) src.getHeight();
		return copyImage(src,0,0,width,height);
	}
	public void handleButtonLoadImage(ActionEvent e) {
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			// originalImage = new Image(file.toURI().toString());
			currentImage = new Image(file.toURI().toString());
			originalImage = copyImage(currentImage);
			fillMat();
			im.setImage(currentImage);
			im.setFitWidth(250);
			im.setPreserveRatio(true);
		}
	}
	private void fillMat() {
		imageMat = Utils.matify(currentImage);
	}
	public void handleButtonSaveImage(ActionEvent e) {
		Bounds selectionBounds = poly.getBoundsInLocal();
		crop(selectionBounds);
	}
	public void handleButtonFilter(ActionEvent e) {
		if (imageMat == null)
			fillMat();
		Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY, 1);
		/*
		//just test countouring
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat image32S = new Mat();
		imageMat.convertTo(image32S, CvType.CV_32SC1);

		Imgproc.findContours(image32S, contours, new Mat(), Imgproc.RETR_FLOODFILL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Draw all the contours such that they are filled in.
		Mat contourImg = new Mat(image32S.size(), image32S.type());
		for (int i = 0; i < contours.size(); i++) {
		    Imgproc.drawContours(contourImg, contours, i, new Scalar(255, 255, 255), -1);
		}

		Imgcodecs.imwrite("/Users/user/temp/debug_countourimage.jpg", contourImg); // DEBUG
		//just test countouring
		*/
		
		Imgproc.GaussianBlur(imageMat, imageMat, new Size(11, 11), 0);
		
		//Imgproc.erode(imageMat, imageMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(11,11)));        
		//Imgproc.dilate(imageMat, imageMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(11, 11)));
		
		//Imgproc.GaussianBlur(imageMat, imageMat, new Size(11, 11), 0);
		//int kernel_length = 11;
		//Imgproc.medianBlur(imageMat, imageMat,  kernel_length);
		
		
	/*	BackgroundSubtractorMOG2 bg = Video.createBackgroundSubtractorMOG2();
		bg.setNMixtures(3);
	    bg.setDetectShadows(true);// .bShadowDetection = true;
	    bg.setShadowValue(0);// .nShadowDetection = 0; //resolved!
	    bg.setShadowThreshold(0.5); //.fTau = 0.5;           //resolved!
		*/
		
		
		Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 2);//ADAPTIVE_THRESH_MEAN_C
		
		//It is not good when you have shadow
		//Imgproc.threshold(imageMat, imageMat, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
		//Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 2);//ADAPTIVE_THRESH_MEAN_C
		
		
		im.setImage(Utils.mat2Image(imageMat));
		im.setFitWidth(250);
		im.setPreserveRatio(true);
		currentImage = im.getImage();
	}
	public void handleOnMousePressed(MouseEvent mouseEvent, Circle circle) {
		dragDeltaX = circle.getCenterX() - mouseEvent.getSceneX();
		dragDeltaY = circle.getCenterY() - mouseEvent.getSceneY();
	}
	public void handleOnMouseDragged(MouseEvent mouseEvent, Circle circle) {
		circle.setCenterX(mouseEvent.getSceneX() + dragDeltaX);
		circle.setCenterY(mouseEvent.getSceneY() + dragDeltaY);
		circle.setCursor(Cursor.MOVE);
		point1.x = poly.getPoints().get(0);
		point1.y = poly.getPoints().get(1);
		point2.x = poly.getPoints().get(2);
		point2.y = poly.getPoints().get(3);
		point3.x = poly.getPoints().get(4);
		point3.y = poly.getPoints().get(5);
		point4.x = poly.getPoints().get(6);
		point4.y = poly.getPoints().get(7);
		
	}
	private File filechooserTOsave() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		File file = fileChooser.showSaveDialog(stage);
		return file;
	}

	private void saveSelectedArea(WritableImage wi, File file) {

		// save image (without alpha)
		// --------------------------------
		BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(wi, null);
		BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(),
				BufferedImage.OPAQUE);

		Graphics2D graphics = bufImageRGB.createGraphics();
		graphics.drawImage(bufImageARGB, 0, 0, null);

		try {
			ImageIO.write(bufImageRGB, "jpg", file);

			System.out.println("Image saved to " + file.getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}
		graphics.dispose();
	}

	private void crop(Bounds bounds) {
		Bounds bb = im.getBoundsInParent();
		
		double minX = bounds.getMinX() - bb.getMinX();
		double minY = bounds.getMinY() - bb.getMinY();
		double width = bounds.getWidth();
		double height =  bounds.getHeight();
		
		double scaleX = currentImage.getWidth() / bb.getWidth();
		double scaleY = currentImage.getHeight() / bb.getHeight();
			
		width = Math.min(width, bb.getWidth())*scaleX;
		height = Math.min(height, bb.getHeight())*scaleY;
		minX = Math.max(0, minX)*scaleX;
		minY = Math.max(0, minY)*scaleY;
		
		/* Point2Dim ptBottomLeft = new Point2Dim(minX, minY + height);
		 Point2Dim ptBottomRight = new Point2Dim(minX + width, minY + height);
		 Point2Dim ptTopRight = new Point2Dim(minX + width, minY);
		 Point2Dim ptTopLeft = new Point2Dim(minX, minY);
		 */
		 double mx =  bb.getMinX();
		 double my =  bb.getMinY();
		 Point2Dim ptBottomLeft = new Point2Dim( (point4.x-mx)*scaleX, (point4.y-my)*scaleY);
		 Point2Dim ptBottomRight = new Point2Dim((point3.x-mx)*scaleX, (point3.y-my)*scaleY);
		 Point2Dim ptTopRight = new Point2Dim((point2.x-mx)*scaleX, (point2.y-my)*scaleY);
		 Point2Dim ptTopLeft = new Point2Dim((point1.x-mx)*scaleX, (point1.y-my)*scaleY);
	        
	     double w1 = Math.sqrt( Math.pow(ptBottomRight.x - ptBottomLeft.x , 2) + Math.pow(ptBottomRight.x - ptBottomLeft.x, 2));
	     double w2 = Math.sqrt( Math.pow(ptTopRight.x - ptTopLeft.x , 2) + Math.pow(ptTopRight.x - ptTopLeft.x, 2));
	        
	     double h1 = Math.sqrt( Math.pow(ptTopRight.y - ptBottomRight.y , 2) + Math.pow(ptTopRight.y - ptBottomRight.y, 2));
	     double h2 = Math.sqrt( Math.pow(ptTopLeft.y - ptBottomLeft.y , 2) + Math.pow(ptTopLeft.y - ptBottomLeft.y, 2));
	        
	     double maxWidth = (w1 < w2) ? w1 : w2;
	     double maxHeight = (h1 < h2) ? h1 : h2;
	    
	     Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
	     Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);
	     
	     src_mat.put(0,0,ptTopLeft.x, ptTopLeft.y, ptTopRight.x, ptTopRight.y, ptBottomRight.x, ptBottomRight.y,ptBottomLeft.x,ptBottomLeft.y);
	     dst_mat.put(0,0,0,0,maxWidth - 1,0, maxWidth - 1, maxHeight - 1, 0, maxHeight - 1);
	     Mat perspectiveTransform=Imgproc.getPerspectiveTransform(src_mat, dst_mat);
	     
	    
	     Mat undistorted = new Mat( new Size(maxWidth,maxHeight), CvType.CV_8UC1);
	     Mat original = Utils.matify(currentImage);
	     Imgproc.warpPerspective(original, undistorted, perspectiveTransform, new Size(maxWidth, maxHeight)); 
	    
		//System.out.println("minX:"+bb.getMinX()+",minY:"+bb.getMinY()+",width:"+bb.getWidth()+",height:"+bb.getHeight());
		//System.out.println("minX:"+bounds.getMinX()+",minY:"+bounds.getMinY()+",width:"+width+",height:"+height);
		//WritableImage wi = copyImage(Utils.mat2Image(undistorted), (int)minX,(int)minY,(int)width,(int)height);
	     WritableImage wi = copyImage(Utils.mat2Image(undistorted));
		setCurrentImage(wi);
		// saveSelectedArea(wi);
		original.release();
	}

	private void setCurrentImage(WritableImage wimg) {
		im.setImage(wimg);
		im.setFitWidth(250);
		im.setPreserveRatio(true);
		currentImage = im.getImage();
		fillMat();
	}

	public void handleButtonRestart(ActionEvent e) {
		setCurrentImage(originalImage);
	}

	final String fileLoc = "/Users/user/temp/filtered.jpg";
	
	private void callOCR()
	{
		BytePointer outText;
		tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
		// Initialize tesseract-ocr with English, without specifying tessdata
		// path
		if (api.Init("src/test/resource/reciepts", "ENG") != 0) {
			System.err.println("Could not initialize tesseract.");
			System.exit(1);
		}
		// Open input image with leptonica library
		lept.PIX image = pixRead(fileLoc);
		api.SetImage(image);
		// Get OCR result
		outText = api.GetUTF8Text();
		String string = outText.getString();
		//assertTrue(!string.isEmpty());
		System.out.println("OCR output:\n" + string);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(string);

		alert.showAndWait();

		// Destroy used object and release memory
		api.End();
		outText.deallocate();
		pixDestroy(image);
	}
	
	public void handleButtonOCR(ActionEvent e) {
		File file = new File(fileLoc);
		WritableImage wi = copyImage(currentImage);
		saveSelectedArea(wi, file);
		callOCR();
	}

	public void setOriginalImage(Image image) {
		currentImage = image;
		originalImage = copyImage(currentImage);
	}

	public void setPointsSelect(double x1, double y1, double x2, double y2, double x3, double y3,
			double x4, double y4)
	{
		point1 = new Point2Dim(x1, y1);
		point2 = new Point2Dim(x2, y2);
		point3 = new Point2Dim(x3, y3);
		point4 = new Point2Dim(x4, y4);
	}
}
