package org.firstinspires.ftc.teamcode.AutonomousNew.Camera;

import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;
import org.firstinspires.ftc.vision.VisionProcessor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor implements VisionProcessor {
        static final int colorCVT = Imgproc.COLOR_RGB2YCrCb;
        static final int colorChannel = 2;

        public enum SkystonePosition {
                LEFT,
                CENTER,
                RIGHT
        }

        static final Scalar BLUE = new Scalar(0, 0, 255);
        static final Scalar GREEN = new Scalar(0, 255, 0);

        static final int SCREEN_WIDTH = 640;
        static final int SCREEN_HEIGHT = 480;

        static final Point REGION1_TOPLEFT_ANCHOR_POINT = new Point(0, 0);
        static final Point REGION2_TOPLEFT_ANCHOR_POINT = new Point(SCREEN_WIDTH / 3, 0);
        static final Point REGION3_TOPLEFT_ANCHOR_POINT = new Point(SCREEN_WIDTH / 1.5, 0);

        static final int REGION_WIDTH = SCREEN_WIDTH / 3;
        static final int REGION_HEIGHT = SCREEN_HEIGHT;

        Point region1_pointA = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT.x,
                        REGION1_TOPLEFT_ANCHOR_POINT.y);
        Point region1_pointB = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
                        REGION1_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);
        Point region2_pointA = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT.x,
                        REGION2_TOPLEFT_ANCHOR_POINT.y);
        Point region2_pointB = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
                        REGION2_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);
        Point region3_pointA = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT.x,
                        REGION3_TOPLEFT_ANCHOR_POINT.y);
        Point region3_pointB = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
                        REGION3_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);

        /*
         * Working variables
         */
        Mat region1_Val, region2_Val, region3_Val;
        Mat Converted = new Mat();
        Mat Val = new Mat();
        int avg1, avg2, avg3;

        // Volatile since accessed by OpMode thread w/o synchronization
        private volatile SkystonePosition position = SkystonePosition.LEFT;

        @Override
        public void init(int width, int height, CameraCalibration calibration) {
                // Code executed on the first frame dispatched into this VisionProcessor
        }

        void inputToCb(Mat input) {
                // 0 = Luma
                // 1 = Diff from Red
                // 2 = Diff from Blue

                Imgproc.cvtColor(input, Converted, colorCVT);
                Core.extractChannel(Converted, Val, colorChannel);
        }

        @Override
        public Object processFrame(Mat input, long captureTimeNanos) {
                // Actual computer vision magic will happen here

                /*
                 * Get the Cb channel of the input frame after conversion to YCrCb
                 */
                inputToCb(input);

                Imgproc.cvtColor(input, input, colorCVT);

                if (region1_Val == null) {
                        /*
                         * Submats are a persistent reference to a region of the parent
                         * buffer. Any changes to the child affect the parent, and the
                         * reverse also holds true.
                         */
                        region1_Val = Val.submat(new Rect(region1_pointA, region1_pointB));
                        region2_Val = Val.submat(new Rect(region2_pointA, region2_pointB));
                        region3_Val = Val.submat(new Rect(region3_pointA, region3_pointB));
                }

                /*
                 * Compute the average pixel value of each submat region. We're
                 * taking the average of a single channel buffer, so the value
                 * we need is at index 0. We could have also taken the average
                 * pixel value of the 3-channel image, and referenced the value
                 * at index 2 here.
                 */
                avg1 = (int) Core.mean(region1_Val).val[0];
                avg2 = (int) Core.mean(region2_Val).val[0];
                avg3 = (int) Core.mean(region3_Val).val[0];

                Imgproc.rectangle(
                                input, // Buffer to draw on
                                region1_pointA, // First point which defines the rectangle
                                region1_pointB, // Second point which defines the rectangle
                                BLUE, // The color the rectangle is drawn in
                                2); // Thickness of the rectangle lines

                Imgproc.rectangle(
                                input, // Buffer to draw on
                                region2_pointA, // First point which defines the rectangle
                                region2_pointB, // Second point which defines the rectangle
                                BLUE, // The color the rectangle is drawn in
                                2); // Thickness of the rectangle lines

                Imgproc.rectangle(
                                input, // Buffer to draw on
                                region3_pointA, // First point which defines the rectangle
                                region3_pointB, // Second point which defines the rectangle
                                BLUE, // The color the rectangle is drawn in
                                2); // Thickness of the rectangle lines

                /*
                 * Find the max of the 3 averages
                 */
                int maxOneTwo = Math.max(avg1, avg2);
                int max = Math.max(maxOneTwo, avg3);

                /*
                 * Now that we found the max, we actually need to go and
                 * figure out which sample region that value was from
                 */
                if (max == avg1) // Was it from region 1?
                {
                        position = SkystonePosition.LEFT; // Record our analysis

                        /*
                         * Draw a solid rectangle on top of the chosen region.
                         * Simply a visual aid. Serves no functional purpose.
                         */
                        Imgproc.rectangle(
                                        input, // Buffer to draw on
                                        region1_pointA, // First point which defines the rectangle
                                        region1_pointB, // Second point which defines the rectangle
                                        GREEN, // The color the rectangle is drawn in
                                        -1); // Negative thickness means solid fill
                } else if (max == avg2) // Was it from region 2?
                {
                        position = SkystonePosition.CENTER; // Record our analysis

                        /*
                         * Draw a solid rectangle on top of the chosen region.
                         * Simply a visual aid. Serves no functional purpose.
                         */
                        Imgproc.rectangle(
                                        input, // Buffer to draw on
                                        region2_pointA, // First point which defines the rectangle
                                        region2_pointB, // Second point which defines the rectangle
                                        GREEN, // The color the rectangle is drawn in
                                        -1); // Negative thickness means solid fill
                } else if (max == avg3) // Was it from region 3?
                {
                        position = SkystonePosition.RIGHT; // Record our analysis

                        /*
                         * Draw a solid rectangle on top of the chosen region.
                         * Simply a visual aid. Serves no functional purpose.
                         */
                        Imgproc.rectangle(
                                        input, // Buffer to draw on
                                        region3_pointA, // First point which defines the rectangle
                                        region3_pointB, // Second point which defines the rectangle
                                        GREEN, // The color the rectangle is drawn in
                                        -1); // Negative thickness means solid fill
                }

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
