package com.example.ravan.invest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.basel.DualButton.DualButton;
import com.example.ravan.invest.adapter.CurrenciesAdapter;
import com.example.ravan.invest.converters.WebSocketConverterFactory;
import com.navin.flintstones.rxwebsocket.RxWebsocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebSocketFragment extends Fragment {


    @BindView(R.id.dualBtn)
    DualButton mDualBtn;
    @BindView(R.id.rv_currencies)
    RecyclerView mRvCurrencies;
    Unbinder unbinder;
    private View rootView;
    private RxWebsocket mWebsocket;
    private CurrenciesAdapter mCurrenciesAdapter;

    public WebSocketFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web_socket, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initAdapter();
        configureClicks();
        openWebsocket();

        return rootView;
    }

    private void configureClicks() {
        mDualBtn.setDualClickListener(new DualButton.OnDualClickListener() {
            private void showError(Throwable throwable) {
                showError(throwable);
            }

            @Override
            public void onClickFirst(Button button) {
                openWebsocket();
                mWebsocket.connect()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                event -> Log.d(MainActivity.class.getSimpleName(), event.toString()),
                                this::showError);
            }

            @Override
            public void onClickSecond(Button button) {
                if (mWebsocket != null) {
                    mWebsocket.disconnect(1000, "Disconnect")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    event -> Log.d(MainActivity.class.getSimpleName(), event.toString()),
                                    this::showError);
                }
            }
        });
    }

    private void showError(Throwable throwable) {
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showState(String stateText) {
        Toast.makeText(getContext(), stateText, Toast.LENGTH_SHORT).show();
    }



    private void openWebsocket() {
        mWebsocket = new RxWebsocket.Builder()
                .addConverterFactory(WebSocketConverterFactory.create())
                .build("wss://q.investaz.net:3000/socket.io/?EIO=3&transport=websocket");
        logEvents();
    }

    private void logEvents() {
        mWebsocket.eventStream()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(event -> {
                    if (event instanceof RxWebsocket.Open) {
                        showState("CONNECTED");
                    } else if (event instanceof RxWebsocket.Closed) {
                        showState("DISCONNECTED");
                    } else if (event instanceof RxWebsocket.QueuedMessage) {
                        showState("[MESSAGE QUEUED]:" + ((RxWebsocket.QueuedMessage) event).message().toString());
                    } else if (event instanceof RxWebsocket.Message) {
                        try {
                            String message =  ((RxWebsocket.Message) event).data();
                            mCurrenciesAdapter.updateItems(getCorrectCurrencies(message));

                        } catch (Throwable throwable) {
                            showState("[MESSAGE RECEIVED]:" + ((RxWebsocket.Message) event).data().toString());
                        }
                    }
                })
                .subscribe(event -> {
                }, this::showError);
    }

    private JSONArray getCorrectCurrencies(String message) throws JSONException {
        String messageCorrect = message.substring(2);
        JSONArray jsonArray = new JSONArray(messageCorrect);
        JSONObject jsonObject = jsonArray.getJSONObject(1);
        JSONArray result = jsonObject.getJSONArray("result");

        return result;
    }

    private void initAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRvCurrencies.setLayoutManager(layoutManager);
        mCurrenciesAdapter= new CurrenciesAdapter(getContext());
        mRvCurrencies.setAdapter(mCurrenciesAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
