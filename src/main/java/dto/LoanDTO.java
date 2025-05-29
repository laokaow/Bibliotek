package dto;

import model.Loan;
import java.time.LocalDate;

//Klass skapad för att slippa ändra attribut i model.Loan
public class LoanDTO {
    private final Loan loan;
    private final String mediaName;

    public LoanDTO(Loan loan, String mediaName) {
        this.loan = loan;
        this.mediaName = mediaName;
    }

    public Loan getLoan() {
        return loan;
    }

    public String getMediaName() {
        return mediaName;
    }

    public LocalDate getDueDate() {
        return loan.getDueDate();
    }
}