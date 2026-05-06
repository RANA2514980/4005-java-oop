package views;

import dao.BookDAO;
import models.Book;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.util.List;

public class BookPanel extends JPanel {
    private final BookDAO bookDAO = new BookDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField titleField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField categoryField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Borrowed"});

    public BookPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Category", "Status"}, 0) {
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

        add(buildSearchPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.EAST);

        loadBooksAsync(null);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel searchLabel = new JLabel("Search (Title or Author)");
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(searchLabel, BorderLayout.WEST);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(0, 36));
        panel.add(searchField, BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(1, 0, 6, 6));
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchButton.addActionListener(event -> searchBooks());
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        resetButton.addActionListener(event -> {
            searchField.setText("");
            loadBooksAsync(null);
        });
        actions.add(searchButton);
        actions.add(resetButton);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setPreferredSize(new Dimension(280, 10));

        addFormRow(form, "Title", titleField);
        addFormRow(form, "Author", authorField);
        addFormRow(form, "Category", categoryField);
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        form.add(statusLabel);
        statusBox.setFont(new Font("SansSerif", Font.PLAIN, 18));
        statusBox.setPreferredSize(new Dimension(0, 36));
        form.add(statusBox);

        JButton addButton = new JButton("Add");
        addButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        addButton.addActionListener(event -> addBook());
        JButton updateButton = new JButton("Update");
        updateButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        updateButton.addActionListener(event -> updateBook());
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        deleteButton.addActionListener(event -> deleteBook());

        form.add(addButton);
        form.add(updateButton);
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

    private void loadBooksAsync(String query) {
        new SwingWorker<List<Book>, Void>() {
            @Override
            protected List<Book> doInBackground() {
                if (query == null || query.isBlank()) {
                    return bookDAO.getAll();
                }
                return bookDAO.searchByTitleOrAuthor(query.trim());
            }

            @Override
            protected void done() {
                try {
                    updateTable(get());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(BookPanel.this, "Failed to load books.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getStatus()
            });
        }
    }

    private void searchBooks() {
        loadBooksAsync(searchField.getText());
    }

    private void addBook() {
        if (!validateInput()) {
            return;
        }
        Book book = new Book(
            titleField.getText().trim(),
            authorField.getText().trim(),
            categoryField.getText().trim(),
            statusBox.getSelectedItem().toString()
        );
        if (bookDAO.insert(book)) {
            clearForm();
            loadBooksAsync(searchField.getText());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a book to update.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) {
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Book book = new Book(
            id,
            titleField.getText().trim(),
            authorField.getText().trim(),
            categoryField.getText().trim(),
            statusBox.getSelectedItem().toString()
        );
        if (bookDAO.update(book)) {
            clearForm();
            loadBooksAsync(searchField.getText());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected book?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (bookDAO.delete(id)) {
            clearForm();
            loadBooksAsync(searchField.getText());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        titleField.setText(tableModel.getValueAt(row, 1).toString());
        authorField.setText(tableModel.getValueAt(row, 2).toString());
        categoryField.setText(tableModel.getValueAt(row, 3).toString());
        statusBox.setSelectedItem(tableModel.getValueAt(row, 4).toString());
    }

    private boolean validateInput() {
        if (titleField.getText().isBlank() || authorField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Title and author are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        categoryField.setText("");
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }
}
