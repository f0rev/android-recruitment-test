package com.example.ravan.invest.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import io.reactivex.functions.Function;

@AutoValue
public abstract class CurrenciesItem implements Parcelable {
    public static final String TABLE = "currencies_item";

    public static final String ID = "_id";
    public static final String LIST_ID = "currencies_list_id";
    public static final String RESULT = "result";

    public abstract long id();

    public abstract long listId();

    public abstract String result();


    public static final Function<Cursor, CurrenciesItem> MAPPER = new Function<Cursor, CurrenciesItem>() {
        @Override
        public CurrenciesItem apply(Cursor cursor) {
            long id = Db.getLong(cursor, ID);
            long listId = Db.getLong(cursor, LIST_ID);
            String result = Db.getString(cursor, RESULT);
            return new AutoValue_CurrenciesItem(id, listId, result);
        }
    };

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public Builder listId(long listId) {
            values.put(LIST_ID, listId);
            return this;
        }

        public Builder result(String result) {
            values.put(RESULT, result);
            return this;
        }

        public ContentValues build() {
            return values; // TODO defensive copy?
        }
    }
}
