package fatdog.uv.logotest;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class F_Main extends Fragment {

	String brand;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public boolean checkApi() {
		int version = android.os.Build.VERSION.SDK_INT;
		
		if (version >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (ViewConfiguration.get(getActivity()).hasPermanentMenuKey()) // 물리버튼이 여부
				return true; // 있다
			else
				return false; // 없다
		}
		else
			return false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v;
		v = inflater.inflate(R.layout.fragment_main, container, false);
		
		// 메인뷰를 구성하고, 리스너를 셋업한다.
		MainView mv = new MainView(getActivity(), getActivity().getIntent(), v);
		mv.viewConstruct();
		return v;
	}
}