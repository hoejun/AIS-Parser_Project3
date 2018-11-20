import java.util.ArrayList;
import java.util.List;

/* 아스키코드 문자 계산 */
public class Static_Bit {
	String bit(List<String> field)
	{
		List<String> list = new ArrayList<String>();
		
		int start = 0;//시작
		int end = 6;//6비트 마지막

		for (int i = 0; i < field.size(); i++) {
			
			while (true) {
				
				if (field.get(i).length() < 6) {
					list.add(field.get(i).substring(start, field.get(i).length()));
					break;
				} else {
					
					list.add(field.get(i).substring(start, end));

					if (end <= field.get(i).length()) {
						start += 6;
						end += 6;
					}

					if (end > field.get(i).length()) {
						end = field.get(i).length();
					}

					if (start > field.get(i).length() || start == end) {
						break;
					}
				}
			}
		}

		String field_result = "";
		for (String str : list) {

			int i = Integer.parseInt(str, 2);
			
			if (i < 32) { i = i + 64; } 
//			if (i > 63) { break; }

			char convert_char = (char) i;
			field_result = field_result + convert_char;
			
		}
		
		field_result = field_result.replace("@", "");//@문자 제거
		field.clear();
		
		return field_result;
	}
}