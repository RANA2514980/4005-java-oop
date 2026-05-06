package views;

import dao.MemberDAO;
import models.Member;
import utils.ValidationUtils;

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
import java.awt.GridLayout;
import java.util.List;

public class MemberPanel extends JPanel {
    private final MemberDAO memberDAO = new MemberDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Student", "Staff", "Guest"});

    public MemberPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> populateFormFromSelection());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.EAST);

        loadMembersAsync();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setPreferredSize(new Dimension(260, 10));

        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Type"));
        form.add(typeBox);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> addMember());
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(event -> updateMember());
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(event -> deleteMember());

        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);
        return form;
    }

    private void loadMembersAsync() {
        new SwingWorker<List<Member>, Void>() {
            @Override
            protected List<Member> doInBackground() {
                return memberDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    updateTable(get());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MemberPanel.this, "Failed to load members.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTable(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member member : members) {
            tableModel.addRow(new Object[]{
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getType()
            });
        }
    }

    private void addMember() {
        if (!validateInput()) {
            return;
        }
        Member member = new Member(
            nameField.getText().trim(),
            emailField.getText().trim(),
            typeBox.getSelectedItem().toString()
        );
        if (memberDAO.insert(member)) {
            clearForm();
            loadMembersAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add member.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member to update.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) {
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Member member = new Member(
            id,
            nameField.getText().trim(),
            emailField.getText().trim(),
            typeBox.getSelectedItem().toString()
        );
        if (memberDAO.update(member)) {
            clearForm();
            loadMembersAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update member.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member to delete.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (memberDAO.delete(id)) {
            clearForm();
            loadMembersAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete member.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        emailField.setText(tableModel.getValueAt(row, 2).toString());
        typeBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
    }

    private boolean validateInput() {
        if (nameField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidEmail(emailField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        typeBox.setSelectedIndex(0);
        table.clearSelection();
    }
}
