package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Func;

enum BotState {
    Loading,
    Ready,
    Running,
    Cancelled,
    Stopped
}

@TeleOp(name = "MainOp")
public class MainOp extends LinearOpMode {
  // Bot
  private BotState state;

  // Sensors
  private IMU imu;
  private YawPitchRollAngles angles;

  private double straightAngle;

  // Wheels
  private DcMotor backLeft;
  private DcMotor backRight;
  private DcMotor frontLeft;
  private DcMotor frontRight;

  private double dpad_up_down_power = 0.6;
  private double dpad_left_right_power = 0.4;

  private double backLeft_forward_correction = 0.99;
  private double backRight_forward_correction = 1.00;

  private double backLeft_strafe_correction = 0.7;
  private double backRight_strafe_correction = 0.7;

  private DcMotor.Direction rightWheelDirection = DcMotor.Direction.FORWARD;
  private DcMotor.Direction leftWheelDirection = DcMotor.Direction.REVERSE;

  private DcMotor.ZeroPowerBehavior wheelZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private DcMotor.RunMode wheelRunMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

  /**
   * This function is executed when this Op Mode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    updateTelemetry();
    
    setState(BotState.Loading);

    imu = hardwareMap.get(IMU.class, "imu");
    imu.initialize(
      new IMU.Parameters(
        new RevHubOrientationOnRobot(
          RevHubOrientationOnRobot.LogoFacingDirection.UP,
          RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        )
      )
    );

    backLeft = hardwareMap.get(DcMotor.class, "back_left");
    backLeft.setDirection(leftWheelDirection);
    backLeft.setZeroPowerBehavior(wheelZeroPowerBehavior);
    backLeft.setMode(wheelRunMode);

    backRight = hardwareMap.get(DcMotor.class, "back_right");
    backRight.setDirection(rightWheelDirection);
    backRight.setZeroPowerBehavior(wheelZeroPowerBehavior);
    backRight.setMode(wheelRunMode);

    frontLeft = hardwareMap.get(DcMotor.class, "front_left");
    frontLeft.setDirection(leftWheelDirection);
    frontLeft.setZeroPowerBehavior(wheelZeroPowerBehavior);
    frontLeft.setMode(wheelRunMode);

    frontRight = hardwareMap.get(DcMotor.class, "front_right");
    frontRight.setDirection(rightWheelDirection);
    frontRight.setZeroPowerBehavior(wheelZeroPowerBehavior);
    frontRight.setMode(wheelRunMode);

    setState(BotState.Ready);

    waitForStart();

    if (opModeIsActive()) {
      setState(BotState.Running);

      sleep(250);

      angles = imu.getRobotYawPitchRollAngles();

      straightAngle = angles.getYaw(AngleUnit.DEGREES);

      while (opModeIsActive()) {
        // Live Telemetry
        telemetry.update();

        // Allow Input Changing
        double rawY = -gamepad1.left_stick_y;
        double rawX = gamepad1.left_stick_x;

        // - Up/Down
        if (gamepad1.dpad_up) {
          rawY += dpad_up_down_power;
        }
        if(gamepad1.dpad_down) {
          rawY -= dpad_up_down_power;
        }

        // - Left/Right
        if (gamepad1.dpad_left) {
          rawX -= dpad_left_right_power;
        }
        if(gamepad1.dpad_right) {
          rawX += dpad_left_right_power;
        }

        // Headless Control Mapping

        angles = imu.getRobotYawPitchRollAngles();

        double angle = (straightAngle - angles.getYaw(AngleUnit.DEGREES)) * Math.PI / 180;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double controlX = cos * rawX - sin * rawY;
        double controlY = sin * rawX + cos * rawY;

        // Re-straighten Headless
        if (gamepad1.start) {
          straightAngle = angles.getYaw(AngleUnit.DEGREES);
        }

        // Vehicle Control
        double backLeftPower = 0;
        double backRightPower = 0;
        double frontLeftPower = 0;
        double frontRightPower = 0;

        // Forward/Backward
        backLeftPower += controlY * backLeft_forward_correction;
        backRightPower += controlY * backRight_forward_correction;
        frontLeftPower += controlY;
        frontRightPower += controlY;

        // Strafing
        backLeftPower -= controlX * backLeft_strafe_correction;
        backRightPower += controlX * backRight_strafe_correction;
        frontLeftPower += controlX;
        frontRightPower -= controlX;

        // Turning
        backLeftPower += gamepad1.right_stick_x;
        backRightPower -= gamepad1.right_stick_x;
        frontLeftPower += gamepad1.right_stick_x;
        frontRightPower -= gamepad1.right_stick_x;

        // Send Power to Motors
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
      }

      setState(BotState.Stopped);
    } else {
      setState(BotState.Cancelled);
    }
  }
  
  private void setState(BotState newState) {
    state = newState;
    
    telemetry.update();
  }

  private void updateTelemetry() {
    telemetry.addData("State", new Func < String > () {
        @Override public String value() {
            return state.name();
        }
    });

    telemetry.addLine();
    
    telemetry.addLine("--- Bot ---");
    telemetry.addLine();
    telemetry.addData("Angle", new Func < String > () {
      @Override public String value() {
        return round(angles == null ? 0 : angles.getYaw(AngleUnit.DEGREES)) + "°";
      }
    });

    telemetry.addLine();

    telemetry.addLine("--- Headless ---");
    telemetry.addLine();
    telemetry.addData("Straight Angle", new Func < String > () {
      @Override public String value() {
        return round(straightAngle) + "°";
      }
    });

    telemetry.addLine();

    telemetry.addLine("--- Wheels ---");
    telemetry.addLine();
    telemetry.addData("Back Forward Correction ", " Left = " + backLeft_forward_correction + ", Right = " + backRight_forward_correction)
    .addData("Back Strafe Correction     ", " Left = " + backLeft_strafe_correction + ", Right = " + backRight_strafe_correction)
    .addData("Wheel Direction           ", " Left = " + leftWheelDirection.toString().charAt(0) + ", Right = " + rightWheelDirection.toString().charAt(0))
    .addData("Zero Power Behavior ", " " + wheelZeroPowerBehavior)
    .addData("Wheel Run Mode         ", " " + wheelRunMode);

    telemetry.addLine();
  }
  
  private double round(double value) {
    return Math.round(value * 100) / 100;
  }
}
