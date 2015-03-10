import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorpusTool {
	private static Pattern lessonBeginPattern = Pattern.compile("\n?<课 课名");
	private static Pattern lessonEndPattern = Pattern.compile("\n?</课>");
	private static Pattern textBeginPattern = Pattern.compile("\n?<课文 课文名");
	private static Pattern textEndPattern = Pattern.compile("\n?</课文>");
	private static Pattern sectionBeginPattern = Pattern.compile("\n?<段>\n?");
	private static Pattern sectionEndPattern = Pattern.compile("\n?</段>");
	private static Pattern exampleSentencesBeginPattern = Pattern.compile("\n?<例句>\n?");
	private static Pattern exampleSentencesEndPattern = Pattern.compile("\n?</例句>");
	private static Pattern exampleBeginPattern = Pattern.compile("\n?<例>\n?");
	private static Pattern exampleEndPattern = Pattern.compile("\n?</例>");

	private static Pattern rightPattern = Pattern.compile(">");
	private static Pattern sentencePattern = Pattern.compile("。”?|！”?|？！?”?|：");
	
	private Matcher lessonBeginMatcher;
	private Matcher textBeginMatcher;
	private Matcher sectionBeginMatcher;
	private Matcher exampleSentencesBeginMatcher;
	private Matcher exampleBeginMatcher;

	private Matcher lessonEndMatcher;
	private Matcher textEndMatcher;
	private Matcher sectionEndMatcher;
	private Matcher exampleSentencesEndMatcher;
	private Matcher exampleEndMatcher;

	private Matcher rightMatcher;
	
	private String textbookPath;
	private StringBuffer textbookBuffer;
	private StringBuffer textbookWithIdBuffer;
	
	private int lesson = 0;
	private int text = 0;
	private int section = 0;
	private int exampleSentences = 0;
	private int example = 0;

	public CorpusTool(String textbookPath) {
		this.textbookPath = textbookPath;
		this.textbookWithIdBuffer = new StringBuffer();
	}
	
	public void constructMatcher() {
		lessonBeginMatcher = lessonBeginPattern.matcher(textbookBuffer);
		textBeginMatcher = textBeginPattern.matcher(textbookBuffer);
		sectionBeginMatcher = sectionBeginPattern.matcher(textbookBuffer);
		exampleSentencesBeginMatcher = exampleSentencesBeginPattern.matcher(textbookBuffer);
		exampleBeginMatcher = exampleBeginPattern.matcher(textbookBuffer);

		lessonEndMatcher = lessonEndPattern.matcher(textbookBuffer);
		textEndMatcher = textEndPattern.matcher(textbookBuffer);
		sectionEndMatcher = sectionEndPattern.matcher(textbookBuffer);
		exampleSentencesEndMatcher = exampleSentencesEndPattern.matcher(textbookBuffer);
		exampleEndMatcher = exampleEndPattern.matcher(textbookBuffer);

		rightMatcher = rightPattern.matcher(textbookBuffer);
	}
	
	private StringBuffer handleSentences(String sentences) {
		Matcher sentenceMatcher = sentencePattern.matcher(sentences);
		StringBuffer sentencesBuffer = new StringBuffer();
		int front = 0;
		int sentence = 0;
		while (sentenceMatcher.find()) {
			sentence++;
			if (sentences.charAt(front) == '\n') {
				front++;
			}
			sentencesBuffer.append("<句 句Id=\"" + sentence + "\">\n");
			sentencesBuffer.append(sentences.substring(front, sentenceMatcher.end()) + "\n");
			sentencesBuffer.append("</句>\n");
			front = sentenceMatcher.end();
		}
		if (sentence == 0) {
			sentencesBuffer.append(sentences + "\n");
		}
		return sentencesBuffer;
	}
	
	public StringBuffer handleCorpus() {
		lessonBeginMatcher.find();
		textbookWithIdBuffer.append(textbookBuffer.substring(0, lessonBeginMatcher.start()) + "\n");
		textbookBuffer.delete(0, lessonBeginMatcher.start());
		while (textbookBuffer.length() != 0 && lessonBeginMatcher.lookingAt()) {
			lesson++;
			textbookWithIdBuffer.append("<课 课Id=\"" + lesson + "\" 课名");
			textbookBuffer.delete(0, lessonBeginMatcher.end());

			rightMatcher.reset().find();
			textbookWithIdBuffer.append(textbookBuffer.substring(0, rightMatcher.end()) + "\n");
			textbookBuffer.delete(0, rightMatcher.end());

			while (textBeginMatcher.lookingAt()) {
				text++;
				textbookWithIdBuffer.append("<课文 课文Id=\"" + text + "\" 课文名");
				textbookBuffer.delete(0, textBeginMatcher.end());

				rightMatcher.reset().find();
				textbookWithIdBuffer.append(textbookBuffer.substring(0, rightMatcher.end()) + "\n");
				textbookBuffer.delete(0, rightMatcher.end());

				while (sectionBeginMatcher.lookingAt()) {
					section++;
					textbookWithIdBuffer.append("<段 段Id=\"" + section + "\">\n");
					textbookBuffer.delete(0, sectionBeginMatcher.end());

					sectionEndMatcher.reset().find();
					String sentences = textbookBuffer.substring(0, sectionEndMatcher.start());
					StringBuffer sentencesBuffer = handleSentences(sentences);
					textbookWithIdBuffer.append(sentencesBuffer);

					textbookWithIdBuffer.append("</段>\n");
					textbookBuffer.delete(0, sectionEndMatcher.end());
				}

				textEndMatcher.reset().find();
				textbookWithIdBuffer.append("</课文>\n");
				textbookBuffer.delete(0, textEndMatcher.end());
				section = 0;
			}

			while (exampleSentencesBeginMatcher.lookingAt()) {
				exampleSentences++;
				textbookWithIdBuffer.append("<例句 例句Id=\"" + exampleSentences + "\">\n");
				textbookBuffer.delete(0, exampleSentencesBeginMatcher.end());

				while (exampleBeginMatcher.lookingAt()) {
					example++;
					textbookWithIdBuffer.append("<例 例Id=\"" + example + "\">\n");
					textbookBuffer.delete(0, exampleBeginMatcher.end());

					exampleEndMatcher.reset().find();
					textbookWithIdBuffer.append(textbookBuffer.substring(0, exampleEndMatcher.end()) + "\n");
					textbookBuffer.delete(0, exampleEndMatcher.end());
				}

				exampleSentencesEndMatcher.reset().find();
				textbookWithIdBuffer.append("</例句>\n");
				textbookBuffer.delete(0, exampleSentencesEndMatcher.end());
				example = 0;
			}

			lessonEndMatcher.reset().find();
			textbookWithIdBuffer.append("</课>\n");
			textbookBuffer.delete(0, lessonEndMatcher.end());
			text = 0;
			exampleSentences = 0;
		}

		textbookWithIdBuffer.append("</教材>");
		return textbookWithIdBuffer;
	}
	
	public static StringBuffer readTextInBuffer(String path) throws IOException {
		StringBuffer textBuffer = new StringBuffer();

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String s = "";
		while ((s = br.readLine()) != null) {
			textBuffer.append(s + "\n");
		}
		br.close();

		return textBuffer;
	}
	
	public static void writeBufferToText(StringBuffer sb, String textPath) throws IOException {
		FileWriter fw = new FileWriter(textPath);
		fw.write(sb.toString());
		fw.flush();
		fw.close();
	}

	public static void main(String args[]) {
		String path = "G:\\4-大四\\毕业设计(论文)\\项目\\XML文件样本\\90021A0002661 汉语入门.xml";
		CorpusTool ct = new CorpusTool(path);
		try {
			ct.textbookBuffer = readTextInBuffer(ct.textbookPath);
		} catch (IOException e) {
			System.out.println("READING ERROR");
			System.exit(0);
		}

		ct.constructMatcher();
		StringBuffer resultBuffer = ct.handleCorpus();

		String outPath = "G:\\4-大四\\毕业设计(论文)\\项目\\XML文件样本\\90021A0002661 汉语入门-1.xml";
		try {
			writeBufferToText(resultBuffer, outPath);
		} catch (IOException e) {
			System.out.println("WRITING ERROR");
			System.exit(0);
		}
		
		System.out.println("FINISHED");
	}
}