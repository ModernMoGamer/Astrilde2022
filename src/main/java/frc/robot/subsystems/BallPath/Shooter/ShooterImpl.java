package frc.robot.subsystems.BallPath.Shooter;

import java.util.concurrent.TimeUnit;

// import ca.team3161.lib.robot.subsystem.RepeatingIndependentSubsystem;
import ca.team3161.lib.robot.subsystem.RepeatingPooledSubsystem;

public class ShooterImpl extends RepeatingPooledSubsystem implements Shooter {
    public ShooterImpl() {
        super(10, TimeUnit.MILLISECONDS);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void defineResources(){}

    @Override
    public void task(){}

    @Override
    public void findAndCenterTarget() {}

    @Override
    public void centerTarget(double tx){}

    @Override
    public void getDistance(double ty, double angle1, double angle2){}

    // runs flywheel
    @Override
    public boolean readyToShoot(){
        return true;
    }

    @Override
    public int checkBalls(){
        return 0;
    }

    @Override
    public void setHoodAngle(double distance){}

    @Override
    public void start(){}

    @Override
    public void stop(){}
}
