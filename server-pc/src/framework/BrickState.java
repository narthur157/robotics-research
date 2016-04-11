package framework;

import communication.Command;

public class BrickState {
	public int time, controlPower, disturbPower, angle, torquePower;
	public double disturbSpeed;
	
	public BrickState(int time, double disturbSpeed, int disturbPower, 
					  int controlPower, int torquePower, int angle) {
		this.time = time; 					// total elapsed time
		this.disturbSpeed = disturbSpeed;
		this.disturbPower = disturbPower;
		this.controlPower = controlPower; 	// power of the measured wheel
		this.torquePower = torquePower;
		this.angle = angle; 				// angle of the arm
	}
	
	public int getMotorPower(byte motor) {
		if (motor == Command.CONTROL_WHEEL) return controlPower;
		else if (motor == Command.DISTURB_WHEEL) return disturbPower;
		else if (motor == Command.TORQUE_ARM) return torquePower;
		else {
			// should use an enum for this....
			System.err.println("Invalid motor given");
			return -999;
		}
	}
	
	public String toString() {
		return "" + time +  '\t' + disturbSpeed + '\t' + disturbPower + 
				'\t' + controlPower + '\t' + torquePower + '\t' + angle;
	}
	
	// generated by eclipse
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + angle;
		result = prime * result + controlPower;
		result = prime * result + disturbPower;
		result = prime * result + torquePower;
	
		long temp;
		temp = Double.doubleToLongBits(disturbSpeed);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	// partiallyeclipse generated equals method
	// 10% error allowance for equality on disturb speed
	// doesn't include time in comparison
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BrickState))
			return false;
		BrickState other = (BrickState) obj;
		if (angle != other.angle)
			return false;
		if (controlPower != other.controlPower)
			return false;
		if (disturbPower != other.disturbPower)
			return false;
		if (torquePower != other.torquePower)
			return false;
		
		// allow for a bit of error in this comparison..more than just float error
		double diff = Math.abs(Math.abs(disturbSpeed) - Math.abs(other.disturbSpeed));
		// allow 10% error on the speed
		double errAllowance = Math.abs(disturbSpeed) / 10;
		
		if (diff > errAllowance) {
			return false;
		}
			
		return true;
	}
}
