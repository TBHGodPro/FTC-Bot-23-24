package org.firstinspires.ftc.teamcode.AutonomousNew.Ops;

import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "New Auto 1 (*START RIGHT SIDE OF TILE*)", group = "New Autonomous")
public class Op1 extends BaseOp {
    public void runLoop() {
        gamepad.right_trigger = 0.25f;
    }
}