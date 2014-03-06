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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author andreas
 * 
 */
public class DBController {

	private static String TAG = "DBController";

	// The database file name
	private final String DATABASE = "generic_messenger";

	private static final String TABLE_MESSAGES = "messages";

	private static final String KEY_ID = "id";
	private static final String KEY_SENDER = "sender";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_CREATED_AT = "created_at";

	private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE "
			+ TABLE_MESSAGES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SENDER + " TEXT," + KEY_MESSAGE + " TEXT," + KEY_CREATED_AT
			+ " DATETIME" + ")";

	// Version of the database, used to f.ex. update structure of database in
	// "onUpgrade"
	public final int VERSION = 1;

	// You need both the database and the helper
	private SQLiteDatabase database;
	private DatabaseHelper helper;

	private MainActivity activity;

	public DBController(Activity ctx) {
		// Instantiate the helper using the context of the activity
		helper = new DatabaseHelper(ctx);
		activity = (MainActivity) ctx;
	}

	// Allways use JDoc comments for public methods in your controllers!

	/**
	 * Opens the database
	 */
	private void open() {
		database = helper.getWritableDatabase();
	}

	/**
	 * Closes the database
	 */
	private void close() {
		database.close();
	}

	/**
	 * Add a new message to the database
	 * 
	 * @param sender
	 *            the sender
	 * @param message
	 *            the message
	 */
	public long putMessage(String sender, String message) {
		open();

		ContentValues values = new ContentValues();
		values.put(KEY_SENDER, sender);
		values.put(KEY_MESSAGE, message);
		values.put(KEY_CREATED_AT, getDateTime());

		long id = database.insert(TABLE_MESSAGES, null, values);

		close();

		return id;
	}

	/**
	 * Returns all unique chats, searches for unique senders in table
	 * {@link #TABLE_MESSAGES}. Only returns the last message of each chat.
	 * 
	 * @return all chats
	 */
	public ArrayList<Message> getAllChats() {
		ArrayList<Message> messages = new ArrayList<Message>();

		String myNbr = activity.getPhonenbr();

		String select = "SELECT DISTINCT *,  max(id) as _id FROM "
				+ TABLE_MESSAGES + " WHERE sender <> '"
				+ activity.getPhonenbr() + "' GROUP BY sender";

		open();

		Cursor c = database.rawQuery(select, null);

		if (c.moveToFirst()) {
			do {
				String phoneNbr = c.getString(1);
				Message msg = null;
				if (!phoneNbr.equals(myNbr))
					msg = new IncommingMessage();
				else
					msg = new OutgoingMessage();
				msg.setId(c.getInt(0));
				msg.setSender(c.getString(1));
				msg.setMessage(c.getString(2));
				msg.setCreatedAt(c.getString(3));

				messages.add(msg);
			} while (c.moveToNext());
		}

		c.close();
		close();

		return messages;
	}

	public ArrayList<Message> getAllMessagesForSender(String sender) {
		ArrayList<Message> messages = new ArrayList<Message>();

		String myNbr = activity.getPhonenbr();

		String where = KEY_SENDER + "=?";
		String[] args = { sender };

		open();

		Cursor c = database.query(TABLE_MESSAGES, null, where, args, null,
				null, null);

		if (c.moveToFirst()) {
			do {
				String phoneNbr = c.getString(1);
				Message msg = null;
				if (!phoneNbr.equals(myNbr))
					msg = new IncommingMessage();
				else
					msg = new OutgoingMessage();
				msg.setId(c.getInt(0));
				msg.setSender(c.getString(1));
				msg.setMessage(c.getString(2));
				msg.setCreatedAt(c.getString(3));

				messages.add(msg);
			} while (c.moveToNext());
		}

		c.close();
		close();

		return messages;
	}

	/**
	 * Get all messages
	 * 
	 * @return
	 */
	public Cursor getAllMessages() {
		Cursor c = database.query(TABLE_MESSAGES, new String[] { "id",
				"sender", "message", "created_at" }, null, null, null, null,
				null);
		return c;
	}

	public Cursor getChat(String sender) {
		Cursor c = database.query(TABLE_MESSAGES, new String[] { "id",
				"sender", "message" }, null, null, null, null, null);
		return c;
	}

	/**
	 * Get single message
	 * 
	 * @param id
	 *            the row id
	 * @return the Message
	 */
	public Message getMessage(long id) {
		open();

		String myNbr = activity.getPhonenbr();

		String where = KEY_ID + "=?";
		String[] args = { Long.toString(id) };
		String limit = Integer.toString(1);

		Cursor c = database.query(TABLE_MESSAGES, null, where, args, null,
				null, limit);

		Message msg = null;
		if (c.moveToFirst()) {
			String phoneNbr = c.getString(1);

			if (!phoneNbr.equals(myNbr))
				msg = new IncommingMessage();
			else
				msg = new OutgoingMessage();
			msg.setId(c.getInt(0));
			msg.setSender(c.getString(1));
			msg.setMessage(c.getString(2));
			msg.setCreatedAt(c.getString(3));
		}

		c.close();
		close();

		return msg;
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context ctx) {
			super(ctx, DATABASE, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_MESSAGES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Only use this if you (eventually) find a need to change your
			// applications database. Use oldVersion and newVersion to compare
			// and decide what action to take.
		}
	}

}
