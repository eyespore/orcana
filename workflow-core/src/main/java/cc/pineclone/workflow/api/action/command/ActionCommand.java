package cc.pineclone.workflow.api.action.command;

public sealed interface ActionCommand permits
        ActionCommand.CoordinateCommand,
        ActionCommand.CustomCommand {

    sealed interface CoordinateCommand
            extends ActionCommand
            permits ActionCommand.CoordinateCommand.Basic {

        enum Basic implements CoordinateCommand {
            START, CANCEL, PAUSE, RESUME;
        }
    }

    non-sealed interface CustomCommand extends ActionCommand { }
}
