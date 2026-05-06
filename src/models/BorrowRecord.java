package models;

public class BorrowRecord {
    private int id;
    private int bookId;
    private int memberId;
    private String borrowDate;
    private String returnDate;

    public BorrowRecord() {
    }

    public BorrowRecord(int id, int bookId, int memberId, String borrowDate, String returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public BorrowRecord(int bookId, int memberId, String borrowDate, String returnDate) {
        this(0, bookId, memberId, borrowDate, returnDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
            "id=" + id +
            ", bookId=" + bookId +
            ", memberId=" + memberId +
            ", borrowDate='" + borrowDate + '\'' +
            ", returnDate='" + returnDate + '\'' +
            '}';
    }
}
