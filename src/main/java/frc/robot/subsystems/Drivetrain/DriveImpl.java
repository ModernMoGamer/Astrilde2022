package frc.robot.subsystems.Drivetrain;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import ca.team3161.lib.robot.LifecycleEvent;
// import ca.team3161.lib.robot.motion.drivetrains.SpeedControllerGroup;
import ca.team3161.lib.robot.subsystem.RepeatingPooledSubsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.DifferentialDrive.WheelSpeeds;
// import edu.wpi.first.wpilibj.Encoder;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Pair;

public class DriveImpl extends RepeatingPooledSubsystem implements Drive {

    // motor controller groups
    private CANSparkMax leftSide;
    private CANSparkMax rightSide;

    // encoder
    
    private final RelativeEncoder leftEncoder;
    private final RelativeEncoder rightEncoder;

    // PID controller values

    private double kP = 0.000750; 
    private double kI = 0;
    private double kD = 0.000100; 
    private double kIz = 0; 
    private double kFF = 0; 
    private double kMaxOutput = 1; 
    private double kMinOutput = -1;
    private double maxRPM = 5200;

    // PID controllers
    
    private final SparkMaxPIDController leftPIDController;
    private final SparkMaxPIDController rightPIDController;
    private DifferentialDrive drivetrain;


    public DriveImpl(CANSparkMax leftSide, CANSparkMax rightSide, RelativeEncoder leftEncoder, RelativeEncoder rightEncoder) {
        super(20, TimeUnit.MILLISECONDS);
        // basic drivetrain stuff
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.drivetrain = new DifferentialDrive(leftSide, rightSide);
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder; 

        // PID controller impl
        this.leftPIDController = leftSide.getPIDController();
        this.rightPIDController = rightSide.getPIDController();
        //this.leftPIDController = new PIDController(k, ki, kd);
        //this.rightPIDController = new PIDController(kp, ki, kd);

        leftPIDController.setP(kP);
        leftPIDController.setI(kI);
        leftPIDController.setD(kD);
        leftPIDController.setIZone(kIz);
        leftPIDController.setFF(kFF);
        leftPIDController.setOutputRange(kMinOutput, kMaxOutput);

        rightPIDController.setP(kP);
        rightPIDController.setI(kI);
        rightPIDController.setD(kD);
        rightPIDController.setIZone(kIz);
        rightPIDController.setFF(kFF);
        rightPIDController.setOutputRange(kMinOutput, kMaxOutput);


        // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("P Gain", kP);
        SmartDashboard.putNumber("I Gain", kI);
        SmartDashboard.putNumber("D Gain", kD);
        SmartDashboard.putNumber("I Zone", kIz);
        SmartDashboard.putNumber("Feed Forward", kFF);
        SmartDashboard.putNumber("Max Output", kMaxOutput);
        SmartDashboard.putNumber("Min Output", kMinOutput);

    }

    @Override
    public void defineResources() {
        require(leftSide);
        require(rightSide);
        
        require(leftEncoder);
        require(rightEncoder);
    }

    @Override
    public void task() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void driveTank(double leftSpeed, double rightSpeed) {
        this.drivetrain.tankDrive(leftSpeed, rightSpeed);
        SmartDashboard.putNumber("Left Encoder Velocity", leftEncoder.getVelocity());
        SmartDashboard.putNumber("Right Encoder Velocity", rightEncoder.getVelocity());
    }

    @Override
    public void driveArcade(double speed, double rotation) {
        //this.drivetrain.arcadeDrive(speed, rotation);
    }

    public static WheelSpeeds arcadeDriveIK(double xSpeed, double zRotation, boolean squareInputs) {
        xSpeed = MathUtil.clamp(xSpeed, -1.0, 1.0);
        zRotation = MathUtil.clamp(zRotation, -1.0, 1.0);
    
        // Square the inputs (while preserving the sign) to increase fine control
        // while permitting full power.
        // if (squareInputs) {
        //   xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
        //   zRotation = Math.copySign(zRotation * zRotation, zRotation);
        // }
    
        double leftSpeed;
        double rightSpeed;
    
        double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);
    
        if (xSpeed >= 0.0) {
          // First quadrant, else second quadrant
          if (zRotation >= 0.0) {
            leftSpeed = maxInput;
            rightSpeed = xSpeed - zRotation;
          } else {
            leftSpeed = xSpeed + zRotation;
            rightSpeed = maxInput;
          }
        } else {
          // Third quadrant, else fourth quadrant
          if (zRotation >= 0.0) {
            leftSpeed = xSpeed + zRotation;
            rightSpeed = maxInput;
          } else {
            leftSpeed = maxInput;
            rightSpeed = xSpeed - zRotation;
          }
        }
    
        // Normalize the wheel speeds
        double maxMagnitude = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
        if (maxMagnitude > 1.0) {
          leftSpeed /= maxMagnitude;
          rightSpeed /= maxMagnitude;
        }
    
        return new WheelSpeeds(leftSpeed, rightSpeed);
      }

    public void  drivePidTank(double leftSpeed, double rotation){


        // read PID coefficients from SmartDashboard
        double p = SmartDashboard.getNumber("P Gain", 0);
        double i = SmartDashboard.getNumber("I Gain", 0);
        double d = SmartDashboard.getNumber("D Gain", 0);
        double iz = SmartDashboard.getNumber("I Zone", 0);
        double ff = SmartDashboard.getNumber("Feed Forward", 0);
        double max = SmartDashboard.getNumber("Max Output", 1);
        double min = SmartDashboard.getNumber("Min Output", 0);

        // if PID coefficients on SmartDashboard have changed, write new values to controller
        
        if((p != kP)) { leftPIDController.setP(p); kP = p; rightPIDController.setP(p); kP = p; }
        if((i != kI)) { leftPIDController.setI(i); kI = i; rightPIDController.setI(i); kI = i; }
        if((d != kD)) { leftPIDController.setD(d); kD = d; rightPIDController.setD(d); kD = d; }
        if((iz != kIz)) { leftPIDController.setIZone(iz); kIz = iz; rightPIDController.setIZone(iz); kIz = iz; }
        if((ff != kFF)) { leftPIDController.setFF(ff); kFF = ff; rightPIDController.setFF(ff); kFF = ff; }
        if((max != kMaxOutput) || (min != kMinOutput)) { 
        leftPIDController.setOutputRange(min, max); 
        rightPIDController.setOutputRange(min, max); 
        kMinOutput = min; kMaxOutput = max; 
        }

        /**
         * PIDController objects are commanded to a set point using the 
         * SetReference() method.
         * 
         * The first parameter is the value of the set point, whose units vary
         * depending on the control type set in the second parameter.
         * 
         * The second parameter is the control type can be set to one of four 
         * parameters:
         *  com.revrobotics.CANSparkMax.ControlType.kDutyCycle
         *  com.revrobotics.CANSparkMax.ControlType.kPosition
         *  com.revrobotics.CANSparkMax.ControlType.kVelocity
         *  com.revrobotics.CANSparkMax.ControlType.kVoltage
         */
    
        var speeds = arcadeDriveIK(leftSpeed, rotation, true);
    
        double leftSetPoint = speeds.left * maxRPM;
        double rightSetPoint = speeds.right * maxRPM;

        leftPIDController.setReference(leftSetPoint, CANSparkMax.ControlType.kVelocity);
        rightPIDController.setReference(rightSetPoint, CANSparkMax.ControlType.kVelocity);
        
        SmartDashboard.putNumber("Left SetPoint", leftSetPoint);
        SmartDashboard.putNumber("Right SetPoint", leftSetPoint);
        SmartDashboard.putNumber("Left Encoder Velocity", leftEncoder.getVelocity());
        SmartDashboard.putNumber("Right Encoder Velocity", rightEncoder.getVelocity());


        
        //this.drivetrain.tankDrive(this.leftPIDController.calculate(this.leftEncoder.getVelocity(), ), this.rightPIDController.calculate(this.rightEncoder.getVelocity()));
        
    }

    @Override
    public double getHeading() {
        return 0;
    }

    @Override
    public void resetEncoderTicks() {
        // Done
        // this.leftEncoder.reset();
        // this.rightEncoder.reset();
        // removing this for now as relative encoder reset on boot
    }

    @Override
    public Pair<Double, Double> distanceDriven() {
        // return distance of either encoder
        return Pair.of(this.leftEncoder.getPosition() , this.rightEncoder.getPosition());
    }

    //public void setSetpoint(double setpoint){
    //    this.leftPIDController.setSetpoint(setpoint);
    //    this.rightPIDController.setSetpoint(setpoint);
    //    
    //}
    
    @Override
    public void lifecycleStatusChanged(LifecycleEvent previous, LifecycleEvent current) {}

}
