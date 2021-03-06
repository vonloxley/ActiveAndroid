package com.activeandroid;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collection;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LruCache;

import com.activeandroid.serializer.TypeSerializer;
import com.activeandroid.util.Log;

public final class Cache {
	//////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC CONSTANTS
	//////////////////////////////////////////////////////////////////////////////////////

	public static final int DEFAULT_CACHE_SIZE = 1024;

	//////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE MEMBERS
	//////////////////////////////////////////////////////////////////////////////////////

	private static Context sContext;

	private static ModelInfo sModelInfo;
	private static DatabaseHelper sDatabaseHelper;

	private static LruCache<String, Model> sEntities;
	private final static int CACHESIZE = 1024; // Arbitrary cachsize

	private static boolean sIsInitialized = false;

	//////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	//////////////////////////////////////////////////////////////////////////////////////

	private Cache() {
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	//////////////////////////////////////////////////////////////////////////////////////

	public static synchronized void initialize(Application application, int cacheSize) {
		if (sIsInitialized) {
			Log.v("ActiveAndroid already initialized.");
			return;
		}

		sContext = application;

		sModelInfo = new ModelInfo(application);
		sDatabaseHelper = new DatabaseHelper(sContext);

		sEntities = new LruCache<String, Model>(CACHESIZE);

		sIsInitialized = true;

		openDatabase();

		Log.v("ActiveAndroid initialized succesfully.");
	}

	public static synchronized void clear() {
		sEntities.evictAll();
		Log.v("Cache cleared.");
	}

	public static synchronized void dispose() {
		checkInitialization();
		closeDatabase();

		sEntities = null;
		sModelInfo = null;
		sDatabaseHelper = null;

		sIsInitialized = false;

		Log.v("ActiveAndroid disposed. Call initialize to use library.");
	}

	// Database access

	public static synchronized SQLiteDatabase openDatabase() {
		if (sDatabaseHelper == null) {
			checkInitialization();
		}
		return sDatabaseHelper.getWritableDatabase();
	}

	public static synchronized void closeDatabase() {
		checkInitialization();
		sDatabaseHelper.close();
	}

	// Context access

	public static Context getContext() {
		checkInitialization();
		return sContext;
	}

	// Entity cache

	public static synchronized void addEntity(Model entity) {
		checkInitialization();

		if (entity.getId() != null) {
			sEntities.put(entity.getClass().toString().replaceAll("^class ", "")+'|'+entity.getId(), entity);
		}
	}

	public static synchronized Model getEntity(Class<? extends Model> type, long id) {
		checkInitialization();

		Model entity=sEntities.get(type.getName()+'|'+id);

		return entity;
	}

	public static synchronized void removeEntity(Model entity) {
		checkInitialization();
		sEntities.remove(entity.getClass().toString().replaceAll("^class ", "")+'|'+entity.getId());
	}

	// Model cache

	public static synchronized Collection<TableInfo> getTableInfos() {
		checkInitialization();
		return sModelInfo.getTableInfos();
	}

	public static synchronized TableInfo getTableInfo(Class<? extends Model> type) {
		checkInitialization();
		return sModelInfo.getTableInfo(type);
	}

	public static synchronized TypeSerializer getParserForType(Class<?> type) {
		checkInitialization();
		return sModelInfo.getTypeSerializer(type);
	}

	public static synchronized String getTableName(Class<? extends Model> type) {
		checkInitialization();
		return sModelInfo.getTableInfo(type).getTableName();
	}

	private static void checkInitialization() {
		if (!sIsInitialized) {
			throw new ActiveAndroidNotInitialized();
		}
	}
}
