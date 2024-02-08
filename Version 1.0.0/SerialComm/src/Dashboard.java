import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Dashboard extends JFrame implements ActionListener, ItemListener {

    // Components of the Form
    private Container mainContainer;
    private JLabel title;
    private JLabel selectFileLabel;
    private JTextField filePathTextField;

    private JLabel selectBaudRateLabel;

    JLabel selectComPortLabel,imageLabel;

    JComboBox baudRateComboBox, comPortComboBox;

    JLabel dataTransferSuccessLabel;
    JLabel dataTransferCountLabel;



    private JButton startComm,closePort,openFile,refresh;

    JLabel openFileLabel;

    JProgressBar progressBar;

    String selectedFileName = "",selectedFileNameOnly;


    // constructor, to initialize the components
    // with default values.
    public Dashboard() throws IOException {

        Image icon = Toolkit.getDefaultToolkit().getImage("src/resources/klug_logo.png" );
        setIconImage(icon);
        setTitle("Serial Communication");
        setBounds(300, 90, 900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);


        mainContainer = getContentPane();
        mainContainer.setBackground(Color.white);
        mainContainer.setLayout(null);

        title = new JLabel("File Transfer Via Serial Port");
        title.setFont(new Font("Arial", Font.PLAIN, 25));
        title.setSize(400, 30);
        title.setLocation(50, 30);
        mainContainer.add(title);


       /* BufferedImage img = ImageIO.read(new File("src/resources/klug_logo_smll1.png"));
        ImageIcon imgIcon = new ImageIcon(img);
        // loginContainer.add(icon);


        imageLabel = new JLabel("",imgIcon,JLabel.CENTER);
        imageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        imageLabel.setSize(200, 150);
        imageLabel.setLocation(650, 5);
        mainContainer.add(imageLabel);
*/
     /*   refresh = new JButton("Refresh");
        refresh.setFont(new Font("Arial", Font.BOLD, 12));
        refresh.setSize(80, 25);
        refresh.setLocation(750, 30);
        refresh.addActionListener(this);
        refresh.setVisible(true);
        mainContainer.add(refresh);*/

        selectComPortLabel = new JLabel("Select Com Port");
        selectComPortLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectComPortLabel.setSize(200, 30);
        selectComPortLabel.setLocation(50, 150);
        mainContainer.add(selectComPortLabel);

        comPortComboBox = new JComboBox(SerialCommunication.comPorts);
        comPortComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        comPortComboBox.setSize(200, 30);
        comPortComboBox.setLocation(400, 150);
        comPortComboBox.addItemListener(this);
        mainContainer.add(comPortComboBox);


        selectFileLabel = new JLabel("Select File");
        selectFileLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectFileLabel.setSize(250, 30);
        selectFileLabel.setLocation(50, 210);
        mainContainer.add(selectFileLabel);


        filePathTextField = new JTextField();
        filePathTextField.setFont(new Font("Arial", Font.BOLD, 16));
        filePathTextField.setSize(300, 30);
        filePathTextField.setLocation(400, 210);
        mainContainer.add(filePathTextField);

        openFile = new JButton("Open");
        openFile.setFont(new Font("Arial", Font.PLAIN, 15));
        openFile.setSize(50, 27);
        openFile.setLocation(710, 210);
        openFile.addActionListener(this);
        mainContainer.add(openFile);


        selectBaudRateLabel = new JLabel("Select Baud Rates");
        selectBaudRateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectBaudRateLabel.setSize(200, 30);
        selectBaudRateLabel.setLocation(50, 270);
        mainContainer.add(selectBaudRateLabel);

        baudRateComboBox = new JComboBox(Constants.baudRates);
        baudRateComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        baudRateComboBox.setSize(200, 30);
        baudRateComboBox.setLocation(400, 270);
        baudRateComboBox.setSelectedIndex(Constants.baudRateDefaultIndex);
        baudRateComboBox.addItemListener(this);
        mainContainer.add(baudRateComboBox);


        closePort = new JButton("Close Port");
        closePort.setFont(new Font("Arial", Font.PLAIN, 15));
        closePort.setSize(150, 30);
        closePort.setLocation(150, 350);
        closePort.addActionListener(this);
        mainContainer.add(closePort);

        startComm = new JButton("Start");
        startComm.setFont(new Font("Arial", Font.BOLD, 15));
        startComm.setSize(150, 30);
        startComm.setLocation(350, 350);
        startComm.addActionListener(this);
        startComm.setVisible(true);
        mainContainer.add(startComm);

        dataTransferCountLabel = new JLabel("");
        dataTransferCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dataTransferCountLabel.setForeground(Color.BLACK);
        dataTransferCountLabel.setSize(300, 30);
        dataTransferCountLabel.setLocation(480, 420);
        dataTransferCountLabel.setVisible(false);
        mainContainer.add(dataTransferCountLabel);


        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(250, 450, 500, 30);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        mainContainer.add(progressBar);


        dataTransferSuccessLabel = new JLabel("Data Transfer Successfully....!");
        dataTransferSuccessLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dataTransferSuccessLabel.setForeground(Color.green);
        dataTransferSuccessLabel.setSize(300, 30);
        dataTransferSuccessLabel.setLocation(400, 500);
        dataTransferSuccessLabel.setVisible(false);
        mainContainer.add(dataTransferSuccessLabel);


        setVisible(true);
    }

    // method actionPerformed()
    // to get the action performed
    // by the user and act accordingly
    public void actionPerformed(ActionEvent e) {


        JFileChooser fileChooser;

        if(e.getSource() == refresh){

            SerialCommunication.initializeComPorts();
        }
        else if (e.getSource() == openFile) {

  //          fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            fileChooser = new JFileChooser();
            String home=System.getProperty("user.home");
            fileChooser.setCurrentDirectory(new File(home+"/Downloads"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

            // create an object of JFileChooser class


            // invoke the showsOpenDialog function to show the save dialog
            int r = fileChooser.showOpenDialog(null);

            // if the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                selectedFileName = fileChooser.getSelectedFile().getAbsolutePath();
                selectedFileNameOnly= fileChooser.getSelectedFile().getName();
                // set the label to the path of the selected file
                filePathTextField.setText(selectedFileNameOnly);
                startComm.setVisible(true);
                System.out.println(selectedFileName);

            }
            // if the user cancelled the operation
            else
                openFileLabel.setText("the user cancelled the operation");

        } else if (e.getSource() == closePort) {

            if (SerialCommunication.mySerialPort != null) {
                SerialCommunication.mySerialPort.closePort();
            }

        } else if (e.getSource() == startComm) {


            try {

                /*if (SerialCommunication.selectedCOMPort == 0) {
                    showErrorDialog(mainContainer, "COM Port Not Selected");
                } else if (filePathTextField.getText().equals("")) {
                    showErrorDialog(mainContainer, "Please Select File");
                } else if (baudRateComboBox.getSelectedItem().toString().equals(Constants.selectBaudRate)) {
                    showErrorDialog(mainContainer, "Baud Rate Not Selected");
                } else {

                    SerialCommunication.noOfPagesTransfer=0;
                    progressBar.setValue(0);
                    dataTransferSuccessLabel.setText("");
                    progressBar.setVisible(true);
                    dataTransferCountLabel.setVisible(true);

                    SerialCommunication.readFile(selectedFileName);
                    SerialCommunication.setUpdatedParameter();
                }*/

                SerialCommunication.noOfPagesTransfer=0;
                progressBar.setValue(0);
                dataTransferSuccessLabel.setText("");
                progressBar.setVisible(true);
                dataTransferCountLabel.setVisible(true);

                SerialCommunication.readFile(selectedFileName);
                SerialCommunication.setUpdatedParameter();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent event) {

        if (event.getSource() == comPortComboBox) {

            System.out.println(comPortComboBox.getSelectedItem());

            SerialCommunication.selectedCOMPort = comPortComboBox.getSelectedIndex();

        } else if (event.getSource() == baudRateComboBox) {

            if (baudRateComboBox.getSelectedItem().toString().equals(Constants.selectBaudRate)) {
                return;
            } else {
                SerialCommunication.BaudRate = Integer.parseInt(baudRateComboBox.getSelectedItem().toString());
                System.out.println(baudRateComboBox.getSelectedItem());
            }
        }

    }

    private static void showErrorDialog(final Container frame, final String error) {

        JOptionPane.showMessageDialog(frame, error,
                "Serial Communication", JOptionPane.ERROR_MESSAGE);
    }
}