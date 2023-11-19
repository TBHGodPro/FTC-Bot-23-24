package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.modules.MovementController;
import org.firstinspires.ftc.teamcode.modules.WheelController;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

@TeleOp(name = "MainOp")
public class MainOp extends OpMode {
    public Boolean isAutonomous;

    public Gamepad gamepad;

    public WheelController wheels;
    public MovementController movements;

    // Run once INIT is pressed
    public void init() {
        // Check for Autonomous
        if (isAutonomous == null) {
            isAutonomous = false;
        }

        // Allow gamepad overriding (by autonomous)
        if (gamepad == null) {
            gamepad = gamepad1;
        }

        wheels = new WheelController(
                hardwareMap.get(DcMotorEx.class, "back_left"),
                hardwareMap.get(DcMotorEx.class, "back_right"),
                hardwareMap.get(DcMotorEx.class, "front_left"),
                hardwareMap.get(DcMotorEx.class, "front_right"));

        wheels.init();

        movements = new MovementController(hardwareMap.get(IMU.class, "imu"), gamepad, !isAutonomous);

        movements.init();
    }

    // Run in a loop after INIT is pressed until PLAY is pressed
    public void init_loop() {
    }

    // Run once PLAY is pressed
    public void start() {
        movements.prep();
    }

    // Run in a loop after PLAY is pressed until STOP is pressed
    public void loop() {
        movements.updatePowers(wheels);

        wheels.update();
    }

    // Run once STOP is pressed
    public void stop() {
    }
}
