package framework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseInfo {

	private int code;

	private String msg;

	private Object result;

	public static BaseInfo success(Object o) {
		BaseInfo baseInfo = new BaseInfo();
		if (o instanceof Entry) {
			Entry entry = (Entry)o;
			baseInfo.setCode(entry.code);
			baseInfo.setMsg(entry.msg);
		} else {
			baseInfo.setResult(o);
		}
		return baseInfo;
	}

	public static BaseInfo error(Throwable throwable) {
		BaseInfo baseInfo = new BaseInfo();
		baseInfo.setCode(-1);
		baseInfo.setMsg(throwable.getMessage());
		return baseInfo;
	}

	public static BaseInfo error(String msg) {
		BaseInfo baseInfo = new BaseInfo();
		baseInfo.setCode(-1);
		baseInfo.setMsg(msg);
		return baseInfo;
	}

	@Data
	@AllArgsConstructor
	public static class Entry {
		private int code;
		private String msg;
	}
}
