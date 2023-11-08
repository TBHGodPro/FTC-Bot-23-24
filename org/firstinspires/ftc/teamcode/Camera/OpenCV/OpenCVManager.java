package org.firstinspires.ftc.teamcode.Camera.OpenCV;

import org.firstinspires.ftc.teamcode.Camera.OpenCV.Processor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class OpenCVManager {
    public VisionPortal portal;
    
    public Processor processor;

    public WebcamName camera;

    public OpenCVManager(WebcamName camera) {
        this.camera = camera;

        processor = new Processor();
    }

    public void create() {
        portal = VisionPortal.easyCreateWithDefaults(camera, processor);
    }

    public void close() {
        portal.close();
    }
}
