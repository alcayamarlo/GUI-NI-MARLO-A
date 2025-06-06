/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import Successfull.noAccount;
import config.Session;
import config.dbConnect;
import config.passwordHasher;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author alcay
 */
public class createUserForm extends javax.swing.JFrame {

    /**
     * Creates new form createUserForm
     */
    public createUserForm() {
        initComponents();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }
    
      private String userId; // Declare userId at the class level

    public void setUserId(String id) {
        this.userId = id; // Store the user ID for later use
    }
 public String destination = ""; 
    File selectedFile;
    public String oldpath;
    public String path; 
    
    
      public boolean duplicateCheck() {
    dbConnect dbc = new dbConnect();
    try {
        String query = "SELECT * FROM users WHERE username = '" + un.getText() + "' OR email = '" + em.getText() + "'";
        ResultSet resultSet = dbc.getData(query);

        if (resultSet.next()) {
            String email = resultSet.getString("email");
            if (email.equals(em.getText())) {
                JOptionPane.showMessageDialog(null, "Email is Already used");
                em.setText("");
            }
            String username = resultSet.getString("username");
            if (username.equals(un.getText())) { //Error: You were comparing username with email's text field. changed to un1's text field
                JOptionPane.showMessageDialog(null, "Username is Already used"); //Error: Changed the message to reflect username duplication
                un.setText("");
            }
            return true;
        } else {
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("" + ex);
        return false;
    }
}
  
  public boolean updateCheck() {
    dbConnect dbc = new dbConnect();
    try {
        String query = "SELECT * FROM users WHERE (username = '" + un.getText() + "' OR email = '" + em.getText() + "')AND p_id!= '"+p_id.getText()+"'";
        ResultSet resultSet = dbc.getData(query);

        if (resultSet.next()) {
            String email = resultSet.getString("email");
            if (email.equals(em.getText())) {
                JOptionPane.showMessageDialog(null, "Email is Already used");
                em.setText("");
            }
            String username = resultSet.getString("username");
            if (username.equals(un.getText())) { //Error: You were comparing username with email's text field. changed to un1's text field
                JOptionPane.showMessageDialog(null, "Username is Already used"); //Error: Changed the message to reflect username duplication
                un.setText("");
            }
            return true;
        } else {
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("" + ex);
        return false;
    }
}
    public static int getHeightFromWidth(String imagePath, int desiredWidth) {
        try {
            // Read the image file
            File imageFile = new File(imagePath);
            BufferedImage image = ImageIO.read(imageFile);
            
            // Get the original width and height of the image
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            
            // Calculate the new height based on the desired width and the aspect ratio
            int newHeight = (int) ((double) desiredWidth / originalWidth * originalHeight);
            
            return newHeight;
        } catch (IOException ex) {
            System.out.println("No image found!");
        }
        
        return -1;
    }    
    
       public  ImageIcon ResizeImage(String ImagePath, byte[] pic, JLabel label) {
        ImageIcon MyImage = null;
            if(ImagePath !=null){
                MyImage = new ImageIcon(ImagePath);
            }else{
                MyImage = new ImageIcon(pic);
            }

        int newHeight = getHeightFromWidth(ImagePath, label.getWidth());

        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), newHeight, Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
     public int FileExistenceChecker(String path){
        File file = new File(path);
        String fileName = file.getName();
        
        Path filePath = Paths.get("src/usersimages", fileName);
        boolean fileExists = Files.exists(filePath);
        
        if (fileExists) {
            return 1;
        } else {
            return 0;
        }
    
    }
    
        public void imageUpdater(String existingFilePath, String newFilePath){
        File existingFile = new File(existingFilePath);
        if (existingFile.exists()) {
            String parentDirectory = existingFile.getParent();
            File newFile = new File(newFilePath);
            String newFileName = newFile.getName();
            File updatedFile = new File(parentDirectory, newFileName);
            existingFile.delete();
            try {
                Files.copy(newFile.toPath(), updatedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image updated successfully.");
            } catch (IOException e) {
                System.out.println("Error occurred while updating the image: "+e);
            }
        } else {
            try{
                Files.copy(selectedFile.toPath(), new File(destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException e){
                System.out.println("Error on update!");
            }
        }
   }
           public void logEvent(int userId, String username, String userType, String logDescription) {
    dbConnect dbc = new dbConnect();
    Connection con = dbc.getConnection();
    PreparedStatement pstmt = null;

    try {
        String sql = "INSERT INTO tbl_log (p_id, u_username, login_time, u_type, log_status) VALUES (?, ?, ?, ?, ?)";
        pstmt = con.prepareStatement(sql);

        pstmt.setInt(1, userId);
        pstmt.setString(2, username);
        pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
        pstmt.setString(4, userType); // This should be "Admin" or "User"
        pstmt.setString(5, "Active");

        pstmt.executeUpdate();
        System.out.println("Log recorded successfully.");
    } catch (SQLException e) {
        System.out.println("Error recording log: " + e.getMessage());
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing resources: " + e.getMessage());
        }
    }
}
     
  
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        utype = new javax.swing.JComboBox<>();
        fn = new textfield.TextField();
        ct = new textfield.TextField();
        em = new textfield.TextField();
        cn = new textfield.TextField();
        un = new textfield.TextField();
        pass = new textfield.PasswordField();
        status = new javax.swing.JComboBox<>();
        clear = new rojerusan.RSMaterialButtonCircle();
        add = new rojerusan.RSMaterialButtonCircle();
        update = new rojerusan.RSMaterialButtonCircle();
        delete = new rojerusan.RSMaterialButtonCircle();
        jLabel24 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        p_id = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        u_image = new javax.swing.JLabel();
        remove = new rojerusan.RSMaterialButtonCircle();
        select = new rojerusan.RSMaterialButtonCircle();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(51, 51, 255)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        utype.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        utype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User Type ", "Admin", "User" }));
        utype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utypeActionPerformed(evt);
            }
        });
        jPanel4.add(utype, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 640, 150, 40));

        fn.setBackground(new java.awt.Color(204, 255, 255));
        fn.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        fn.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        fn.setLabelText("Enter your Full Name");
        jPanel4.add(fn, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, 330, 60));

        ct.setBackground(new java.awt.Color(204, 255, 255));
        ct.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        ct.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        ct.setLabelText("Enter your City and Address");
        ct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctActionPerformed(evt);
            }
        });
        jPanel4.add(ct, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, 330, 60));

        em.setBackground(new java.awt.Color(204, 255, 255));
        em.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        em.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        em.setLabelText("Enter your Email");
        jPanel4.add(em, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 320, 330, 60));

        cn.setBackground(new java.awt.Color(204, 255, 255));
        cn.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        cn.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        cn.setLabelText("Enter your Contact No");
        jPanel4.add(cn, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, 330, 60));

        un.setBackground(new java.awt.Color(204, 255, 255));
        un.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        un.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        un.setLabelText("Enter your Username");
        un.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unActionPerformed(evt);
            }
        });
        jPanel4.add(un, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 480, 330, 60));

        pass.setBackground(new java.awt.Color(204, 255, 255));
        pass.setEnabled(false);
        pass.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        pass.setLabelText("Enter your Password");
        pass.setShowAndHide(true);
        pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passActionPerformed(evt);
            }
        });
        jPanel4.add(pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 570, 320, -1));

        status.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User Status", "Approved", "Pending" }));
        status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusActionPerformed(evt);
            }
        });
        jPanel4.add(status, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, 150, 40));

        clear.setBackground(new java.awt.Color(255, 51, 51));
        clear.setText("CLEAR");
        clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearMouseClicked(evt);
            }
        });
        jPanel4.add(clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 710, 150, 50));

        add.setBackground(new java.awt.Color(255, 51, 51));
        add.setText("ADD");
        add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addMouseClicked(evt);
            }
        });
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });
        jPanel4.add(add, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 710, 150, 50));

        update.setBackground(new java.awt.Color(255, 51, 51));
        update.setText("UPDATE");
        update.setEnabled(false);
        update.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateMouseClicked(evt);
            }
        });
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });
        jPanel4.add(update, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 710, 150, 50));

        delete.setBackground(new java.awt.Color(255, 51, 51));
        delete.setText("DELETE");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        jPanel4.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 710, 150, 50));

        jLabel24.setFont(new java.awt.Font("Arial Black", 0, 36)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setText("MANAGE USERS");
        jPanel4.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI Semilight", 0, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Update Users Information.");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, -1, 30));

        jLabel3.setBackground(new java.awt.Color(51, 51, 255));
        jLabel3.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel3.setText("  X");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 40, 40));

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(51, 51, 255)));
        jPanel4.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 40, 40));

        p_id.setFont(new java.awt.Font("Segoe UI Semilight", 1, 20)); // NOI18N
        p_id.setText("id user");
        jPanel4.add(p_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 260, 40));

        jLabel27.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/idd.png"))); // NOI18N
        jPanel4.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, 60));

        jLabel21.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/full.png"))); // NOI18N
        jPanel4.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, 60));

        jLabel25.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/emm.png"))); // NOI18N
        jPanel4.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, -1, 60));

        jLabel30.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/cnn.png"))); // NOI18N
        jPanel4.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, 60));

        jLabel23.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/user.png"))); // NOI18N
        jPanel4.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 490, -1, 60));

        jLabel26.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/images-24-removebg-preview.png"))); // NOI18N
        jPanel4.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, 60));

        jLabel22.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dashboardImage/pass.png"))); // NOI18N
        jPanel4.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, -1, 60));

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(u_image, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 260, 190));

        jPanel4.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 140, 280, 210));

        remove.setText("REMOVE");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        jPanel4.add(remove, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 430, 250, 60));

        select.setText("ADD PROFILE");
        select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectActionPerformed(evt);
            }
        });
        jPanel4.add(select, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 370, 260, 60));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearMouseClicked
        p_id.setText("");
        fn.setText("");
        ct.setText("");
        em.setText("");
        cn.setText("");
        un.setText("");
        pass.setText("");
        utype.setSelectedItem("");
        status.setSelectedItem("");

    }//GEN-LAST:event_clearMouseClicked

    private void addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseClicked
     
        
    }//GEN-LAST:event_addMouseClicked

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
 dbConnect dbc = new dbConnect();
Session sess = Session.getInstance(); // Logged-in admin

// Get form values
String fname = fn.getText().trim();
String cityAddress = ct.getText().trim();
String email = em.getText().trim();
String contactNo = cn.getText().trim();
String username = un.getText().trim();
String password = new String(pass.getPassword()).trim(); // Fix for JPasswordField
String usertype = utype.getSelectedItem() != null ? utype.getSelectedItem().toString() : "";


// Validate image file
if (selectedFile == null) {
    JOptionPane.showMessageDialog(null, "Please select an image.");
    return;
}

// Prepare image path
String imageName = fname + "_" + selectedFile.getName();
String destinationDir = "src/usersimages";
new File(destinationDir).mkdirs(); // Make sure folder exists
String destinationPath = destinationDir + "/" + imageName;

// Input validation
if (fname.isEmpty() || cityAddress.isEmpty() || email.isEmpty() ||
    contactNo.isEmpty() || username.isEmpty() || password.isEmpty() ||
   usertype.isEmpty()) {

    JOptionPane.showMessageDialog(null, "Please fill all fields.");
    return;

} else if (password.length() < 8) {
    JOptionPane.showMessageDialog(null, "Password must be at least 8 characters.");
    return;

} else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
    JOptionPane.showMessageDialog(null, "Enter a valid email address.");
    return;


}

try {
    // Save image
    Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

    // Hash password & answer
    String hashedPassword = passwordHasher.hashPassword(password);

    // Insert user
    String insertQuery = "INSERT INTO users(fn, cityAddress, email, contactNo, username, password, usertype, status, securityQ, answer, image) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = dbc.getConnection();
         PreparedStatement pst = conn.prepareStatement(insertQuery)) {

        pst.setString(1, fname);
        pst.setString(2, cityAddress);
        pst.setString(3, email);
        pst.setString(4, contactNo);
        pst.setString(5, username);
        pst.setString(6, hashedPassword);
        pst.setString(7, usertype);
        pst.setString(8, "Pending");
        pst.setString(9, imageName);

        int rowsInserted = pst.executeUpdate();

        if (rowsInserted > 0) {
            // Log the registration
            String logQuery = "INSERT INTO tbl_log (p_id,username, u_type, log_status, log_description) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement logPst = conn.prepareStatement(logQuery)) {
                logPst.setInt(1, sess.getPid());
                logPst.setString(2, sess.getUsername());
                logPst.setString(3, sess.getUsertype());
                logPst.setString(4, "Active");
                logPst.setString(5, "Admin added new user: " + username);
                logPst.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Registered Successfully!");
            new ManageUsers().setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(null, "Registration failed!");
        }
    }

} catch (Exception ex) {
    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

  
    }//GEN-LAST:event_addActionPerformed

    private void updateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateMouseClicked
       
    }//GEN-LAST:event_updateMouseClicked

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActionPerformed
   if (fn.getText().isEmpty() || ct.getText().isEmpty() || em.getText().isEmpty() || cn.getText().isEmpty() || un.getText().isEmpty() || pass.getText().isEmpty() || utype.getSelectedItem().toString().isEmpty()) {
  JOptionPane.showMessageDialog(null,"All Fields are Required!");
  
   }else if (pass.getText().length()<8){
       JOptionPane.showMessageDialog(null,"Password character should be 8 above!");
       pass.setText("");
       
   }else{
        dbConnect dbc = new dbConnect();
        dbc.updateData("UPDATE users SET fn = '"+fn.getText()+"', cityAddress = '"+ct.getText()
                +"', email = '"+em.getText()+"', contactNo = '"+cn.getText()+"', username = '"+un.getText()
                +"', password = '"+pass.getText()+"', usertype = '"+utype.getSelectedItem()
                +"',status = '"+status.getSelectedItem()+"' WHERE p_id = '"+p_id.getText()+"'");
              new ManageUsers().setVisible(true);
                    this.setVisible(false);
                    this.dispose();
   }
    }//GEN-LAST:event_updateActionPerformed

    private void statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        Session sess = Session.getInstance();
       int pid = sess.getPid();
       if(sess.getPid() == 0){
       new noAccount().setVisible(true);
       this.setVisible(false);
       this.dispose();
       }
       p_id.setText(""+sess.getPid());
    }//GEN-LAST:event_formWindowActivated

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
       ManageUsers user = new ManageUsers();
       user.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void ctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ctActionPerformed

    private void utypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_utypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_utypeActionPerformed

    private void passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passActionPerformed

    private void unActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        String userIdText = p_id.getText().trim(); // Get text and remove leading/trailing whitespace

        if (userIdText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter the User ID to delete.");
            return; // Stop the deletion process if the field is empty
        }

        try {
            int userId = Integer.parseInt(userIdText);
            dbConnect connect = new dbConnect();
            try (Connection con = connect.getConnection()) {
                String query = "DELETE FROM users WHERE p_id = ?";
                try (PreparedStatement pst = con.prepareStatement(query)) {
                    pst.setInt(1, userId);
                    int rowsDeleted = pst.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(null, "User deleted successfully.");

                        // Clear all the fields
                        fn.setText("");
                        ct.setText("");
                        em.setText("");
                        cn.setText("");
                        un.setText("");
                        p_id.setText("");
                        utype.setSelectedIndex(0);

                    } else {
                        JOptionPane.showMessageDialog(null, "User not found with the specified ID.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting user: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid User ID format. Please enter a valid number.");
            ex.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure resources are closed (connection is handled by try-with-resources)
            // Open the ManageUsers window and close the current one
            java.awt.EventQueue.invokeLater(() -> {
                ManageUsers user = new ManageUsers();
                user.setVisible(true);
                this.dispose();
            });
        }
    }

    }//GEN-LAST:event_deleteActionPerformed

    private void selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectActionPerformed
    JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                selectedFile = fileChooser.getSelectedFile();
                destination = "src/usersimages/" + selectedFile.getName();
                path  = selectedFile.getAbsolutePath();

                if(FileExistenceChecker(path) == 1){
                    JOptionPane.showMessageDialog(null, "File Already Exist, Rename or Choose another!");
                    destination = "";
                    path= "";
                }else{
                    u_image.setIcon(ResizeImage(path, null, u_image));
                    select.setEnabled(false);
                    remove.setEnabled(true);
                }
            } catch (Exception ex) {
                System.out.println("File Error!");
            }
        }       
    }//GEN-LAST:event_selectActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
       remove.setEnabled(false);
        select.setEnabled(true);
        u_image.setIcon(null);
        destination = "";
        path = "";
    }//GEN-LAST:event_removeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(createUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(createUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(createUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(createUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new createUserForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public rojerusan.RSMaterialButtonCircle add;
    public rojerusan.RSMaterialButtonCircle clear;
    public textfield.TextField cn;
    public textfield.TextField ct;
    public rojerusan.RSMaterialButtonCircle delete;
    public textfield.TextField em;
    public textfield.TextField fn;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JLabel p_id;
    public textfield.PasswordField pass;
    private rojerusan.RSMaterialButtonCircle remove;
    private rojerusan.RSMaterialButtonCircle select;
    public javax.swing.JComboBox<String> status;
    private javax.swing.JLabel u_image;
    public textfield.TextField un;
    public rojerusan.RSMaterialButtonCircle update;
    public javax.swing.JComboBox<String> utype;
    // End of variables declaration//GEN-END:variables
}
