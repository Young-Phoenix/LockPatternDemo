package cn.net.lockpatterndemo;

import java.util.List;

import com.haibison.android.lockpattern.util.Settings;

import cn.net.xyd.lockpatterndemo.utils.Config;
import cn.net.xyd.lockpatterndemo.utils.Constants;
import cn.net.xyd.lockpatterndemo.utils.SPUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Intent;

public class BaseActivity extends Activity {
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Config.isBackgroundRunning=(boolean)SPUtils.get(getApplicationContext(), Constants.LOCK_PARTTERN_STATE, false)){
			char[] savedPattern = Settings.Security.getPattern(this);
			Intent compare = new Intent(
					LockPatternActivity.ACTION_COMPARE_PATTERN, null, this,
					LockPatternActivity.class);
			compare.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
			//startActivityForResult(compare, REQ_COMPARE_PATTERN);
			startActivity(compare);
		}
	}
	// 如果APP在后台或者锁屏了返回TRUE,如果在前台返回FALSE
	private boolean isBackgroundRunning() {
		String processName = "cn.net.lockpatterndemo";

		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

		if (activityManager == null)
			return false;
		// get running application processes
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo process : processList) {
			if (process.processName.startsWith(processName)) {
				boolean isBackground = process.importance != android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& process.importance != android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
				boolean isLockedState = keyguardManager
						.inKeyguardRestrictedInputMode();
				if (isBackground || isLockedState)
					return true;
				else
					return false;
			}
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Config.isBackgroundRunning=isBackgroundRunning();
	}
}
