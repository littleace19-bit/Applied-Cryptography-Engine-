## CryptoStego Engine

## Project Objective
The CryptoStego Engine is a Java-based implementation of advanced cryptographic and steganographic algorithms. The primary objective of this project is to demonstrate the mathematical foundations of symmetric encryption, data hiding, and cryptographic hashing by building core tools from scratch. By bypassing high-level cryptographic abstraction libraries for the AES engine and custom hash functions, the project accurately represents the underlying finite field mathematics, bitwise operations, and security vulnerabilities of cryptographic systems.

## System Architecture
The suite is built using modular Java components and interactive Swing GUIs to process both text and binary image data.

AES Encryption Engine (AES.java): A fully custom 128-bit implementation of the Advanced Encryption Standard. It handles SubBytes, ShiftRows, MixColumns, and AddRoundKey transformations using pure Galois field arithmetic and bitwise shifts.

AES Image Processor (AESdemo.java): A GUI desktop application that encrypts and decrypts BMP images. It features a specialized algorithm to isolate and preserve the 54-byte BMP metadata header, ensuring the resulting ciphertext remains a valid, viewable image file.

Visual Steganography (ImageHiding.java): A dual-purpose GUI that manipulates Least Significant Bits (LSBs) and Most Significant Bits (MSBs) to securely hide secret images within host images, seamlessly integrating with the AES engine for an added layer of security.

Cryptanalysis & Hashing (SimpleHash.java & SHA256Hasher.java): Experimental modules designed to measure the "Avalanche Effect" in modern hashes (SHA-256) and programmatically break weak cryptographic hashes.

## Performance Analysis/ Benchmarks
To test the security limits of cryptographic hashing, automated cryptanalysis attacks were executed against a custom 160-bit XOR-based hash function. Additionally, a bit-level diffusion test was run on SHA-256 to verify mathematical non-linearity.

### Security Benchmarks
| Attack / Test | Target Function | Success Status | Vulnerability Demonstrated |
| :--- | :--- | :--- | :--- |
| **Pre-image (One-Way) Attack** | SimpleHash (XOR) | 100% (Forged) | Message perfectly mirrors the hash |
| **Weak Collision Attack** | SimpleHash (XOR) | 100% (Collided) | Appending empty blocks bypasses XOR |
| **Strong Collision Attack** | SimpleHash (XOR) | 100% (Collided) | Identical repeating blocks cancel out |
| **Avalanche Test (1-byte change)**| SHA-256 | Passed (~50% diff) | High non-linearity / Secure |

## Key Insights
These benchmarks and implementations prove how seemingly minor mathematical choices completely alter cryptographic security:

XOR Hashing is Fundamentally Broken: The cryptanalysis proves that relying on basic bitwise XOR for hashing completely fails standard security requirements. Because XORing a value by zero leaves it unchanged, and XORing a value by itself results in zero, attackers can trivially forge messages or force collisions by appending dummy blocks.

The Importance of the Avalanche Effect: The SHA-256 experiment verifies that changing a single byte (like one pixel in a high-resolution image) flips approximately 50% of the output bits. This proves the algorithm's high non-linearity, which is what prevents attackers from predicting the hash based on the plaintext.

Metadata Preservation: In image encryption, blindly encrypting a file destroys it. By mathematically bypassing the first 54 bytes (the header) of a BMP file before applying the custom AES engine, the encrypted output remains perfectly readable by standard operating systems, appearing as raw visual static rather than a corrupted file.

## Prerequisites
Java Development Kit (JDK) 8 or higher

IntelliJ IDEA, Eclipse, or standard Java IDE

Sample .jpg and .bmp image files for testing

## Instalation/ Usage
1. Clone the repository to your local machine.

2. Open the project folder in your preferred Java IDE (ensure the src folder is recognized).

3. Important for Steganography: Place your test images (e.g., host_image.jpg, secret_image.jpg) in the root project directory, outside of the src folder, so the relative paths can locate them.

4. Navigate to the src folder and run the desired main method:

Run ImageHiding.java to launch the Steganography GUI.

Run AESdemo.java to launch the standalone Image Encryption GUI.

Run SimpleHash.java to view the automated hash cryptanalysis in the console.
