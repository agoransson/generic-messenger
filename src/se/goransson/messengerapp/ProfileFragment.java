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

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author andreas
 * 
 */
public class ProfileFragment extends Fragment implements Notifiable {

	private static final String TAG = "ProfileFragment";

	private TextView phoneNbr;

	private ArrayList<Message> chats = new ArrayList<Message>();

	private ListView chatView;
	private ChatAdapter adapter;

	private ChatClickListener itemClickListener = new ChatClickListener();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, container, false);

		phoneNbr = (TextView) v.findViewById(R.id.profile_fragment_username);
		chatView = (ListView) v.findViewById(R.id.profile_fragment_chatlist);

		adapter = new ChatAdapter(getActivity(), R.layout.chat_item, chats);
		chatView.setAdapter(adapter);

		chatView.setOnItemClickListener(itemClickListener);

		Bundle args = getArguments();
		phoneNbr.setText(args.getString("phonenbr", "not found"));
		
		return v;
	}

	@Override
	public void onResume() {
		MainActivity act = (MainActivity) getActivity();
		act.subscribe();

		loadAllChats();
		super.onResume();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.chat_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if( item.getItemId() == R.id.new_message ){
			((MainActivity) getActivity()).openContacts();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class ChatClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
			((MainActivity) getActivity()).openChat(chats.get(pos).getSender());
		}
	}

	@Override
	public void notifyMessage() {
		// TODO get all messages for this conversation...
	}

	@Override
	public void notifyMessage(long id) {
		MainActivity act = (MainActivity) getActivity();
		DBController db = act.getDatabaseController();

		Message msg = db.getMessage(id);

		String sender = msg.getSender();

		int index = -1;
		
		for (int i = 0; i < chats.size(); i++) {
			if (chats.get(i).getSender().equals(sender)) {
				index = i;
				break;
			}
		}
		
		if( index != -1 ){
			chats.remove(index);
			chats.add(index, msg);
		}else{
			chats.add(msg);
		}

		adapter.notifyDataSetChanged();
	}

	private void loadAllChats() {
		MainActivity act = (MainActivity) getActivity();
		DBController db = act.getDatabaseController();

		ArrayList<Message> messages = db.getAllChats();
		
		chats.clear();
		chats.addAll(messages);

		adapter.notifyDataSetChanged();
	}

}
