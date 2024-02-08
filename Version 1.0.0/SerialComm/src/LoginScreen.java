import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginScreen extends JFrame implements ActionListener {

    private Container loginContainer;
    private JLabel title;

    private JLabel userNameLabel, passwordLabel,imageLabel;
    private JTextField userNameField;

    private JPasswordField passwordField;


    private ImageIcon imageIcon;

    private JButton btnSubmit;

    LoginScreen() throws IOException {


      /*  Image icon = Toolkit.getDefaultToolkit().getImage("src/resources/klug_logo.png" );
        setIconImage(icon);
        setTitle("RS232 Downloader");
        setBounds(300, 90, 900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        loginContainer = getContentPane();
        loginContainer.setBackground(Color.white);
        loginContainer.setLayout(null);*/

        title = new JLabel("File Transfer Via Serial Port");
        title.setFont(new Font("Arial", Font.PLAIN, 25));
        title.setSize(500, 30);
        title.setLocation(300, 30);
        loginContainer.add(title);


    /*    loginContainer.add(new DisplayImage("src/resources/logo1.png"));

        BufferedImage img = ImageIO.read(new File("src/resources/klug_logo.png"));
        ImageIcon imgIcon = new ImageIcon(img);
        // loginContainer.add(icon);


        imageLabel = new JLabel("",imgIcon,JLabel.CENTER);
        imageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        imageLabel.setSize(400, 400);
        imageLabel.setLocation(10, 90);
        loginContainer.add(imageLabel);*/



        userNameLabel = new JLabel("Klug Avalon Mechatronics Pvt. Ltd.");
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userNameLabel.setSize(400, 30);
        userNameLabel.setLocation(50, 450);
        loginContainer.add(userNameLabel);


        userNameLabel = new JLabel("Username");
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userNameLabel.setSize(100, 30);
        userNameLabel.setLocation(400, 160);
        loginContainer.add(userNameLabel);


        userNameField = new JTextField();
        userNameField.setText("admin");
        userNameField.setFont(new Font("Arial", Font.BOLD, 16));
        userNameField.setSize(200, 30);
        userNameField.setLocation(600, 160);
        loginContainer.add(userNameField);


        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passwordLabel.setSize(100, 30);
        passwordLabel.setLocation(400, 250);
        loginContainer.add(passwordLabel);


        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.BOLD, 16));
        passwordField.setSize(200, 30);
        passwordField.setLocation(600, 250);
        loginContainer.add(passwordField);


        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 15));
        btnSubmit.setSize(150, 30);
        btnSubmit.setLocation(500, 350);
        btnSubmit.addActionListener(this);
        btnSubmit.setVisible(true);
        loginContainer.add(btnSubmit);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {


        if (e.getSource() == btnSubmit) {

            if (passwordField.getText().equals(Constants.password)) {
                setVisible(false);
                try {
                    new Dashboard();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(loginContainer, "Password Mismatched",
                        "Password Validation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
