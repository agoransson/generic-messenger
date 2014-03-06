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

import org.json.JSONException;
import org.json.JSONObject;

import se.goransson.qatja.MQTTConnectionConstants;
import se.goransson.qatja.MQTTConstants;
import se.goransson.qatja.QatjaService;
import se.goransson.qatja.QatjaService.QatjaBinder;
import se.goransson.qatja.messages.MQTTPublish;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * 
 * @author andreas
 * 
 */
public class MainActivity extends Activity implements MQTTConnectionConstants,
		MQTTConstants {

	protected static final String TAG = "GenericMessenger";

	private Controller mController;

	private DBController mDbController;

	private QatjaService client;
	private boolean isBound;

	private Handler mHandler = new Handler(new MQTTCallback());

	private static String topicBase = "/genericmessenger/";
	private String phoneNbr = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mController = new Controller(this);

		mDbController = new DBController(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent service = new Intent(MainActivity.this, QatjaService.class);
		bindService(service, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		mController.showConnectionFragment();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO - disconnect?
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(connection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			client = ((QatjaBinder) binder).getService();
			isBound = true;

			client.setHandler(mHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
		}
	};

	private class MQTTCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case STATE_CHANGE:
				Log.i(TAG, "STATE CHANGE " + msg.arg1);
				switch (msg.arg1) {
				case STATE_NONE:
					Toast.makeText(MainActivity.this, "Not connected",
							Toast.LENGTH_SHORT).show();
					mController.showConnectionFragment();
					return true;

				case STATE_CONNECTING:
					Toast.makeText(MainActivity.this, "Trying to connect...",
							Toast.LENGTH_SHORT).show();
					return true;

				case STATE_CONNECTED:
					Toast.makeText(MainActivity.this, "Yay! Connected!",
							Toast.LENGTH_SHORT).show();
					mController.showProfileFragment(phoneNbr);
					return true;

				case STATE_CONNECTION_FAILED:
					Toast.makeText(MainActivity.this, "Connection failed",
							Toast.LENGTH_SHORT).show();
					return true;

				}
				return true;

			case PUBLISH:
				MQTTPublish publish = (MQTTPublish) msg.obj;

				String encoded = new String(publish.getPayload());

				try {
					JSONObject json = new JSONObject(encoded);
					String sender = json.getString("sender");
					String message = json.getString("message");

					long id = mDbController.putMessage(sender, message);

//					Toast.makeText(MainActivity.this,
//							"Added message with id " + id, Toast.LENGTH_SHORT)
//							.show();

					mController.notifyMessage(id);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				return true;

			default:
				return false;
			}
		}
	};
	
	protected void connect() {
		TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneNbr = mTelephonyManager.getLine1Number();

		if (phoneNbr != null) {
			client.setHost("195.178.228.45");
			client.setPort(1883);
			client.setId(phoneNbr);
			client.setKeepAlive(7000);
			client.connect();
		} else {
			Toast.makeText(this, R.string.failed_phone, Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void subscribe() {
		StringBuilder topic = new StringBuilder();
		topic.append(topicBase);
		topic.append(phoneNbr);

		client.subscribe(topic.toString());
	}

	protected void openChat(String recipient) {
		mController.showChatFragment(recipient);
	}

	public void sendMessage(String recipient, String message) {
		StringBuilder topic = new StringBuilder();
		topic.append(topicBase);
		topic.append(recipient);

		JSONObject msg = new JSONObject();
		try {
			msg.put("sender", phoneNbr);
			msg.put("message", message);

			
			mDbController.putMessage(phoneNbr, message);
			
			client.publishRetain(topic.toString(), msg.toString().getBytes(), EXACTLY_ONCE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected DBController getDatabaseController() {
		return mDbController;
	}

	protected void disconnect() {
		if (client != null)
			client.disconnect();
	}
	
	protected String getPhonenbr(){
		return phoneNbr;
	}

}
