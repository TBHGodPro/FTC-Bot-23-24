package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class WheelController {
    // --- Constants ---

    public static final int maxVelocity = 500;

    public static final double dpad_vertical_power = 0.35;
    public static final double dpad_horizontal_power = 0.25;

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

    public class WheelVelocities {
        int backLeft;
        int backRight;
        int frontLeft;
        int frontRight;

        public WheelVelocities(int backLeft, int backRight, int frontLeft, int frontRight) {
            this.set(backLeft, backRight, frontLeft, frontRight);
        }

        public void set(int backLeft, int backRight, int frontLeft, int frontRight) {
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

    public final DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotorEx.ZeroPowerBehavior.BRAKE;

    public final WheelVelocities velocities;

    public WheelTarget target = null;

    public WheelController(DcMotorEx backLeft, DcMotorEx backRight, DcMotorEx frontLeft, DcMotorEx frontRight) {
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        velocities = new WheelVelocities(0, 0, 0, 0);
    }

    public void setTarget(WheelTarget target) {
        this.target = target;
    }

    public void setPowers(double backLeft, double backRight, double frontLeft, double frontRight) {
        velocities.set(
                (int) Math.round(backLeft * maxVelocity),
                (int) Math.round(backRight * maxVelocity),
                (int) Math.round(frontLeft * maxVelocity),
                (int) Math.round(frontRight * maxVelocity));
    }

    public void run() {
        if (target == null) {
            setRunMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        } else {
            if ((isAtTarget(backLeft.getCurrentPosition(), target.backLeft)
                    && isAtTarget(backRight.getCurrentPosition(), target.backRight)
                    && isAtTarget(frontLeft.getCurrentPosition(), target.frontLeft)
                    && isAtTarget(frontRight.getCurrentPosition(), target.frontRight))
                    || (target.currentTime.milliseconds() >= (target.targetTime + setPositionTimeLeewayMS))) {
                target = null;

                setRunMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            } else {
                setRunMode(DcMotorEx.RunMode.RUN_TO_POSITION);

                backLeft.setTargetPosition(target.backLeft);
                backRight.setTargetPosition(target.backRight);
                frontLeft.setTargetPosition(target.frontLeft);
                frontRight.setTargetPosition(target.frontRight);

                velocities.set(
                        Math.abs(target.backLeft) / target.targetTime * 1000,
                        Math.abs(target.backRight) / target.targetTime * 1000,
                        Math.abs(target.frontLeft) / target.targetTime * 1000,
                        Math.abs(target.frontRight) / target.targetTime * 1000);
            }
        }

        backLeft.setVelocity(velocities.backLeft);
        backRight.setVelocity(velocities.backRight);
        frontLeft.setVelocity(velocities.frontLeft);
        frontRight.setVelocity(velocities.frontRight);
    }

    public boolean isAtTarget(int current, int target) {
        return (Math.round(target / setPositionClearance * 2) - Math.round(current - setPositionClearance * 2)) == 0;
    }

    public void setRunMode(DcMotorEx.RunMode mode) {
        if (backLeft.getMode() == mode)
            return;

        backLeft.setMode(mode);
        backRight.setMode(mode);
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
    }
}