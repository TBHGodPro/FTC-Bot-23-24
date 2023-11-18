package org.firstinspires.ftc.teamcode.AutonomousNew.Camera;

import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;
import org.firstinspires.ftc.teamcode.AutonomousNew.Util.*;
import org.firstinspires.ftc.vision.VisionProcessor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor implements VisionProcessor {
        // Options

        public boolean shouldDisplayBinaryImage = true;
        public boolean shouldFillBox = false;

        // Alliance

        public Alliance alliance;

        // Static Colors

        private static final Scalar REGION_BOUNDARY = new Scalar(0, 0, 255);
        private static final Scalar REGION_FILL = new Scalar(0, 255, 0);

        // Processing

        private Mat hsvMat = new Mat();
        private Mat binaryMat = new Mat();

        private Mat region1_Mat, region2_Mat, region3_Mat;

        private Scalar lower;
        private Scalar upper;

        // Regions

        private static final int SCREEN_WIDTH = 640;
        private static final int SCREEN_HEIGHT = 480;

        private static final int REGION_WIDTH = SCREEN_WIDTH / 3;
        private static final int REGION_HEIGHT = SCREEN_HEIGHT - 275;

        private static final Point REGION1_TOPLEFT_ANCHOR = new Point(0, 275);
        private static final Point REGION2_TOPLEFT_ANCHOR = new Point(SCREEN_WIDTH / 3, 275);
        private static final Point REGION3_TOPLEFT_ANCHOR = new Point(SCREEN_WIDTH / 1.5, 275);

        private Point region1_A = new Point(
                        REGION1_TOPLEFT_ANCHOR.x,
                        REGION1_TOPLEFT_ANCHOR.y);
        private Point region1_B = new Point(
                        REGION1_TOPLEFT_ANCHOR.x + REGION_WIDTH,
                        REGION1_TOPLEFT_ANCHOR.y + REGION_HEIGHT);
        private Point region2_A = new Point(
                        REGION2_TOPLEFT_ANCHOR.x,
                        REGION2_TOPLEFT_ANCHOR.y);
        private Point region2_B = new Point(
                        REGION2_TOPLEFT_ANCHOR.x + REGION_WIDTH,
                        REGION2_TOPLEFT_ANCHOR.y + REGION_HEIGHT);
        private Point region3_A = new Point(
                        REGION3_TOPLEFT_ANCHOR.x,
                        REGION3_TOPLEFT_ANCHOR.y);
        private Point region3_B = new Point(
                        REGION3_TOPLEFT_ANCHOR.x + REGION_WIDTH,
                        REGION3_TOPLEFT_ANCHOR.y + REGION_HEIGHT);

        // Position

        // - Volatile since accessed by OpMode thread w/o synchronization
        public volatile PossiblePosition position = PossiblePosition.CENTER;

        // Constructor

        public ImageProcessor(Alliance alliance) {
                this.alliance = alliance;

                switch (alliance) {
                        case RED: {
                                lower = new Scalar(0, 0, 0);
                                upper = new Scalar(165, 255, 255);

                                break;
                        }

                        case BLUE: {
                                lower = new Scalar(100, 60, 60);
                                upper = new Scalar(140, 255, 255);

                                break;
                        }
                }
        }

        // Methods

        @Override
        public void init(int width, int height, CameraCalibration calibration) {
                // Code executed on the first frame dispatched into this VisionProcessor
        }

        @Override
        public Object processFrame(Mat input, long captureTimeNanos) {
                // Image Conversion

                Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);
                Core.inRange(hsvMat, lower, upper, binaryMat);

                if (shouldDisplayBinaryImage) {
                        Core.inRange(hsvMat, lower, upper, input);
                } else {
                        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);
                }

                // Draw Region Boundaries

                Imgproc.rectangle(
                                input,
                                region1_A,
                                region1_B,
                                REGION_BOUNDARY,
                                2);

                Imgproc.rectangle(
                                input,
                                region2_A,
                                region2_B,
                                REGION_BOUNDARY,
                                2);

                Imgproc.rectangle(
                                input,
                                region3_A,
                                region3_B,
                                REGION_BOUNDARY,
                                2);

                // Locate Most Likely Region

                // - Set Submats
                if (region1_Mat == null) {
                        region1_Mat = binaryMat.submat(new Rect(region1_A, region1_B));
                        region2_Mat = binaryMat.submat(new Rect(region2_A, region2_B));
                        region3_Mat = binaryMat.submat(new Rect(region3_A, region3_B));
                }

                // - Find Averages

                int avg1 = (int) Core.mean(region1_Mat).val[0];
                int avg2 = (int) Core.mean(region2_Mat).val[0];
                int avg3 = (int) Core.mean(region3_Mat).val[0];

                // - Find Best Average

                int found;

                if (alliance == Alliance.RED) {
                        int foundOneTwo = Math.min(avg1, avg2);
                        found = Math.min(foundOneTwo, avg3);
                } else {
                        int foundOneTwo = Math.max(avg1, avg2);
                        found = Math.max(foundOneTwo, avg3);
                }

                // Display and Record Findings

                if (found == avg1) {
                        position = PossiblePosition.LEFT;

                        Imgproc.rectangle(
                                        input,
                                        region1_A,
                                        region1_B,
                                        REGION_FILL,
                                        shouldFillBox ? -1 : 8);
                } else if (found == avg2) {
                        position = PossiblePosition.CENTER;

                        Imgproc.rectangle(
                                        input,
                                        region2_A,
                                        region2_B,
                                        REGION_FILL,
                                        shouldFillBox ? -1 : 8);
                } else if (found == avg3) {
                        position = PossiblePosition.RIGHT;

                        Imgproc.rectangle(
                                        input,
                                        region3_A,
                                        region3_B,
                                        REGION_FILL,
                                        shouldFillBox ? -1 : 8);
                }

                return null;
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
