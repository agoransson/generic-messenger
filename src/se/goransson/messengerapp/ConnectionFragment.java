package se.goransson.messengerapp;

/*
 * Copyright 2014 Andreas Goransson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 
 * @author andreas
 * 
 */
public class ConnectionFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_connect, container, false);

		Button btn = (Button) v.findViewById(R.id.connect_fragment_button);
		btn.setOnClickListener(connectListener);
		return v;
	}

	@Override
	public void onResume() {
		MainActivity act = (MainActivity) getActivity();
		act.disconnect();

		super.onResume();
	}

	private OnClickListener connectListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MainActivity act = (MainActivity) getActivity();
			act.connect();
		}
	};
}
