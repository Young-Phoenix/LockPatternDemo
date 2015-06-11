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
	// ���󴴽�һ���µ�ͼ��
	private static final int REQ_CREATE_PATTERN = 1;
	// �Ƚ����е�ͼ��
	private static final int REQ_COMPARE_PATTERN = 2;
	// ���������ͼ�������˸о�ûɶ��(������)/ .��
	private static final int REQ_VERIFY_PATTERN = 3;
	// �ԱȲ��Ե����ģ�����һ��pattern
	private String testChars = "101b2a675e9fb9546336d5b9ef70418b594184f4";
	private Button openLockPatternBtn, compareButton, verifyModeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// lockpattern��ʹ��Sharepreference�Զ���������
		Settings.Security.setAutoSavePattern(this, true);
		// ����ģʽ������ʾ������������,Ĭ�Ϲر�
		Settings.Display.setStealthMode(this, false);
		// �����Զ���Ľ�����ʽ Ĭ��ʹ��SHA1�㷨ժҪ
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
				Toast.makeText(this, "��ϢժҪ��" + buffer, Toast.LENGTH_SHORT)
						.show();
				Config.isSetLockPattern=true;
				SPUtils.put(getApplicationContext(), Constants.LOCK_PARTTERN_STATE, true);
				// test:101b2a675e9fb9546336d5b9ef70418b594184f4
			}
			break;
		case REQ_COMPARE_PATTERN:
			/*
			 * ע�⣡�����ֿ��ܳ�������ķ��ؽ��
			 */
			switch (resultCode) {
			case RESULT_OK:
				// �û�ͨ����֤
				Log.d(TAG, "user passed");
				break;
			case RESULT_CANCELED:
				// �û�ȡ��
				Log.d(TAG, "user cancelled");
				break;
			case LockPatternActivity.RESULT_FAILED:
				// �û����ʧ��
				Log.d(TAG, "user failed");
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
				Log.d(TAG, "user forgot");
				break;
			}

			/*
			 * ���κ�����£�EXTRA_RETRY_COUNT���������û����Ե�ͼ���Ĵ���
			 */
			int retryCount = data.getIntExtra(
					LockPatternActivity.EXTRA_RETRY_COUNT, 0);
			Log.i(TAG, "�û�������" + retryCount + "����");

			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ��һ���µ�ͼ��
		case R.id.open_button:
			Intent intent = new Intent(
					LockPatternActivity.ACTION_CREATE_PATTERN, null, this,
					LockPatternActivity.class);
			startActivityForResult(intent, REQ_CREATE_PATTERN);
			break;
		// ��ָ�������ͼ�����Ƚ�
		case R.id.compare_button:
			// char[] savedPattern = testChars.toCharArray();
			char[] savedPattern = Settings.Security.getPattern(this);
			Intent compare = new Intent(
					LockPatternActivity.ACTION_COMPARE_PATTERN, null, this,
					LockPatternActivity.class);
			compare.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
			startActivityForResult(compare, REQ_COMPARE_PATTERN);
			break;
		// ���ͼ��
		case R.id.verify_button:
			// ������֤����ʾ������Ĭ����4��
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
