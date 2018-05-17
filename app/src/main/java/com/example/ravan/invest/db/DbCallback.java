/*
 * Copyright (C) 2015 Square, Inc.
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
package com.example.ravan.invest.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;

final class DbCallback extends SupportSQLiteOpenHelper.Callback {
  private static final int VERSION = 1;

  private static final String CREATE_LIST = ""
          + "CREATE TABLE " + CurrenciesList.TABLE + "("
          + CurrenciesList.ID + " INTEGER NOT NULL PRIMARY KEY,"
          + CurrenciesList.NAME + " TEXT NOT NULL,"
          + CurrenciesList.ARCHIVED + " INTEGER NOT NULL DEFAULT 0"
          + ")";
  private static final String CREATE_ITEM = ""
      + "CREATE TABLE " + CurrenciesItem.TABLE + "("
      + CurrenciesItem.ID + " INTEGER NOT NULL PRIMARY KEY,"
      + CurrenciesItem.LIST_ID + " INTEGER NOT NULL REFERENCES " + CurrenciesItem.TABLE + "(" + CurrenciesItem.ID + "),"
      + CurrenciesItem.TREND + " TEXT NOT NULL,"
      + CurrenciesItem.NAME + " TEXT NOT NULL,"
      + CurrenciesItem.THIRD_FIELD + " TEXT NOT NULL,"
      + CurrenciesItem.FOURTH_FIELD + " TEXT NOT NULL,"
      + CurrenciesItem.FIFTH_FIELD + " TEXT NOT NULL,"
      + CurrenciesItem.NUMBER + " INTEGER NOT NULL DEFAULT 0"
      + ")";
  private static final String CREATE_ITEM_LIST_ID_INDEX =
      "CREATE INDEX item_list_id ON " + CurrenciesItem.TABLE + " (" + CurrenciesItem.LIST_ID + ")";

  DbCallback() {
    super(VERSION);
  }

  @Override public void onCreate(SupportSQLiteDatabase db) {
    db.execSQL(CREATE_LIST);
    db.execSQL(CREATE_ITEM);
    db.execSQL(CREATE_ITEM_LIST_ID_INDEX);
//
//    long groceryListId = db.insert(CurrenciesItem.TABLE, CONFLICT_FAIL, new CurrenciesItem.Builder()
//        .name("Grocery List")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(groceryListId)
//        .description("Beer")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(groceryListId)
//        .description("Point Break on DVD")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(groceryListId)
//        .description("Bad Boys 2 on DVD")
//        .build());
//
//    long holidayPresentsListId = db.insert(CurrenciesItem.TABLE, CONFLICT_FAIL, new CurrenciesItem.Builder()
//        .name("Holiday Presents")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(holidayPresentsListId)
//        .description("Pogo Stick for Jake W.")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(holidayPresentsListId)
//        .description("Jack-in-the-box for Alec S.")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(holidayPresentsListId)
//        .description("Pogs for Matt P.")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(holidayPresentsListId)
//        .description("Cola for Jesse W.")
//        .build());
//
//    long workListId = db.insert(CurrenciesItem.TABLE, CONFLICT_FAIL, new CurrenciesItem.Builder()
//        .name("Work Items")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(workListId)
//        .description("Finish SqlBrite library")
//        .complete(true)
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(workListId)
//        .description("Finish SqlBrite sample app")
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder()
//        .listId(workListId)
//        .description("Publish SqlBrite to GitHub")
//        .build());
//
//    long birthdayPresentsListId = db.insert(CurrenciesItem.TABLE, CONFLICT_FAIL, new CurrenciesItem.Builder()
//        .name("Birthday Presents")
//        .archived(true)
//        .build());
//    db.insert(TodoItem.TABLE, CONFLICT_FAIL, new TodoItem.Builder().listId(birthdayPresentsListId)
//        .description("New car")
//        .complete(true)
//        .build());
  }

  @Override public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
