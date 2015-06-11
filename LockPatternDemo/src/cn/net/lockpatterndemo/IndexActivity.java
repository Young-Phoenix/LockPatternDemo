package cn.net.lockpatterndemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cn.net.xyd.lockpatterndemo.utils.Config;

import com.haibison.android.lockpattern.util.Settings;

public class IndexActivity extends Activity {
	private static final String TAG = "BaseActivity";
	private static final int REQ_COMPARE_PATTERN = 2;

	// 比较已有的图案

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		Log.v(TAG, "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
		if (Config.isSetLockPattern) {
			char[] savedPattern = Settings.Security.getPattern(this);
			Intent compare = new Intent(
					LockPatternActivity.ACTION_COMPARE_PATTERN, null, this,
					LockPatternActivity.class);
			compare.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
			startActivityForResult(compare, REQ_COMPARE_PATTERN);
		}else{
			start();
		}

	}

	private void start(){
		Handler handler = new Handler();
		handler.postDelayed(thread, 2000);
	}
	private Runnable thread = new Runnable() {
		
		@Override
		public void run() {
			IndexActivity.this.startActivity(new Intent(IndexActivity.this, MainActivity.class));
			IndexActivity.this.finish();
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_COMPARE_PATTERN:
			/*
			 * 注意！有四种可能出现情况的返回结果
			 */
			switch (resultCode) {
			case RESULT_OK:
				// 用户通过验证
				Log.d(TAG, "user passed");
				start();
				break;
			case RESULT_CANCELED:
				// 用户取消
				Log.d(TAG, "user cancelled");
				break;
			case LockPatternActivity.RESULT_FAILED:
				// 用户多次失败
				Log.d(TAG, "user failed");
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
				Log.d(TAG, "user forgot");
				break;
			}

			/*
			 * 在任何情况下，EXTRA_RETRY_COUNT都代表着用户尝试的图案的次数
			 */
			int retryCount = data.getIntExtra(
					LockPatternActivity.EXTRA_RETRY_COUNT, 0);
			Log.i(TAG, "用户尝试了" + retryCount + "次数");

			break;
		}
	}
}
