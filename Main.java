from pathlib import Path

code = r'''import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Digital Wallet & Payment Platform
 * Single-file Java Swing desktop prototype.
 *
 * Run:
 *   javac Main.java
 *   java Main
 *
 * Demo data and balances are held in memory and reset when the app closes.
 * This is not a real payment processor or production financial system.
 */
public class Main extends JFrame {
    private static final Color NAVY = new Color(19, 32, 57);
    private static final Color BLUE = new Color(55, 105, 245);
    private static final Color BG = new Color(244, 247, 252);
    private static final Color TEXT = new Color(39, 52, 76);
    private static final Color MUTED = new Color(119, 132, 153);
    private static final Color GREEN = new Color(24, 156, 105);
    private static final Color RED = new Color(205, 66, 76);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private BigDecimal balance = new BigDecimal("12500.00");
    private final List<Tx> transactions = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Transaction ID", "Date & Time", "Type", "Description", "Amount", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel outgoingLabel;
    private JLabel countLabel;
    private JLabel statusLabel;
    private JTable table;
    private JTextField searchField;
    private JLabel walletIdLabel;

    public Main() {
        super("NovaPay | Digital Wallet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 720));
        setSize(1220, 790);
        setLocationRelativeTo(null);
        seedDemoTransactions();
        buildUI();
        refreshAll();
    }

    private void seedDemoTransactions() {
        transactions.add(new Tx("TX-1005", LocalDateTime.now().minusHours(2), "Received",
                "Refund from Green Mart", new BigDecimal("450.00"), "SUCCESS"));
        transactions.add(new Tx("TX-1004", LocalDateTime.now().minusDays(1), "Transfer",
                "Sent to Priya S.", new BigDecimal("-1200.00"), "SUCCESS"));
        transactions.add(new Tx("TX-1003", LocalDateTime.now().minusDays(2), "Added",
                "Bank top-up", new BigDecimal("3000.00"), "SUCCESS"));
        transactions.add(new Tx("TX-1002", LocalDateTime.now().minusDays(3), "Transfer",
                "Sent to Arun K.", new BigDecimal("-650.00"), "SUCCESS"));
        transactions.add(new Tx("TX-1001", LocalDateTime.now().minusDays(4), "Added",
                "Initial wallet funding", new BigDecimal("5000.00"), "SUCCESS"));
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(24, 28, 22, 28));
        root.add(main, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Good day, Oviya 👋");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Here's what's happening with your wallet today.");
        subtitle.setFont(BODY);
        subtitle.setForeground(MUTED);
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));
        heading.add(title);
        heading.add(subtitle);
        top.add(heading, BorderLayout.WEST);

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        profile.setOpaque(false);
        JLabel avatar = new JLabel("OR");
        avatar.setOpaque(true);
        avatar.setBackground(new Color(224, 233, 255));
        avatar.setForeground(BLUE);
        avatar.setFont(BOLD);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(42, 42));
        JLabel user = new JLabel("<html><b>Oviya R.</b><br><font color='#778499'>Personal wallet</font></html>");
        user.setFont(BODY);
        profile.add(avatar);
        profile.add(user);
        top.add(profile, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.add(makeBalanceCard());
        cards.add(makeStatCard("MONEY ADDED", "₹ 0.00", "↑  Funds added to wallet", GREEN, "INCOME"));
        cards.add(makeStatCard("MONEY SENT", "₹ 0.00", "↗  Outgoing transfers", RED, "OUTGOING"));
        center.add(cards, BorderLayout.NORTH);
        center.add(buildTransactionsPanel(), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel note = new JLabel("  Demo environment  •  Balances reset when the app closes");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        note.setForeground(MUTED);
        bottom.add(note, BorderLayout.WEST);
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(MUTED);
        bottom.add(statusLabel, BorderLayout.EAST);

        JPanel centerAndBottom = new JPanel(new BorderLayout(0, 12));
        centerAndBottom.setOpaque(false);
        centerAndBottom.add(center, BorderLayout.CENTER);
        centerAndBottom.add(bottom, BorderLayout.SOUTH);
        main.add(centerAndBottom, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(NAVY);
        side.setPreferredSize(new Dimension(230, 0));
        side.setBorder(new EmptyBorder(24, 16, 20, 16));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel("N");
        logo.setOpaque(true);
        logo.setBackground(BLUE);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(40, 40));
        JLabel brandName = new JLabel("<html><font color='#FFFFFF'><b>NovaPay</b></font><br><font color='#AEBBD0' size='3'>WALLET PLATFORM</font></html>");
        brandName.setFont(BODY);
        brand.add(logo);
        brand.add(brandName);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);
        side.add(Box.createVerticalStrut(42));

        JLabel menu = new JLabel("  WORKSPACE");
        menu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menu.setForeground(new Color(145, 161, 185));
        menu.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(menu);
        side.add(Box.createVerticalStrut(12));
        side.add(navItem("▦", "Dashboard", true));
        side.add(navItem("↗", "Transfer money", false));
        side.add(navItem("＋", "Add money", false));
        side.add(navItem("▤", "Transactions", false));
        side.add(Box.createVerticalGlue());

        JPanel help = new JPanel();
        help.setOpaque(true);
        help.setBackground(new Color(31, 47, 76));
        help.setLayout(new BoxLayout(help, BoxLayout.Y_AXIS));
        help.setBorder(new EmptyBorder(14, 12, 14, 12));
        JLabel helpTitle = new JLabel("Need a hand?");
        helpTitle.setForeground(Color.WHITE);
        helpTitle.setFont(BOLD);
        JLabel helpText = new JLabel("<html><font color='#B5C2D6'>This is a local demo<br>with sample data.</font></html>");
        helpText.setFont(BODY);
        help.add(helpTitle);
        help.add(Box.createVerticalStrut(7));
        help.add(helpText);
        help.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(help);
        side.add(Box.createVerticalStrut(10));
        JLabel version = new JLabel("NovaPay Desktop 1.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(145, 161, 185));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(version);
        return side;
    }

    private JLabel navItem(String icon, String text, boolean selected) {
        JLabel item = new JLabel("  " + icon + "     " + text);
        item.setFont(selected ? BOLD : BODY);
        item.setForeground(selected ? Color.WHITE : new Color(190, 201, 218));
        item.setOpaque(true);
        item.setBackground(selected ? new Color(48, 68, 105) : NAVY);
        item.setBorder(new EmptyBorder(13, 8, 13, 8));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (text.equals("Transfer money")) transferMoney();
                else if (text.equals("Add money")) addMoney();
                else if (text.equals("Transactions")) {
                    if (table != null) table.requestFocusInWindow();
                    if (searchField != null) searchField.requestFocusInWindow();
                }
            }
        });
        return item;
    }

    private JPanel makeBalanceCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(new Color(48, 91, 222));
        card.setBorder(new EmptyBorder(20, 21, 20, 21));
        JLabel label = new JLabel("TOTAL WALLET BALANCE");
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(220, 229, 255));
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        balanceLabel.setForeground(Color.WHITE);
        walletIdLabel = new JLabel("Wallet ID  •  WAL-2401");
        walletIdLabel.setFont(BODY);
        walletIdLabel.setForeground(new Color(220, 229, 255));
        JButton add = smallButton("+ Add money", Color.WHITE, BLUE);
        JButton transfer = smallButton("Transfer ↗", new Color(82, 122, 245), Color.WHITE);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(add);
        buttons.add(transfer);
        JPanel lower = new JPanel();
        lower.setOpaque(false);
        lower.setLayout(new BoxLayout(lower, BoxLayout.Y_AXIS));
        lower.add(walletIdLabel);
        lower.add(Box.createVerticalStrut(13));
        lower.add(buttons);
        card.add(label, BorderLayout.NORTH);
        card.add(balanceLabel, BorderLayout.CENTER);
        card.add(lower, BorderLayout.SOUTH);
        add.addActionListener(e -> addMoney());
        transfer.addActionListener(e -> transferMoney());
        return card;
    }

    private JPanel makeStatCard(String title, String amount, String caption, Color accent, String kind) {
        JPanel card = new JPanel(new BorderLayout(0, 13));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 234, 242)),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(MUTED);
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        amountLabel.setForeground(TEXT);
        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        captionLabel.setForeground(accent);
        if (kind.equals("INCOME")) incomeLabel = amountLabel;
        else outgoingLabel = amountLabel;
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.CENTER);
        card.add(captionLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 234, 242)),
                new EmptyBorder(18, 18, 16, 18)));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Recent transactions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT);
        JLabel sub = new JLabel("Review and search your wallet activity");
        sub.setFont(BODY);
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(4, 0, 0, 0));
        titles.add(title);
        titles.add(sub);
        heading.add(titles, BorderLayout.WEST);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tools.setOpaque(false);
        searchField = new JTextField(15);
        searchField.setFont(BODY);
        searchField.setToolTipText("Search ID, type, description or status");
        searchField.putClientProperty("JTextField.placeholderText", "Search transactions...");
        JButton export = smallButton("↓ Export CSV", BLUE, Color.WHITE);
        tools.add(searchField);
        tools.add(export);
        heading.add(tools, BorderLayout.EAST);
        panel.add(heading, BorderLayout.NORTH);

        table = new JTable(tableModel);
        table.setFont(BODY);
        table.setForeground(TEXT);
        table.setRowHeight(37);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(237, 240, 246));
        table.setSelectionBackground(new Color(232, 239, 255));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(MUTED);
        header.setBackground(new Color(248, 250, 253));
        header.setPreferredSize(new Dimension(0, 36));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(237, 240, 246)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        countLabel.setForeground(MUTED);
        footer.add(countLabel, BorderLayout.WEST);
        JButton clear = smallButton("Clear search", new Color(242, 245, 250), TEXT);
        clear.addActionListener(e -> searchField.setText(""));
        footer.add(clear, BorderLayout.EAST);
        panel.add(footer, BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshTable(); }
        });
        export.addActionListener(e -> exportCsv());
        return panel;
    }

    private JButton smallButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(9, 13, 9, 13));
        return b;
    }

    private void addMoney() {
        JTextField amountField = new JTextField();
        JTextField sourceField = new JTextField("Bank top-up");
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Amount to add (₹):"));
        form.add(amountField);
        form.add(new JLabel("Description / source:"));
        form.add(sourceField);
        int result = JOptionPane.showConfirmDialog(this, form, "Add money to wallet",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        BigDecimal amount = parseAmount(amountField.getText());
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Enter a valid amount greater than ₹0.00.");
            return;
        }
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        addTransaction("Added", safeDescription(sourceField.getText(), "Wallet top-up"), amount);
        refreshAll();
        statusLabel.setText("Money added successfully");
        JOptionPane.showMessageDialog(this, "₹" + money(amount) + " added to your demo wallet.",
                "Top-up successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void transferMoney() {
        JTextField recipientField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField noteField = new JTextField("Personal transfer");
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Recipient wallet ID or name:"));
        form.add(recipientField);
        form.add(new JLabel("Amount (₹):"));
        form.add(amountField);
        form.add(new JLabel("Note:"));
        form.add(noteField);
        int result = JOptionPane.showConfirmDialog(this, form, "Transfer money",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String recipient = recipientField.getText().trim();
        BigDecimal amount = parseAmount(amountField.getText());
        if (recipient.isEmpty()) {
            showError("Enter a recipient wallet ID or name.");
            return;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Enter a valid amount greater than ₹0.00.");
            return;
        }
        if (balance.compareTo(amount) < 0) {
            showError("Insufficient balance. Available: ₹" + money(balance));
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Transfer ₹" + money(amount) + " to " + recipient + "?",
                "Confirm transfer", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        addTransaction("Transfer", "To " + recipient + " — " +
                safeDescription(noteField.getText(), "Wallet transfer"), amount.negate());
        refreshAll();
        statusLabel.setText("Transfer completed");
        JOptionPane.showMessageDialog(this,
                "Transfer successful!\nReference: " + transactions.get(0).id +
                        "\nAmount: ₹" + money(amount),
                "Transfer complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addTransaction(String type, String description, BigDecimal amount) {
        String id = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        transactions.add(0, new Tx(id, LocalDateTime.now(), type, description, amount, "SUCCESS"));
    }

    private void refreshAll() {
        balanceLabel.setText("₹ " + money(balance));
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal outgoing = BigDecimal.ZERO;
        for (Tx tx : transactions) {
            if (tx.amount.signum() > 0) income = income.add(tx.amount);
            else outgoing = outgoing.add(tx.amount.abs());
        }
        incomeLabel.setText("₹ " + money(income));
        outgoingLabel.setText("₹ " + money(outgoing));
        refreshTable();
    }

    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        for (Tx tx : transactions) {
            String combined = (tx.id + " " + tx.date.format(DATE_FMT) + " " + tx.type + " " +
                    tx.description + " " + tx.amount + " " + tx.status).toLowerCase(Locale.ROOT);
            if (!combined.contains(query)) continue;
            String amountText = (tx.amount.signum() > 0 ? "+ ₹ " : "- ₹ ") + money(tx.amount.abs());
            tableModel.addRow(new Object[]{tx.id, tx.date.format(DATE_FMT), tx.type,
                    tx.description, amountText, tx.status});
            shown++;
        }
        if (countLabel != null) countLabel.setText("Showing " + shown + " of " + transactions.size() + " transactions");
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export transaction report");
        chooser.setSelectedFile(new File("novapay-transactions.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            out.println("Transaction ID,Date Time,Type,Description,Amount,Status");
            for (Tx tx : transactions) {
                out.println(csv(tx.id) + "," + csv(tx.date.format(DATE_FMT)) + "," +
                        csv(tx.type) + "," + csv(tx.description) + "," +
                        csv(tx.amount.toPlainString()) + "," + csv(tx.status));
            }
            statusLabel.setText("CSV exported: " + file.getName());
            JOptionPane.showMessageDialog(this, "Transaction report saved to:\n" + file.getAbsolutePath(),
                    "Export complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Could not export CSV: " + ex.getMessage());
        }
    }

    private String csv(String value) {
        String v = value == null ? "" : value;
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }

    private BigDecimal parseAmount(String text) {
        try {
            BigDecimal amount = new BigDecimal(text.trim()).setScale(2, RoundingMode.UNNECESSARY);
            return amount;
        } catch (Exception ex) {
            return null;
        }
    }

    private String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String safeDescription(String value, String fallback) {
        String v = value == null ? "" : value.trim();
        return v.isEmpty() ? fallback : v;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Unable to complete request", JOptionPane.WARNING_MESSAGE);
        statusLabel.setText("Action needs attention");
    }

    private static class Tx {
        final String id;
        final LocalDateTime date;
        final String type;
        final String description;
        final BigDecimal amount;
        final String status;
        Tx(String id, LocalDateTime date, String type, String description, BigDecimal amount, String status) {
            this.id = id;
            this.date = date;
            this.type = type;
            this.description = description;
            this.amount = amount;
            this.status = status;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new Main().setVisible(true);
        });
    }
}
'''
path = Path("/mnt/data/Main.java")
path.write_text(code, encoding="utf-8")
print(f"Created {path} ({len(code.splitlines())} lines).")
