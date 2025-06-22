package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@TeleOp(name="LimelightTracker", group="Robot")
public class BlockTracker extends LinearOpMode {
    Limelight3A limelight;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!

        limelight.pipelineSwitch(0);
    }

    @Override
    public void runOpMode() {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                double tx = result.getTx(); // How far left or right the target is (degrees)
                double ty = result.getTy(); // How far up or down the target is (degrees)
                double ta = result.getTa(); // How big the target looks (0%-100% of the image)
                
                final double ROTATE_DEADZONE = 5;
                final double STRAFE_SPEED = 0.3;

                if (tx > ROTATE_DEADZONE) {
                    // Strafe left
                    frontLeftMotor.setPower(-STRAFE_SPEED);
                    backLeftMotor.setPower(STRAFE_SPEED);
                    frontRightMotor.setPower(STRAFE_SPEED);
                    backRightMotor.setPower(-STRAFE_SPEED);
                } else if (tx < -ROTATE_DEADZONE) {
                    // Strafe right
                    frontLeftMotor.setPower(STRAFE_SPEED);
                    backLeftMotor.setPower(-STRAFE_SPEED);
                    frontRightMotor.setPower(-STRAFE_SPEED);
                    backRightMotor.setPower(STRAFE_SPEED);
                } else {
                    // Stop rotation
                    final double CLOSE_THRESHOLD = 50;
                    final double CLOSE_DEADZONE = 5;
                    final double APPROACH_SPEED = 0.3;

                    if (ta + CLOSE_DEADZONE < CLOSE_THRESHOLD) {
                        // Move forward to target
                        frontLeftMotor.setPower(APPROACH_SPEED);
                        backLeftMotor.setPower(APPROACH_SPEED);
                        frontRightMotor.setPower(APPROACH_SPEED);
                        backRightMotor.setPower(APPROACH_SPEED);
                    } else if (ta - CLOSE_DEADZONE > CLOSE_THRESHOLD) {
                        // Move backward from target
                        frontLeftMotor.setPower(-APPROACH_SPEED);
                        backLeftMotor.setPower(-APPROACH_SPEED);
                        frontRightMotor.setPower(-APPROACH_SPEED);
                        backRightMotor.setPower(-APPROACH_SPEED);
                    }

                    frontLeftMotor.setPower(0);
                    backLeftMotor.setPower(0);
                    frontRightMotor.setPower(0);
                    backRightMotor.setPower(0);
                }
                
                telemetry.addData("Target X", tx);
                telemetry.addData("Target Y", ty);
                telemetry.addData("Target Area", ta);
            } else {
                telemetry.addData("Limelight", "No Targets");
            }
        }
    }

}
