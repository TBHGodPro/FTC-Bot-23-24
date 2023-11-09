package org.firstinspires.ftc.teamcode.AutonomousOld.Movements;

import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.GamepadButton;
import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.GamepadDynamicInput;
import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.MoveDirection;

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
