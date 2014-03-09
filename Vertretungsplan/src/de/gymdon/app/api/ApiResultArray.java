package de.gymdon.app.api;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
			return (T[]) copyOf(array, array.length, a.getClass());
		System.arraycopy(array, 0, a, 0, array.length);
		if (a.length > array.length)
			a[array.length] = null;
		return a;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(List<T> list) {
		list.clear();
		for (ApiResult r : array)
			list.add((T) r);
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T, U> T[] copyOf(U[] original, int newLength,
			Class<? extends T[]> newType) {
		T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
				: (T[]) Array
						.newInstance(newType.getComponentType(), newLength);
		System.arraycopy(original, 0, copy, 0,
				Math.min(original.length, newLength));
		return copy;
	}
}
