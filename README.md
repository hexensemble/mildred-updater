# Mildred Updater

This program is included as part of the game Mildred and provides update services.

https://github.com/hexensemble/mildred

Key features, highlights, and things learnt:

**Swing-Based GUI:** Implements a user-friendly graphical interface using Java Swing components (JPanel, JButton, JTextArea, etc.). Customizes look and feel to match the system’s native design using UIManager. Text output logs the update process in real-time to a JTextArea, giving users feedback.

**Dynamic Update Fetching:** Downloads update metadata and files from a server using URL. Retrieves dynamic update links and file size details by parsing custom [url] and [size] tags in the server response.

**HTTP Connection Handling:** Manages connections with URLConnection, adding a User-Agent header for compatibility with modern servers. Handles potential networking errors gracefully with try-catch blocks.

**Download Mechanism:** Downloads files in chunks using buffered streams, with a progress update logged for each byte transferred. Saves the update as a compressed .zip file in a temporary directory.

**Zip File Handling:** Unzips downloaded updates using java.util.zip.ZipFile. Efficiently creates directories and writes extracted files to disk with buffered streams.

**Dynamic File Paths:** Uses System.getProperty("user.home") to ensure compatibility across Windows, macOS, and Linux. Special Handling for Linux: Includes explicit instructions for Linux users to manually relocate updated files if needed.

**File Copying:** Implements recursive directory traversal to copy all update files from the temporary directory to the game directory. Deletes old temporary files after the update is complete for cleanup.

**Buffered I/O:** Optimizes file read/write operations using buffers to reduce I/O overhead.

**Resilient Exception Management:** Catches and logs specific exceptions (IOException, MalformedURLException) to ensure that the program remains stable during network and file operations. Displays user-friendly error messages using JOptionPane.
