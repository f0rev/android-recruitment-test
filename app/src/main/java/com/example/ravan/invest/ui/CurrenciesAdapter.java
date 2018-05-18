package com.example.ravan.invest.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ravan.invest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrenciesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final SimpleDateFormat mDateFormat;
    private JSONArray mData = new JSONArray();
    private Context mContext;
    private SimpleDateFormat mLocalTimeFormat;
    private SimpleDateFormat mLocalDateFormat;

    public CurrenciesAdapter(Context context) {
        mContext = context;
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        mLocalTimeFormat = new SimpleDateFormat("HH:mm:ss");
        mLocalDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currencies_item, parent, false);
        return new CurrenciesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CurrenciesViewHolder currenciesViewHolder = (CurrenciesViewHolder) holder;
        final JSONObject currency;
        try {
            currency = mData.getJSONObject(position);
            Iterator<?> keys = currency.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                int keyInt = Integer.parseInt(key);
                String value = currency.getString(String.valueOf(key));
                switch (keyInt) {
                    case 0:
                        currenciesViewHolder.setTrend(value);
                        break;
                    case 1:
                        currenciesViewHolder.setName(value);
                        break;
                    case 2:
                        currenciesViewHolder.setSecondField(value);
                        break;
                    case 3:
                        currenciesViewHolder.setThirdField(value);
                        break;
                    case 4:
                        currenciesViewHolder.setFourthField(value);
                        break;
                    case 5:
                        currenciesViewHolder.setFifthField(value);
                        break;
                    case 6:
                        currenciesViewHolder.setNumber(value);
                        break;
                    case 7:
                        try {
                            Date dateObject = mDateFormat.parse(value);
                            String time = mLocalTimeFormat.format(dateObject);
                            String date = mLocalDateFormat.format(dateObject);
                            currenciesViewHolder.setTime(time);
                            currenciesViewHolder.setDate(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public void updateItems(JSONArray data) {
        mData = data;
        notifyDataSetChanged();
    }

    public class CurrenciesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trend)
        ImageView trendIcon;
        @BindView(R.id.first_field)
        TextView name;
        @BindView(R.id.second_field)
        TextView secondField;
        @BindView(R.id.third_field)
        TextView thirdField;
        @BindView(R.id.fourth_field)
        TextView fourthField;
        @BindView(R.id.fifthh_field)
        TextView fifthField;
        @BindView(R.id.sixth_field)
        TextView number;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.date)
        TextView date;

        CurrenciesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setTrend(String trend) {
            switch (trend) {
                case "up":
                    trendIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_trending_up));
                    break;
                case "down":
                    trendIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_trending_down));
                    break;
            }
        }

        private void setName(String txt) {
            name.setText(txt);
        }

        private void setSecondField(String txt) {
            secondField.setText(txt);
        }

        private void setThirdField(String txt) {
            thirdField.setText(txt);
        }

        private void setFourthField(String txt) {
            fourthField.setText(txt);
        }

        private void setFifthField(String txt) {
            fifthField.setText(txt);
        }

        private void setNumber(String txt) {
            number.setText(txt);
        }

        private void setTime(String txt) {
            time.setText(txt);
        }

        private void setDate(String txt) {
            date.setText(txt);
        }

    }
}
