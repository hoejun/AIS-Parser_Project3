import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Static_AIS {

	public static void S_Ais(List<String> Ais) {

		//파일 쓰기
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		List<String> AisMessage = new ArrayList<String>();//타입 1 ~ 3 메시지 페이로드
		List<String> sixbit_sum = new ArrayList<String>();//6비트 합

		List<String> type_field = new ArrayList<String>();//타입
		List<String> mmsi_field = new ArrayList<String>();//mmsi
		List<String> N_ship_field = new ArrayList<String>();//선박명
		List<String> call_field = new ArrayList<String>();//콜사인
		List<String> T_ship_field = new ArrayList<String>();//선박 type

		Static_Bit s_bit = new Static_Bit();

		try {

			fw = new FileWriter("static.txt");
			bw = new BufferedWriter(fw);
			
			//정적정보 메시지 출력
			for (int i = 0; i < Ais.size(); i++) {
				AisMessage.add(Ais.get(i));
				System.out.println("정적정보 : " + AisMessage.get(i));
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
				/* 데이터 타입 5 */
				type_field.add(sixbit_sum.get(i).substring(0, 6));
				System.out.println("Type : " + Integer.parseInt(type_field.get(i), 2));
				bw.write("Type : " + Integer.parseInt(type_field.get(i), 2));
				bw.newLine();

				/* 정적정보 */
				/* 정적 MMSI */
				mmsi_field.add(sixbit_sum.get(i).substring(8, 38));
				System.out.println("정적 MMSI : " + Integer.parseInt(mmsi_field.get(i), 2));
				bw.write("정적 MMSI : " + Integer.parseInt(mmsi_field.get(i), 2));
				bw.newLine();

				/* Vessel Name(shipname) */
				N_ship_field.add(sixbit_sum.get(i).substring(112, 232));
				String shipname = s_bit.bit(N_ship_field);
				System.out.println("선박명 : " + shipname);
				bw.write("선박명 : " + shipname);
				bw.newLine();

				/* Call Sign(callsign) */
				call_field.add(sixbit_sum.get(i).substring(70, 112));
				String callsign = s_bit.bit(call_field);
				System.out.println("콜사인 : " + callsign);
				bw.write("콜사인 : " + callsign);
				bw.newLine();

				/* Ship Type(shiptype) */
				T_ship_field.add(sixbit_sum.get(i).substring(232, 240));
				String shiptype = s_bit.bit(T_ship_field);
				System.out.println("선박  Type : " + shiptype);
				bw.write("선박  Type : " + shiptype);
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