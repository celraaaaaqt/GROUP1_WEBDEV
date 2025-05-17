import java.awt.Color;
import java.awt.Cursor;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class LoginForm extends javax.swing.JFrame {
    
    public LoginForm() {
        initComponents();
        Connect();
    }
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/information_system","root","");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnUsername = new javax.swing.JTextField();
        btnLog = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        checkbox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        btnPassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(java.awt.Color.white);
        jLabel1.setText("Username:");
        jLabel1.setToolTipText("");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(java.awt.Color.white);
        jLabel2.setText("Password:");
        jLabel2.setToolTipText("");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, 80, -1));

        btnUsername.setText("Enter Username");
        btnUsername.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnUsernameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnUsernameFocusLost(evt);
            }
        });
        getContentPane().add(btnUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 200, 30));

        btnLog.setBackground(new java.awt.Color(0, 204, 0));
        btnLog.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnLog.setForeground(java.awt.Color.white);
        btnLog.setText("Log In");
        btnLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogActionPerformed(evt);
            }
        });
        getContentPane().add(btnLog, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 360, 90, 34));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(java.awt.Color.white);
        jLabel3.setText("HI, WELCOME!");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, 40));

        btnExit.setBackground(new java.awt.Color(255, 0, 0));
        btnExit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnExit.setForeground(java.awt.Color.white);
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 360, 90, 34));

        checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxActionPerformed(evt);
            }
        });
        getContentPane().add(checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 260, -1, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(java.awt.Color.white);
        jLabel4.setText("Forgot Password? Click here!");
        jLabel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel4MouseMoved(evt);
            }
        });
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 300, -1, -1));

        btnPassword.setText("Enter Password");
        btnPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnPasswordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnPasswordFocusLost(evt);
            }
        });
        getContentPane().add(btnPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 260, 200, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/07022f87-e7ee-4fb1-a06c-f314bdaf257e (1).jpg"))); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 696, 424));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogActionPerformed
String user = btnUsername.getText();
String pass = btnPassword.getText();
boolean loginSuccess = false;

try {
    // query for a specific user
    pst = con.prepareStatement("SELECT * FROM accounts WHERE Username = ?");
    pst.setString(1, user);
    rs = pst.executeQuery();

    if (rs.next()) { // if a record is found
        String dbPassword = rs.getString("Password");
        String dbPermission = rs.getString("permission"); // get permission type from database

        if (pass.equals(dbPassword)) { // check password
            if ("admin".equalsIgnoreCase(dbPermission)) {
                JOptionPane.showMessageDialog(this, "Admin Login Successfully!");
                new AdminAccount().setVisible(true);
                dispose();
                loginSuccess = true;
            } else if ("staff".equalsIgnoreCase(dbPermission)) {
                JOptionPane.showMessageDialog(this, "Staff Login Successfully!");
                new StaffDashboard().setVisible(true);
                dispose();
                loginSuccess = true;
            } else {
                JOptionPane.showMessageDialog(this, "Unknown permission type: " + dbPermission);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect Username or Password.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Incorrect Username or Password.");
    }

} catch (SQLException ex) {
    Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
}
    }//GEN-LAST:event_btnLogActionPerformed

    private void checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxActionPerformed
        // TODO add your handling code here:
        if (checkbox.isSelected()){
            btnPassword.setEchoChar((char)0);
    }
        else {
            btnPassword.setEchoChar('*');
        }
    }//GEN-LAST:event_checkboxActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnUsernameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnUsernameFocusGained
        // TODO add your handling code here:
        if(btnUsername.getText().equals("Enter Username")){
            btnUsername.setText("");
            btnUsername.setForeground(new Color(0, 0 ,0));
        }
    }//GEN-LAST:event_btnUsernameFocusGained

    private void btnUsernameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnUsernameFocusLost
        // TODO add your handling code here:
         if(btnUsername.getText().equals("")){
            btnUsername.setText("Enter Username");
            btnUsername.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_btnUsernameFocusLost

    private void btnPasswordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPasswordFocusGained
        // TODO add your handling code here:
        if(btnPassword.getText().equals("Enter Password")){
            btnPassword.setText("");
            btnPassword.setForeground(new Color(0, 0 ,0));
        }
    }//GEN-LAST:event_btnPasswordFocusGained

    private void btnPasswordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPasswordFocusLost
        // TODO add your handling code here:
          if(btnPassword.getText().equals("")){
            btnPassword.setText("Enter Password");
            btnPassword.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_btnPasswordFocusLost

    private void jLabel4MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseMoved
        // TODO add your handling code here:
        jLabel4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLabel4MouseMoved

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
        // TODO add your handling code here:
        SendCode sc = new SendCode();
        this.setVisible(false);
        sc.setVisible(true);
    }//GEN-LAST:event_jLabel4MousePressed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnLog;
    private javax.swing.JPasswordField btnPassword;
    private javax.swing.JTextField btnUsername;
    private javax.swing.JCheckBox checkbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    // End of variables declaration//GEN-END:variables
}