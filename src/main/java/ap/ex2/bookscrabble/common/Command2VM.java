package ap.ex2.bookscrabble.common;

public class Command2VM {
    public final Command command;
    public final Object args;

    public Command2VM(Command command) {
        this(command, null);
    }

    public Command2VM(Command command, Object args) {
        this.command = command;
        this.args = args;
    }
}
