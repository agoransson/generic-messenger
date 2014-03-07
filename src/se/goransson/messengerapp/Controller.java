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

import java.security.spec.MGF1ParameterSpec;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author andreas
 * 
 */
public class Controller {

	private static final String TAG = "Controller";

	private Activity mActivity;
	private FragmentManager mFragmentManager;

	private ConnectionFragment mConnectionFragment;
	private ProfileFragment mProfileFragment;
	private ChatFragment mChatFragment;
	private ContactsFragment mContactFragment;

	public Controller(Activity activity) {
		this.mActivity = activity;

		mFragmentManager = mActivity.getFragmentManager();
	}

	public void showConnectionFragment() {
		if (mConnectionFragment == null) {
			mConnectionFragment = new ConnectionFragment();

			Bundle args = new Bundle();
			// Add arguments
			mConnectionFragment.setArguments(args);
		}

		showFragment(mConnectionFragment, "connection", false);
	}

	public void showProfileFragment(String phoneNbr) {
		if (mProfileFragment == null) {
			mProfileFragment = new ProfileFragment();

			Bundle args = new Bundle();
			args.putString("phonenbr", phoneNbr);
			mProfileFragment.setArguments(args);

			showFragment(mProfileFragment, "profile");
		}
	}

	public void showChatFragment(String phoneNbr) {
		if (mChatFragment == null) {
			Log.i(TAG, "new chat fragment");
			mChatFragment = new ChatFragment();

			Bundle args = new Bundle();
			args.putString("phonenbr", phoneNbr);
			mChatFragment.setArguments(args);
		}

		mChatFragment.setSender(phoneNbr);

		showFragment(mChatFragment, "chat");
	}

	public void showContactsFragment() {
		if (mContactFragment == null) {
			mContactFragment = new ContactsFragment();

			Bundle args = new Bundle();
			// No args
			mContactFragment.setArguments(args);
		}

		showFragment(mContactFragment, "contacts", false);
	}

	/**
	 * 
	 * @param frag
	 * @param tag
	 */
	private void showFragment(Fragment frag, String tag) {
		showFragment(frag, tag, true);
	}

	/**
	 * 
	 * @param frag
	 * @param tag
	 * @param backstack
	 */
	private void showFragment(Fragment frag, String tag, boolean backstack) {
		if( mFragmentManager.findFragmentByTag(tag) == null ){
			if (backstack)
				mFragmentManager.beginTransaction()
						.replace(R.id.container, frag, tag).addToBackStack(tag)
						.commitAllowingStateLoss();
			else {
				mFragmentManager.beginTransaction()
						.replace(R.id.container, frag, tag)
						.commitAllowingStateLoss();
			}
		}else{
			mFragmentManager.beginTransaction().
		}
	}

	public void removeFragment(String tag) {
		Fragment frag = mFragmentManager.findFragmentByTag(tag);
		
		mFragmentManager.beginTransaction().remove(frag).commit();
	}
	
	public Fragment getActiveFragment() {
		if (mFragmentManager.getBackStackEntryCount() == 0) {
			return null;
		}
		String tag = mFragmentManager.getBackStackEntryAt(
				mFragmentManager.getBackStackEntryCount() - 1).getName();
		return mFragmentManager.findFragmentByTag(tag);
	}

	public void notifyMessage() {
		Fragment curFragment = getActiveFragment();

		if (curFragment == mProfileFragment)
			mProfileFragment.notifyMessage();
		else if (curFragment == mChatFragment)
			mChatFragment.notifyMessage();
	}

	public void notifyMessage(long id) {
		Fragment curFragment = getActiveFragment();

		if (curFragment instanceof Notifiable) {
			if (curFragment.isVisible())
				((Notifiable) curFragment).notifyMessage(id);
		}
	}

	public void pop() {
		mFragmentManager.popBackStack();
	}
}
