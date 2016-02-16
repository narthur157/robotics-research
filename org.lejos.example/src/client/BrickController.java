package client;

import java.io.EOFException;
import java.io.IOException;
import java.util.Random;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.util.Delay;
import lejos.util.Stopwatch;

public class BrickController {
	private PCComm comm;
	
	private final int CHANGE_DISTURBANCE = 101;
	
	private double currentSpeed = 0;
	
	private int disturbPower = 0,
				iter = 0;		//display which iteration of the PID loop is currently executing
	
	private boolean stopping = false;

	Random rand = new Random();

	
	NXTMotor controlWheel = new NXTMotor(MotorPort.A),	//use port A for the wheel we want to control
			 disturbWheel = new NXTMotor(MotorPort.B),	//use port B for the disturbance wheel
			 angleArm 	  =	new NXTMotor(MotorPort.C);	//use port C for the arm angle
	
	Stopwatch procTimer = new Stopwatch();		//times the WHole process


	public BrickController(PCComm commInit) {
		comm = commInit;
	}
	
	public void start() throws Exception {
		WheelTimer controlTimer = new WheelTimer(controlWheel);
		procTimer.reset();	//start timing when connection is made
		while (true) {
			int intMsg;
			try {
				intMsg = comm.receiveInt();
			}
			catch (EOFException e) {
				LCD.clear();
				LCD.drawString("EOFE Exception", 1, 1);
				Delay.msDelay(5000);
				break;
			}         
			//change this to match desiredSpeed in Send.java
			//Note also speed depends on multipliers in getSpeed()
			if (intMsg == CHANGE_DISTURBANCE) { nextCycle(); } 
			else {
				// values in [-100, 100] represent a power level to be set for controlWheel
				// 
				if (inRange(intMsg, -100, 100)) {
					controlWheel.setPower(controlWheel.getPower() + intMsg);
				}
				
				// measure new speed and report values back to pc
				currentSpeed = controlTimer.getSpeed();
				
				comm.sendBrick(getState());
				printState();
			}
		}
		comm.close();
	}
	
	private void printState() {
		//print readings to screen
		LCD.clear();
		LCD.drawString("iter " + iter, 0, 2);
		LCD.drawString("ctrlPow " + controlWheel.getPower(), 0, 3);
		LCD.drawString("curSpd " + currentSpeed, 0, 4);
		LCD.drawString("distrbPow " + disturbPower, 0, 5);
		LCD.refresh();
	}
	
	public BrickState getState() {
		return new BrickState(procTimer.elapsed(), currentSpeed, controlWheel.getPower(), angleArm.getTachoCount());
	}
	
	private void nextCycle() throws IOException {
		++iter;
		disturbPower = rand.nextInt(41) + 35;
		disturbWheel.setPower(-disturbPower);	//negate this since wheels face opposite directions
		comm.sendInt(disturbPower);
	}
	
	private boolean inRange(int val, int min, int max) {
		return val >= min && val <= max;
	}
}
