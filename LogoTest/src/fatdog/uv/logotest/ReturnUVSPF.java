package fatdog.uv.logotest;

public class ReturnUVSPF {

	// 위험 메시지를 등급에 따라 구분하기 위해서...
	public String returnGradeText(int value) {
		if (value == 0)
			return "해가 없어요";
		else if (value < 3)
			return "안전";
		else if (value < 6)
			return "보통";
		else if (value < 8)
			return "위험";
		else if (value < 11)
			return "매우위험";
		else
			return "극도위험";
	}

	// 자외선 지수에 따른 SPF지수를 반환
	public String returnSPFGrade(int value) {
		if (value == 0)
			return "--";
		else if (value < 3)
			return "0 - 15";
		else if (value < 6)
			return "15 - 30";
		else if (value < 8)
			return "30 - 45";
		else if (value < 11)
			return "45 - 50";
		else
			return "50";
	}

	// 자외선 지수에 따른 행동사항을 반환
	public String returnSPFText(int value) {
		if (value < 3)
			return "안 발라도 돼요 걱정 마세요";
		else if (value < 6)
			return "아침에 한 번 발라주세요";
		else if (value < 8)
			return "아침에 한 번\n" + "점심에 한 번 발라주세요";
		else if (value < 11)
			return "아침에 한 번\n" + "외출시 2시간 마다 발라주세요";
		else
			return "아침에 한 번\n" + "외출시 2시간 마다 발라주세요";
	}

	// 자외선 색상 반환
	public String returnColor(int value) {
		if (value < 2)
			return "#b7d982";
		else if (value < 4)
			return "#f0d16b";
		else if (value < 6)
			return "#efb864";
		else if (value < 8)
			return "#e79065";
		else if (value < 10)
			return "#e16274";
		else
			return "#e16274";
	}
}