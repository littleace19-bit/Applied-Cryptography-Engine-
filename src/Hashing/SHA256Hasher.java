
import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;


public class SHA256Hasher {

    // Computes the SHA-256 hash of a byte array
    public static byte[] computeSHA256(byte[] inputData) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(inputData);
    }

    // Compares two byte arrays bit-by-bit and returns the percentage of bits that differ
    public static double calculateBitDifferencePercentage(byte[] hash1, byte[] hash2) {
        if (hash1.length != hash2.length) {
            throw new IllegalArgumentException("Hashes must be the same length to compare.");
        }

        int differingBits = 0;
        int totalBits = hash1.length * 8; // 256 bits total

        for (int i = 0; i < hash1.length; i++) {
            // ADDED & 0xFF: This prevents "Sign Extension" from adding 24 extra 1s
            int xorResult = (hash1[i] ^ hash2[i]) & 0xFF;

            // Count only the 1s in the 8-bit result
            while (xorResult != 0) {
                differingBits += (xorResult & 1);
                xorResult >>>= 1;
            }
        }

        return ((double) differingBits / totalBits) * 100.0;
    }

    // Converts a byte array to a readable Hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // Main logic to test a file, modify it, and compare hashes
    public static void runHashExperiment(String filePath, String fileType) {
        System.out.println("==================================================");
        System.out.println("Running Hash Experiment for: " + fileType);
        System.out.println("File: " + filePath);
        System.out.println("==================================================");

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("ERROR: File not found -> " + filePath);
                return;
            }

            // 1. Read the original file into a byte array
            byte[] originalData = Files.readAllBytes(file.toPath());

            // 2. Compute the hash of the original file
            byte[] originalHash = computeSHA256(originalData);
            System.out.println("Original Hash: " + bytesToHex(originalHash));

            // 3. Modify exactly ONE byte (character/pixel) in the data
            byte[] modifiedData = originalData.clone();
            // We flip the bits of the 50th byte using the NOT operator (~)
            // This perfectly simulates modifying a single character or pixel
            modifiedData[50] = (byte) ~modifiedData[50];

            // 4. Recompute the hash of the modified data
            byte[] modifiedHash = computeSHA256(modifiedData);
            System.out.println("Modified Hash: " + bytesToHex(modifiedHash));

            // 5. Compare the two hashes and calculate the percentage of bits changed
            double bitDiffPercentage = calculateBitDifferencePercentage(originalHash, modifiedHash);
            System.out.printf("Percentage of bits changed: %.2f%%\n\n", bitDiffPercentage);

        } catch (Exception e) {
            System.out.println("An error occurred during the experiment: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // You can change these filenames to match whatever files are in your folder
        String textFileName = "target_text.txt";
        String imageFileName = "image_b93800.png";

        // Run Task 2(b)
        runHashExperiment(textFileName, "Text File (~2000 chars)");

        // Run Task 2(c)
        runHashExperiment(imageFileName, "Image File (High Resolution)");
    }
}