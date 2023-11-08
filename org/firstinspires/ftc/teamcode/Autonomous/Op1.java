package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoOp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Auto Op 1")
public class Op1 extends AutoOp {
    public void runLoop() {
        gamepad.right_trigger = 0.25f;
    }
}