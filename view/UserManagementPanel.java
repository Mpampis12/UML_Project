package view;

import model.User;
import services.BankSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private List<User> sourceList;
    private JTextField searchField;

    public UserManagementPanel(List<User> usersToManage) {
        this.sourceList = usersToManage;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleHelpers.MUSTARD_BG);

        // --- Search Bar ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        searchField = new StyleHelpers.RoundedTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchBtn = StyleHelpers.createRoundedButton("Search (AFM)");
        
        searchBtn.addActionListener(e -> filterList(searchField.getText()));
        
        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- Table ---
        String[] columns = {"Username", "First Name", "Last Name", "AFM", "Email"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        refreshTable(sourceList);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Edit Button ---
        JButton editBtn = StyleHelpers.createRoundedButton("Edit Selected User");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String username = (String) model.getValueAt(row, 0);
                User selected = findUser(username);
                if (selected != null)
                    try {
                        showEditDialog(selected);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(editBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshTable(List<User> list) {
        model.setRowCount(0);
        for (User u : list) {
            model.addRow(new Object[]{u.getUsername(), u.getFirstName(), u.getLastName(), u.getAfm(), u.getEmail()});
        }
    }

    private void filterList(String query) {
        if (query.isEmpty()) {
            refreshTable(sourceList);
            return;
        }
        List<User> filtered = sourceList.stream()
            .filter(u -> u.getAfm().contains(query) || 
                         u.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                         u.getFirstName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
        refreshTable(filtered);
    }

    private User findUser(String username) {
        return sourceList.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    private void showEditDialog(User user) throws Exception {
        JTextField fName = new JTextField(user.getFirstName());
        JTextField lName = new JTextField(user.getLastName());
        JTextField email = new JTextField(user.getEmail());
        JTextField phone = new JTextField(user.getPhone());
        JTextField afm = new JTextField(user.getAfm());
        JTextField username = new JTextField(user.getUsername());
        JTextField password = new JTextField("-"); // Placeholder

        Object[] message = {
            "First Name:", fName,
            "Last Name:", lName,
            "Email:", email,
            "Phone:", phone,
            "AFM: " , afm,
            "Username: " , username,
            "Password: " , password

        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit User: " + user.getUsername(), JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            BankSystem.getInstance().getUserManager().updateUser(user, fName.getText(), lName.getText(), email.getText(), phone.getText(),username.getText(),password.getText(),afm.getText());
            
            refreshTable(sourceList); // Update UI
        }
    }
}