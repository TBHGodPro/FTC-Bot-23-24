package org.firstinspires.ftc.teamcode.modules.ABPS;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.MainOp;
import org.firstinspires.ftc.teamcode.modules.ABPSController.ABPSState;
import org.firstinspires.ftc.teamcode.modules.WheelController.WheelTarget;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class ABPSThread extends Thread {
    public MainOp op;

    public ABPSThread(MainOp op) {
        this.op = op;
    }

    @Override
    public void run() {
        op.movements.desiredAngle = op.abps.state == ABPSState.LEFT ? 90d : -90d;
        ;

        while ((Math.round(op.movements.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) / 5) != Math
                .round(op.movements.desiredAngle / 5)
                || op.abps.camera.processor.getDetections().size() == 0) && op.abps.state != ABPSState.STOPPED) {
            waitTime(5);
        }

        if (op.abps.state != ABPSState.STOPPED) {
            op.arm.gotToBackboardPosition();
        }

        while (op.abps.state != ABPSState.STOPPED) {
            List<AprilTagDetection> detections = op.abps.camera.processor.getDetections();

            if (detections.size() == 0)
                break;

            AprilTagDetection bestDetection = null;

            for (AprilTagDetection detection : detections) {
                if (bestDetection == null) {
                    bestDetection = detection;
                } else if (bestDetection.ftcPose.yaw > detection.ftcPose.yaw) {
                    bestDetection = detection;
                }
            }

            double distance = bestDetection.ftcPose.range;

            if (distance <= 13.5)
                break;

            int wheelTicks = (int) (distance - 14) * 35;

            if (wheelTicks < 10)
                break;

            op.wheels.setTarget(
                    new WheelTarget(wheelTicks, wheelTicks, wheelTicks, wheelTicks, (int) (wheelTicks / 1.6)));

            while (op.wheels.target != null && op.abps.state != ABPSState.STOPPED)
                waitTime(5);
        }

        if (op.abps.state != ABPSState.STOPPED) {
            op.arm.isHandClosed = false;
        }

        op.abps.state = ABPSState.STOPPED;
    }

    private void waitTime(long milliseconds) {
        if (Thread.currentThread().isInterrupted())
            return;
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
        }
        return;
    }
}
