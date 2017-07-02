package simple;

import java.io.IOException;
import java.io.StringReader;

public class StringReaderTest {
	public static void main(String[] args) throws IOException {
		String x = "P2_1#JCSPF^2-001:[�?,负]/JCSPF^2-002:[�?,负]#1#4#N#JCZQFH@P2_1#JCSPF^2-001:[�?,负]/JCBQC^2-002:[�?-�?,�?-平]#1#4#N#JCZQFH@P2_1#JCSPF^2-001:[�?,负]/JCRQSPF^2-002:[�?,平]#1#4#N#JCZQFH@P2_1#JCSPF^2-001:[�?,负]/JCJQS^2-002:[6,2]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[�?,平]/JCSPF^2-002:[�?,负]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[�?,平]/JCBQC^2-002:[�?-�?,�?-平]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[�?,平]/JCRQSPF^2-002:[�?,平]#1#4#N#JCZQFH@P2_1#JCRQSPF^2-001:[�?,平]/JCJQS^2-002:[6,2]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCSPF^2-002:[�?,负]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCBQC^2-002:[�?-�?,�?-平]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCRQSPF^2-002:[�?,平]#1#4#N#JCZQFH@P2_1#JCJQS^2-001:[6,2]/JCJQS^2-002:[6,2]#1#4#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCSPF^2-002:[�?,负]#1#6#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCBQC^2-002:[�?-�?,�?-平]#1#6#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCRQSPF^2-002:[�?,平]#1#6#N#JCZQFH@P2_1#JCBF^2-001:[4:2,5:1,3:1]/JCJQS^2-002:[6,2]#1#6#N#JCZQFH@P2_1#JCBQC^2-001:[�?-�?,�?-�?,�?-�?,�?-�?,�?-平]/JCSPF^2-002:[�?,负]#1#10#N#JCZQFH@P2_1#JCBQC^2-001:[�?-�?,�?-�?,�?-�?,�?-�?,�?-平]/JCBQC^2-002:[�?-�?,�?-平]#1#10#N#JCZQFH@P2_1#JCBQC^2-001:[�?-�?,�?-�?,�?-�?,�?-�?,�?-平]/JCRQSPF^2-002:[�?,平]#1#10#N#JCZQFH@P2_1#JCBQC^2-001:[�?-�?,�?-�?,�?-�?,�?-�?,�?-平]/JCJQS^2-002:[6,2]#1#10#N#JCZQFH";
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
