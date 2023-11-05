package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Func;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Gamepad;

enum BotState {
  Loading,
  Ready,
  Running,
  Cancelled,
  Stopped
}

@TeleOp(name = "MainOp")
public class MainOp extends LinearOpMode {
  // Gamepad
  public Gamepad gamepad;

  // Dynamic Constants
  private int armIntakePos = 32;
  private double wristIntakePos = 0.35;
  public boolean shouldOpenHandAtIntake = true;

  // Bot
  private BotState state;
  private int motorResetZeroMS = 150;
  private int imuInitTimeoutMS = 150;

  // Sensors
  private IMU imu;
  private YawPitchRollAngles angles;

  private double straightAngle;

  // Wheels
  private DcMotorEx backLeft;
  private DcMotorEx backRight;
  private DcMotorEx frontLeft;
  private DcMotorEx frontRight;

  private double dpad_up_down_power = 0.6;
  private double dpad_left_right_power = 0.4;

  public double backLeft_forward_correction = 0.99;
  public double backRight_forward_correction = 1.00;

  public double backLeft_strafe_correction = 0.85;
  public double backRight_strafe_correction = 0.8;

  private DcMotor.Direction leftWheelDirection = DcMotor.Direction.REVERSE;
  private DcMotor.Direction rightWheelDirection = DcMotor.Direction.FORWARD;

  private DcMotor.ZeroPowerBehavior wheelZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private Integer backLeftTargetPos = null;
  private Integer backRightTargetPos = null;
  private Integer frontLeftTargetPos = null;
  private Integer frontRightTargetPos = null;
  public Double wheelSetPositionTargetTime = null;

  private double wheelSetPositionPower = 0.4;

  // Arm
  private DcMotorEx armLeft;
  private DcMotor.Direction armLeftDirection = DcMotor.Direction.FORWARD;

  private DcMotorEx armRight;
  private DcMotor.Direction armRightDirection = DcMotor.Direction.REVERSE;

  private DcMotor.ZeroPowerBehavior armZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private Integer armTargetPos = null;
  private int armSetPosSpeed = 500;

  private double armManualPower = 0.75;

  private double armSetPositionAccuracy /* Lower is higher accuracy */ = 3.5;

  // Wrist
  private Servo wrist;
  private Servo.Direction wristDirection = Servo.Direction.REVERSE;

  private double wristPos = 1;
  private double wristPosInterval = 0.002;

  // Hand
  private Servo hand;
  private Servo.Direction handDirection = Servo.Direction.FORWARD;

  public boolean isHandClosed = false;
  private double handOpenPos = 0.7;
  private double handClosedPos = 0.95;

  /**
   * This function is executed when this Op Mode is selected from the Driver
   * Station.
   */
  @Override
  public void runOpMode() {
    if (gamepad == null) {
      gamepad = gamepad1;
    }

    updateTelemetry();

    setState(BotState.Loading);

    imu = hardwareMap.get(IMU.class, "imu");
    imu.initialize(
        new IMU.Parameters(
            new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)));

    backLeft = hardwareMap.get(DcMotorEx.class, "back_left");
    backLeft.setDirection(leftWheelDirection);
    backLeft.setZeroPowerBehavior(wheelZeroPowerBehavior);

    backRight = hardwareMap.get(DcMotorEx.class, "back_right");
    backRight.setDirection(rightWheelDirection);
    backRight.setZeroPowerBehavior(wheelZeroPowerBehavior);

    frontLeft = hardwareMap.get(DcMotorEx.class, "front_left");
    frontLeft.setDirection(leftWheelDirection);
    frontLeft.setZeroPowerBehavior(wheelZeroPowerBehavior);

    frontRight = hardwareMap.get(DcMotorEx.class, "front_right");
    frontRight.setDirection(rightWheelDirection);
    frontRight.setZeroPowerBehavior(wheelZeroPowerBehavior);

    resetWheelPositions();

    armLeft = hardwareMap.get(DcMotorEx.class, "arm_left");
    armLeft.setDirection(armLeftDirection);
    armLeft.setZeroPowerBehavior(armZeroPowerBehavior);

    armRight = hardwareMap.get(DcMotorEx.class, "arm_right");
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

      hand.setPosition(isHandClosed ? handClosedPos : handOpenPos);

      sleep(imuInitTimeoutMS);

      angles = imu.getRobotYawPitchRollAngles();

      straightAngle = angles.getYaw(AngleUnit.DEGREES);

      while (opModeIsActive()) {
        // Live Telemetry
        telemetry.update();

        // Allow Input Changing
        double rawY = -gamepad.left_stick_y;
        double rawX = gamepad.left_stick_x;

        // - Up/Down
        if (gamepad.dpad_up) {
          rawY += dpad_up_down_power;
        }
        if (gamepad.dpad_down) {
          rawY -= dpad_up_down_power;
        }

        // - Left/Right
        if (gamepad.dpad_left) {
          rawX -= dpad_left_right_power;
        }
        if (gamepad.dpad_right) {
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
        if (gamepad.start) {
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
        backLeftPower += gamepad.right_stick_x;
        backRightPower -= gamepad.right_stick_x;
        frontLeftPower += gamepad.right_stick_x;
        frontRightPower -= gamepad.right_stick_x;

        // Send Power to Motors
        if (isWheelAtTarget()) {
          backLeft.setPower(backLeftPower);
          backRight.setPower(backRightPower);
          frontLeft.setPower(frontLeftPower);
          frontRight.setPower(frontRightPower);
        } else {
          backLeft.setVelocity(Math.abs(backLeftTargetPos) / wheelSetPositionTargetTime);
          backRight.setVelocity(Math.abs(backRightTargetPos) / wheelSetPositionTargetTime);
          frontLeft.setVelocity(Math.abs(frontLeftTargetPos) / wheelSetPositionTargetTime);
          frontRight.setVelocity(Math.abs(frontRightTargetPos) / wheelSetPositionTargetTime);
        }

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

          armLeft.setVelocity(armSetPosSpeed);
          armRight.setVelocity(armSetPosSpeed);
        }
        // - No Target Position - Manual Control
        else {
          armLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
          armRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

          double armPower = 0;

          armPower += gamepad.right_trigger;
          armPower -= gamepad.left_trigger;

          armLeft.setPower(armPower * armManualPower);
          armRight.setPower(armPower * armManualPower);
        }

        // Wrist Control
        if (gamepad.right_bumper) {
          wristPos += wristPosInterval;
        }
        if (gamepad.left_bumper) {
          wristPos -= wristPosInterval;
        }

        // Send Power to Wrist
        if (wrist.getPosition() != wristPos) {
          wrist.setPosition(wristPos);
        }

        // Hand Control
        if (gamepad.x) {
          isHandClosed = true;
        }
        if (gamepad.y) {
          isHandClosed = false;
        }

        // Send Power to Hand
        if (hand.getPosition() != (isHandClosed ? handClosedPos : handOpenPos)) {
          hand.setPosition(isHandClosed ? handClosedPos : handOpenPos);
        }

        // Intake Position
        if (gamepad.b) {
          setArmPosition(armIntakePos);
          wristPos = wristIntakePos;
          if (shouldOpenHandAtIntake) {
            isHandClosed = false;
          }
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
        .addData("Target Position ", new Func<String>() {
          @Override
          public String value() {
            return backLeftTargetPos == null ? ""
                : " " + backLeftTargetPos + " " + backRightTargetPos + " " + frontLeftTargetPos + " "
                    + frontRightTargetPos;
          }
        })
        .addData("Set Target Position ", new Func<String>() {
          @Override
          public String value() {
            return backLeft == null ? ""
                : " " + backLeft.getTargetPosition() + " " + backRight.getTargetPosition() + " "
                    + frontLeft.getTargetPosition() + " " + frontRight.getTargetPosition();
          }
        })
        .addData("Position ", new Func<String>() {
          @Override
          public String value() {
            return backLeft == null ? ""
                : " " + backLeft.getCurrentPosition() + " " + backRight.getCurrentPosition() + " "
                    + frontLeft.getCurrentPosition() + " " + frontRight.getCurrentPosition();
          }
        });

    telemetry.addLine();
  }

  private double round(double value) {
    return Math.round(value * 100) / 100;
  }

  public boolean isArmAtTarget() {
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

  public void setArmPosition(int pos) {
    armTargetPos = pos;
  }

  public boolean isWheelAtTarget() {
    if (backLeftTargetPos == null || backRightTargetPos == null || frontLeftTargetPos == null
        || frontRightTargetPos == null || wheelSetPositionTargetTime == null) {
      backLeftTargetPos = null;
      backRightTargetPos = null;
      frontLeftTargetPos = null;
      frontRightTargetPos = null;

      wheelSetPositionTargetTime = null;

      return true;
    }

    if (backLeft.getTargetPosition() != backLeftTargetPos) {
      backLeft.setTargetPosition(backLeftTargetPos);
      backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    if (backRight.getTargetPosition() != backRightTargetPos) {
      backRight.setTargetPosition(backRightTargetPos);
      backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    if (frontLeft.getTargetPosition() != frontLeftTargetPos) {
      frontLeft.setTargetPosition(frontLeftTargetPos);
      frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    if (frontRight.getTargetPosition() != frontRightTargetPos) {
      frontRight.setTargetPosition(frontRightTargetPos);
      frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    if (!backLeft.isBusy() && !backRight.isBusy() && !frontLeft.isBusy() && !frontRight.isBusy()) {
      backLeftTargetPos = null;
      backRightTargetPos = null;
      frontLeftTargetPos = null;
      frontRightTargetPos = null;

      wheelSetPositionTargetTime = null;

      return true;
    }

    return false;
  }

  public void setWheelTargets(double targetTime, int backLeftPos, int backRightPos, int frontLeftPos,
      int frontRightPos) {
    resetWheelPositions();

    wheelSetPositionTargetTime = targetTime;

    backLeftTargetPos = Math.round(backLeftPos);
    backRightTargetPos = Math.round(backRightPos);
    frontLeftTargetPos = Math.round(frontLeftPos);
    frontRightTargetPos = Math.round(frontRightPos);
  }

  public void resetWheelPositions() {
    backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    backLeft.setTargetPosition(0);
    backRight.setTargetPosition(0);
    frontLeft.setTargetPosition(0);
    frontRight.setTargetPosition(0);

    sleep(motorResetZeroMS);

    backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }
}
