package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import org.firstinspires.ftc.teamcode.AutoOp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Test Op 1", group = "Tests")
public class AutoOp1 extends AutoOp {
    @Override
    public void runOP() {
        gamepad.guide = true;
        gamepad.right_stick_x = 0.5f;

        awaitFrame();

        gamepad.guide = false;

        sleep(3000);
    }
}
