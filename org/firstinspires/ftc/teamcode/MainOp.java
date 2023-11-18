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
import com.qualcomm.robotcore.util.ElapsedTime;

enum BotState {
  Loading,
  Ready,
  Running,
  Cancelled,
  Stopped
}

abstract class MainOpBase extends LinearOpMode {
  public abstract void setupExtraTelemetry();
}

@TeleOp(name = "MainOp")
public class MainOp extends MainOpBase {
  // Autonomous
  public Boolean isAutonomous;

  // Gamepad
  public Gamepad gamepad;

  // Dynamic Constants
  private double turningNonlinearity = 1.75; // 1 = linear
  private double armNonlinearity = 2; // 1 = linear

  private double steeringCounterDivisor = 32; // Lower = More Powerful

  private int armIntakePos;
  private int armIntakePosManual = 170;
  private int armIntakePosAutonomous = 315;
  private double wristIntakePos = 0.435;

  private int armBackboardPos = 480;
  private double wristBackboardPos = 0.715;
  
  private int armMaxPos = 1950;
  private double armManualInterval = 15;
  private int armSpeed = 1100;

  // Bot
  private BotState state;

  private long frames = 0;

  private int motorResetZeroMS = 100;
  private int imuInitTimeoutMS = 150;

  // Sensors
  private IMU imu;
  private YawPitchRollAngles angles;

  private double straightAngle;
  private Double currentAngle;

  // Wheels
  private DcMotorEx backLeft;
  private DcMotorEx backRight;
  private DcMotorEx frontLeft;
  private DcMotorEx frontRight;

  private double dpad_up_down_power = 0.35;
  private double dpad_left_right_power = 0.25;

  private DcMotor.Direction leftWheelDirection = DcMotor.Direction.REVERSE;
  private DcMotor.Direction rightWheelDirection = DcMotor.Direction.FORWARD;

  private DcMotor.ZeroPowerBehavior wheelZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private Integer backLeftTargetPos = null;
  private Integer backRightTargetPos = null;
  private Integer frontLeftTargetPos = null;
  private Integer frontRightTargetPos = null;
  public Double wheelSetPositionTargetTime = null;
  private ElapsedTime wheelSetPositionMoveTime = null;

  // Arm
  private DcMotorEx arm;
  private DcMotor.Direction armDirection = DcMotor.Direction.REVERSE;

  private DcMotor.ZeroPowerBehavior armZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

  private int armTargetPos = 0;

  // Wrist
  private Servo wrist;
  private Servo.Direction wristDirection = Servo.Direction.REVERSE;

  public double wristPos = 1;
  private double wristPosInterval = 0.0035;

  // Hand
  private Servo hand;
  private Servo.Direction handDirection = Servo.Direction.FORWARD;

  public boolean isHandClosed = false;
  private double handOpenPos = 0.65;
  private double handClosedPos = 1;

  /**
   * This function is executed when this Op Mode is selected from the Driver
   * Station.
   */
  @Override
  public void runOpMode() {
    if (isAutonomous == null) {
      isAutonomous = false;
    }

    if (gamepad == null) {
      gamepad = gamepad1;
    }

    armIntakePos = isAutonomous ? armIntakePosAutonomous : armIntakePosManual;

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

    arm = hardwareMap.get(DcMotorEx.class, "arm");
    arm.setDirection(armDirection);
    arm.setZeroPowerBehavior(armZeroPowerBehavior);

    wrist = hardwareMap.get(Servo.class, "wrist");
    wrist.setDirection(wristDirection);
    wrist.scaleRange(0.25, 1);

    hand = hardwareMap.get(Servo.class, "hand");
    hand.setDirection(handDirection);

    arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    setState(BotState.Ready);

    waitForStart();

    if (opModeIsActive()) {
      setState(BotState.Running);

      hand.setPosition(isHandClosed ? handClosedPos : handOpenPos);

      arm.setTargetPosition(armTargetPos);
      arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

      sleep(imuInitTimeoutMS);

      angles = imu.getRobotYawPitchRollAngles();

      straightAngle = angles.getYaw(AngleUnit.DEGREES);

      while (opModeIsActive()) {
        // Frame Rate Counter
        frames += 1;

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

        double rawAngle = angles.getYaw(AngleUnit.DEGREES);

        double angle = (straightAngle - rawAngle) * Math.PI / 180;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double controlX = cos * rawX - sin * rawY;
        double controlY = sin * rawX + cos * rawY;

        // Re-straighten Headless
        if (gamepad.start) {
          straightAngle = rawAngle;
        }

        // Vehicle Control
        double backLeftPower = 0;
        double backRightPower = 0;
        double frontLeftPower = 0;
        double frontRightPower = 0;

        // Forward/Backward
        backLeftPower += controlY;
        backRightPower += controlY;
        frontLeftPower += controlY;
        frontRightPower += controlY;

        // Strafing
        backLeftPower -= controlX;
        backRightPower += controlX;
        frontLeftPower += controlX;
        frontRightPower -= controlX;

        // Turning

        // - Non-Linearity
        double turnPower;
        if (gamepad.right_stick_x >= 0) {
          turnPower = Math.pow(gamepad.right_stick_x, turningNonlinearity);
        } else {
          turnPower = -Math.pow(-gamepad.right_stick_x, turningNonlinearity);
        }

        // - Yaw Correction (Not in Autonomous)
        if (!isAutonomous) {
          if (gamepad.right_stick_x != 0) {
            currentAngle = null;
          } else {
            if (currentAngle == null) {
              currentAngle = rawAngle;
            } else {
              if (rawAngle != currentAngle) {
                if (rawAngle > currentAngle) {
                  turnPower += (((rawAngle - currentAngle) + 180) % 360 - 180) / steeringCounterDivisor;
                } else {
                  turnPower -= (((currentAngle - rawAngle) + 180) % 360 - 180) / steeringCounterDivisor;
                }
              }
            }
          }
        }

        // - Power
        backLeftPower += turnPower;
        backRightPower -= turnPower;
        frontLeftPower += turnPower;
        frontRightPower -= turnPower;

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

        // - Reset Intake Position
        if (gamepad.back) {
          armIntakePos = arm.getCurrentPosition();
          wristIntakePos = wristPos;
        }

        // - Control
        double armPower = 0;

        armPower += Math.pow(gamepad.right_trigger, armNonlinearity);
        armPower -= Math.pow(gamepad.left_trigger, armNonlinearity);

        if ((armTargetPos + (armPower * armManualInterval)) <= armMaxPos) {
          armTargetPos += (int) (armPower * armManualInterval);
        }

        // - Set Target Pos
        arm.setTargetPosition(armTargetPos);

        // - Set Power
        arm.setVelocity(armSpeed);

        // Wrist Control
        if (gamepad.right_bumper) {
          wristPos += wristPosInterval;
        }
        if (gamepad.left_bumper) {
          wristPos -= wristPosInterval;
        }

        // Send Power to Wrist
        wrist.setPosition(wristPos);

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

          if (!isAutonomous) {
            isHandClosed = false;
          }
        }

        // Backboard Position
        if (gamepad.a) {
          setArmPosition(armBackboardPos);
          wristPos = wristBackboardPos;
        }

        // Protected Position
        if (gamepad.guide) {
          setArmPosition(0);
          wristPos = 1;
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

  public void setupExtraTelemetry() {
  }

  private void updateTelemetry() {
    telemetry.addData("State", new Func<String>() {
      @Override
      public String value() {
        return state.name();
      }
    })
        .addData("FPS", new Func<String>() {
          @Override
          public String value() {
            return (frames / getRuntime()) + "/s";
          }
        });

    setupExtraTelemetry();

    telemetry.addLine();

    telemetry.addLine("--- Bot ---");
    telemetry.addLine();
    telemetry.addData("Angle", new Func<String>() {
      @Override
      public String value() {
        return round(angles == null ? 0 : angles.getYaw(AngleUnit.DEGREES)) + "°";
      }
    })
        .addData("Desired Angle", new Func<String>() {
          @Override
          public String value() {
            return round(currentAngle == null ? (angles == null ? 0 : angles.getYaw(AngleUnit.DEGREES)) : currentAngle)
                + "°";
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
        return arm == null ? "0"
            : arm.getCurrentPosition() + "";
      }
    })
        .addData("Target", new Func<String>() {
          @Override
          public String value() {
            return armTargetPos + "";
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
    telemetry.addData("Desired Target Position ", new Func<String>() {
      @Override
      public String value() {
        return backLeftTargetPos == null ? ""
            : " " + backLeftTargetPos + " " + backRightTargetPos + " " + frontLeftTargetPos + " "
                + frontRightTargetPos;
      }
    })
        .addData("Active Target Position ", new Func<String>() {
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
        })
        .addData("Move Time", new Func<String>() {
          @Override
          public String value() {
            return wheelSetPositionTargetTime == null ? ""
                : "Target = " + wheelSetPositionTargetTime + ", Current = " + wheelSetPositionMoveTime.seconds();
          }
        });

    telemetry.addLine();
  }

  private double round(double value) {
    return Math.round(value * 100) / 100;
  }

  public void setArmPosition(int pos) {
    armTargetPos = pos;
  }

  public boolean isWheelAtTarget() {
    if (backLeftTargetPos == null || backRightTargetPos == null || frontLeftTargetPos == null
        || frontRightTargetPos == null || wheelSetPositionTargetTime == null || wheelSetPositionMoveTime == null) {
      backLeftTargetPos = null;
      backRightTargetPos = null;
      frontLeftTargetPos = null;
      frontRightTargetPos = null;

      wheelSetPositionTargetTime = null;

      wheelSetPositionMoveTime = null;

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

    if ((!backLeft.isBusy() && !backRight.isBusy() && !frontLeft.isBusy() && !frontRight.isBusy())
        || (wheelSetPositionTargetTime + 200) <= wheelSetPositionMoveTime.seconds()) {
      backLeftTargetPos = null;
      backRightTargetPos = null;
      frontLeftTargetPos = null;
      frontRightTargetPos = null;

      wheelSetPositionTargetTime = null;

      wheelSetPositionMoveTime = null;

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

    wheelSetPositionMoveTime = new ElapsedTime();
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
