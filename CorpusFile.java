import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CorpusFile {
	private String textbookPath;
	
	public CorpusFile(String textbookPath) {
		this.textbookPath = textbookPath;
	}
	
	protected StringBuffer readTextBuffer() throws IOException {
		return readTextInBuffer(textbookPath);
	}
	
	protected void writeTextBuffer(StringBuffer sb) throws IOException {
		writeBufferToText(sb, textbookPath);
	}
	
	private static StringBuffer readTextInBuffer(String path) throws IOException {
		StringBuffer textBuffer = new StringBuffer();

		FileInputStream fis = new FileInputStream(path);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String s = "";
		while ((s = br.readLine()) != null) {
			textBuffer.append(s + "\n");
		}
		br.close();

		return textBuffer;
	}
	
	private static void writeBufferToText(StringBuffer sb, String textPath) throws IOException {
		FileOutputStream fos = new FileOutputStream(textPath);
		fos.write(sb.toString().getBytes("UTF-8"));
		fos.close();
	}
}