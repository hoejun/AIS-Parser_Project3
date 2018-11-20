import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Dynamic_AIS {
	
	public static void D_Ais(List<String> Ais) {
		
		//파일 쓰기
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		List<String> AisMessage = new ArrayList<String>();//타입 1 ~ 3 메시지 페이로드
		List<String> sixbit_sum = new ArrayList<String>();//6비트 합
		
		List<String> type_field = new ArrayList<String>();//타입
		List<String> mmsi_field = new ArrayList<String>();//mmsi
		List<String> lat_field = new ArrayList<String>();//위도
		List<String> lon_field = new ArrayList<String>();//경도
		List<String> course_field = new ArrayList<String>();//COG
		List<String> sog_field = new ArrayList<String>();//SOG
		List<String> hdg_field = new ArrayList<String>();//true heading

		try {
			
			fw = new FileWriter("dynamic.txt");
			bw = new BufferedWriter(fw);
			
			//동적정보 메시지 출력
			for (int i = 0; i < Ais.size(); i++) {
				AisMessage.add(Ais.get(i));
				System.out.println("동적정보 : " + AisMessage.get(i));
			}
			
			//아스키코드 2진수 6비트 변환 작업
			for (int i = 0; i < AisMessage.size(); i++) {
				
				String binary_sum = "";
				
				for (int j = 0; j < AisMessage.get(i).length(); j++)
				{
					
					int ascii_payload;
					ascii_payload = AisMessage.get(i).charAt(j);
					
					//아스키코드 10진수 계산
					if (ascii_payload <= 87) {
						ascii_payload = ascii_payload - 48;//ASCII (48 ~ 87) - 48
					} else if (ascii_payload >= 96) {
						ascii_payload = ascii_payload - 56;//ASCII (96 ~ 119) - 56
					}
					
					// 6비트 계산
					String binnary = Integer.toBinaryString(ascii_payload);//2진수 변환
					int sixbit_binnary = Integer.parseInt(binnary);//정수 변환
					String sixbit = String.format("%06d", sixbit_binnary);//6자리 중 빈칸은 0으로 채우기

					binary_sum = binary_sum.concat(sixbit);
				}
				sixbit_sum.add(binary_sum);
				binary_sum = "";
				
			}
			
			System.out.println();
			
			/* 데이터 디코딩 */
			for (int i = 0; i < sixbit_sum.size(); i++) {
				
				/* AIS 타입 출력 */
				/* 데이터 타입 1 ~ 3 */
				type_field.add(sixbit_sum.get(i).substring(0, 6));
				System.out.println("Type : " + Integer.parseInt(type_field.get(i), 2));
				bw.write("Type : " + Integer.parseInt(type_field.get(i), 2));
				bw.newLine();
				
				/* 동적정보 */
				/* 동적 MMSI */
				mmsi_field.add(sixbit_sum.get(i).substring(8, 38));
				System.out.println("동적 MMSI : " + Integer.parseInt(mmsi_field.get(i),2));
				bw.write("동적 MMSI : " + Integer.parseInt(mmsi_field.get(i),2));
				bw.newLine();
				
				/* Latitude(lat) */  
				lat_field.add(sixbit_sum.get(i).substring(89, 116));
				int lat = Integer.parseInt(lat_field.get(i),2);
				double dou_lat = (double) lat / 600000.0;
				System.out.println("위도 : " + dou_lat);
				bw.write("위도 : " + dou_lat);
				bw.newLine();
				
				/* Longitude(lon) */ 
				lon_field.add(sixbit_sum.get(i).substring(61, 89));
				int lon = Integer.parseInt(lon_field.get(i), 2);
				double dou_lon = (double) lon / 600000.0;
				System.out.println("경도 : " + dou_lon);
				bw.write("경도 : " + dou_lon);
				bw.newLine();
				
				/* Course Over Ground-COG(course) */ 
				course_field.add(sixbit_sum.get(i).substring(116, 128));
				System.out.println("COG : " + Integer.parseInt(course_field.get(i),2) / 10.0);
				bw.write("COG : " + Integer.parseInt(course_field.get(i),2) / 10.0);
				bw.newLine();
				
				/* Speed Over Ground-SOG(speed) */ 
				sog_field.add(sixbit_sum.get(i).substring(50, 60));
				System.out.println("SOG : " + Integer.parseInt(sog_field.get(i),2) / 10.0);
				bw.write("SOG : " + Integer.parseInt(sog_field.get(i),2) / 10.0);
				bw.newLine();
				
				/* True Heading-HDG(heading) */ 
				hdg_field.add(sixbit_sum.get(i).substring(128, 137));
				System.out.println("True Heading : " + Integer.parseInt(hdg_field.get(i),2));
				bw.write("True Heading : " + Integer.parseInt(hdg_field.get(i),2));
				bw.newLine();
				bw.newLine();
				
				bw.flush();
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { fw.close(); } catch ( Exception err ) {}
			try { bw.close(); } catch ( Exception err ) {}
		}
	}
}