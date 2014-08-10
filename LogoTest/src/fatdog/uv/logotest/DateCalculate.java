package fatdog.uv.logotest;

import java.util.Calendar;
import java.util.Date;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class DateCalculate {
	private int curMonth; // 오늘
	private int curDay;
	private int tomMonth; // 내일
	private int tomDay;
	private int afterMonth; // 내일 모레
	private int afterDay;
	private TextView tomorrow;
	private TextView afterTomorrow;

	public DateCalculate(View v, FragmentActivity act, int tomorrowUV, int afterTomorrowUV) {
		tomorrow = (TextView) v.findViewById(R.id.tomorrow_date);
		afterTomorrow = (TextView) v.findViewById(R.id.after_tomorow_date);
	}
	
	/* if문에서 저러한 계산을 하는 이유는 
	 * 28, 30, 31일 이후에 달이 바뀌게 되는데
	 * 그때 바뀌는 달을 계산 하기 위해서 만들었다. */
	
	public void calculateDate() {
		Calendar c = null;
		c = Calendar.getInstance();
		c.setTime(new Date());

		// 오늘 날짜 구하기
		curDay = c.get(Calendar.DATE); // 오늘 일
		curMonth = c.get(Calendar.MONTH) + 1; // 오늘 월

		// 내일 날짜 구하기
		c.add(Calendar.DATE, 1);
		tomDay = c.get(Calendar.DATE);
		tomMonth = curMonth;

		if (curDay == 28 && tomDay == 1)
			tomMonth = curMonth + 1;
		else if (curDay == 30 && tomDay == 1)
			tomMonth = curMonth + 1;
		else if (curDay == 31 && tomDay == 1)
			tomMonth = curMonth + 1;
		// tomorrow.setText(tomMonth + " / " + tomDay);
		tomorrow.setText("내일");
		
		// 내일 모레 날짜 구하기
		c.add(Calendar.DATE, 1);
		afterDay = c.get(Calendar.DATE);
		afterMonth = tomMonth;

		if (tomDay == 28 && afterDay == 1)
			afterMonth = tomMonth + 1;
		else if (tomDay == 30 && afterDay == 1)
			afterMonth = tomMonth + 1;
		else if (tomDay == 31 && afterDay == 1)
			afterMonth = tomMonth + 1;
		// afterTomorrow.setText(afterMonth + " / " + afterDay);
		afterTomorrow.setText("모레");
	}
}