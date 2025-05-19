package dao;

import javafx.util.converter.LocalDateStringConverter;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class LoanDAO {
    private final Connection connection;
    private final ReceiptDAO receiptDAO;
    private final UserDAO userDAO;
    private final CopyDAO copyDAO;

    public LoanDAO(Connection connection) throws SQLException {
        this.connection = connection;
        this.receiptDAO = new ReceiptDAO(this.connection); //ReceiptDAO använder samma connection objekt som LoanDAO
        this.userDAO = new UserDAO(this.connection);
        this.copyDAO = new CopyDAO(this.connection);
    }

    //Nedanstående kan flyttas till MediaDAO, eller CopyDAO för bättre objektorientering
    private int getLoanPeriod(Media.MediaType mediaType) throws SQLException { //Hämtar lånperioden för de olika sorternas media
        String sql = "SELECT getLoanPeriod(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(mediaType));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("getLoanPeriod returnerade inget.");
        }
    }

    //Nedanstående kan flyttas till UserDAO för bättre objekorientering
    private int getMaxLoanCount(String userType) throws SQLException { //Färdig funktion i MySQL som räknar ut lånmängden beroende på användartyp
        String sql = "SELECT getMaxLoanCount(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("getMaxLoanCount returnerade inget.");
        }
    }

    private int getUserLoanCount(int userId) throws SQLException { //Hämtar hur många aktiva lån en användare har
        String sql = "SELECT COUNT(*) FROM Loan WHERE userId = ? AND returnDate IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("Kunde inte räkna aktiva lån.");
        }
    }

    public Loan createLoan(User user, Copy copy, LocalDate borrowDate) throws SQLException {
        connection.setAutoCommit(false); // Då funktionerna är beroende av varandra så behövs det här så att inte nästa funktion exekverar --> att det blir inkonsekvent i databasen

        try {
            // Kontrollera att användaren inte har för många lön
            int maxLoans = getMaxLoanCount(String.valueOf(user.getUserType()));
            int activeLoans = getUserLoanCount(user.getUserId());
            if (activeLoans >= maxLoans) {
                throw new IllegalArgumentException("Du har redan max antal lån: " + maxLoans);
            }
            if (copy.isReferenceCopy()) {
                throw new IllegalStateException("Referensliteratur får ej lånas ut ");
            }
            if (copy.getMediaType() == Media.MediaType.JOURNAL) {
                throw new IllegalStateException("Tidsskrifter får ej lånas ut");

            }

            // Kontrollera att kopian är tillgänglig att låna
            if (!copy.getAvailability().equals(Copy.AvailabilityStatus.AVAILABLE)) {
                throw new IllegalStateException("Exemplaret är inte tillgängligt.");
            }

            // 1. Hämta låneperiod och räkna ut dueDate
            Media.MediaType mediaType = copy.getMediaType(); // t.ex. BOOK
            int period = getLoanPeriod(mediaType);
            LocalDate dueDate = borrowDate.plusDays(period);

            // 2. Skapa kvitto
            String info = "Lån av exemplar ID: " +
                    copy.getCopyId() +
                    ", titel: " +
                    copy.getMediaName() +
                    ", låntagare: " +
                    user.getName() +
                    ", datum: "
                    + borrowDate +
                    ", förfallodatum: " + dueDate;
            LocalDateTime receiptDate = LocalDateTime.now();
            int receiptId = receiptDAO.createReceipt(user, info, receiptDate);

            // Inserta lånet till databasen
            int loanId;
            String loanSql = "INSERT INTO Loan(userId, copyId, receiptId, borrowDate, dueDate) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement loanStmt = connection.prepareStatement(loanSql, Statement.RETURN_GENERATED_KEYS)) {
                loanStmt.setInt(1, user.getUserId());
                loanStmt.setInt(2, copy.getCopyId());
                loanStmt.setInt(3, receiptId);
                loanStmt.setDate(4, Date.valueOf(borrowDate));
                loanStmt.setDate(5, Date.valueOf(dueDate));

                int affectedRows = loanStmt.executeUpdate();
                if (affectedRows == 0) throw new SQLException("Skapandet av lån misslyckades.");

                try (ResultSet generatedKeys = loanStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        loanId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Misslyckades att hämta loanId.");
                    }
                }
            }

            // Sätter kopian till Loaned ifall lånet har lyckats
            String updateCopySql = "UPDATE Copy SET availability = 'LOANED' WHERE copyId = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateCopySql)) {
                updateStmt.setInt(1, copy.getCopyId());
                int affectedRows = updateStmt.executeUpdate();

                if(affectedRows== 0) throw new SQLException("Uppdatering av availability status misslyckades");
            }

            connection.commit();

            Receipt receipt = new Receipt(receiptId, user, info, receiptDate);
            System.out.println(receipt);

            return new Loan(loanId, user, copy, receiptId, null, borrowDate, dueDate);

        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Loan returnLoan(Loan loan, LocalDate returnDate) throws SQLException {
        connection.setAutoCommit(false);


        try {
            if (returnDate.isBefore(loan.getBorrowDate())) {
                throw new IllegalArgumentException("Returdatum kan inte vara före lånedatum");
            }
            if (loan.getReturnDate() != null) {
                throw new IllegalArgumentException("Lånet är redan tillbakalämnat");
            }


            String updateLoanSql = "UPDATE Loan SET returnDate = ? WHERE loanId = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateLoanSql)) {
                statement.setDate(1, Date.valueOf(returnDate));
                statement.setInt(2, loan.getLoanId());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Kunde inte uppdatera lånet med returnDate");
                }
            }

            String updateCopySql = "UPDATE Copy SET availability = 'AVAILABLE' WHERE copyId = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateCopySql)) {
                statement.setInt(1, loan.getCopy().getCopyId());
               int rowsAffected = statement.executeUpdate();
               if (rowsAffected == 0){
                   throw new SQLException("Kunde inte uppdatera exemplarets tillgänglighet");
               }
            }
            //Skapa nytt kvitto
            String info = "Återlämning av exemplar ID: " +
                    loan.getCopy().getCopyId() +
                    ", titel:" + loan.getCopy().getMediaName() +
                    ", av användare: " + loan.getUser().getName() +
                    ", returdatum: " + returnDate;
            LocalDateTime receiptDate = LocalDateTime.now();
            receiptDAO.createReceipt(loan.getUser(), info, receiptDate);
            connection.commit();

            loan.setReturnDate(returnDate);
            return loan;
        }
         catch (Exception e) {
            connection.rollback(); //Går tillbaka och återställer transaktionerna vid fel. Så att inte databasen blir inconsistent
            throw e;
        } finally {
            connection.setAutoCommit(true); //Återställer autocommit
        }
    }


    public List<Loan> getActiveLoansForUser(int userId) throws SQLException {
        String sql = "SELECT * FROM ActiveLoansView WHERE userId = ?";
        List<Loan> loans = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int loanId = rs.getInt("loanId");
                    LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                    LocalDate dueDate = rs.getDate("dueDate") != null ? rs.getDate("dueDate").toLocalDate() : null;
                    User user = userDAO.getUserById(rs.getInt("userId"));
                    int copyId = rs.getInt("copyId");
                    Copy copy = copyDAO.getCopyById(copyId);
                    Loan loan = new Loan(loanId, user, copy, -1, borrowDate, null, dueDate); //Anropar konstruktorn där receiptId får vara NULL, alltså -1
                    loans.add(loan);
                }
            }
        }

        return loans;
    }

    public List<Loan> getDueSoonLoansForUser(int userId) throws SQLException {
        String sql = "SELECT * FROM DueSoonLoansView WHERE userId = ?";
        List<Loan> loans = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int loanId = rs.getInt("loanId");
                    LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                    LocalDate dueDate = rs.getDate("dueDate") != null ? rs.getDate("dueDate").toLocalDate() : null;
                    int copyId = rs.getInt("copyId");
                    User user = userDAO.getUserById(userId);
                    Copy copy = copyDAO.getCopyById(copyId);

                    Loan loan = new Loan(loanId, user, copy, -1, borrowDate, null, dueDate);
                    loans.add(loan);
                }
            }
        }

        return loans;
    }

    public List<Loan> getOverdueLoansForUser(int userId) throws SQLException {
        String sql = "SELECT * FROM OverdueLoansView WHERE userId = ?";
        List<Loan> loans = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int loanId = rs.getInt("loanId");
                    LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                    LocalDate dueDate = rs.getDate("dueDate") != null ? rs.getDate("dueDate").toLocalDate() : null;
                    int copyId = rs.getInt("copyId");
                    User user = userDAO.getUserById(userId);
                    Copy copy = copyDAO.getCopyById(copyId);

                    Loan loan = new Loan(loanId, user, copy, -1, borrowDate, null, dueDate);
                    loans.add(loan);
                }
            }
        }

        return loans;
    }

}




//getLoansByUserId -- Vy av aktiva lån
//getOverDueLoans -- Hämta färdigt från SQL
//viewLoans?

//Användare trycker på lån
//Programmet måste skapa ett lån, ett kvitto. Både uppdatera i databasen och presentera för användare
//Säkerställa att Copy är tillgänglig och ändra availabilityStatus
//Kontrollera funktioner från databasen så att mediaTyp får en lånelängd
//Och så att användaren inte lånar för många saker
//Kvittot med relevant information skrivs ut
