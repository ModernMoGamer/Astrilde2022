package frc.robot.subsystems.Climber;

import ca.team3161.lib.robot.subsystem.Subsystem;

public interface Climber extends Subsystem{
    void extendOuter();
    void retractOuter();
    // to attach inner, non retractable arm to the bar
    void attachInner();
    void angleOuter(double angle);
}