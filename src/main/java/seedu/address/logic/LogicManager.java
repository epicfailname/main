package seedu.address.logic;

import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.EntryBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyEntryBook;
import seedu.address.model.entry.Entry;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final CommandHistory history;
    private final EntryBookParser entryBookParser;

    public LogicManager(Model model) {
        this.model = model;
        history = new CommandHistory();
        entryBookParser = new EntryBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        try {
            Command command = entryBookParser.parseCommand(commandText);
            commandResult = command.execute(model, history);
        } finally {
            history.add(commandText);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyEntryBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Entry> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<String> getHistory() {
        return history.getHistory();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public ReadOnlyProperty<Entry> selectedPersonProperty() {
        return model.selectedPersonProperty();
    }

    @Override
    public void setSelectedPerson(Entry entry) {
        model.setSelectedPerson(entry);
    }

    @Override
    public ReadOnlyProperty<Exception> exceptionProperty() {
        return model.exceptionProperty();
    }

    @Override
    public void setException(Exception exception) {
        model.setException(exception);
    }

    @Override
    public ReadOnlyProperty<CommandResult> commandResultProperty() {
        return model.commandResultProperty();
    }

    @Override
    public void setCommandResult(CommandResult commandResult) {
        model.setCommandResult(commandResult);
    }

}
