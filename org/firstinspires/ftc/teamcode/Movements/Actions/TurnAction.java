package org.firstinspires.ftc.teamcode.Movements.Actions;

import org.firstinspires.ftc.teamcode.Movements.*;

public class TurnAction extends Action {
    public TurnAction(double turnDegrees, double turnTime) {
        type = ActionType.TURN;

        timeDouble = turnTime;

        degrees = turnDegrees;
    }
}
