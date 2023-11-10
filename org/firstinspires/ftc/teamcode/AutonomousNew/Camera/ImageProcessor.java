package org.firstinspires.ftc.teamcode.AutonomousNew.Camera;

import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

public class ImageProcessor implements VisionProcessor {
    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        // Code executed on the first frame dispatched into this VisionProcessor
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        // Actual computer vision magic will happen here

        Imgproc.rectangle(
                frame,
                new Point(
                        frame.cols() / 4,
                        frame.rows() / 4),
                new Point(
                        frame.cols() * (3f / 4f),
                        frame.rows() * (3f / 4f)),
                new Scalar(0, 255, 0), 4);

        return null; // No context object
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx,
            float scaleCanvasDensity, Object userContext) {
        // Cool feature: This method is used for drawing annotations onto
        // the displayed image, e.g outlining and indicating which objects
        // are being detected on the screen, using a GPU and high quality
        // graphics Canvas which allow for crisp quality shapes.
    }
}
