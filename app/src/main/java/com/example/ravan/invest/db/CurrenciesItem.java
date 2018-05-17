package com.example.ravan.invest.db;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CurrenciesItem implements Parcelable {
    public static final String TABLE = "currencies_item";

    public static final String ID = "_id";
    public static final String LIST_ID = "currencies_list_id";
    public static final String TREND = "trend";
    public static final String NAME = "name";
    public static final String THIRD_FIELD = "third_field";
    public static final String FOURTH_FIELD = "fourth_field";
    public static final String FIFTH_FIELD = "fifth_field";
    public static final String NUMBER = "number";

    public abstract long id();

    public abstract String trend();

    public abstract String name();

    public abstract String thirdField();

    public abstract String fourthField();

    public abstract String fifthField();

    public abstract int number();

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public Builder trend(String trend) {
            values.put(TREND, trend);
            return this;
        }

        public Builder name(String name) {
            values.put(NAME, name);
            return this;
        }

        public Builder thirdField(String thirdField) {
            values.put(THIRD_FIELD, thirdField);
            return this;
        }

        public Builder fourthField(String fourthField) {
            values.put(FOURTH_FIELD, fourthField);
            return this;
        }

        public Builder fifthField(String fifthField) {
            values.put(FIFTH_FIELD, fifthField);
            return this;
        }

        public Builder number(int number) {
            values.put(NUMBER, number);
            return this;
        }


        public ContentValues build() {
            return values; // TODO defensive copy?
        }
    }
}
