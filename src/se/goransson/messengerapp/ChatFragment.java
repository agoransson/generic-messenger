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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author andreas
 * 
 */
public class ChatFragment extends Fragment implements Notifiable {

	private static final String TAG = "ChatFragment";

	private TextView otherParty;

	private ListView chat;

	private ArrayList<Message> messages = new ArrayList<Message>();

	private MessageAdapter adapter;

	private EditText text;

	private SendListener sendListener = new SendListener();

	private String sender;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chat, container, false);

		otherParty = (TextView) v.findViewById(R.id.fragment_send_otherparty);
		chat = (ListView) v.findViewById(R.id.fragment_send_chat);
		text = (EditText) v.findViewById(R.id.fragment_chat_text);

		adapter = new MessageAdapter(getActivity(),
				R.layout.message_item_incomming, messages);

		chat.setAdapter(adapter);

		ImageButton send = (ImageButton) v
				.findViewById(R.id.fragment_chat_send);
		send.setOnClickListener(sendListener);

		return v;
	}

	@Override
	public void onResume() {
		MainActivity act = (MainActivity) getActivity();
		otherParty.setText(this.sender);
		loadAllMessages();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	private class SendListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String recipient = sender;
			String message = text.getText().toString();
			text.setText("");

			((MainActivity) getActivity()).sendMessage(recipient, message);
		}
	}

	public void newMessage(String json) {
		try {
			JSONObject msg = new JSONObject(json);
			String sender = msg.getString("sender");
			String message = msg.getString("message");

			boolean newSender = true;

			if (otherParty.getText().toString().equals(sender)) {
				Message m = new OutgoingMessage();
				m.setSender(sender);
				m.setMessage(message);

				messages.add(m);

				adapter.notifyDataSetChanged();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyMessage() {
		// TODO get all chats
	}

	@Override
	public void notifyMessage(long id) {
		MainActivity act = (MainActivity) getActivity();
		DBController db = act.getDatabaseController();

		Message msg = db.getMessage(id);

		String sender = msg.getSender();

//		if (sender.equals(this.sender)) {
			messages.add(msg);
//		}

		adapter.notifyDataSetChanged();

		scrollMyListViewToBottom();
	}

	private void loadAllMessages() {
		MainActivity act = (MainActivity) getActivity();
		DBController db = act.getDatabaseController();

		ArrayList<Message> messages = db.getAllMessagesForSender(sender);

		this.messages.clear();
		this.messages.addAll(messages);

		adapter.notifyDataSetChanged();

		scrollMyListViewToBottom();
	}

	public void setSender(String sender) {
		Log.i(TAG, "setSender: " + sender);
		this.sender = sender;
	}

	private void scrollMyListViewToBottom() {
		chat.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				chat.setSelection(adapter.getCount() - 1);
			}
		});
	}
}
