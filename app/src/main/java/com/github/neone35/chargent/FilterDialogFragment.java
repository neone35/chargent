package com.github.neone35.chargent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.appyvet.materialrangebar.RangeBar;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterDialogFragment extends DialogFragment {

    private boolean mIsFilterEnabled = false;
    private static final String ARG_TITLE_ID = "dialog-message";
    private static final String ARG_ENABLED = "is-enabled";
    private static final String ARG_TICK_START = "tick-start";
    private static final String ARG_TICK_END = "tick-end";
    private static final String ARG_LEFT_VALUE = "left-value";
    private static final String ARG_RIGHT_VALUE = "right-value";
    private int mTitleID;
    private int mTickStart;
    private int mTickEnd;
    private int mLeftValue;
    private int mRightValue;
    private Context mCtx;
    private FilterDialogListener mListener;
    @BindView(R.id.cb_enabled)
    CheckBox cbEnabled;
    @BindView(R.id.rb_filter)
    RangeBar rbFilter;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FilterDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int titleID,
                                   boolean isEnabled, String leftValue, String rightValue);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    static FilterDialogFragment newInstance(int titleID, boolean isEnabled,
                                            int tickStart, int tickEnd,
                                            int leftValue, int rightValue) {
        FilterDialogFragment fragment = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_ID, titleID);
        args.putBoolean(ARG_ENABLED, isEnabled);
        args.putInt(ARG_TICK_START, tickStart);
        args.putInt(ARG_TICK_END, tickEnd);
        args.putInt(ARG_LEFT_VALUE, leftValue);
        args.putInt(ARG_RIGHT_VALUE, rightValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mCtx = this.getActivity();
        if (getArguments() != null) {
            mTitleID = getArguments().getInt(ARG_TITLE_ID);
            mIsFilterEnabled = getArguments().getBoolean(ARG_ENABLED);
            mTickStart = getArguments().getInt(ARG_TICK_START);
            mTickEnd = getArguments().getInt(ARG_TICK_END);
            mLeftValue = getArguments().getInt(ARG_LEFT_VALUE);
            mRightValue = getArguments().getInt(ARG_RIGHT_VALUE);
        }
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_filter_dialog, null);
        ButterKnife.bind(this, dialogView);
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setView(dialogView)
                .setTitle(getString(mTitleID))
                .setPositiveButton(getString(R.string.ok), (dialogInterface, id) ->
                        mListener.onDialogPositiveClick(this, mTitleID,
                                cbEnabled.isChecked(), rbFilter.getLeftPinValue(), rbFilter.getRightPinValue()))
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) ->
                        mListener.onDialogNegativeClick(this));

        setUpCheckbox();
        setUpRangeBar();

        return builder.create();
    }

    private void setUpRangeBar() {
        // set min and max values
        // ! first end, then start
        rbFilter.setTickEnd(mTickEnd);
        rbFilter.setTickStart(mTickStart);
        // set current filter values (ticks) if they're less than min/max values
        if (mLeftValue >= mTickStart && mRightValue <= mTickEnd)
            rbFilter.setRangePinsByValue(mLeftValue, mRightValue);
        rbFilter.setTickInterval(1);
    }

    private void setUpCheckbox() {
        // update checkbox and rangebar state on creation
        if (!mIsFilterEnabled) {
            cbEnabled.setChecked(false);
            rbFilter.setEnabled(false);
        } else {
            cbEnabled.setChecked(true);
            rbFilter.setEnabled(true);
        }
        // listen for checbox click
        cbEnabled.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) rbFilter.setEnabled(true);
            else rbFilter.setEnabled(false);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(mCtx.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
