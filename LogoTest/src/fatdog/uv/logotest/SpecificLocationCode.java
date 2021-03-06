package fatdog.uv.logotest;

public class SpecificLocationCode {
	private Long[] map;

	// 지역검색에 사용되는 특정 위치 코드들
	public SpecificLocationCode() {
		map = new Long[64];
		// 서울부산대구광주대전인천울산제주
		map[1] = 1100000000L;
		map[2] = 2600000000L;
		map[3] = 2700000000L;
		map[4] = 2900000000L;
		map[5] = 3000000000L;
		map[6] = 2800000000L;
		map[7] = 3100000000L;
		map[8] = 5011000000L;
		//강원도 춘천원주강릉동해태백속초
		map[9] = 4211000000L;
		map[10] = 4213000000L;
		map[11] = 4215000000L;
		map[12] = 4217000000L;
		map[13] = 4219000000L;
		map[14] = 4221000000L;
		//충청남도 천안공주보령아산서산논산계룡
		map[15] = 4413000000L;
		map[16] = 4415000000L;
		map[17] = 4418000000L;
		map[18] = 4420000000L;
		map[19] = 4421000000L;
		map[20] = 4423000000L;
		map[21] = 4425000000L;
		//충청북도 청주
		map[22] = 4311000000L;
		//경상남도 진주통영사천김해거제
		map[23] = 4817000000L;
		map[24] = 4822000000L;
		map[25] = 4824000000L;
		map[26] = 4825000000L;
		map[27] = 4831000000L;
		//경기도 수원성남의정부안양부천평택동두천안산고양
		map[28] = 4111000000L;
		map[29] = 4728000000L;	
		map[30] = 4115000000L;
		map[31] = 4111700000L;
		map[32] = 4119000000L;
		map[33] = 4122000000L;
		map[34] = 4125000000L;
		map[35] = 4127000000L;
		map[36] = 4128000000L;
		//과천구리남양주오산시흥의왕하남용인파주이천안성
		map[37] = 4129000000L;
		map[38] = 4131000000L;
		map[39] = 4136000000L;
		map[40] = 4137000000L;
		map[41] = 4139000000L;
		map[42] = 4143000000L;
		map[43] = 4145000000L;
		map[44] = 4146000000L;
		map[45] = 4148000000L;
		map[46] = 4150000000L;
		map[47] = 4155000000L;
		//김포화성광주양주포천
		map[48] = 4157000000L;
		map[49] = 4159000000L;
		map[50] = 4161000000L;
		map[51] = 4163000000L;
		map[52] = 4165000000L;
		//전라북도 전주군산익산
		map[53] = 4511000000L;
		map[54] = 4513000000L;
		map[55] = 4514000000L;
		//전라남도 목포여수순천
		map[56] = 4611000000L;
		map[57] = 4613000000L;
		map[58] = 4615000000L;
		//경상북도 포항경주안동구미문경
		map[59] = 4711000000L;
		map[60] = 4713000000L;
		map[61] = 4717000000L;
		map[62] = 4719000000L;
		map[63] = 4728000000L;	
	}

	// 해당 index에 대해서 지역코드를 반환해준다.
	public Long areaNum(int index) {
		return map[index];
	}
}