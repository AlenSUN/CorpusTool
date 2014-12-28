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
	   
	   Pattern lessonBeginPattern = Pattern.compile("<课 课名");
	   Pattern lessonEndPattern = Pattern.compile("</课>");
	   Pattern textBeginPattern = Pattern.compile("<课文 课文名");
	   Pattern textEndPattern = Pattern.compile("</课文>");
	   Pattern sectionBeginPattern = Pattern.compile("<段>");
	   Pattern sectionEndPattern = Pattern.compile("</段>");
	   Pattern exampleSentencesBeginPattern = Pattern.compile("<例句>");
	   Pattern exampleSentencesEndPattern = Pattern.compile("</例句>");
	   Pattern exampleBeginPattern = Pattern.compile("<例>");
	   Pattern exampleEndPattern = Pattern.compile("</例>");
	   
	   Pattern rightPattern = Pattern.compile(">");
	   Pattern sentencePattern = Pattern.compile("。”?|！”?|？”?|：");
	   
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
	   textbookWithIdBuffer.append(textbookBuffer.substring(0, lessonBeginMatcher.start()));
	   textbookBuffer.delete(0, lessonBeginMatcher.start());
	   while (textbookBuffer.length() != 0 && lessonBeginMatcher.lookingAt()) {
		   lesson++;
		   textbookWithIdBuffer.append("<课 课Id=\"" + lesson + "\" 课名");
		   textbookBuffer.delete(0, lessonBeginMatcher.end());
		   
		   rightMatcher.reset().find();
		   textbookWithIdBuffer.append(textbookBuffer.substring(0, rightMatcher.end() + 1));
		   textbookBuffer.delete(0, rightMatcher.end() + 1);
		   
		   while (textBeginMatcher.lookingAt()) {
			   text++;
			   textbookWithIdBuffer.append("<课文 课文Id=\"" + text + "\" 课文名");
			   textbookBuffer.delete(0, textBeginMatcher.end());
			   
			   rightMatcher.reset().find();
			   textbookWithIdBuffer.append(textbookBuffer.substring(0, rightMatcher.end() + 1));
			   textbookBuffer.delete(0, rightMatcher.end() + 1);
			   
			   while (sectionBeginMatcher.lookingAt()) {
				   section++;
				   textbookWithIdBuffer.append("<段 段Id=\"" + section + "\">\n");
				   textbookBuffer.delete(0, sectionBeginMatcher.end() + 1);
				   
				   sectionEndMatcher.reset().find();
				   String sentences = textbookBuffer.substring(0, sectionEndMatcher.start());
				   Matcher sentenceMatcher = sentencePattern.matcher(sentences);
				   int before = 0;
				   while (sentenceMatcher.find()) {
					   sentence++;
					   textbookWithIdBuffer.append("<句 句Id=\"" + sentence + "\">\n");
					   textbookWithIdBuffer.append(sentences.substring(before, sentenceMatcher.end()) + "\n");
					   textbookWithIdBuffer.append("</句>\n");
					   before = sentenceMatcher.end();
				   }
				   
				   textbookWithIdBuffer.append(textbookBuffer.substring(sectionEndMatcher.start(), sectionEndMatcher.end() + 1));
				   textbookBuffer.delete(0, sectionEndMatcher.end() + 1);
				   sentence = 0;
			   }
			   
			   textEndMatcher.reset().find();
			   textbookWithIdBuffer.append(textbookBuffer.substring(0, textEndMatcher.end() + 1));
			   textbookBuffer.delete(0, textEndMatcher.end() + 1);
			   section = 0;
		   }
		   
		   while (exampleSentencesBeginMatcher.lookingAt()) {
			   exampleSentences++;
			   textbookWithIdBuffer.append("<例句 例句Id=\"" + exampleSentences + "\">\n");
			   textbookBuffer.delete(0, exampleSentencesBeginMatcher.end() + 1);
			   
			   while (exampleBeginMatcher.lookingAt()) {
				   example++;
				   textbookWithIdBuffer.append("<例 例Id=\"" + example + "\">\n");
				   textbookBuffer.delete(0, exampleBeginMatcher.end() + 1);
				   
				   exampleEndMatcher.reset().find();
				   textbookWithIdBuffer.append(textbookBuffer.substring(0, exampleEndMatcher.end() + 1));
				   textbookBuffer.delete(0, exampleEndMatcher.end() + 1);
			   }
			   
			   exampleSentencesEndMatcher.reset().find();
			   textbookWithIdBuffer.append(textbookBuffer.substring(0, exampleSentencesEndMatcher.end() + 1));
			   textbookBuffer.delete(0, exampleSentencesEndMatcher.end() + 1);
			   example = 0;
		   }
		   
		   lessonEndMatcher.reset().find();
		   textbookWithIdBuffer.append(textbookBuffer.substring(0, lessonEndMatcher.end() + 1));
		   textbookBuffer.delete(0, lessonEndMatcher.end() + 1);
		   text = 0;
		   exampleSentences = 0;
	   }
	   
	   System.out.println(textbookWithIdBuffer);
   }
}