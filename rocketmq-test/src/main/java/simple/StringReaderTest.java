package simple;

import java.io.IOException;
import java.io.StringReader;

public class StringReaderTest {
	public static void main(String[] args) throws IOException {
		String x = "P2_1#JCSPF^2-001:[å¹?,è´Ÿ]/JCSPF^2-002:[å¹?,è´Ÿ]#1#4#N#JCZQFH@P2_1#JCSPF^2-001:[å¹?,è´Ÿ]/JCBQC^2-002:[å¹?-å¹?,èƒ?-å¹³]#1#4#N#JCZQFH@P2_1#JCSPF^2-001:[å¹?,è´Ÿ]/JCRQSPF^2-002:[è´?,å¹³]#1#4#N#JCZQFH@P2_1#JCSPF^2-001:[å¹?,è´Ÿ]/JCJQS^2-002:[6,2]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[è´?,å¹³]/JCSPF^2-002:[å¹?,è´Ÿ]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[è´?,å¹³]/JCBQC^2-002:[å¹?-å¹?,èƒ?-å¹³]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[è´?,å¹³]/JCRQSPF^2-002:[è´?,å¹³]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[è´?,å¹³]/JCJQS^2-002:[6,2]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCSPF^2-002:[å¹?,è´Ÿ]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCBQC^2-002:[å¹?-å¹?,èƒ?-å¹³]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCRQSPF^2-002:[è´?,å¹³]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCJQS^2-002:[6,2]#1#4#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCSPF^2-002:[å¹?,è´Ÿ]#1#6#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCBQC^2-002:[å¹?-å¹?,èƒ?-å¹³]#1#6#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCRQSPF^2-002:[è´?,å¹³]#1#6#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCJQS^2-002:[6,2]#1#6#N#JCZQFH@P2_1#JCBQC^2-001:[èƒ?-è´?,è´?-å¹?,èƒ?-å¹?,è´?-è´?,å¹?-å¹³]/JCSPF^2-002:[å¹?,è´Ÿ]#1#10#N#JCZQFH@P2_1#JCBQC^2-001:[èƒ?-è´?,è´?-å¹?,èƒ?-å¹?,è´?-è´?,å¹?-å¹³]/JCBQC^2-002:[å¹?-å¹?,èƒ?-å¹³]#1#10#N#JCZQFH@P2_1#JCBQC^2-001:[èƒ?-è´?,è´?-å¹?,èƒ?-å¹?,è´?-è´?,å¹?-å¹³]/JCRQSPF^2-002:[è´?,å¹³]#1#10#N#JCZQFH@P2_1#JCBQC^2-001:[èƒ?-è´?,è´?-å¹?,èƒ?-å¹?,è´?-è´?,å¹?-å¹³]/JCJQS^2-002:[6,2]#1#10#N#JCZQFH";
		System.out.println(x.length());
		StringReader reader = new StringReader(x);
		char[] c = new char[256];
		int len;
		while( (len = reader.read(c)) != -1){
			 String strRead =  new  String(c,  0 , len);  
             System.out.println(strRead); 
		}
	}
}
