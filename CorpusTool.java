import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorpusTool {

   public static void main(String args[]) throws Exception {
	   StringBuffer textbookBuffer = new StringBuffer();
	   StringBuffer textbookWithIdBuffer = new StringBuffer();
	   
	   FileReader fr = new FileReader("G:\\4-大四\\毕业设计(论文)\\项目\\XML文件样本\\90021A0002661 汉语入门.xml");
	   BufferedReader br = new BufferedReader(fr);
	   String s = "";
	   while ((s = br.readLine()) != null) {
		   textbookBuffer.append(s + "\n");
	   }
	   br.close();
	   
	   Pattern lessonBeginPattern = Pattern.compile("\n?<课 课名");
	   Pattern lessonEndPattern = Pattern.compile("\n?</课>");
	   Pattern textBeginPattern = Pattern.compile("\n?<课文 课文名");
	   Pattern textEndPattern = Pattern.compile("\n?</课文>");
	   Pattern sectionBeginPattern = Pattern.compile("\n?<段>\n?");
	   Pattern sectionEndPattern = Pattern.compile("\n?</段>");
	   Pattern exampleSentencesBeginPattern = Pattern.compile("\n?<例句>\n?");
	   Pattern exampleSentencesEndPattern = Pattern.compile("\n?</例句>");
	   Pattern exampleBeginPattern = Pattern.compile("\n?<例>\n?");
	   Pattern exampleEndPattern = Pattern.compile("\n?</例>");
	   
	   Pattern rightPattern = Pattern.compile(">");
	   Pattern sentencePattern = Pattern.compile("。”?|！”?|？！?”?|：");
	   
	   int lesson = 0;
	   int text = 0;
	   int section = 0;
	   int sentence = 0;
	   int exampleSentences = 0;
	   int example = 0;
	   
	   Matcher lessonBeginMatcher = lessonBeginPattern.matcher(textbookBuffer);
	   Matcher textBeginMatcher = textBeginPattern.matcher(textbookBuffer);
	   Matcher sectionBeginMatcher = sectionBeginPattern.matcher(textbookBuffer);
	   Matcher exampleSentencesBeginMatcher = exampleSentencesBeginPattern.matcher(textbookBuffer);
	   Matcher exampleBeginMatcher = exampleBeginPattern.matcher(textbookBuffer);
	   
	   Matcher lessonEndMatcher = lessonEndPattern.matcher(textbookBuffer);
	   Matcher textEndMatcher = textEndPattern.matcher(textbookBuffer);
	   Matcher sectionEndMatcher = sectionEndPattern.matcher(textbookBuffer);
	   Matcher exampleSentencesEndMatcher = exampleSentencesEndPattern.matcher(textbookBuffer);
	   Matcher exampleEndMatcher = exampleEndPattern.matcher(textbookBuffer);
	   
	   Matcher rightMatcher = rightPattern.matcher(textbookBuffer);
	   
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
				   Matcher sentenceMatcher = sentencePattern.matcher(sentences);
				   int before = 0;
				   while (sentenceMatcher.find()) {
					   sentence++;
					   if (sentences.charAt(before) == '\n') {
						   before++;
					   }
					   textbookWithIdBuffer.append("<句 句Id=\"" + sentence + "\">\n");
					   textbookWithIdBuffer.append(sentences.substring(before, sentenceMatcher.end()) + "\n");
					   textbookWithIdBuffer.append("</句>\n");
					   before = sentenceMatcher.end();
				   }
				   if (sentence == 0) {
					   textbookWithIdBuffer.append(sentences + "\n");
				   }
				   
				   textbookWithIdBuffer.append("</段>\n");
				   textbookBuffer.delete(0, sectionEndMatcher.end());
				   sentence = 0;
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
	   System.out.println(textbookWithIdBuffer);
   }
}