import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorpusTool {

   public static void main(String args[]) {
	   String textbook = "<课 课名=    ><课文 课文名=1 形式=对话><段></段></课文><课文 课文名=2 形式=成段表达><段></段></课文><例句><例></例><例></例></例句></课>";
	   
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
	   
	   int lesson = 0;
	   int text = 0;
	   int section = 0;
	   int exampleSentences = 0;
	   int example = 0;
	   
	   StringBuffer textbookBuffer = new StringBuffer(textbook);
	   StringBuffer textbookWithIdBuffer = new StringBuffer();
	   
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
	   
	   while (textbookBuffer.length() != 0 && lessonBeginMatcher.lookingAt()) {
		   lesson++;
		   textbookWithIdBuffer.append("<课 Id=\"" + lesson + "\" 课名");
		   textbookBuffer.delete(0, lessonBeginMatcher.end());
		   
		   rightMatcher.find();
		   textbookWithIdBuffer.append(textbookBuffer.substring(0, rightMatcher.end()));
		   textbookBuffer.delete(0, rightMatcher.end());
		   
		   while (textBeginMatcher.lookingAt()) {
			   text++;
			   textbookWithIdBuffer.append("<课文 Id=\"" + text + "\" 课文名");
			   textbookBuffer.delete(0, textBeginMatcher.end());
			   
			   rightMatcher.find();
			   textbookWithIdBuffer.append(textbookBuffer.substring(0, rightMatcher.end()));
			   textbookBuffer.delete(0, rightMatcher.end());
			   
			   while (sectionBeginMatcher.lookingAt()) {
				   section++;
				   textbookWithIdBuffer.append("<段 Id=\"" + section + "\">");
				   textbookBuffer.delete(0, sectionBeginMatcher.end());
				   
				   sectionEndMatcher.reset().find();
				   textbookWithIdBuffer.append(textbookBuffer.substring(0, sectionEndMatcher.end()));
				   textbookBuffer.delete(0, sectionEndMatcher.end());
			   }
			   
			   textEndMatcher.reset().find();
			   textbookWithIdBuffer.append(textbookBuffer.substring(0, textEndMatcher.end()));
			   textbookBuffer.delete(0, textEndMatcher.end());
			   section = 0;
		   }
		   
		   while (exampleSentencesBeginMatcher.lookingAt()) {
			   exampleSentences++;
			   textbookWithIdBuffer.append("<例句 Id=\"" + exampleSentences + "\">");
			   textbookBuffer.delete(0, exampleSentencesBeginMatcher.end());
			   
			   while (exampleBeginMatcher.lookingAt()) {
				   example++;
				   textbookWithIdBuffer.append("<例 Id=\"" + example + "\">");
				   textbookBuffer.delete(0, exampleBeginMatcher.end());
				   
				   exampleEndMatcher.reset().find();
				   textbookWithIdBuffer.append(textbookBuffer.substring(0, exampleEndMatcher.end()));
				   textbookBuffer.delete(0, exampleEndMatcher.end());
			   }
			   
			   exampleSentencesEndMatcher.find();
			   textbookWithIdBuffer.append(textbookBuffer.substring(0, exampleSentencesEndMatcher.end()));
			   textbookBuffer.delete(0, exampleSentencesEndMatcher.end());
			   example = 0;
		   }
		   
		   lessonEndMatcher.find();
		   textbookWithIdBuffer.append(textbookBuffer.substring(0, lessonEndMatcher.end()));
		   textbookBuffer.delete(0, lessonEndMatcher.end());
		   text = 0;
		   exampleSentences = 0;
	   }
	   
	   System.out.println(textbookWithIdBuffer);
   }
}