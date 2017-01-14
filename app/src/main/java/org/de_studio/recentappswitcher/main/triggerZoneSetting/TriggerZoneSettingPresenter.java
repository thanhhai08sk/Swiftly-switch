package org.de_studio.recentappswitcher.main.triggerZoneSetting;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.SeekBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Edge;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/13/17.
 */

public class TriggerZoneSettingPresenter extends BasePresenter<TriggerZoneSettingPresenter.View, TriggerZoneSettingModel> {
    private static final String TAG = TriggerZoneSettingPresenter.class.getSimpleName();
    Realm realm = Realm.getDefaultInstance();
    Edge edge;
    String edgeId;

    PublishSubject<Void> applyChangesSJ = PublishSubject.create();


    public TriggerZoneSettingPresenter(TriggerZoneSettingModel model, String edgeId) {
        super(model);
        this.edgeId = edgeId;
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        edge = realm.where(Edge.class).equalTo(Cons.EDGE_ID, edgeId).findFirst();
        edge.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                updateEdge();
                if (view == null) {
                    Log.e(TAG, "onChange: view null");
                }

            }
        });

        addSubscription(
                view.onViewCreated().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        updateEdge();
                    }
                })
        );

        addSubscription(
                view.onChangeSensitive().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                edge.sensitive = integer + Cons.EDGE_SENSITIVE_MIN;
                            }
                        });
                    }
                })
        );

        addSubscription(
                view.onChangeLength()
                        .sample(100, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        if (!realm.isClosed()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    edge.length = integer + Cons.EDGE_LENGTH_MIN;
                                }
                            });
                        } else {
                            Log.e(TAG, "call change length: realm is closed");
                        }
                    }
                })
        );

        addSubscription(
                view.onChangeOffset().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        if (!realm.isClosed()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    edge.offset = integer + Cons.EDGE_OFFSET_MIN;
                                }
                            });
                        } else {
                            Log.e(TAG, "call change position: realm is close");
                        }
                    }
                })
        );

        addSubscription(
                view.onChangePosition().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        if (!realm.isClosed()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    edge.position = integer;
                                }
                            });
                            applyChangesSJ.onNext(null);
                        } else {
                            Log.e(TAG, "call change position: realm is closed");
                        }
                    }
                })
        );

        addSubscription(
                view.onStopTrackingSeekBar().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        applyChangesSJ.onNext(null);
                    }
                })
        );

        addSubscription(
                applyChangesSJ.sample(2,TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.restartService();

                    }
                })
        );


    }


    private void updateEdge() {
        if (view != null) {
            view.setCurrentSensitive(edge.sensitive, edge.position);
            view.setCurrentLength(edge.length, edge.position);
            view.setCurrentOffset(edge.offset, edge.position);
            view.setCurrentPosition(edge.position);
        } else {
            Log.e(TAG, "updateEdge: view null");
        }
    }

    public void onDefaultClick() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                switch (edgeId) {
                    case Edge.EDGE_1_ID:
                        edge.position = Cons.POSITION_RIGHT_CENTRE;
                        break;
                    case Edge.EDGE_2_ID:
                        edge.position = Cons.POSITION_LEFT_BOTTOM;
                        break;
                }
                edge.sensitive = Cons.DEFAULT_EDGE_SENSITIVE;
                edge.length = Cons.DEFAULT_EDGE_LENGTH;
                edge.offset = 0;
            }
        });
        updateEdge();
        applyChangesSJ.onNext(null);
    }

    @Override
    public void onViewDetach() {
        Log.e(TAG, "onViewDetach: close realm ");
        edge.removeChangeListeners();
        realm.close();
        super.onViewDetach();
    }

    public interface View extends PresenterView, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
        PublishSubject<Integer> onChangePosition();

        PublishSubject<Integer> onChangeSensitive();

        PublishSubject<Integer> onChangeLength();

        PublishSubject<Integer> onChangeOffset();

        PublishSubject<Void> onViewCreated();

        PublishSubject<Void> onStopTrackingSeekBar();

        void restartService();

        void setCurrentPosition(int position);

        void setCurrentSensitive(int sensitive, int position);

        void setCurrentLength(int length, int position);

        void setCurrentOffset(int offset, int position);

    }
}
