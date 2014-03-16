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
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author andreas
 * 
 */
public class ConnectionFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_connect, container, false);

		Button btn = (Button) v.findViewById(R.id.connect_fragment_button);
		btn.setOnClickListener(connectListener);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.set_broker:
			showDialog();
			return true;
			
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void showDialog() {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage("Set the MQTT server").setTitle("Server");
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		builder.setView(input);
		
		input.setText(getBroker());
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setBroker(input.getText().toString());
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
	
	private String getBroker() {
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		return prefs.getString("broker", "mqtt.mah.se");
	}
	
	private void setBroker(String broker){
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString("broker", broker);
		edit.commit();
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
