package com.inf1315.vertretungsplan.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.json.*;

public class ApiResultArray extends ApiResult {
	private ApiResult[] array = new ApiResult[0];

	public ApiResultArray(JSONArray arr, Class<? extends ApiResult> type)
			throws JSONException {
		array = new ApiResult[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			try {
				array[i] = type.getConstructor(JSONObject.class).newInstance(o);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public ApiResult[] getArray() {
		return array;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getArray(T[] a) {
		if (a.length < array.length)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(array, array.length, a.getClass());
		System.arraycopy(array, 0, a, 0, array.length);
		if (a.length > array.length)
			a[array.length] = null;
		return a;
	}
}
