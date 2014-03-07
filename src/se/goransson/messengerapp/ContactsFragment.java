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

import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author andreas
 * 
 */
public class ContactsFragment extends ListFragment {

	private static final String TAG = "ContactFragment";

	private ArrayList<Contact> contacts = new ArrayList<Contact>();

	private ArrayAdapter<Contact> adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new ArrayAdapter<Contact>(getActivity(),
				android.R.layout.simple_list_item_1, contacts);
		setListAdapter(adapter);

		getAllContacts();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String phoneNbr = contacts.get(position).getPhone();

		// By default, use your own country code
		TelephonyManager mTelephonyManager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String localCode = mTelephonyManager.getNetworkCountryIso()
				.toUpperCase();

		phoneNbr = MainActivity.fixInternationalPhoneForSubscribe(MainActivity
				.getInternaltionalNumber(phoneNbr, localCode));

//		Toast.makeText(getActivity(), phoneNbr, Toast.LENGTH_SHORT).show();
		
		MainActivity act = (MainActivity) getActivity();
		act.startChat(phoneNbr);
	}

	private void getAllContacts() {
		contacts.clear();

		Cursor phones = getActivity().getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			Contact c = new Contact(name, phoneNumber);
			contacts.add(c);

		}
		phones.close();

		adapter.notifyDataSetChanged();
	}
}
