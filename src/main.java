import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
	
	public static void main(String[] args) {
			
		//추가 데이터 질문
		char yes_or_no;
		Scanner sc = new Scanner(System.in);
		
		List<String> array_ais = new ArrayList<String>();//AIS 메시지
		List<String> AIVDM_ANVDM = new ArrayList<String>();//!AIVDM, !ANVDM 담을 변수
		List<String> data_payload = new ArrayList<String>();//분류된 데이터 페이로드
		List<String> sixbit_sum = new ArrayList<String>();//아스키코드 6비트 변수 합

		List<String> type_field = new ArrayList<String>();//모든 타입 값
		List<String> field_1_2_3 = new ArrayList<String>();//타입 1 ~ 3 데이터 페이로드
		List<String> two_field = new ArrayList<String>();//2개의 AIS 메시지
		List<String> two_field_ais = new ArrayList<String>();//타입 5 AIS 메시지
		List<String> two_data_payload = new ArrayList<String>();//타입 5 데이터 페이로드
		List<String> field_5 = new ArrayList<String>();//타입 5 데이터 페이로드

		String S_data_payload = null;//스트링 데이터 페이로드
		
		try {		
			
			//소켓 생성 ip "xxx.xxx.x.xxx", 포트번호 8030
			Socket socket = new Socket("xxx.xxx.x.xxx", 8030);
			boolean connect = socket.isConnected();//연결
					
			//서버 접속 확인
			if (connect) {
				System.out.println("서버 연결 성공");
			} else {
				System.out.println("서버 연결 실패");
			}
			
			DataInputStream read = new DataInputStream(socket.getInputStream());//AIS 읽기

			//데이터 수신 시작
			while (true) {
				
				String ais = read.readLine();//수신 데이터 저장
				System.out.println(ais);//AIS 데이터 출력
				
				array_ais.add(ais);
				
				//AIS 신호 질문
				if (array_ais.size() > 200) {
					
					System.out.println("계속 신호를 받아 올것입니까? y/n");
					yes_or_no = sc.next().charAt(0);
					
					if (yes_or_no == 'y') { continue; }
					else if (yes_or_no == 'n') {
						
						System.out.println("신호 종료 \n");
						socket.close();
						sc.close();
						break;
						
					}
				}//신호 질문 끝
			}//데이터 수신 끝
			
			System.out.println("\n\n AIS 신호 받아오기");
			
			//AIVDM, ANVDM 분류
			for (int i = 0; i < array_ais.size(); i++) {
				//AIS 메시지가 있으면 검사
				if (array_ais.get(i).length() > 0) {
					if (array_ais.get(i).substring(0, 6).equals("!AIVDM") || 
							array_ais.get(i).substring(0, 6).equals("!ANVDM")) {
						
						AIVDM_ANVDM.add(array_ais.get(i));
					}
				}
			}
			
			//AIVDM, ANVDM 개수
			for (int i = 0; i < AIVDM_ANVDM.size(); i++) {
				System.out.println(AIVDM_ANVDM.get(i));
			}
			
			System.out.println(AIVDM_ANVDM.size() + "개 메시지");
						
			//AIS 데이터 페이로드 분류
			for (int i = 0; i < AIVDM_ANVDM.size(); i++) {
				
				String[] arr_split = AIVDM_ANVDM.get(i).split(",");//,로 구분
				
				S_data_payload = arr_split[arr_split.length-4];//데이터 페이로드 위치 
				data_payload.add(S_data_payload);
				
			}
			
			System.out.println();
			
			//아스키코드 2진수 6비트 변환 작업
			for (int i = 0; i < data_payload.size(); i++) {
				
				String sum = "";
				
				for (int j = 0; j < data_payload.get(i).length(); j++) {
					
					int ascii_payload;
					ascii_payload = data_payload.get(i).charAt(j);
					
					//아스키코드 10진수 계산
					if (ascii_payload <= 87) {
						ascii_payload = ascii_payload - 48;//ASCII (48 ~ 87) - 48
					} else if (ascii_payload >= 96) {
						ascii_payload = ascii_payload - 56;//ASCII (96 ~ 119) - 56
					}

					String binnary = Integer.toBinaryString(ascii_payload);//2진수 변환
					int sixbit_binnary = Integer.parseInt(binnary);//정수 변환
					String sixbit = String.format("%06d", sixbit_binnary);//6자리 중 빈칸은 0으로 채우기
					
					sum = sum.concat(sixbit);
				}
				sixbit_sum.add(sum);
				sum = "";//초기화
			}
			
			//타입 분류 작업
			for (int i = 0; i < sixbit_sum.size(); i++) {

				type_field.add(sixbit_sum.get(i).substring(0, 6));//타입 값 구분
				
				int dec = Integer.parseInt(type_field.get(i), 2);
				
				//Type 1 ~ 3
				if (0< dec && dec < 5) {
					field_1_2_3.add(data_payload.get(i));
				}
			
				two_field.add(AIVDM_ANVDM.get(i).substring(7,8));
				
				//Type 5
				if (two_field.get(i).equals("2")) {
					two_field_ais.add(AIVDM_ANVDM.get(i));
				}
			}
			
			//Type 5 데이터 페이로드 2개씩 합치기
			int count = 0;
			String two_sum = "";
			for (int i = 0; i < two_field_ais.size(); i++) {
				String[] arr_split = two_field_ais.get(i).split(",");//,로 구분
				S_data_payload = arr_split[arr_split.length-4];//데이터 페이로드 위치 
				two_data_payload.add(S_data_payload);
				two_sum = two_sum.concat(two_data_payload.get(i));//데이터 합치기

				++count;
				if (count == 2) {//카운터가 2이면 list에 값을 초기화 후 다음 list 초기화
					field_5.add(two_sum);
					two_sum = "";//문자열 비우기
					count=0;
				}
			}
					
			Dynamic_AIS.D_Ais(field_1_2_3);//동적 정보
			Static_AIS.S_Ais(field_5);//정적 정보
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
