package cn.net.lockpatterndemo;

import cn.net.xyd.lockpatterndemo.utils.Config;
import cn.net.xyd.lockpatterndemo.utils.Constants;
import cn.net.xyd.lockpatterndemo.utils.SPUtils;

import com.haibison.android.lockpattern.util.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "MainActivity";
	// 请求创建一个新的图案
	private static final int REQ_CREATE_PATTERN = 1;
	// 比较已有的图案
	private static final int REQ_COMPARE_PATTERN = 2;
	// 生成随机的图案（个人感觉没啥用(＃－－)/ .）
	private static final int REQ_VERIFY_PATTERN = 3;
	// 对比测试的密文，代表一个pattern
	private String testChars = "101b2a675e9fb9546336d5b9ef70418b594184f4";
	private Button openLockPatternBtn, compareButton, verifyModeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// lockpattern会使用Sharepreference自动保存密文
		Settings.Security.setAutoSavePattern(this, true);
		// 隐身模式：不显示勾画的连接线,默认关闭
		Settings.Display.setStealthMode(this, false);
		// 启用自定义的解析方式 默认使用SHA1算法摘要
		// <activity
		// android:name="com.haibison.android.lockpattern.LockPatternActivity"
		// android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
		// android:screenOrientation="user"
		// android:theme="@style/Alp.42447968.Theme.Dialog.Dark" >
		// <meta-data
		// android:name="encrypterClass"
		// android:value="...full.qualified.name.to.your.LPEncrypter" />
		// </activity>

		// AlpSettings.Security.setEncrypterClass(this, LPEncrypter.class);
		openLockPatternBtn = (Button) findViewById(R.id.open_button);
		openLockPatternBtn.setOnClickListener(this);
		compareButton = (Button) findViewById(R.id.compare_button);
		compareButton.setOnClickListener(this);
		verifyModeButton = (Button) findViewById(R.id.verify_button);
		verifyModeButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_CREATE_PATTERN:
			if (resultCode == RESULT_OK) {
				char[] pattern = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				StringBuffer buffer = new StringBuffer();
				for (char c : pattern) {
					buffer.append(c);
				}
				Log.i(TAG, "result=>" + buffer.toString());
				Toast.makeText(this, "消息摘要：" + buffer, Toast.LENGTH_SHORT)
						.show();
				Config.isSetLockPattern=true;
				SPUtils.put(getApplicationContext(), Constants.LOCK_PARTTERN_STATE, true);
				// test:101b2a675e9fb9546336d5b9ef70418b594184f4
			}
			break;
		case REQ_COMPARE_PATTERN:
			/*
			 * 注意！有四种可能出现情况的返回结果
			 */
			switch (resultCode) {
			case RESULT_OK:
				// 用户通过验证
				Log.d(TAG, "user passed");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 打开一个新的图案
		case R.id.open_button:
			Intent intent = new Intent(
					LockPatternActivity.ACTION_CREATE_PATTERN, null, this,
					LockPatternActivity.class);
			startActivityForResult(intent, REQ_CREATE_PATTERN);
			break;
		// 对指定保存的图案做比较
		case R.id.compare_button:
			// char[] savedPattern = testChars.toCharArray();
			char[] savedPattern = Settings.Security.getPattern(this);
			Intent compare = new Intent(
					LockPatternActivity.ACTION_COMPARE_PATTERN, null, this,
					LockPatternActivity.class);
			compare.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
			startActivityForResult(compare, REQ_COMPARE_PATTERN);
			break;
		// 随机图案
		case R.id.verify_button:
			// 设置验证的显示次数，默认是4次
			/*
			 * Settings.Display.setCaptchaWiredDots(this, 9); Intent
			 * verifyIntent = new
			 * Intent(LockPatternActivity.ACTION_VERIFY_CAPTCHA, null, this,
			 * LockPatternActivity.class); startActivityForResult(verifyIntent,
			 * REQ_VERIFY_PATTERN);
			 */
			startActivity(new Intent(this, TextActivity.class));
			break;
		}
	}

}
