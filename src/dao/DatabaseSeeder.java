package dao;

import models.Book;
import models.Member;
import models.BorrowRecord;
import java.time.LocalDate;

public final class DatabaseSeeder {
    private DatabaseSeeder() {
    }

    public static void seedTestData() {
        BookDAO bookDAO = new BookDAO();
        MemberDAO memberDAO = new MemberDAO();
        BorrowRecordDAO borrowDAO = new BorrowRecordDAO();

        // Clear existing data
        try {
            var connection = DatabaseConnection.getInstance().getConnection();
            var stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM borrow_records");
            stmt.executeUpdate("DELETE FROM books");
            stmt.executeUpdate("DELETE FROM members");
        } catch (Exception e) {
            System.err.println("Error clearing data: " + e.getMessage());
        }

        // Add sample books
        Book[] books = {
            new Book("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "Available"),
            new Book("To Kill a Mockingbird", "Harper Lee", "Fiction", "Available"),
            new Book("1984", "George Orwell", "Dystopian", "Borrowed"),
            new Book("Pride and Prejudice", "Jane Austen", "Romance", "Available"),
            new Book("The Catcher in the Rye", "J.D. Salinger", "Fiction", "Borrowed"),
            new Book("Java Programming", "Herbert Schildt", "Programming", "Available"),
            new Book("Clean Code", "Robert C. Martin", "Programming", "Available"),
            new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy", "Available"),
            new Book("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "Fantasy", "Borrowed"),
            new Book("Dune", "Frank Herbert", "Science Fiction", "Available"),
            new Book("Sapiens", "Yuval Noah Harari", "Non-Fiction", "Available"),
            new Book("A Brief History of Time", "Stephen Hawking", "Science", "Available"),
        };

        for (Book book : books) {
            if (!bookDAO.insert(book)) {
                System.err.println("Failed to insert book: " + book.getTitle());
            }
        }
        System.out.println("✓ Inserted " + books.length + " sample books");

        // Add sample members
        Member[] members = {
            new Member("John Smith", "john.smith@stmary.edu", "Student"),
            new Member("Sarah Johnson", "sarah.johnson@stmary.edu", "Staff"),
            new Member("Michael Brown", "michael.brown@stmary.edu", "Student"),
            new Member("Emily Davis", "emily.davis@stmary.edu", "Student"),
            new Member("David Wilson", "david.wilson@stmary.edu", "Staff"),
            new Member("Lisa Anderson", "lisa.anderson@stmary.edu", "Guest"),
            new Member("Robert Taylor", "robert.taylor@stmary.edu", "Student"),
            new Member("Jennifer Martinez", "jennifer.martinez@stmary.edu", "Staff"),
            new Member("James White", "james.white@stmary.edu", "Student"),
            new Member("Patricia Thomas", "patricia.thomas@stmary.edu", "Guest"),
        };

        for (Member member : members) {
            if (!memberDAO.insert(member)) {
                System.err.println("Failed to insert member: " + member.getName());
            }
        }
        System.out.println("✓ Inserted " + members.length + " sample members");

        // Add sample borrow records
        BorrowRecord[] records = {
            new BorrowRecord(1, 1, LocalDate.now().minusDays(7).toString(), LocalDate.now().minusDays(1).toString()),
            new BorrowRecord(2, 2, LocalDate.now().minusDays(14).toString(), null),
            new BorrowRecord(3, 3, LocalDate.now().minusDays(3).toString(), LocalDate.now().plusDays(7).toString()),
            new BorrowRecord(4, 4, LocalDate.now().minusDays(21).toString(), null),
            new BorrowRecord(5, 5, LocalDate.now().minusDays(2).toString(), LocalDate.now().plusDays(12).toString()),
            new BorrowRecord(6, 1, LocalDate.now().minusDays(30).toString(), LocalDate.now().minusDays(25).toString()),
            new BorrowRecord(7, 6, LocalDate.now().minusDays(5).toString(), LocalDate.now().plusDays(9).toString()),
            new BorrowRecord(8, 7, LocalDate.now().toString(), LocalDate.now().plusDays(14).toString()),
            new BorrowRecord(9, 8, LocalDate.now().minusDays(11).toString(), null),
            new BorrowRecord(10, 9, LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(13).toString()),
        };

        for (BorrowRecord record : records) {
            if (!borrowDAO.insert(record)) {
                System.err.println("Failed to insert borrow record");
            }
        }
        System.out.println("✓ Inserted " + records.length + " sample borrow records");
        System.out.println("\n✓ Database seeded with test data successfully!");
    }
}
