package cn.net.xyd.lockpatterndemo.utils;

import java.util.List;

import android.content.Context;

import com.haibison.android.lockpattern.collect.Lists;
import com.haibison.android.lockpattern.util.IEncrypter;
import com.haibison.android.lockpattern.widget.LockPatternView.Cell;

public class LPEncrypter implements IEncrypter {

	@Override
	public char[] encrypt(Context context, List<Cell> pattern) {
		StringBuilder result = new StringBuilder();
		for (Cell cell : pattern)
			result.append(Integer.toString(cell.getId() + 1)).append('-');

		return result.substring(0, result.length() - 1).toCharArray();
	}

	@Override
	public List<Cell> decrypt(Context context, char[] encryptedPattern) {
		List<Cell> result = Lists.newArrayList();
		String[] ids = new String(encryptedPattern).split("[^0-9]");
		for (String id : ids)
			result.add(Cell.of(Integer.parseInt(id) - 1));

		return result;
	}

}
