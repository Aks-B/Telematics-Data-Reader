// Program to Receive tha data send from a  Arduino UNO over Serial Port
// 
// Compiled using jSerialComm-2.9.0.jar
//
// Tutorial -> https://www.xanthium.in/cross-platform-serial-port-programming-tutorial-java-jdk-arduino-embedded-system-tutorial


// (c) 2022 www.xanthium.in


import com.fazecast.jSerialComm.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerialCommunication {

    public static int noOfPages = 0;

    public static double percentage = 0;
    public static List<String> noOfPagesList = new ArrayList<>();

    public static int noOfPagesTransfer = 0;
    static Dashboard dashboard;
    static LoginScreen loginScreen;

    static SerialPort mySerialPort;
    public static List<String> pageDataList = new ArrayList<>();

    static String[] comPorts;


    static int BaudRate = 38400;


    static int selectedCOMPort = 0;
    static int DataBits = 8;
    static int StopBits = SerialPort.ONE_STOP_BIT;
    static int Parity = SerialPort.NO_PARITY;
    static SerialPort[] availablePorts = new SerialPort[5];


    static void setUpdatedParameter() {
        mySerialPort.setComPortParameters(BaudRate, DataBits, StopBits, Parity);//Sets all serial port parameters at one time
        System.out.println(" Selected Baud rate          = " + mySerialPort.getBaudRate());
    }


    public static void main(String[] Args) throws IOException {

        //  loginScreen=new LoginScreen();

        try {
            availablePorts = SerialPort.getCommPorts();
            comPorts = new String[availablePorts.length + 1];
            if (comPorts.length >= 2) {
                for (int i = 0; i < availablePorts.length + 1; i++) {
                    if (i == 0) {
                        comPorts[i] = Constants.selectComPort;
                    } else {
                        String name = availablePorts[i - 1].getSystemPortName();
                        comPorts[i] = name;
                    }
                }
            } else {
                //Initialize Dummy COM Ports
                comPorts[0] = Constants.selectComPort;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (availablePorts.length != 0) {

            System.out.println("\n\n SerialPort Data Reception");

            // use the for loop to print the available serial ports
            System.out.print("\n\n Available Ports ");
            for (int i = 0; i < availablePorts.length; i++) {
                System.out.println(i + " - " + availablePorts[i].getSystemPortName() + " -> " + availablePorts[i].getDescriptivePortName());
            }

            //Open the first Available port
            if (selectedCOMPort == 0) {
                mySerialPort = availablePorts[selectedCOMPort];
            } else {
                mySerialPort = availablePorts[selectedCOMPort - 1];

            }
            dashboard = new Dashboard();

            // Set Serial port Parameters
            mySerialPort.setComPortParameters(BaudRate, DataBits, StopBits, Parity);//Sets all serial port parameters at one time

            mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, Constants.bufferReadTime, 0); //Set Read Time outs
            //.setComPortTimeouts(TIMEOUT_Mode, READ_TIMEOUT_milliSec, WRITE_TIMEOUT_milliSec);

            mySerialPort.openPort(); //open the port

            if (mySerialPort.isOpen())
                System.out.println("\n" + mySerialPort.getSystemPortName() + " is Open ");
            else
                System.out.println(" Port not open ");


            //Display the Serial Port parameters
            System.out.println("\n Selected Port               = " + mySerialPort.getSystemPortName());
            System.out.println(" Selected Baud rate          = " + mySerialPort.getBaudRate());
            System.out.println(" Selected Number of DataBits = " + mySerialPort.getNumDataBits());
            System.out.println(" Selected Number of StopBits = " + mySerialPort.getNumStopBits());
            System.out.println(" Selected Parity             = " + mySerialPort.getParity());
            System.out.println(" Selected Read Time Out      = " + mySerialPort.getReadTimeout() + "mS");

            mySerialPort.flushIOBuffers();


            Thread readerThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] readBuffer = new byte[19];
                        int numRead = mySerialPort.readBytes(readBuffer, readBuffer.length);
                        //  System.out.print("Read " + numRead + " bytes -");
                        //System.out.println(Arrays.toString(readBuffer));

                        byte response = readBuffer[0];
                         System.out.println(Arrays.toString(readBuffer));

                        // System.out.println("Response Is " + response);


                        if (response == Constants.communicationModeStart) {


                            try {

                                String data = Constants.startPage + noOfPagesList.get(0) + noOfPagesList.get(1) + Constants.endPage;

                                List<String> noOfPagesList = new ArrayList<>();
                                for (int i = 0; i < data.length(); i += 2) {
                                    noOfPagesList.add(data.substring(i, Math.min(data.length(), i + 2)));
                                }

                                System.out.println(noOfPagesList);


                                int[] pageDataArray = new int[4];
                                for (int k = 0; k < noOfPagesList.size(); k++) {
                                    pageDataArray[k] = Integer.parseInt(noOfPagesList.get(k), 16);
                                }

                                byte[] result = Util.toByte(pageDataArray);
                                mySerialPort.writeBytes(result, 4);
                                System.out.println(Arrays.toString(result));

                                //   System.out.println(noOfPagesByteArray);
                                System.out.println("No Of Pages Transmitted -> " + noOfPages);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (response == Constants.pageAcknowledgement) {

                            // String pageData=pageDataList.get(noOfPagesTransfer);


                            if (noOfPagesTransfer == pageDataList.size()) {
                                int bytesTxed = 0;
                                System.out.println("Page Data End");
                                byte[] endAcknowledgement = new byte[1];
                                endAcknowledgement[0] = 0x03;
                                bytesTxed = mySerialPort.writeBytes(endAcknowledgement, 1);
                                dashboard.dataTransferSuccessLabel.setVisible(true);
                                dashboard.dataTransferSuccessLabel.setText("Data Transferred Successfully");
                                System.out.println(bytesTxed);

                            } else {

                                final String data = pageDataList.get(noOfPagesTransfer);

                                List<String> pageData = new ArrayList<>();
                                for (int i = 0; i < data.length(); i += 2) {
                                    pageData.add(data.substring(i, Math.min(data.length(), i + 2)));
                                }

                                System.out.println(pageData);


                                int[] pageDataArray = new int[258];
                                for (int k = 0; k < pageData.size(); k++) {
                                    pageDataArray[k] = Integer.parseInt(pageData.get(k), 16);
                                }

                                byte[] result = Util.toByte(pageDataArray);
                                mySerialPort.writeBytes(result, 258);

                                noOfPagesTransfer++;
                                percentage = (double) noOfPagesTransfer / noOfPages * 100;

                                int value = (int) percentage;

                                System.out.println("No Of Pages Transfer" + noOfPagesTransfer + "\t Percentage : " + percentage);
                                dashboard.progressBar.setValue(value);
                                dashboard.dataTransferCountLabel.setText(noOfPagesTransfer + " / " + noOfPages);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (mySerialPort.isOpen())
                System.out.println(mySerialPort.getSystemPortName() + " is Open ");
            else
                System.out.println("\n Port not open ");
        } else {
            // loginScreen=new LoginScreen();
            dashboard = new Dashboard();
        }
    }//end of main()

    public static void readFile(String pathName) throws IOException {

        noOfPages = 0;
        // Clear the read file

        pageDataList.clear();

        File file = new File(pathName);

        // Creating an object of BufferedReader class
        BufferedReader br
                = new BufferedReader(new FileReader(file));


        // Declaring a string variable
        String st;
        // Condition holds true till
        // there is character in a string
        while ((st = br.readLine()) != null) {
            noOfPages++;
            // Print the string

            System.out.println("File Data " + st);
            System.out.println("Trim Data " + st.trim());

            pageDataList.add(st.trim());
        }

        System.out.println("No Of Pages : " + noOfPages);


        final String data = pageDataList.get(0).trim();


        System.out.println(data);

        List<String> pageData = new ArrayList<>();
        for (int i = 0; i < data.length(); i += 2) {
            pageData.add(data.substring(i, Math.min(data.length(), i + 2)));
        }

        System.out.println(pageData);


        int[] pageDataArray = new int[258];

        for (int k = 0; k < pageData.size(); k++) {
            pageDataArray[k] = Integer.parseInt(pageData.get(k), 16);
        }


        for (int i = 0; i < data.length(); i += 2) {
            noOfPagesList.add(data.substring(i, Math.min(data.length(), i + 2)));
        }

        System.out.println("Total Lines " + noOfPages);

        int intVal = noOfPages;
        if (intVal >= 0 && intVal <= 0xffff) {
            final String hexVal = String.format("%04x", intVal);
            System.out.println("1");
            System.out.println(hexVal);
            noOfPagesList = new ArrayList<>();
            for (int i = 0; i < hexVal.length(); i += 2) {
                noOfPagesList.add(hexVal.substring(i, Math.min(hexVal.length(), i + 2)));
            }
            System.out.println(noOfPagesList);
        } else {
            final String hexVal = String.format("%08x", intVal);
            System.out.println("2");
            noOfPagesList = new ArrayList<>();
            for (int i = 0; i < hexVal.length(); i += 2) {
                noOfPagesList.add(hexVal.substring(i, Math.min(hexVal.length(), i + 2)));
            }
            System.out.println(noOfPagesList);
        }
        dashboard.dataTransferCountLabel.setText(noOfPagesTransfer + " / " + noOfPages);
    }

    static void initializeComPorts() {
        try {
            availablePorts = SerialPort.getCommPorts();
            comPorts = new String[availablePorts.length + 1];
            if (comPorts.length > 1) {
                for (int i = 0; i < availablePorts.length + 1; i++) {
                    if (i == 0) {
                        comPorts[i] = Constants.selectComPort;
                    } else {
                        String name = availablePorts[i - 1].getSystemPortName();
                        comPorts[i] = name;
                    }
                }
                initializeConnection();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void initializeConnection() {


        System.out.println("\n\n SerialPort Data Reception");

        // use the for loop to print the available serial ports
        System.out.print("\n\n Available Ports ");
        for (int i = 0; i < availablePorts.length; i++) {
            System.out.println(i + " - " + availablePorts[i].getSystemPortName() + " -> " + availablePorts[i].getDescriptivePortName());
        }

        //Open the first Available port
        if (selectedCOMPort == 0) {
            mySerialPort = availablePorts[selectedCOMPort];
        } else {
            mySerialPort = availablePorts[selectedCOMPort - 1];

        }


        // Set Serial port Parameters
        mySerialPort.setComPortParameters(BaudRate, DataBits, StopBits, Parity);//Sets all serial port parameters at one time

        mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, Constants.bufferReadTime, 0); //Set Read Time outs
        //.setComPortTimeouts(TIMEOUT_Mode, READ_TIMEOUT_milliSec, WRITE_TIMEOUT_milliSec);

        //   mySerialPort.openPort(); //open the port

        if (mySerialPort.isOpen())
            System.out.println("\n" + mySerialPort.getSystemPortName() + " is Open ");
        else
            System.out.println(" Port not open ");


        //Display the Serial Port parameters
        System.out.println("\n Selected Port               = " + mySerialPort.getSystemPortName());
        System.out.println(" Selected Baud rate          = " + mySerialPort.getBaudRate());
        System.out.println(" Selected Number of DataBits = " + mySerialPort.getNumDataBits());
        System.out.println(" Selected Number of StopBits = " + mySerialPort.getNumStopBits());
        System.out.println(" Selected Parity             = " + mySerialPort.getParity());
        System.out.println(" Selected Read Time Out      = " + mySerialPort.getReadTimeout() + "mS");

        mySerialPort.flushIOBuffers();


        try {
            while (true) {
                byte[] readBuffer = new byte[5];
                int numRead = mySerialPort.readBytes(readBuffer, readBuffer.length);
                System.out.print("Read " + numRead + " bytes -");
                //System.out.println(Arrays.toString(readBuffer));

                byte response = readBuffer[0];


                System.out.println("Response Is " + response);


                if (response == Constants.communicationModeStart) {


                    try {
                        //Start 24,00,3c,23

                        String data = Constants.startPage + noOfPagesList.get(0) + noOfPagesList.get(1) + Constants.endPage;

                        List<String> noOfPagesList = new ArrayList<>();
                        for (int i = 0; i < data.length(); i += 2) {
                            noOfPagesList.add(data.substring(i, Math.min(data.length(), i + 2)));
                        }

                        System.out.println(noOfPagesList);


                        int[] pageDataArray = new int[4];
                        for (int k = 0; k < noOfPagesList.size(); k++) {
                            pageDataArray[k] = Integer.parseInt(noOfPagesList.get(k), 16);
                        }

                        byte[] result = Util.toByte(pageDataArray);
                        mySerialPort.writeBytes(result, 4);

                        //   System.out.println(noOfPagesByteArray);
                        System.out.println("No Of Pages Transmitted -> " + noOfPages);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response == Constants.pageAcknowledgement) {

                    // String pageData=pageDataList.get(noOfPagesTransfer);


                    if (noOfPagesTransfer == pageDataList.size()) {
                        int bytesTxed = 0;
                        System.out.println("Page Data End");
                        byte[] endAcknowledgement = new byte[1];
                        endAcknowledgement[0] = 0x03;
                        bytesTxed = mySerialPort.writeBytes(endAcknowledgement, 1);
                        dashboard.dataTransferSuccessLabel.setVisible(true);
                        dashboard.dataTransferSuccessLabel.setText("Data Transferred Successfully");

                        noOfPagesTransfer = 0;

                        System.out.println(bytesTxed);

                    } else {

                        final String data = pageDataList.get(noOfPagesTransfer);

                        List<String> pageData = new ArrayList<>();
                        for (int i = 0; i < data.length(); i += 2) {
                            pageData.add(data.substring(i, Math.min(data.length(), i + 2)));
                        }

                        System.out.println(pageData);


                        int[] pageDataArray = new int[258];
                        for (int k = 0; k < pageData.size(); k++) {
                            pageDataArray[k] = Integer.parseInt(pageData.get(k), 16);
                        }

                        byte[] result = Util.toByte(pageDataArray);
                        mySerialPort.writeBytes(result, 258);

                        noOfPagesTransfer++;
                        percentage = (double) noOfPagesTransfer / noOfPages * 100;

                        int value = (int) percentage;

                        System.out.println("No Of Pages Transfer" + noOfPagesTransfer + "\t Percentage : " + percentage);
                        dashboard.progressBar.setValue(value);
                        dashboard.dataTransferCountLabel.setText(noOfPagesTransfer + " / " + noOfPages);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (mySerialPort.isOpen())
            System.out.println(mySerialPort.getSystemPortName() + " is Open ");
        else
            System.out.println("\n Port not open ");
    }
}




   /* static void initializeConnection() {

        if (availablePorts.length != 0) {

            System.out.println("\n\n SerialPort Data Reception");

            // use the for loop to print the available serial ports
            System.out.print("\n\n Available Ports ");
            for (int i = 0; i < availablePorts.length; i++) {
                System.out.println(i + " - " + availablePorts[i].getSystemPortName() + " -> " + availablePorts[i].getDescriptivePortName());
            }

            //Open the first Available port
            mySerialPort = availablePorts[selectedCOMPort - 1];

            // Set Serial port Parameters
            mySerialPort.setComPortParameters(BaudRate, DataBits, StopBits, Parity);//Sets all serial port parameters at one time

            mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, Constants.bufferReadTime, 0); //Set Read Time outs
            //.setComPortTimeouts(TIMEOUT_Mode, READ_TIMEOUT_milliSec, WRITE_TIMEOUT_milliSec);

            mySerialPort.openPort(); //open the port

            if (mySerialPort.isOpen())
                System.out.println("\n" + mySerialPort.getSystemPortName() + " is Open ");
            else
                System.out.println(" Port not open ");


            //Display the Serial Port parameters
            System.out.println("\n Selected Port               = " + mySerialPort.getSystemPortName());
            System.out.println(" Selected Number of DataBits = " + mySerialPort.getNumDataBits());
            System.out.println(" Selected Number of StopBits = " + mySerialPort.getNumStopBits());
            System.out.println(" Selected Parity             = " + mySerialPort.getParity());
            System.out.println(" Selected Read Time Out      = " + mySerialPort.getReadTimeout() + "mS");

            mySerialPort.flushIOBuffers();


            try {
                while (true) {
                    byte[] readBuffer = new byte[5];
                    int numRead = mySerialPort.readBytes(readBuffer, readBuffer.length);
                    System.out.print("Read " + numRead + " bytes -");
                    //System.out.println(Arrays.toString(readBuffer));

                    byte response = readBuffer[0];


                    System.out.println("Response Is " + response);


                    if (response == Constants.communicationModeStart) {

                        try {
                            int bytesTxed = 0;

                            byte[] pagesArray = new byte[4];
                            pagesArray[0] = 0x24;
                            pagesArray[1] = 0x00;
                            pagesArray[2] = (byte) noOfPages;
                            pagesArray[3] = 0x23;

                            bytesTxed = mySerialPort.writeBytes(pagesArray, 4);

                            System.out.println("No Of Pages Transmitted -> " + bytesTxed);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response == Constants.pageAcknowledgement) {

                        // String pageData=pageDataList.get(noOfPagesTransfer);


                        if (noOfPagesTransfer == pageDataList.size()) {
                            int bytesTxed = 0;
                            System.out.println("Page Data End");
                            byte[] endAcknowledgement = new byte[1];
                            endAcknowledgement[0] = 0x03;
                            bytesTxed = mySerialPort.writeBytes(endAcknowledgement, 1);
                            dashboard.dataTransferSuccessLabel.setVisible(true);
                            dashboard.dataTransferSuccessLabel.setText("Data Transferred Successfully");
                            System.out.println(bytesTxed);

                        } else {

                            final String data = pageDataList.get(noOfPagesTransfer);

                            List<String> pageData = new ArrayList<>();
                            for (int i = 0; i < data.length(); i += 2) {
                                pageData.add(data.substring(i, Math.min(data.length(), i + 2)));
                            }

                            System.out.println(pageData);


                            int[] pageDataArray = new int[258];
                            for (int k = 0; k < pageData.size(); k++) {
                                pageDataArray[k] = Integer.parseInt(pageData.get(k), 16);
                            }

                            byte[] result = Util.toByte(pageDataArray);
                            mySerialPort.writeBytes(result, 258);

                            noOfPagesTransfer++;
                            percentage = (double) noOfPagesTransfer / noOfPages * 100;

                            int value = (int) percentage;

                            System.out.println("No Of Pages Transfer" + noOfPagesTransfer + "\t Percentage : " + percentage);
                            dashboard.progressBar.setValue(value);
                            dashboard.dataTransferCountLabel.setText(noOfPagesTransfer + " / " + noOfPages);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //	MySerialPort.closePort(); //Close the port

            if (mySerialPort.isOpen())
                System.out.println(mySerialPort.getSystemPortName() + " is Open ");
            else
                System.out.println("\n Port not open ");
        }

    }

    static void initializeComPorts() {
        try {
            availablePorts = SerialPort.getCommPorts();
            comPorts = new String[availablePorts.length + 1];
            for (int i = 0; i < availablePorts.length + 1; i++) {
                if (i == 0) {
                    comPorts[i] = Constants.selectComPort;
                } else {
                    String name = availablePorts[i - 1].getSystemPortName();
                    comPorts[i] = name;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }*/
