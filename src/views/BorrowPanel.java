package views;

import dao.BookDAO;
import dao.BorrowRecordDAO;
import models.Book;
import models.BorrowRecord;
import utils.ValidationUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BorrowPanel extends JPanel {
    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField bookIdField = new JTextField();
    private final JTextField memberIdField = new JTextField();
    private final JTextField borrowDateField = new JTextField();
    private final JTextField returnDateField = new JTextField();

    public BorrowPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Book ID", "Member ID", "Borrow Date", "Return Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 18));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> populateFormFromSelection());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.EAST);

        borrowDateField.setText(LocalDate.now().toString());
        loadBorrowRecordsAsync();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setPreferredSize(new Dimension(320, 10));

        addFormRow(form, "Book ID", bookIdField);
        addFormRow(form, "Member ID", memberIdField);
        addFormRow(form, "Borrow Date (YYYY-MM-DD)", borrowDateField);
        addFormRow(form, "Return Date (YYYY-MM-DD)", returnDateField);

        JButton issueButton = new JButton("Issue Book");
        issueButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        issueButton.addActionListener(event -> issueBook());
        JButton returnButton = new JButton("Return Book");
        returnButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        returnButton.addActionListener(event -> returnBook());
        JButton deleteButton = new JButton("Delete Record");
        deleteButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        deleteButton.addActionListener(event -> deleteRecord());

        form.add(issueButton);
        form.add(returnButton);
        form.add(deleteButton);
        return form;
    }

    private void addFormRow(JPanel form, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        form.add(label);
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setPreferredSize(new Dimension(0, 36));
        form.add(field);
    }

    private void loadBorrowRecordsAsync() {
        new SwingWorker<List<BorrowRecord>, Void>() {
            @Override
            protected List<BorrowRecord> doInBackground() {
                return borrowRecordDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    updateTable(get());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(BorrowPanel.this, "Failed to load borrow records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTable(List<BorrowRecord> records) {
        tableModel.setRowCount(0);
        for (BorrowRecord record : records) {
            tableModel.addRow(new Object[]{
                record.getId(),
                record.getBookId(),
                record.getMemberId(),
                record.getBorrowDate(),
                record.getReturnDate()
            });
        }
    }

    private void issueBook() {
        if (!validateInput(false)) {
            return;
        }
        int bookId = Integer.parseInt(bookIdField.getText().trim());
        Optional<Book> bookOpt = bookDAO.getById(bookId);
        if (bookOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book not found.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Book book = bookOpt.get();
        if (!"Available".equalsIgnoreCase(book.getStatus())) {
            JOptionPane.showMessageDialog(this, "Book is not available.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BorrowRecord record = new BorrowRecord(
            bookId,
            Integer.parseInt(memberIdField.getText().trim()),
            borrowDateField.getText().trim(),
            returnDateField.getText().trim().isBlank() ? null : returnDateField.getText().trim()
        );
        if (borrowRecordDAO.insert(record)) {
            book.setStatus("Borrowed");
            bookDAO.update(book);
            clearForm();
            loadBorrowRecordsAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to issue book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a record to return.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int recordId = (int) tableModel.getValueAt(row, 0);
        int bookId = (int) tableModel.getValueAt(row, 1);
        int memberId = (int) tableModel.getValueAt(row, 2);
        String borrowDate = tableModel.getValueAt(row, 3).toString();
        String returnDate = returnDateField.getText().trim();
        if (returnDate.isBlank()) {
            returnDate = LocalDate.now().toString();
        }
        if (!ValidationUtils.isValidDate(returnDate)) {
            JOptionPane.showMessageDialog(this, "Enter a valid return date.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BorrowRecord updated = new BorrowRecord(
            recordId,
            bookId,
            memberId,
            borrowDate,
            returnDate
        );
        if (borrowRecordDAO.update(updated)) {
            bookDAO.getById(bookId).ifPresent(book -> {
                book.setStatus("Available");
                bookDAO.update(book);
            });
            clearForm();
            loadBorrowRecordsAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to return book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (borrowRecordDAO.delete(id)) {
            clearForm();
            loadBorrowRecordsAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        bookIdField.setText(tableModel.getValueAt(row, 1).toString());
        memberIdField.setText(tableModel.getValueAt(row, 2).toString());
        borrowDateField.setText(tableModel.getValueAt(row, 3).toString());
        Object returnDate = tableModel.getValueAt(row, 4);
        returnDateField.setText(returnDate == null ? "" : returnDate.toString());
    }

    private boolean validateInput(boolean requireReturnDate) {
        if (!ValidationUtils.isNumeric(bookIdField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Book ID must be numeric.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isNumeric(memberIdField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Member ID must be numeric.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidDate(borrowDateField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Borrow date must be valid.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (requireReturnDate && !ValidationUtils.isValidDate(returnDateField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Return date must be valid.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        bookIdField.setText("");
        memberIdField.setText("");
        borrowDateField.setText(LocalDate.now().toString());
        returnDateField.setText("");
        table.clearSelection();
    }
}
