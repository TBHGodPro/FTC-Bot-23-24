package org.firstinspires.ftc.teamcode.modules;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.MainOp;
import org.firstinspires.ftc.teamcode.modules.ABPS.ABPSThread;
import org.firstinspires.ftc.teamcode.modules.ABPS.ManualCameraManager;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import com.qualcomm.robotcore.hardware.Gamepad;

// Automatic Backboard Positioning System

public class ABPSController extends Module {
    public final MainOp op;

    public final Gamepad gamepad;

    public final ManualCameraManager camera;

    public final ABPSThread thread;
    public final Runnable emptyRunnable;

    public static enum ABPSState {
        STOPPED,
        LEFT,
        RIGHT
    }

    public ABPSState state = ABPSState.STOPPED;

    public final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ABPSController(MainOp op, Gamepad gamepad, WebcamName camera) {
        this.op = op;

        this.gamepad = gamepad;

        this.camera = new ManualCameraManager(camera);

        thread = new ABPSThread(op);
        emptyRunnable = new Runnable() {
            @Override
            public void run() {
                return;
            }
        };
    }

    public void init() {
        camera.init();
    }

    public void loop() {
        if (executor.isTerminated()) {
            state = ABPSState.STOPPED;
        }

        if (gamepad.dpad_left && state == ABPSState.STOPPED) {
            state = ABPSState.LEFT;

            executor.submit(thread);
        }
        if (gamepad.dpad_right && state == ABPSState.STOPPED) {
            state = ABPSState.RIGHT;

            executor.submit(thread);
        }

        if (state != ABPSState.STOPPED
                && (gamepad.left_stick_x != 0 || gamepad.left_stick_y != 0 || gamepad.right_stick_x != 0
                        || gamepad.right_stick_y != 0)) {
            state = ABPSState.STOPPED;

            executor.submit(emptyRunnable);

            op.wheels.setTarget(null);
        }
    }

    @Override
    public void addTelemetry(Telemetry telemetry) {

        telemetry.addData("State", new Func<String>() {
            @Override
            public String value() {
                return state == ABPSState.STOPPED ? "Stopped" : ("Active (" + state.name() + ")");
            }
        })
                .addData("Detections", new Func<String>() {
                    @Override
                    public String value() {
                        return camera.processor.getDetections().size() + "";
                    }
                })
                .addData("Info", new Func<String>() {
                    @Override
                    public String value() {
                        List<AprilTagDetection> detections = camera.processor.getDetections();

                        String msg = "";

                        for (AprilTagDetection detection : detections) {
                            msg += "\n";
                            msg += "\nID = " + detection.id;
                            msg += "\nXYZ = " + detection.ftcPose.x + ", " + detection.ftcPose.y + ","
                                    + detection.ftcPose.z;
                            msg += "\nYaw = " + detection.ftcPose.yaw;
                            msg += "\nRange = " + detection.ftcPose.range;
                        }

                        return msg;
                    }
                });
    }
}
