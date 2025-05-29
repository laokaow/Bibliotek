package dao;

import controller.UserLoanInfo;
import dto.LoanDTO;
import javafx.util.converter.LocalDateStringConverter;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import dao.MediaDAO;
import dao.UserDAO;

public class LoanDAO {
    private final Connection connection;
    private final ReceiptDAO receiptDAO;
    private final UserDAO userDAO;
    private final CopyDAO copyDAO;
    private final MediaDAO mediaDAO;

    public LoanDAO(Connection connection) throws SQLException {
        this.connection = connection;
        this.receiptDAO = new ReceiptDAO(this.connection); //ReceiptDAO använder samma connection objekt som LoanDAO
        this.userDAO = new UserDAO(this.connection);
        this.copyDAO = new CopyDAO(this.connection);
        this.mediaDAO = new MediaDAO(this.connection);
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
            int maxLoans = userDAO.getMaxLoanCount(String.valueOf(user.getUserType()));
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


            Media.MediaType mediaType = copy.getMediaType(); // t.ex. BOOK
            int period = mediaDAO.getLoanPeriod(mediaType);
            LocalDate dueDate = borrowDate.plusDays(period);


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

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        int loanId = rs.getInt("loanId");
        int userId = rs.getInt("userId");
        int copyId = rs.getInt("copyId");
        int receiptId = rs.getInt("receiptId");
        LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
        Date returnDateSql = rs.getDate("returnDate");
        LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;
        Date dueDateSql = rs.getDate("dueDate");
        LocalDate dueDate = dueDateSql != null ? dueDateSql.toLocalDate() : null;

        User user = userDAO.getUserById(userId);
        Copy copy = copyDAO.getCopyById(copyId);

        // Anpassa till din konstruktor (se till att argumenten passar)
        return new Loan(loanId, user, copy, receiptId, returnDate, borrowDate, dueDate);
    }
    //Nedanstående skapad för att inte behöva ändra i model.Loan
    public List<LoanDTO> getLoanDTOsByUserId(int userId) {
        List<LoanDTO> loanDTOs = new ArrayList<>();
        String sql = "SELECT l.*, m.mediaName AS mediaName " +
                "FROM Loan l " +
                "JOIN Copy c ON l.copyId = c.copyId " +
                "JOIN Media m ON c.mediaId = m.mediaId " +
                "WHERE l.userId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                String mediaTitle = rs.getString("mediaTitle");
                loanDTOs.add(new LoanDTO(loan, mediaTitle));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loanDTOs;
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

    public List<UserLoanInfo> getActiveLoansWithMediaTitleByUserId(int userId) {
        List<UserLoanInfo> loans = new ArrayList<>();
        String sql = """
        SELECT m.title, l.dueDate
        FROM Loan l
        JOIN Media m ON l.mediaId = m.mediaId
        WHERE l.userId = ? AND l.returnDate IS NULL
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                LocalDate dueDate = rs.getDate("dueDate").toLocalDate();
                String status = dueDate.isBefore(LocalDate.now()) ? "Försenad" : "Aktiv";
                loans.add(new UserLoanInfo(title, dueDate, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

}

