package org.firstinspires.ftc.teamcode.AutonomousNew.Ops;

import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;

import org.firstinspires.ftc.teamcode.AutonomousNew.Camera.PossiblePosition;
import org.firstinspires.ftc.teamcode.AutonomousNew.Util.Alliance;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Func;

@Autonomous(name = "New Auto 1 (*START RIGHT SIDE OF TILE*)", group = "New Autonomous")
public class Op1 extends BaseOp {
    public PossiblePosition position;

    public void runOP() {
        // Prop Detection

        // - Give 2.5 Seconds
        sleep(2_500);

        // - Image Detected
        position = manager.processor.position;

        // - Turn Off Camera (Save CPU Cycles)
        manager.close();
    }

    public void setupExtraTelemetry() {
        telemetry.addLine();

        telemetry.addLine("--- Autonomous ---");

        telemetry.addLine();

        telemetry.addData("Decided Position", new Func<String>() {
            @Override
            public String value() {
                return position == null ? ("DECIDING (" + manager.processor.position + ")") : position.name();
            }
        });
    }

    public Alliance getAlliance() {
        return Alliance.RED;
    };
}