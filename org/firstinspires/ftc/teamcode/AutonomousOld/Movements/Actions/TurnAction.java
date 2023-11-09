package org.firstinspires.ftc.teamcode.AutonomousOld.Movements.Actions;

import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.*;

public class TurnAction extends Action {
    public TurnAction(double turnDegrees, double turnTime) {
        type = ActionType.TURN;

        timeDouble = turnTime;

        degrees = turnDegrees;
    }
}
