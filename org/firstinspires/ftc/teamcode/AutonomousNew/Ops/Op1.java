package org.firstinspires.ftc.teamcode.AutonomousNew.Ops;

import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;

import org.firstinspires.ftc.teamcode.AutonomousNew.Camera.PossiblePosition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Func;

@Autonomous(name = "New Auto 1 (*START RIGHT SIDE OF TILE*)", group = "New Autonomous")
public class Op1 extends BaseOp {
    public PossiblePosition position;

    public void runOP() {
        // Allow time for image detection
        sleep(2_000);

        position = manager.processor.position;

        manager.close();

    }

    public void setupExtraTelemetry() {
        telemetry.addLine();

        telemetry.addLine("--- Autonomous ---");

        telemetry.addLine();

        telemetry.addData("Decided Position", new Func<String>() {
            @Override
            public String value() {
                return position == null ? "NONE" : position.name();
            }
        });
    }
}