package org.firstinspires.ftc.teamcode.Movements;

import org.firstinspires.ftc.teamcode.Movements.MoveDirection;
import org.firstinspires.ftc.teamcode.Movements.GamepadButton;

public class Action {
    public ActionType type;

    // Move and Turn
    public Double time;

    // Move
    public MoveDirection direction;
    public Integer steps;

    // Turn
    public Double degrees;

    // Gamepad
    public Boolean active;
    public GamepadButton button;
}
