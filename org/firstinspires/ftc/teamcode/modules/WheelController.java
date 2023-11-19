package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.util.ElapsedTime;

public class WheelController {
    // --- Constants ---

    public static final int setPositionClearance = 10;
    public static final int setPositionTimeLeewayMS = 200;

    // -----------------

    public class WheelTarget {
        int backLeft;
        int backRight;
        int frontLeft;
        int frontRight;
        int targetTime;
        ElapsedTime currentTime;

        public WheelTarget(int backLeft, int backRight, int frontLeft, int frontRight, int targetTime,
                ElapsedTime currentTime) {
            this.backLeft = backLeft;
            this.backRight = backRight;
            this.frontLeft = frontLeft;
            this.frontRight = frontRight;
            this.targetTime = targetTime;
            this.currentTime = currentTime;
        }
    }

    public class WheelPowers {
        double backLeft;
        double backRight;
        double frontLeft;
        double frontRight;

        public WheelPowers(double backLeft, double backRight, double frontLeft, double frontRight) {
            this.set(backLeft, backRight, frontLeft, frontRight);
        }

        public void set(double backLeft, double backRight, double frontLeft, double frontRight) {
            this.backLeft = backLeft;
            this.backRight = backRight;
            this.frontLeft = frontLeft;
            this.frontRight = frontRight;
        }
    }

    public final DcMotorEx backLeft;
    public final DcMotorEx backRight;
    public final DcMotorEx frontLeft;
    public final DcMotorEx frontRight;

    public final DcMotorEx.Direction leftDirection = DcMotorEx.Direction.REVERSE;
    public final DcMotorEx.Direction rightDirection = DcMotorEx.Direction.FORWARD;

    public final ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.BRAKE;;

    public final WheelPowers powers;

    public WheelTarget target = null;

    public WheelController(DcMotorEx backLeft, DcMotorEx backRight, DcMotorEx frontLeft,
            DcMotorEx frontRight) {
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        powers = new WheelPowers(0, 0, 0, 0);
    }

    public void init() {
        backLeft.setDirection(leftDirection);
        frontLeft.setDirection(leftDirection);

        backRight.setDirection(rightDirection);
        frontRight.setDirection(rightDirection);

        backLeft.setZeroPowerBehavior(zeroPowerBehavior);
        backRight.setZeroPowerBehavior(zeroPowerBehavior);
        frontLeft.setZeroPowerBehavior(zeroPowerBehavior);
        frontRight.setZeroPowerBehavior(zeroPowerBehavior);

        resetZeroPositions();
    }

    public void update() {
        if (target == null) {
            setRunMode(RunMode.RUN_WITHOUT_ENCODER);

            backLeft.setPower(powers.backLeft);
            backRight.setPower(powers.backRight);
            frontLeft.setPower(powers.frontLeft);
            frontRight.setPower(powers.frontRight);
        } else {
            if ((isAtTarget(backLeft.getCurrentPosition(), target.backLeft)
                    && isAtTarget(backRight.getCurrentPosition(), target.backRight)
                    && isAtTarget(frontLeft.getCurrentPosition(), target.frontLeft)
                    && isAtTarget(frontRight.getCurrentPosition(), target.frontRight))
                    || (target.currentTime.milliseconds() >= (target.targetTime + setPositionTimeLeewayMS))) {
                resetZeroPositions();

                target = null;

                setRunMode(RunMode.RUN_WITHOUT_ENCODER);
            } else {
                setRunMode(RunMode.RUN_TO_POSITION);

                backLeft.setTargetPosition(target.backLeft);
                backRight.setTargetPosition(target.backRight);
                frontLeft.setTargetPosition(target.frontLeft);
                frontRight.setTargetPosition(target.frontRight);

                backLeft.setVelocity(Math.abs(target.backLeft) / target.targetTime * 1000);
                backRight.setVelocity(Math.abs(target.backRight) / target.targetTime * 1000);
                frontLeft.setVelocity(Math.abs(target.frontLeft) / target.targetTime * 1000);
                frontRight.setVelocity(Math.abs(target.frontRight) / target.targetTime * 1000);
            }
        }
    }

    public void setTarget(WheelTarget target) {
        this.target = target;
    }

    public void setPowers(double backLeft, double backRight, double frontLeft, double frontRight) {
        powers.set(
                backLeft,
                backRight,
                frontLeft,
                frontRight);
    }

    public boolean isAtTarget(int current, int target) {
        return (Math.round(target / setPositionClearance * 2) - Math.round(current - setPositionClearance * 2)) == 0;
    }

    public void setRunMode(RunMode mode) {
        if (backLeft.getMode() == mode)
            return;

        backLeft.setMode(mode);
        backRight.setMode(mode);
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
    }

    public void resetZeroPositions() {
        RunMode mode = backLeft.getMode();

        setRunMode(RunMode.STOP_AND_RESET_ENCODER);

        setRunMode(mode);
    }
}