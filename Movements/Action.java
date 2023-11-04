package org.firstinspires.ftc.teamcode.Movements;

import org.firstinspires.ftc.teamcode.Movements.MoveDirection;
import org.firstinspires.ftc.teamcode.Movements.GamepadButton;
import org.firstinspires.ftc.teamcode.Movements.GamepadDynamicInput;

public class Action {
    public ActionType type;

    // Wait
    public Long timeLong;

    // Move and Turn
    public Double timeDouble;

    // Move
    public MoveDirection direction;
    public Integer steps;

    // Turn
    public Double degrees;

    // Gamepad
    public Boolean isDynamic;

    public Boolean active;
    public GamepadButton button;

    public Float value;
    public GamepadDynamicInput input;
}
