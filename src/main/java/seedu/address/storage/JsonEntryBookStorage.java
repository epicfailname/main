package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyEntryBook;

/**
 * A class to access EntryBook data stored as a json file on the hard disk.
 */
public class JsonEntryBookStorage implements EntryBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonEntryBookStorage.class);

    private Path filePath;

    public JsonEntryBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getListEntryBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyEntryBook> readListEntryBook() throws DataConversionException {
        return readListEntryBook(filePath);
    }

    /**
     * Similar to {@link #readListEntryBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyEntryBook> readListEntryBook(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableEntryBook> jsonAddressBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableEntryBook.class);
        if (!jsonAddressBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAddressBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveListEntryBook(ReadOnlyEntryBook listEntryBook) throws IOException {
        saveListEntryBook(listEntryBook, filePath);
    }

    /**
     * Similar to {@link #saveListEntryBook(ReadOnlyEntryBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveListEntryBook(ReadOnlyEntryBook listEntryBook, Path filePath) throws IOException {
        requireNonNull(listEntryBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableEntryBook(listEntryBook), filePath);
    }

}
