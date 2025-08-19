package com.cjh.watching.watchback.utils;

import java.util.HashMap;

public class Result extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public Result() {
		put("code", 1);
		put("message", "数据接收成功！");
	}
	
	public static Result error(String msg) {
		return error(500, msg);
	}
	
	public static Result error(int code, String msg) {
		Result r = new Result();
		r.put("code", code);
		r.put("message", msg);
		return r;
	}

	public static Result ok(Object value) {
		if(value == null){
			return new Result().put("result",new HashMap(){});
		}
		return new Result().put("result",new HashMap(){{put("data",value);}});
	}

	public static Result okMsg(Object value) {
		Result result = new Result();
		result.put("code", 1);
		result.put("message", value);
		return result;
	}

	public static Result okPage(Object value) {
		return new Result().put("result",value);
	}

	public static Result ok() {
		return new Result();
	}

	public Result put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
