package ru.ifmo.se.commands;

public class ShowCommand extends ClassCommand {
    public ShowCommand(){
        this.commandName = CommandName.SHOW;
    }

    @Override
    public String execute(Context context) {
        return context.collection().show();
    }
}
