/*
 * Copyright 2013 Mark Injerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.chat.chat.Readnotification;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.chat.chat.R;

public class WidgetProvider extends AppWidgetProvider {
	private static final String ACTION_TOGGLE = "ChatMe.widget.TOGGLE";
	static final String ACTION_UPDATE = "ChatMe.widget.UPDATE";

	private static final String ACTION_ON = "ChatMe.widget.ON";
	private static final String ACTION_OFF = "ChatMe.widget.OFF";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
			updateViews(context, views);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		assert action != null; // Prevent Lint warning. Should never be null, I want a crash report if it is.
		switch (action) {
			case ACTION_TOGGLE:
				if (Service.isRunning()) {
					Toast.makeText(context,
							Service.toggleSuspend() ? R.string.service_suspended : R.string.service_running,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case ACTION_UPDATE:
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
				updateViews(context, views);
				AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, WidgetProvider.class), views);
				break;
            case ACTION_ON:
                if (Service.isRunning()) {
                    Toast.makeText(context,
                            Service.toggleSuspend(false) ? R.string.service_suspended : R.string.service_running,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_OFF:
                if (Service.isRunning()) {
                    Toast.makeText(context,
                            Service.toggleSuspend(true) ? R.string.service_suspended : R.string.service_running,
                            Toast.LENGTH_SHORT).show();
                }
			default:
				super.onReceive(context, intent);
				break;
		}
	}
	
	private static void updateViews(Context context, RemoteViews views) {
		PendingIntent pendingIntent;
		if (Service.isRunning()) {
			pendingIntent = PendingIntent.getBroadcast(context, 0,
					new Intent(context, WidgetProvider.class).setAction(ACTION_TOGGLE), 0);
			if (Service.isSuspended()) {
				views.setImageViewResource(R.id.button, R.drawable.ic_logo);
			} else {
				views.setImageViewResource(R.id.button, R.drawable.ic_logo);
			}
		} else {
			pendingIntent = PendingIntent.getActivity(context, 0, Common.getNotificationListenerSettingsIntent(), 0);
			views.setImageViewResource(R.id.button, R.drawable.ic_logo);
		}
		views.setOnClickPendingIntent(R.id.button, pendingIntent);
	}
}
