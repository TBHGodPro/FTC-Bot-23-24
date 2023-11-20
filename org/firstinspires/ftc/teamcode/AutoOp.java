package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.autonomous.utils.AutonController;

import com.qualcomm.robotcore.hardware.Gamepad;

public abstract class AutoOp extends MainOp {
    public final AutonController controller;

    public AutoOp() {
        super();

        gamepad = new Gamepad();

        controller = new AutonController(this, gamepad);
    }

    @Override
    public void init() {
        super.init();

        controller.init();
    }

    @Override
    public void start() {
        controller.start();

        super.start();
    }

    public abstract void runOP();
}
