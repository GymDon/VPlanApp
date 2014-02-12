package com.inf1315.vertretungsplan.fragments;

import com.inf1315.vertretungsplan.MensaAdapter;
import com.inf1315.vertretungsplan.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MensaFragment extends Fragment {

	private MensaAdapter mensaAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mensaAdapter = new MensaAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_mensa, container, false);
		ListView lv = (ListView) rootView.findViewById(R.id.mensa_ListView);
		lv.setAdapter(mensaAdapter);
		return rootView;
	}
}
