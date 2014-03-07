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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author andreas
 * 
 */
public class MessageAdapter extends ArrayAdapter<Message> {

	private static final String TAG = "MessageAdapter";

	List<Message> messages;
	Activity context;
	int layoutId;

	// Row types for client
	public static final int ROW_TYPE_INCOMMING = 0;
	public static final int ROW_TYPE_OUTGOING = 1;

	public MessageAdapter(Activity context, int textViewResourceId,
			List<Message> messages) {
		super(context, textViewResourceId, messages);
		this.context = context;
		this.messages = messages;
		this.layoutId = textViewResourceId;
	}

	@Override
	public int getItemViewType(int position) {
		MainActivity act = (MainActivity) context;

		Message item = getItem(position);
		if (item.getSender().equals(act.getPhonenbr())) {
			return ROW_TYPE_OUTGOING;
		} else {
			return ROW_TYPE_INCOMMING;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message msg = messages.get(position);
		
		TextView message, createdAt;
		
		View row = convertView;
		LayoutInflater inflater = null;
		int type = getItemViewType(position);

		if (row == null) {
			if (type == ROW_TYPE_INCOMMING) {
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.message_item_incomming, null);
			} else {
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.message_item_outgoing, null);
			}
		}
		if (type == ROW_TYPE_INCOMMING) {
			message = (TextView) row.findViewById(R.id.message_item_incomming_message);
			createdAt = (TextView) row.findViewById(R.id.message_item_incomming_time);
		} else {
			message = (TextView) row.findViewById(R.id.message_item_outgoing_message);
			createdAt = (TextView) row.findViewById(R.id.message_item_outgoing_time);
		}

		if (message != null)
			message.setText(msg.getMessage());

		if (createdAt != null)
			createdAt.setText(msg.getCreatedAt());

		return row;
	}

}
