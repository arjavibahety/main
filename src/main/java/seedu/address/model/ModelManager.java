package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.entity.Entity;
import seedu.address.model.entity.body.Body;
import seedu.address.model.entity.fridge.Fridge;
import seedu.address.model.entity.worker.Worker;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Worker> filteredWorkers;
    private final FilteredList<Body> filteredBodies;
    private final FilteredList<Fridge> filteredFridges;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredWorkers = new FilteredList<>(this.addressBook.getWorkerList());
        filteredBodies = new FilteredList<>(this.addressBook.getBodyList());
        filteredFridges = new FilteredList<>(this.addressBook.getFridgeList());

    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasEntity(Entity entity) {
        requireNonNull(entity);
        return addressBook.hasEntity(entity);
    }

    @Override
    public void deleteEntity(Entity target) {
        addressBook.removeEntity(target);
        target.getIdNum().removeMapping();
    }

    @Override
    public void addEntity(Entity entity) {
        addressBook.addEntity(entity);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setEntity(Entity target, Entity editedEntity) {
        requireAllNonNull(target, editedEntity);

        addressBook.setEntity(target, editedEntity);
    }
    //=========== Filtered Body List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Worker} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Body> getFilteredBodyList() {
        return filteredBodies;
    }

    @Override
    public void updateFilteredBodyList(Predicate<Body> predicate) {
        requireNonNull(predicate);
        filteredBodies.setPredicate(predicate);
    }

    //=========== Filtered Worker List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Worker} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Worker> getFilteredWorkerList() {
        return filteredWorkers;
    }

    @Override
    public void updateFilteredWorkerList(Predicate<Worker> predicate) {
        requireNonNull(predicate);
        filteredWorkers.setPredicate(predicate);
    }

    //=========== Filtered Fridge List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Fridge} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Fridge> getFilteredFridgeList() {
        return filteredFridges;
    }

    @Override
    public void updateFilteredFridgeList(Predicate<Fridge> predicate) {
        requireNonNull(predicate);
        filteredFridges.setPredicate(predicate);
    }

    //=========== Filtered Person List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Filtered Entities List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Entities} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<? extends Entity> getFilteredEntityList(String entityType) {
        String entityTypeLowerCase = entityType.toLowerCase();
        if (entityTypeLowerCase.equals("w")) {
            return filteredWorkers;
        } else if (entityTypeLowerCase.equals("b")) {
            return filteredBodies;
        } else if (entityTypeLowerCase.equals("f")) {
            return filteredFridges;
        } else {
            return filteredPersons;
        }
    }


    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && userPrefs.equals(other.userPrefs)
                && filteredPersons.equals(other.filteredPersons);
        //&& filteredWorkers.equals(other.filteredWorkers);
        //&& filteredBodies.equals(other.filteredBodies);
        //&& filteredFridges.equals(other.filteredFridges);
    }

}
