package seedu.address.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LINK_BOB;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TypicalEntries.ALICE;
import static seedu.address.testutil.TypicalEntries.BENSON;
import static seedu.address.testutil.TypicalEntries.BOB;
import static seedu.address.testutil.TypicalEntries.FILE_TEST_CONTENTS;
import static seedu.address.testutil.TypicalEntries.VALID_FILE_LINK;
import static seedu.address.testutil.TypicalEntries.VALID_HTTPS_LINK;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.address.commons.core.GuiSettings;
import seedu.address.mocks.ModelManagerStub;
import seedu.address.mocks.StorageStub;
import seedu.address.model.entry.Entry;
import seedu.address.model.entry.TitleContainsKeywordsPredicate;
import seedu.address.model.entry.exceptions.EntryNotFoundException;
import seedu.address.storage.ArticleStorage;
import seedu.address.storage.DataDirectoryArticleStorage;
import seedu.address.storage.JsonEntryBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.EntryBookBuilder;
import seedu.address.testutil.EntryBuilder;

public class ModelManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private ModelManager modelManager = new ModelManagerStub();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new EntryBook(), new EntryBook(modelManager.getAddressBook()));
        assertEquals(null, modelManager.getSelectedPerson());
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setUserPrefs(null);
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setArticleDataDirectoryPath(Paths.get("article/data/directory/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        userPrefs.setArticleDataDirectoryPath(Paths.get("new/article/data/directory/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setGuiSettings(null);
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setAddressBookFilePath(null);
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void setArticleDataDirectoryPath_nullPath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setArticleDataDirectoryPath(null);
    }

    @Test
    public void setArticleDataDirectoryPath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("article/data/directory/path");
        modelManager.setArticleDataDirectoryPath(path);
        assertEquals(path, modelManager.getArticleDataDirectoryPath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.hasPerson(null);
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void deletePerson_personIsSelectedAndFirstPersonInFilteredPersonList_selectionCleared() {
        modelManager.addPerson(ALICE);
        modelManager.setSelectedPerson(ALICE);
        modelManager.deletePerson(ALICE);
        assertEquals(null, modelManager.getSelectedPerson());
    }

    @Test
    public void deletePerson_personIsSelectedAndSecondPersonInFilteredPersonList_firstPersonSelected() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BOB);
        assertEquals(Arrays.asList(ALICE, BOB), modelManager.getFilteredPersonList());
        modelManager.setSelectedPerson(BOB);
        modelManager.deletePerson(BOB);
        assertEquals(ALICE, modelManager.getSelectedPerson());
    }

    @Test
    public void setPerson_personIsSelected_selectedPersonUpdated() {
        modelManager.addPerson(ALICE);
        modelManager.setSelectedPerson(ALICE);
        Entry updatedAlice = new EntryBuilder(ALICE).withEmail(VALID_LINK_BOB).build();
        modelManager.setPerson(ALICE, updatedAlice);
        assertEquals(updatedAlice, modelManager.getSelectedPerson());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        modelManager.getFilteredPersonList().remove(0);
    }

    @Test
    public void setSelectedPerson_personNotInFilteredPersonList_throwsPersonNotFoundException() {
        thrown.expect(EntryNotFoundException.class);
        modelManager.setSelectedPerson(ALICE);
    }

    @Test
    public void setSelectedPerson_personInFilteredPersonList_setsSelectedPerson() {
        modelManager.addPerson(ALICE);
        assertEquals(Collections.singletonList(ALICE), modelManager.getFilteredPersonList());
        modelManager.setSelectedPerson(ALICE);
        assertEquals(ALICE, modelManager.getSelectedPerson());
    }

    @Test
    public void equals() {
        EntryBook addressBook = new EntryBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        EntryBook differentAddressBook = new EntryBook();
        UserPrefs userPrefs = new UserPrefs();
        Storage storage = new StorageStub();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs, storage);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs, storage);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs, storage)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getTitle().fullTitle.split("\\s+");
        modelManager.updateFilteredPersonList(new TitleContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs, storage)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs, storage)));

        UserPrefs differentUserPrefs2 = new UserPrefs();
        differentUserPrefs2.setArticleDataDirectoryPath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs, storage)));
    }

    /**
     * This is an integration test to see that ModelManager#addPerson is properly hooked up to ArticleStorage.
     */
    @Test
    public void addPerson_networkArticleSavedToDisk() throws IOException {
        JsonEntryBookStorage addressBookStorage = new JsonEntryBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        ArticleStorage articleStorage = new DataDirectoryArticleStorage(getTempFilePath("articles"));
        Storage storageManager = new StorageManager(addressBookStorage, userPrefsStorage, articleStorage);
        modelManager = new ModelManager(new EntryBook(), new UserPrefs(), storageManager);
        modelManager.addPerson(VALID_HTTPS_LINK);
        String content = new String(
                Files.readAllBytes(
                        modelManager
                                .getStorage()
                                .getArticlePath(VALID_HTTPS_LINK.getLink().value)),
                StandardCharsets.UTF_8);
        assertTrue(content.contains("<p>It works!</p>"));
    }

    /**
     * This is an integration test to see that ModelManager#addPerson is properly hooked up to ArticleStorage.
     */
    @Test
    public void addPerson_localArticleSavedToDisk() throws IOException {
        JsonEntryBookStorage addressBookStorage = new JsonEntryBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        ArticleStorage articleStorage = new DataDirectoryArticleStorage(getTempFilePath("articles"));
        Storage storageManager = new StorageManager(addressBookStorage, userPrefsStorage, articleStorage);
        modelManager = new ModelManager(new EntryBook(), new UserPrefs(), storageManager);
        modelManager.addPerson(VALID_FILE_LINK);
        String content = new String(
                Files.readAllBytes(
                        modelManager
                                .getStorage()
                                .getArticlePath(VALID_FILE_LINK.getLink().value)),
                StandardCharsets.UTF_8);
        assertEquals(FILE_TEST_CONTENTS, content);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.getRoot().toPath().resolve(fileName);
    }
}
