import java.nio.charset.StandardCharsets;


public class SimpleHash {
    private static final int BLOCK_SIZE_BYTES = 20; // 160 bits = 20 bytes

    // ==========================================
    // TASK 1: THE HASH FUNCTION IMPLEMENTATION
    // ==========================================
    public static byte[] computeHash(byte[] message) {
        // Step 1: Calculate padded length (must be a multiple of 20 bytes)
        int numBlocks = (int) Math.ceil((double) message.length / BLOCK_SIZE_BYTES);
        if (numBlocks == 0) numBlocks = 1; // Handle empty string
        int paddedLength = numBlocks * BLOCK_SIZE_BYTES;

        // Step 2: Pad with dummy bits (zeros)
        byte[] paddedMessage = new byte[paddedLength];
        System.arraycopy(message, 0, paddedMessage, 0, message.length);

        // Step 3: Compute M1 + M2 + ... + Mr (mod 2) -> Bitwise XOR
        byte[] hashResult = new byte[BLOCK_SIZE_BYTES];

        for (int i = 0; i < paddedLength; i += BLOCK_SIZE_BYTES) {
            for (int j = 0; j < BLOCK_SIZE_BYTES; j++) {
                hashResult[j] ^= paddedMessage[i + j];
            }
        }
        return hashResult;
    }

    // ==========================================
    // TASK 2: ILLUSTRATING THE FAILURES
    // ==========================================

    // Demonstrate One-Way Property Failure
    public static void breakOneWay() {
        System.out.println("--- 1. Breaking One-Way Property ---");
        // We are given a random target hash
        byte[] targetHash = "TargetHash1234567890".getBytes(StandardCharsets.US_ASCII);

        // ATTACK: To find a message that hashes to this, we just make the message equal to the hash!
        byte[] forgedMessage = targetHash;

        byte[] resultHash = computeHash(forgedMessage);
        System.out.println("Target Hash:    " + bytesToHex(targetHash));
        System.out.println("Forged Msg Hash:" + bytesToHex(resultHash));
        System.out.println("Match? " + java.util.Arrays.equals(targetHash, resultHash));
    }

    // Demonstrate Weak Collision Resistance Failure
    public static void breakWeakCollision() {
        System.out.println("\n--- 2. Breaking Weak Collision Resistance ---");
        String messageX = "This is a highly important and very secret message that must not be altered!";
        byte[] xBytes = messageX.getBytes(StandardCharsets.US_ASCII);

        // ATTACK: Create message Y by appending exactly one block (20 bytes) of zeros to X.
        // XORing with 0 does nothing to the final hash.
        byte[] messageY = new byte[xBytes.length + BLOCK_SIZE_BYTES];
        System.arraycopy(xBytes, 0, messageY, 0, xBytes.length);
        // The rest of messageY defaults to 0 (dummy bits)

        System.out.println("Hash of Message X: " + bytesToHex(computeHash(xBytes)));
        System.out.println("Hash of Message Y: " + bytesToHex(computeHash(messageY)));
        System.out.println("Are X and Y the same length? " + (xBytes.length == messageY.length));
    }

    // Demonstrate Strong Collision Resistance Failure
    public static void breakStrongCollision() {
        System.out.println("\n--- 3. Breaking Strong Collision Resistance ---");
        // ATTACK: Find ANY two pairs that collide. We can do this by appending two identical blocks.
        // A XOR A = 0. So appending "AAAAAAAAAAAAAAAAAAAA" twice cancels itself out.

        String base = "Hello World!";
        String blockToCancel = "12345678901234567890"; // 20 bytes

        String pair1 = base;
        String pair2 = base + blockToCancel + blockToCancel;

        System.out.println("Hash of Pair 1: " + bytesToHex(computeHash(pair1.getBytes())));
        System.out.println("Hash of Pair 2: " + bytesToHex(computeHash(pair2.getBytes())));
    }

    // ==========================================
    // UTILITY METHOD
    // ==========================================
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // TASK 1: Demonstrate functionality with two 100-200 char messages
        System.out.println("=== TASK 1: HASH FUNCTION DEMONSTRATION ===");

        String msg1 = "This is the first long test message. It needs to be over 100 characters long to satisfy the requirements of Task 1 in the cryptography homework assignment.";
        String msg2 = "Here is the second test message. We are demonstrating that the hash function can process arbitrary lengths by padding the final block to 160 bits (20 bytes).";

        System.out.println("Msg 1 length: " + msg1.length() + " chars.");
        System.out.println("Hash 1: " + bytesToHex(computeHash(msg1.getBytes(StandardCharsets.US_ASCII))));

        System.out.println("\nMsg 2 length: " + msg2.length() + " chars.");
        System.out.println("Hash 2: " + bytesToHex(computeHash(msg2.getBytes(StandardCharsets.US_ASCII))));

        System.out.println("\n=================================================");
        System.out.println("=== TASK 2: ILLUSTRATING SECURITY FAILURES ===");
        System.out.println("=================================================");

        breakOneWay();
        breakWeakCollision();
        breakStrongCollision();
    }
}

