package views;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class MainDashboard extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MainDashboard() {
        setTitle("St Mary's Digital Library System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        contentPanel.add(new BookPanel(), "BOOKS");
        contentPanel.add(new MemberPanel(), "MEMBERS");
        contentPanel.add(createPlaceholder("Borrowing module"), "BORROWING");
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "BOOKS");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(new Color(40, 52, 72));
        sidebar.setPreferredSize(new Dimension(200, 640));

        JLabel title = new JLabel("Library", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.add(title, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setBackground(new Color(40, 52, 72));
        nav.setLayout(new java.awt.GridLayout(0, 1, 0, 12));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nav.add(createNavButton("Books", "BOOKS"));
        nav.add(createNavButton("Members", "MEMBERS"));
        nav.add(createNavButton("Borrowing", "BORROWING"));
        sidebar.add(nav, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton createNavButton(String label, String cardName) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.addActionListener(event -> cardLayout.show(contentPanel, cardName));
        return button;
    }

    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
