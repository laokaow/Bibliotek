package dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DAOFactory {

    private static UserDAO userDAO;
    private static CopyDAO copyDAO;
    private static BookDAO bookDAO;
    private static ActorDAO actorDAO;
    private static CategoryDAO categoryDAO;
    private static DvdDAO dvdDAO;
    private static JournalDAO journalDAO;
    private static LoanDAO loanDAO;
    private static MediaDAO mediaDAO;
    private static ReceiptDAO receiptDAO;

    public static void init(Connection connection) throws SQLException {
        userDAO = new UserDAO(connection);
        copyDAO = new CopyDAO(connection);
        bookDAO = new BookDAO(connection, categoryDAO);
        actorDAO = new ActorDAO(connection);
        categoryDAO = new CategoryDAO(connection);
        dvdDAO = new DvdDAO(actorDAO, categoryDAO, connection);
        journalDAO = new JournalDAO(connection);
        loanDAO = new LoanDAO(connection);
        mediaDAO = new MediaDAO(connection);
        receiptDAO = new ReceiptDAO(connection);
    }

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static CopyDAO getCopyDAO() {
        return copyDAO;
    }

    public static BookDAO getBookDAO() {
        return bookDAO;
    }

    public static ActorDAO getActorDAO() {
        return actorDAO;
    }

    public static CategoryDAO getCategoryDAO() {
        return categoryDAO;
    }

    public static DvdDAO getDvdDAO() {
        return dvdDAO;
    }

    public static JournalDAO getJournalDAO() {
        return journalDAO;
    }

    public static LoanDAO getLoanDAO() {
        return loanDAO;
    }

    public static MediaDAO getMediaDAO() {
        return mediaDAO;
    }

    public static ReceiptDAO getReceiptDAO() {
        return receiptDAO;
    }
}