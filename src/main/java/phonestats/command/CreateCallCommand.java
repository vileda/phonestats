package phonestats.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static phonestats.Constants.CREATE_CALL_COMMAND_ADDRESS;

@Getter
@Setter
public class CreateCallCommand extends Command {
	private String callId;

	public CreateCallCommand() {
		super(CREATE_CALL_COMMAND_ADDRESS);
	}
}
