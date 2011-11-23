package at.uni_salzburg.cs.ckgroup.pilot.vcl;

public class CommandFactory {
	
	public static ICommand build (Class<?> cmdClass, String[] cmdParams) {
		
		if (cmdClass == CommandGoAuto.class) {
			return new CommandGoAuto();
			
		} else if (cmdClass == CommandGoManual.class) {
			return new CommandGoManual();
			
		} else if (cmdClass == CommandFlyToAbs.class) {
			return new CommandFlyToAbs(
				Double.parseDouble(cmdParams[0]),
				Double.parseDouble(cmdParams[1]),
				Double.parseDouble(cmdParams[2]),
				Double.parseDouble(cmdParams[3]),
				Double.parseDouble(cmdParams[4])
			);
			
		} else if (cmdClass == CommandHover.class) {
			return new CommandHover(Long.parseLong(cmdParams[0]));
			
		} else if (cmdClass == CommandLand.class) {
			return new CommandLand();
			
		} else if (cmdClass == CommandTakeOff.class) {
			return new CommandTakeOff(
					Double.parseDouble(cmdParams[0]),
					Long.parseLong(cmdParams[1])
				);
			
		} else if (cmdClass == CommandWaitForGo.class) {
			return new CommandWaitForGo();
			
		} else if (cmdClass == CommandNoop.class) {
			return new CommandNoop();
			
		} else {
			
		}
		
		return null;
	}

}
