package com.hexensemble.mildred.updater;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Application updater.
 * 
 * @author HexEnsemble
 * @author www.hexensemble.com
 * @version 1.0.4
 * @since 1.0.0
 */
public class Updater extends JFrame {

	private static final long serialVersionUID = 1L;

	private final String UPDATE_URL = "http://www.hexensemble.com/mildred/update.html";
	private final String ROOT = System.getProperty("user.home") + "/.mildred/temp/";

	private JPanel window;

	private JScrollPane scrollPane;
	private JTextArea text;

	private JPanel buttons;
	private JButton cancel;
	private JButton ready;

	/**
	 * Initialize.
	 */
	public Updater() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		window = new JPanel();

		setTitle("Mildred Updater");
		setSize(640, 480);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(window);
		setLocationRelativeTo(null);
		setResizable(false);

		create();
		actions();
		setVisible(true);

		text.setText("Contacting download server...");

		download();
	}

	private void create() {
		scrollPane = new JScrollPane();
		text = new JTextArea();
		text.setBorder(
				BorderFactory.createCompoundBorder(text.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		text.setEditable(false);
		scrollPane.setViewportView(text);

		buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		cancel = new JButton("Cancel");
		cancel.setEnabled(true);
		buttons.add(cancel);
		ready = new JButton("Ready");
		ready.setEnabled(false);
		buttons.add(ready);

		window.setLayout(new BorderLayout());
		window.add(scrollPane, BorderLayout.CENTER);
		window.add(buttons, BorderLayout.SOUTH);
	}

	private void actions() {
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchApp();
			}
		});

		ready.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchApp();
			}
		});
	}

	private void download() {
		try {
			downloadFile(getURL());
			unzip();
			copyFiles(new File(ROOT), new File("").getAbsolutePath());
			cleanup();
			cancel.setEnabled(false);
			ready.setEnabled(true);
			text.setText(text.getText() + "\nUpdate finished!");
			text.setText(text.getText() + "\n");
			text.setText(
					text.getText() + "\nLINUX USERS: Updated game file may have been placed in your Home directory.");
			text.setText(text.getText()
					+ "\nExit app and move Mildred.jar to your Mildred directory, overwriting the old version.");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while preforming the update!");
		}
	}

	private void downloadFile(String address) throws MalformedURLException, IOException {
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
		InputStream fileIn = connection.getInputStream();

		String size = getFileSize();
		text.append("\nDownloading file...\nUpdate size (compressed): " + size);

		File dir = new File(ROOT);
		dir.mkdirs();
		BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(new File(ROOT + "update.zip")));
		byte[] buffer = new byte[32 * 1024];
		int bytesRead = 0;
		int bytesIn = 0;
		String saveText = text.getText();
		while ((bytesRead = fileIn.read(buffer)) != -1) {
			bytesIn += bytesRead;
			text.setText(saveText + "\nDownloading: " + bytesIn + " Bytes");
			fileOut.write(buffer, 0, bytesRead);
		}
		fileOut.flush();
		fileOut.close();
		fileIn.close();
		text.append("\nDownload complete!");
	}

	private String getURL() throws MalformedURLException, IOException {
		String data = getData(UPDATE_URL);

		return data.substring(data.indexOf("[url]") + 5, data.indexOf("[/url]"));
	}

	private String getFileSize() throws MalformedURLException, IOException {
		String data = getData(UPDATE_URL);

		return data.substring(data.indexOf("[size]") + 6, data.indexOf("[/size]"));
	}

	private String getData(String address) throws MalformedURLException, IOException {
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
		InputStream html = connection.getInputStream();

		StringBuilder buffer = new StringBuilder("");
		int c = 0;
		while (c != -1) {
			c = html.read();
			buffer.append((char) c);
		}

		return buffer.toString();
	}

	private void unzip() throws IOException {
		int BUFFER = 2048;
		BufferedInputStream read = null;
		BufferedOutputStream write = null;
		ZipEntry entry;
		@SuppressWarnings("resource")
		ZipFile zipfile = new ZipFile(ROOT + "update.zip");
		@SuppressWarnings("rawtypes")
		Enumeration e = zipfile.entries();
		while (e.hasMoreElements()) {
			entry = (ZipEntry) e.nextElement();
			text.setText(text.getText() + "\nExtracting: " + entry);
			if (entry.isDirectory())
				(new File(ROOT + entry.getName())).mkdir();
			else {
				(new File(ROOT + entry.getName())).createNewFile();
				read = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream(ROOT + entry.getName());
				write = new BufferedOutputStream(fos, BUFFER);
				while ((count = read.read(data, 0, BUFFER)) != -1) {
					write.write(data, 0, count);
				}
				write.flush();
				write.close();
				read.close();
			}
		}

	}

	private void copyFiles(File f, String dir) throws IOException {
		File[] files = f.listFiles();
		for (File ff : files) {
			if (ff.isDirectory()) {
				new File(dir + "/" + ff.getName()).mkdir();
				copyFiles(ff, dir + "/" + ff.getName());
			} else {
				copy(ff.getAbsolutePath(), dir + "/" + ff.getName());
			}

		}
	}

	private void copy(String srFile, String dtFile) throws FileNotFoundException, IOException {
		File f1 = new File(srFile);
		File f2 = new File(dtFile);
		InputStream in = new FileInputStream(f1);
		OutputStream out = new FileOutputStream(f2);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	private void cleanup() {
		text.setText(text.getText() + "\nPerforming clean up...");
		File f = new File(ROOT + "update.zip");
		f.delete();
		remove(new File(ROOT));
		new File(ROOT).delete();
	}

	private void remove(File f) {
		File[] files = f.listFiles();
		for (File ff : files) {
			if (ff.isDirectory()) {
				remove(ff);
				ff.delete();
			} else {
				ff.delete();
			}
		}
	}

	private void launchApp() {
		try {
			Desktop.getDesktop().open(new File("Mildred.jar"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}

}
