package org.firstinspires.ftc.teamcode.AutonomousNew.Camera;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;
import org.firstinspires.ftc.teamcode.AutonomousNew.Camera.ImageProcessor;
import org.firstinspires.ftc.teamcode.AutonomousNew.Util.*;

public class OpenCVManager {
    public Alliance alliance;

    public VisionPortal portal;

    public ImageProcessor processor;

    public WebcamName camera;

    public OpenCVManager(Alliance alliance, WebcamName camera) {
        this.alliance = alliance;

        this.camera = camera;

        processor = new ImageProcessor(alliance);
    }

    public void create() {
        portal = VisionPortal.easyCreateWithDefaults(camera, processor);
    }

    public void close() {
        portal.close();
    }
}
