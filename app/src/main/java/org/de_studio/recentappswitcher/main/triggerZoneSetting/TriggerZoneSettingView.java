package org.de_studio.recentappswitcher.main.triggerZoneSetting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseFragment;
import org.de_studio.recentappswitcher.dagger.DaggerTriggerZoneSettingComponent;
import org.de_studio.recentappswitcher.dagger.TriggerZoneSettingModule;

import butterknife.BindView;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/13/17.
 */

public class TriggerZoneSettingView extends BaseFragment<TriggerZoneSettingPresenter> implements TriggerZoneSettingPresenter.View {
    @BindView(R.id.position_spinner)
    Spinner positionSpinner;
    @BindView(R.id.sensitive_seek_bar)
    SeekBar sensitiveSeekBar;
    @BindView(R.id.length_seek_bar)
    SeekBar lengthSeekBar;
    @BindView(R.id.offset_seek_bar)
    SeekBar offsetSeekBar;
    @BindView(R.id.default_button)
    Button defaultButton;
    @BindView(R.id.ok_button)
    Button okButton;
    @BindView(R.id.edge)
    View edge;
    String[] positionStrings;
    int[] positionInts = new int[]{10, 11, 12, 20, 21, 22, 31};
    float mScale;
    String edgeId;



    PublishSubject<Integer> changePositionSJ = PublishSubject.create();
    PublishSubject<Integer> changeSensitiveSJ = PublishSubject.create();
    PublishSubject<Integer> changeLengthSJ = PublishSubject.create();
    PublishSubject<Integer> changeOffsetSJ = PublishSubject.create();
    PublishSubject<Void> viewCreatedSJ = PublishSubject.create();

    public static TriggerZoneSettingView newInstance(String edgeId) {

        Bundle args = new Bundle();
        args.putString(Cons.EDGE_ID, edgeId);
        TriggerZoneSettingView fragment = new TriggerZoneSettingView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        edgeId = getArguments().getString(Cons.EDGE_ID);
        super.onCreate(savedInstanceState);
        positionStrings = getResources().getStringArray(R.array.edge_positions_array);
        mScale = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sensitiveSeekBar.setOnSeekBarChangeListener(this);
        lengthSeekBar.setOnSeekBarChangeListener(this);
        offsetSeekBar.setOnSeekBarChangeListener(this);
        positionSpinner.setOnItemSelectedListener(this);
        viewCreatedSJ.onNext(null);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.trigger_zone_setting_view;
    }

    @Override
    protected void inject() {
        DaggerTriggerZoneSettingComponent.builder()
                .triggerZoneSettingModule(new TriggerZoneSettingModule(this, edgeId))
                .build().inject(this);
    }

    @Override
    public PublishSubject<Integer> onChangePosition() {
        return changePositionSJ;
    }

    @Override
    public PublishSubject<Integer> onChangeSensitive() {
        return changeSensitiveSJ;
    }

    @Override
    public PublishSubject<Integer> onChangeLength() {
        return changeLengthSJ;
    }

    @Override
    public PublishSubject<Integer> onChangeOffset() {
        return changeOffsetSJ;
    }

    @Override
    public PublishSubject<Void> onViewCreated() {
        return viewCreatedSJ;
    }

    @Override
    public void setCurrentPosition(int position) {
        positionSpinner.setSelection(Utility.getPositionOfStringArray(positionStrings, positionStrings[Utility.getPositionOfIntArray(positionInts, position)]));
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) edge.getLayoutParams();
        switch (Utility.rightLeftOrBottom(position)){
            case Cons.POSITION_RIGHT:
                lp2.gravity = Gravity.RIGHT;
                break;
            case Cons.POSITION_LEFT:
                lp2.gravity =Gravity.LEFT;
                break;
            case Cons.POSITION_BOTTOM:
                lp2.gravity = Gravity.BOTTOM;
                break;
        }
        edge.setLayoutParams(lp2);
    }

    @Override
    public void setCurrentSensitive(int sensitive, int position) {
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) edge.getLayoutParams();

        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_BOTTOM:
                lp2.height = (int) (sensitive * mScale);
                break;
            default:
                lp2.width = (int) (sensitive * mScale);
                break;
        }
        edge.setLayoutParams(lp2);
    }

    @Override
    public void setCurrentLength(int length, int position) {
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) edge.getLayoutParams();

        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_BOTTOM:
                lp2.width = (int) (length * mScale);
                break;
            default:
                lp2.height = (int) (length * mScale);
                break;
        }
        edge.setLayoutParams(lp2);
    }

    @Override
    public void setCurrentOffset(int offset, int position) {

        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_BOTTOM:
                edge.setTranslationX(- offset*mScale);
                break;
            default:
                edge.setRotationY(-offset * mScale);
                break;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            switch (seekBar.getId()) {
                case R.id.sensitive_seek_bar:
                    changeSensitiveSJ.onNext(progress);
                    break;
                case R.id.length_seek_bar:
                    changeLengthSJ.onNext(progress);
                    break;
                case R.id.offset_seek_bar:
                    changeOffsetSJ.onNext(progress);
                    break;
            }}}
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.position_spinner:
                changePositionSJ.onNext(positionInts[position]);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}

