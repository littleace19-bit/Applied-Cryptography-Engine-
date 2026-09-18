

/**
 *  Image Steganography and Encryption
 * Author: Anthony Acevedo
 *
 * ==========================================
 * HOW TO RUN THIS PROGRAM
 * ==========================================
 * * OPTION A: RUNNING VIA IDE (Eclipse, IntelliJ, NetBeans)
 * 1. Code Placement: Place 'ImageHiding.java' and 'AES.java' inside
 * your 'src' folder (or default package).
 * 2. Image Placement (CRITICAL): Place 'host_image.jpg' and
 * 'secret_image.jpg' in the PROJECT ROOT folder. They must be
 * placed directly inside the main project folder, completely
 * outside of the 'src' folder.
 * 3. Open 'ImageHiding.java' and click Run.
 *

 * ==========================================
 * USAGE NOTES
 * ==========================================
 * - Select the desired task (0, 1, 2, or 3) from the top dropdown menu.
 * - For Tasks 0 and 1, adjust the "Bits to encode" using the +/- buttons.
 * - STATE LOCK: Task 3 (AES Decryption) requires ciphertext to function.
 * If you attempt to execute Task 3 before successfully running Task 2
 * (AES Encryption), the program will block the execution and display
 * a warning dialog.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class ImageHiding extends JFrame implements ActionListener {
 BufferedImage hostImage;
 BufferedImage secretImage;

 JPanel controlPanel;
 JPanel imagePanel;

 JTextField encodeBitsText;
 JButton encodeBitsPlus;
 JButton encodeBitsMinus;

 JComboBox<String> taskSelector;
 JButton executeTaskButton;

 ImageCanvas hostCanvas;
 ImageCanvas secretCanvas;

 Steganography s;

 // State variables for Task 2 and 3
 boolean isTask2Completed = false;
 SecretKey aesKey = null;
 byte[] encryptedImageData = null; // Store encrypted bytes to pass to decrypt

 public BufferedImage getHostImage() {
  BufferedImage img = null;
  try {
   img = ImageIO.read(new File("host_image.jpg"));
  } catch (IOException ioe) {
   ioe.printStackTrace();
  }
  return img;
 }

 public BufferedImage getSecretImage() {
  BufferedImage img = null;
  try {
   img = ImageIO.read(new File("secret_image.jpg"));
  } catch (IOException ioe) {
   ioe.printStackTrace();
  }
  return img;
 }

 public int getBits() {
  return Integer.parseInt(encodeBitsText.getText());
 }

 public void actionPerformed(ActionEvent event) {
  Object source = event.getSource();

  if (source == encodeBitsPlus) {
   int bits = this.getBits() + 1;
   if (bits > 8) { bits = 8; }
   encodeBitsText.setText(Integer.toString(bits));
  } else if (source == encodeBitsMinus) {
   int bits = this.getBits() - 1;
   if (bits < 0) { bits = 0; }
   encodeBitsText.setText(Integer.toString(bits));
  } else if (source == executeTaskButton) {
   int selectedTask = taskSelector.getSelectedIndex();
   int bits = this.getBits();

   if (selectedTask == 0) {
    // Task 0: Original Steganography
    s = new Steganography(this.getHostImage());
    s.encode(this.getSecretImage(), bits);
    hostCanvas.setImage(s.getImage());
    hostCanvas.repaint();

    s = new Steganography(this.getSecretImage());
    s.getMaskedImage(bits);
    secretCanvas.setImage(s.getImage());
    secretCanvas.repaint();
    JOptionPane.showMessageDialog(this, "Task 0 (Original Steganography) Completed.");

   } else if (selectedTask == 1) {
    // Task 1: Reverse Steganography (LSB of Secret into MSB of Host)
    s = new Steganography(this.getHostImage());
    s.encodeTask1(this.getSecretImage(), bits);
    hostCanvas.setImage(s.getImage());
    hostCanvas.repaint();
    JOptionPane.showMessageDialog(this, "Task 1 (LSB -> MSB Steganography) Completed.");

   } else if (selectedTask == 2) {
    // Task 2: Encrypt Host Image using AES preserving header
    s = new Steganography(this.getHostImage()); // Reset to original host for clean encryption
    try {
     aesKey = s.generateKey();
     encryptedImageData = s.encryptImage(aesKey);
     hostCanvas.setImage(s.getImage());
     hostCanvas.repaint();
     isTask2Completed = true;
     JOptionPane.showMessageDialog(this, "Task 2 (AES Encryption) Completed.\nImage is now encrypted!");
    } catch (Exception e) {
     JOptionPane.showMessageDialog(this, "Encryption failed: " + e.getMessage());
    }

   } else if (selectedTask == 3) {
    // Task 3: Decrypt Host Image
    if (!isTask2Completed || aesKey == null || encryptedImageData == null) {
     JOptionPane.showMessageDialog(this, "Error: You must successfully complete Task 2 (Encryption) before running Task 3 (Decryption).", "Task 3 Locked", JOptionPane.WARNING_MESSAGE);
    } else {
     try {
      s = new Steganography(this.getHostImage());
      s.decryptImage(encryptedImageData, aesKey);
      hostCanvas.setImage(s.getImage());
      hostCanvas.repaint();
      JOptionPane.showMessageDialog(this, "Task 3 (AES Decryption) Completed.\nImage restored.");
     } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Decryption failed: " + e.getMessage());
     }
    }
   }
  }
 }

 public ImageHiding() {
  GridBagLayout layout = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  this.setTitle("Image Hiding & Encryption App");

  Container container = this.getContentPane();
  this.setLayout(layout);

  // GUI Task Selector (Task 4 requirement)
  this.add(new JLabel("Select Task:"));
  String[] tasks = {"Task 0: Hide MSB in LSB (Original)", "Task 1: Hide LSB in MSB", "Task 2: AES Encrypt Image", "Task 3: AES Decrypt Image"};
  taskSelector = new JComboBox<>(tasks);
  gbc.weightx = 1.0;
  layout.setConstraints(taskSelector, gbc);
  this.add(taskSelector);

  executeTaskButton = new JButton("Execute Selected Task");
  executeTaskButton.addActionListener(this);
  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(executeTaskButton, gbc);
  this.add(executeTaskButton);

  this.add(new JLabel("Bits to encode into host image:"));

  encodeBitsText = new JTextField("0", 5);
  encodeBitsText.setEditable(false);

  gbc.weightx = -1.0;
  gbc.gridwidth = 1;
  layout.setConstraints(encodeBitsText, gbc);
  this.add(encodeBitsText);

  encodeBitsPlus = new JButton("+");
  encodeBitsPlus.addActionListener(this);

  encodeBitsMinus = new JButton("-");
  encodeBitsMinus.addActionListener(this);

  gbc.weightx = 1.0;
  layout.setConstraints(encodeBitsPlus, gbc);
  this.add(encodeBitsPlus);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(encodeBitsMinus, gbc);
  this.add(encodeBitsMinus);

  GridBagLayout imageGridbag = new GridBagLayout();
  GridBagConstraints imageGBC = new GridBagConstraints();

  imagePanel = new JPanel();
  imagePanel.setLayout(imageGridbag);

  JLabel hostImageLabel = new JLabel("Host image:");
  JLabel secretImageLabel = new JLabel("Secret image:");

  imagePanel.add(hostImageLabel);

  imageGBC.gridwidth = GridBagConstraints.REMAINDER;
  imageGridbag.setConstraints(secretImageLabel, imageGBC);
  imagePanel.add(secretImageLabel);

  hostCanvas = new ImageCanvas(this.getHostImage());
  secretCanvas = new ImageCanvas(this.getSecretImage());

  imagePanel.add(hostCanvas);
  imagePanel.add(secretCanvas);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(imagePanel, gbc);
  this.add(imagePanel);

  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  this.pack();
  this.setVisible(true);
 }

 public static void main(String[] args) {
  ImageHiding frame = new ImageHiding();
  frame.setVisible(true);
 }

 public class ImageCanvas extends JPanel {
  Image img;

  public void paintComponent(Graphics g) {
   g.drawImage(img, 0, 0, this);
  }

  public void setImage(Image img) {
   this.img = img;
  }

  public ImageCanvas(Image img) {
   this.img = img;
   this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
  }
 }
}

class Steganography {
 BufferedImage image;

 public void getMaskedImage(int bits) {
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));
  int maskBits = (int) (Math.pow(2, bits)) - 1 << (8 - bits);
  int mask = (maskBits << 24) | (maskBits << 16) | (maskBits << 8) | maskBits;

  for (int i = 0; i < imageRGB.length; i++) {
   imageRGB[i] = imageRGB[i] & mask;
  }
  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }

 public void encode(BufferedImage encodeImage, int encodeBits) {
  int[] encodeRGB = encodeImage.getRGB(0, 0, encodeImage.getWidth(null), encodeImage.getHeight(null), null, 0, encodeImage.getWidth(null));
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int encodeByteMask = (int) (Math.pow(2, encodeBits)) - 1 << (8 - encodeBits);
  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

  int decodeByteMask = ~(encodeByteMask >>> (8 - encodeBits)) & 0xFF;
  int hostMask = (decodeByteMask << 24) | (decodeByteMask << 16) | (decodeByteMask << 8) | decodeByteMask;

  for (int i = 0; i < imageRGB.length; i++) {
   int encodeData = (encodeRGB[i] & encodeMask) >>> (8 - encodeBits);
   imageRGB[i] = (imageRGB[i] & hostMask) | (encodeData & ~hostMask);
  }
  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }

 // TASK 1: Hide LSB of Secret into MSB of Host
 public void encodeTask1(BufferedImage encodeImage, int encodeBits) {
  int[] encodeRGB = encodeImage.getRGB(0, 0, encodeImage.getWidth(null), encodeImage.getHeight(null), null, 0, encodeImage.getWidth(null));
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int encodeByteMask = (int) (Math.pow(2, encodeBits)) - 1;
  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

  int hostByteMask = (0xFF >>> encodeBits);
  int hostMask = (hostByteMask << 24) | (hostByteMask << 16) | (hostByteMask << 8) | hostByteMask;

  for (int i = 0; i < imageRGB.length; i++) {
   int encodeData = (encodeRGB[i] & encodeMask) << (8 - encodeBits);
   // Ensure alpha channel (transparency) remains fully opaque
   imageRGB[i] = (imageRGB[i] & hostMask) | (encodeData & ~hostMask) | 0xFF000000;
  }
  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }

 // TASK 2 Helper: Key Generation
 public SecretKey generateKey() throws NoSuchAlgorithmException {
  KeyGenerator keygen = KeyGenerator.getInstance("AES");
  keygen.init(128);
  return keygen.generateKey();
 }

 // TASK 2: AES Encryption preserving header
 public byte[] encryptImage(SecretKey key) throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ImageIO.write(image, "BMP", baos);
  baos.flush();
  byte[] b = baos.toByteArray();

  byte[] newb = new byte[b.length - 54];
  byte[] header = new byte[54];

  for (int j = 0; j < b.length; j++) {
   if (j < 54) { header[j] = b[j]; }
   else { newb[j - 54] = b[j]; }
  }

  byte[] encryptImg = AES.encrypt(newb, key.getEncoded());
  byte[] combencImage = new byte[encryptImg.length + header.length];

  for (int x = 0; x < combencImage.length; x++) {
   if (x < 54) { combencImage[x] = header[x]; }
   else { combencImage[x] = encryptImg[x - 54]; }
  }

  ByteArrayInputStream bais = new ByteArrayInputStream(combencImage);
  this.image = ImageIO.read(bais);
  return combencImage; // Return encrypted bytes for Task 3 to use
 }

 // TASK 3: AES Decryption preserving header
 public void decryptImage(byte[] encryptedDataWithHeader, SecretKey key) throws Exception {
  byte[] encryptedPixelData = new byte[encryptedDataWithHeader.length - 54];
  byte[] header = new byte[54];

  for (int j = 0; j < encryptedDataWithHeader.length; j++) {
   if (j < 54) { header[j] = encryptedDataWithHeader[j]; }
   else { encryptedPixelData[j - 54] = encryptedDataWithHeader[j]; }
  }

  byte[] decryptImg = AES.decrypt(encryptedPixelData, key.getEncoded());
  byte[] combdecImage = new byte[decryptImg.length + header.length];

  for (int x = 0; x < combdecImage.length; x++) {
   if (x < 54) { combdecImage[x] = header[x]; }
   else { combdecImage[x] = decryptImg[x - 54]; }
  }

  ByteArrayInputStream bais = new ByteArrayInputStream(combdecImage);
  this.image = ImageIO.read(bais);
 }

 public Image getImage() {
  return image;
 }

 public Steganography(BufferedImage image) {
  this.image = image;
 }
}