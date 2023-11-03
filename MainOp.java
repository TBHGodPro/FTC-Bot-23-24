package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
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
  // Dynamic Constants
  private int armIntakePos = 26;
  private double wristIntakePos = 0.395;

  // Bot
  private BotState state;
  private int imuInitTimeoutMS = 150;

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

  private DcMotor.Direction leftWheelDirection = DcMotor.Direction.REVERSE;
  private DcMotor.Direction rightWheelDirection = DcMotor.Direction.FORWARD;

  private DcMotor.ZeroPowerBehavior wheelZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private DcMotor.RunMode wheelRunMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

  // Arm
  private DcMotor armLeft;
  private DcMotor.Direction armLeftDirection = DcMotor.Direction.FORWARD;

  private DcMotor armRight;
  private DcMotor.Direction armRightDirection = DcMotor.Direction.REVERSE;

  private DcMotor.ZeroPowerBehavior armZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private Integer armTargetPos = null;

  private double armManualPower = 0.75;
  private double armSetPositionPower = 0.6;

  private double armSetPositionAccuracy /* Lower is higher accuracy */ = 5;
  private double armSetPositionDeadZone = 25;

  // Wrist
  private Servo wrist;
  private Servo.Direction wristDirection = Servo.Direction.REVERSE;

  private double wristPos = 1;
  private double wristPosInterval = 0.002;

  // Hand
  private Servo hand;
  private Servo.Direction handDirection = Servo.Direction.FORWARD;

  private boolean isHandClosed = false;
  private double handOpenPos = 0.7;
  private double handClosedPos = 0.95;

  /**
   * This function is executed when this Op Mode is selected from the Driver
   * Station.
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
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)));

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

    armLeft = hardwareMap.get(DcMotor.class, "arm_left");
    armLeft.setDirection(armLeftDirection);
    armLeft.setZeroPowerBehavior(armZeroPowerBehavior);

    armRight = hardwareMap.get(DcMotor.class, "arm_right");
    armRight.setDirection(armRightDirection);
    armRight.setZeroPowerBehavior(armZeroPowerBehavior);

    wrist = hardwareMap.get(Servo.class, "wrist");
    wrist.setDirection(wristDirection);

    hand = hardwareMap.get(Servo.class, "hand");
    hand.setDirection(handDirection);

    armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    setState(BotState.Ready);

    waitForStart();

    if (opModeIsActive()) {
      setState(BotState.Running);

      sleep(imuInitTimeoutMS);

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
        if (gamepad1.dpad_down) {
          rawY -= dpad_up_down_power;
        }

        // - Left/Right
        if (gamepad1.dpad_left) {
          rawX -= dpad_left_right_power;
        }
        if (gamepad1.dpad_right) {
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

        // Arm Handling
        // - Not At Target Position
        if (!isArmAtTarget()) {
          if (armLeft.getTargetPosition() != armTargetPos) {
            armLeft.setTargetPosition(armTargetPos);
            armRight.setTargetPosition(armTargetPos);
          }

          if (armLeft.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
            armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          }

          armLeft.setPower(armSetPositionPower);
          armRight.setPower(armSetPositionPower);
        }
        // - No Target Position - Manual Control
        else {
          armLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
          armRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

          double armPower = 0;

          armPower += gamepad1.right_trigger;
          armPower -= gamepad1.left_trigger;

          armLeft.setPower(armPower * armManualPower);
          armRight.setPower(armPower * armManualPower);
        }

        // Wrist Control
        if (gamepad1.right_bumper) {
          wristPos += wristPosInterval;
        }
        if (gamepad1.left_bumper) {
          wristPos -= wristPosInterval;
        }

        // Send Power to Wrist
        if (wrist.getPosition() != wristPos) {
          wrist.setPosition(wristPos);
        }

        // Hand Control
        if (gamepad1.x) {
          isHandClosed = true;
        }
        if (gamepad1.y) {
          isHandClosed = false;
        }

        // Send Power to Hand
        if (hand.getPosition() != (isHandClosed ? handClosedPos : handOpenPos)) {
          hand.setPosition(isHandClosed ? handClosedPos : handOpenPos);
        }

        // Intake Position
        if (gamepad1.b) {
          setArmPosition(armIntakePos);
          wristPos = wristIntakePos;
          isHandClosed = false;
        }
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
    telemetry.addData("State", new Func<String>() {
      @Override
      public String value() {
        return state.name();
      }
    });

    telemetry.addLine();

    telemetry.addLine("--- Bot ---");
    telemetry.addLine();
    telemetry.addData("Angle", new Func<String>() {
      @Override
      public String value() {
        return round(angles == null ? 0 : angles.getYaw(AngleUnit.DEGREES)) + "°";
      }
    });

    telemetry.addLine();

    telemetry.addLine("--- Headless ---");
    telemetry.addLine();
    telemetry.addData("Straight Angle", new Func<String>() {
      @Override
      public String value() {
        return round(straightAngle) + "°";
      }
    });

    telemetry.addLine();

    telemetry.addLine("--- Arm ---");
    telemetry.addLine();
    telemetry.addData("Position", new Func<String>() {
      @Override
      public String value() {
        return armLeft == null || armRight == null ? "0 0"
            : armLeft.getCurrentPosition() + " " + armRight.getCurrentPosition();
      }
    })
        .addData("Target", new Func<String>() {
          @Override
          public String value() {
            return armTargetPos == null ? "NONE" : armTargetPos + "";
          }
        })
        .addData("Wrist Pos", new Func<String>() {
          @Override
          public String value() {
            return round(wristPos) + "";
          }
        });

    telemetry.addLine();

    telemetry.addLine("--- Wheels ---");
    telemetry.addLine();
    telemetry.addData("Back Forward Correction ",
        " Left = " + backLeft_forward_correction + ", Right = " + backRight_forward_correction)
        .addData("Back Strafe Correction     ",
            " Left = " + backLeft_strafe_correction + ", Right = " + backRight_strafe_correction)
        .addData("Wheel Direction           ",
            " Left = " + leftWheelDirection.toString().charAt(0) + ", Right = "
                + rightWheelDirection.toString().charAt(0))
        .addData("Zero Power Behavior ", " " + wheelZeroPowerBehavior)
        .addData("Wheel Run Mode         ", " " + wheelRunMode);

    telemetry.addLine();
  }

  private double round(double value) {
    return Math.round(value * 100) / 100;
  }

  private boolean isArmAtTarget() {
    if (armTargetPos == null) {
      return true;
    }

    if (Math.round(armLeft.getCurrentPosition() / armSetPositionAccuracy) == Math
        .round(armTargetPos / armSetPositionAccuracy)) {
      armTargetPos = null;

      return true;
    } else {
      return false;
    }
  }

  private void setArmPosition(int pos) {
    armTargetPos = (int) (pos + ((armLeft.getCurrentPosition() - pos) / armSetPositionDeadZone));
  }
}
