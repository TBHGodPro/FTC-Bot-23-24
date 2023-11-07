package org.firstinspires.ftc.teamcode.Camera;

import java.util.ArrayList;

import org.firstinspires.ftc.vision.tfod.TfodProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;


public class TFOD {
    public String TFOD_Model;
    public ArrayList<String> Labels = new ArrayList<String>();
    
    public TfodProcessor processor;
    
    public VisionPortal portal;
    
    public TFOD() {}
    
    public void load(WebcamName camera) {
        TfodProcessor.Builder processorBuilder = new TfodProcessor.Builder();
        
        if (TFOD_Model != null) {
            processorBuilder.setModelFileName(TFOD_Model);
        }
        
        if (Labels.size() > 0) {
            String[] ParsedLabels = new String[Labels.size()];
            ParsedLabels = Labels.toArray(ParsedLabels);
            
            processorBuilder.setModelLabels(ParsedLabels);
        }
        
        processor = processorBuilder.build();
        
        VisionPortal.Builder builder = new VisionPortal.Builder();
        
        builder.setCamera(camera);
        
        builder.enableLiveView(true);
        
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
        
        builder.setAutoStopLiveView(false);
        
        builder.addProcessor(processor);
        
        portal = builder.build();
    }
}