package org.firstinspires.ftc.teamcode.Camera;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Camera.ImageProcessor;

public class OpenCVManager {
    public VisionPortal portal;
    
    public ImageProcessor processor;

    public WebcamName camera;

    public OpenCVManager(WebcamName camera) {
        this.camera = camera;

        processor = new ImageProcessor();
    }

    public void create() {
        portal = VisionPortal.easyCreateWithDefaults(camera, processor);
    }

    public void close() {
        portal.close();
    }
}
