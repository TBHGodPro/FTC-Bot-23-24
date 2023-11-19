package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.modules.WheelController;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "MainOp")
public class MainOp extends OpMode {
    public final WheelController wheels;

    public MainOp() {
        wheels = new WheelController(
                hardwareMap.get(DcMotorEx.class, "backLeft"),
                hardwareMap.get(DcMotorEx.class, "backRight"),
                hardwareMap.get(DcMotorEx.class, "frontLeft"),
                hardwareMap.get(DcMotorEx.class, "frontRight"));
    }

    // Run once INIT is pressed
    public void init() {
    }

    // Run in a loop after INIT is pressed until PLAY is pressed
    public void init_loop() {
    }

    // Run once PLAY is pressed
    public void start() {
    }

    // Run in a loop after PLAY is pressed until STOP is pressed
    public void loop() {
    }

    // Run once STOP is pressed
    public void stop() {
    }
}
