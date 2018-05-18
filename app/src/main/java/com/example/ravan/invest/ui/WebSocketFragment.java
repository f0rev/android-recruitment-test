package com.example.ravan.invest.ui;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.basel.DualButton.DualButton;
import com.example.ravan.invest.R;
import com.example.ravan.invest.WebSocketApp;
import com.example.ravan.invest.converters.WebSocketConverterFactory;
import com.example.ravan.invest.db.CurrenciesItem;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.navin.flintstones.rxwebsocket.RxWebsocket;
import com.squareup.sqlbrite3.BriteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebSocketFragment extends Fragment {

    @Inject
    BriteDatabase db;
    @Inject
    Application mContext;

    @BindView(R.id.dualBtn)
    DualButton mConnectBtn;
    @BindView(R.id.rv_currencies)
    RecyclerView mRvCurrencies;
    Unbinder unbinder;
    @BindView(R.id.condition_exp_layout)
    ExpandableRelativeLayout mConditionExpLayout;
    @BindView(R.id.condition_expand_btn)
    ImageView mConditionExpandBtn;
    @BindView(R.id.log_list)
    ListView mLogListView;
    @BindView(R.id.empty_log_state)
    TextView mEmptyLogState;

    private static final String LIST_QUERY = "SELECT * FROM "
            + CurrenciesItem.TABLE;
    private View rootView;
    private RxWebsocket mWebSocket;
    private CurrenciesAdapter mCurrenciesAdapter;
    private ArrayAdapter<String> mArrayAdapter;
    private CompositeDisposable mCompositeDisposable;
    private RotateAnimation mRotateDownToUp, mRotateUpToDown;
    private List<String> mLogList;


    public WebSocketFragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        WebSocketApp.getComponent(activity).inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web_socket, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initLogList();
        initCurrenciesList();
        configureClicks();
        configureRotateAnimation();

        mConditionExpLayout.collapse();
        mConnectBtn.bringToFront();

        mCompositeDisposable.add(db.createQuery(CurrenciesItem.TABLE, LIST_QUERY)
                .mapToList(CurrenciesItem.MAPPER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.size() > 0) {
                        JSONArray result = new JSONArray(data.get(0).result());
                        mCurrenciesAdapter.updateItems(result);
                    }
                }));

        if (isOnline())
            openWebsocket();

        return rootView;
    }

    private void configureClicks() {
        mConditionExpandBtn.setOnClickListener(v -> {
            if (mConditionExpLayout.isExpanded())
                mConditionExpandBtn.startAnimation(mRotateDownToUp);
            else
                mConditionExpandBtn.startAnimation(mRotateUpToDown);
            mConditionExpLayout.toggle();
        });

        mConnectBtn.setDualClickListener(new DualButton.OnDualClickListener() {
            @Override
            public void onClickFirst(Button button) {
                if (!isOnline()) {
                    Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                openWebsocket();
                mEmptyLogState.setVisibility(View.GONE);
                mLogListView.setVisibility(View.VISIBLE);
                mCompositeDisposable.add(mWebSocket.connect()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                event -> Log.d(MainActivity.class.getSimpleName(), event.toString()),
                                WebSocketFragment.this::logError));
            }
            @Override
            public void onClickSecond(Button button) {
                if (!isOnline()) {
                    Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mWebSocket != null) {
                    mCompositeDisposable.add(
                            mWebSocket.disconnect(1000, "Disconnect")
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            event -> Log.d(MainActivity.class.getSimpleName(), event.toString()),
                                            WebSocketFragment.this::logError));
                }
            }
        });

    }

    private void configureRotateAnimation() {
        mRotateUpToDown = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ZORDER_TOP, 0.5f);
        mRotateDownToUp = new RotateAnimation(180, 360,
                Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
        mRotateUpToDown.setDuration(400);
        mRotateUpToDown.setInterpolator(new LinearInterpolator());

        mRotateUpToDown.setFillAfter(true);
        mRotateUpToDown.setFillEnabled(true);
        mRotateDownToUp.setDuration(400);
        mRotateDownToUp.setInterpolator(new LinearInterpolator());

        mRotateDownToUp.setFillAfter(true);
        mRotateDownToUp.setFillEnabled(true);
    }



    private void logError(Throwable throwable) {
        mLogList.add(String.format("\n[%s]:[ERROR]%s", getCurrentTime(), throwable.getMessage()));
        mArrayAdapter.notifyDataSetChanged();

    }

    private void log(String text) {
        mLogList.add(String.format("\n[%s]:%s", getCurrentTime(), text));
        mArrayAdapter.notifyDataSetChanged();
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(c.getTime());
    }

    private void openWebsocket() {
        mWebSocket = new RxWebsocket.Builder()
                .addConverterFactory(WebSocketConverterFactory.create())
                .build(mContext.getString(R.string.api_url));
        retrieveData();
    }


    private void retrieveData() {
        checkIfLocalDataExists();
        mCompositeDisposable.add(
                mWebSocket.eventStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(event -> {
                            if (event instanceof RxWebsocket.Open) {
                                log("CONNECTED");
                            } else if (event instanceof RxWebsocket.Closed) {
                                log("DISCONNECTED");
                            } else if (event instanceof RxWebsocket.QueuedMessage) {
                                log("[MESSAGE QUEUED]:" + ((RxWebsocket.QueuedMessage) event).message().toString());
                            } else if (event instanceof RxWebsocket.Message) {
                                try {
                                    log("[DE-SERIALIZED MESSAGE RECEIVED]:" + ((RxWebsocket.Message) event).data()
                                            .substring(0, 100) + "...");
                                    String message = ((RxWebsocket.Message) event).data();
                                    JSONArray result = getCorrectCurrencies(message);
                                    mCurrenciesAdapter.updateItems(result);
                                    if (hasLocalData) {
                                        db.update(CurrenciesItem.TABLE, CONFLICT_NONE, new CurrenciesItem.Builder().listId(1
                                                ).result(result.toString()).build(), CurrenciesItem.ID + " = ?",
                                                "1");
                                    } else {
                                        db.insert(CurrenciesItem.TABLE, CONFLICT_NONE,
                                                new CurrenciesItem.Builder().listId(1).result(result.toString()).build());
                                        hasLocalData = true;
                                    }


                                } catch (Throwable throwable) {
                                    log("[MESSAGE RECEIVED]:" + ((RxWebsocket.Message) event).data().toString());
                                }
                            }
                        })
                        .subscribe(event -> {
                        }, this::logError));
    }

    private JSONArray getCorrectCurrencies(String message) throws JSONException {
        String messageCorrect = message.substring(2);
        JSONArray jsonArray = new JSONArray(messageCorrect);
        JSONObject jsonObject = jsonArray.getJSONObject(1);
        JSONArray result = jsonObject.getJSONArray("result");
        return result;
    }

    private void initCurrenciesList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRvCurrencies.setLayoutManager(layoutManager);
        mCurrenciesAdapter = new CurrenciesAdapter(getContext());
        mRvCurrencies.setAdapter(mCurrenciesAdapter);
    }

    private void initLogList(){
        mLogList = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter<>(mContext,
                R.layout.log_item_layout,
                mLogList);

        mLogListView.setAdapter(mArrayAdapter);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    boolean hasLocalData = false;
    private void checkIfLocalDataExists() {
        mCompositeDisposable.add(db.createQuery(CurrenciesItem.TABLE, LIST_QUERY)
                .mapToList(CurrenciesItem.MAPPER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.size() > 0)
                        hasLocalData = true;
                }));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
