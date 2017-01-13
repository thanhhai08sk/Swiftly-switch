package org.de_studio.recentappswitcher.main.triggerZoneSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Edge;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/13/17.
 */

public class TriggerZoneSettingPresenter extends BasePresenter<TriggerZoneSettingPresenter.View, TriggerZoneSettingModel> {
    Realm realm = Realm.getDefaultInstance();
    Edge edge;
    String edgeId;


    public TriggerZoneSettingPresenter(TriggerZoneSettingModel model, String edgeId) {
        super(model);
        this.edgeId = edgeId;
    }

    @Override
    public void onViewAttach(View view) {
        super.onViewAttach(view);
        edge = realm.where(Edge.class).equalTo(Cons.EDGE_ID, edgeId).findFirst();
        edge.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                updateEdge();
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
                                edge.sensitive = integer;
                            }
                        });
                    }
                })
        );

        addSubscription(
                view.onChangeLength().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                edge.length = integer;
                            }
                        });
                    }
                })
        );

        addSubscription(
                view.onChangeOffset().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                edge.offset = integer;
                            }
                        });
                    }
                })
        );

        addSubscription(
                view.onChangePosition().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                edge.position = integer;
                            }
                        });
                    }
                })
        );



    }

    private void updateEdge() {
        view.setCurrentSensitive(edge.sensitive);
        view.setCurrentLength(edge.length);
        view.setCurrentOffset(edge.offset);
        view.setCurrentPosition(edge.position);
    }

    @Override
    public void onViewDetach() {
        realm.removeAllChangeListeners();
        realm.close();
        super.onViewDetach();
    }

    public interface View extends PresenterView {
        PublishSubject<Integer> onChangePosition();
        PublishSubject<Integer> onChangeSensitive();

        PublishSubject<Integer> onChangeLength();

        PublishSubject<Integer> onChangeOffset();

        PublishSubject<Void> onViewCreated();

        void setCurrentPosition(int position);

        void setCurrentSensitive(int sensitive);

        void setCurrentLength(int length);

        void setCurrentOffset(int offset);

    }
}
