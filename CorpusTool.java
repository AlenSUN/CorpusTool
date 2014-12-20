import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorpusTool {

   public static void main(String args[]) {
	   String textbook = "<课 课名=    ><课文 课文名=1 形式=对话><段></段></课文><课文 课文名=2 形式=成段表达><段></段></课文><例句><例></例><例></例></例句></课>";
	   
	   Pattern lessonBeginPattern = Pattern.compile("<课 课名=");
	   Pattern lessonEndPattern = Pattern.compile("<//课>");
	   Pattern textBeginPattern = Pattern.compile("<课文 课文名=");
	   Pattern textEndPattern = Pattern.compile("<//课文>");
	   Pattern sectionBeginPattern = Pattern.compile("<段>");
	   Pattern sectionEndPattern = Pattern.compile("<//段>");
	   Pattern exampleSentencesBeginPattern = Pattern.compile("<例句>");
	   Pattern exampleSentencesEndPattern = Pattern.compile("<//例句>");
	   Pattern exampleBeginPattern = Pattern.compile("<例>");
	   Pattern exampleEndPattern = Pattern.compile("<//例>");
	   
	   int lesson = 0;
	   int text = 0;
	   int section = 0;
	   int exampleSentences = 0;
	   int example = 0;
	   
	   StringBuffer textbookBuffer = new StringBuffer(textbook);
	   StringBuffer textbookWithIdBuffer = new StringBuffer();
	   Matcher lessonBeginMatcher = lessonBeginPattern.matcher(textbookBuffer);
	   if (lessonBeginMatcher.find()) {
		   lesson++;
		   lessonBeginMatcher.appendReplacement(textbookWithIdBuffer, "<课 Id=\"" + lesson + "\" 课名=");
		   textbookBuffer.delete(0, lessonBeginMatcher.end());
		   
		   Matcher textBeginMatcher = textBeginPattern.matcher(textbookBuffer);
		   if (textBeginMatcher.find()) {
			   text++;
			   textBeginMatcher.appendReplacement(textbookWithIdBuffer, "<课文 Id=\"" + text + "\" 课文名=");
			   textbookBuffer.delete(0, textBeginMatcher.end());
		   }
		   
		   
		   System.out.println(textbookBuffer);
		   
		   System.out.println(textbookWithIdBuffer);
		   
	   }
	   
   }
}