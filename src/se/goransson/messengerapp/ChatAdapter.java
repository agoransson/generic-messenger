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
public class ChatAdapter extends ArrayAdapter<Message> {

	private static final String TAG = "ChatAdapter";

	List<Message> messages;
	Activity context;
	int layoutId;

	public ChatAdapter(Activity context, int textViewResourceId,
			List<Message> messages) {
		super(context, textViewResourceId, messages);
		this.context = context;
		this.messages = messages;
		this.layoutId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message msg = messages.get(position);

		View v = convertView;
		ViewHolder holder;

		if (v == null) {
			LayoutInflater vi = LayoutInflater.from(context);

			v = vi.inflate(layoutId, null);

			holder = new ViewHolder();

			holder.profile = (ImageView) v.findViewById(R.id.chat_item_profile);
			holder.sender = (TextView) v.findViewById(R.id.chat_item_sender);
			holder.message = (TextView) v
					.findViewById(R.id.chat_item_lastmessage);

			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		if (holder.sender != null)
			holder.sender
					.setText("Chat with " + messages.get(position).getSender());

		if (holder.message != null)
			holder.message.setText(messages.get(position).getMessage());

		if (holder.createdAt != null)
			holder.createdAt.setText(messages.get(position).getCreatedAt());

		return v;
	}

	static class ViewHolder {
		ImageView profile;
		TextView sender;
		TextView message;
		TextView createdAt;
	}
}
