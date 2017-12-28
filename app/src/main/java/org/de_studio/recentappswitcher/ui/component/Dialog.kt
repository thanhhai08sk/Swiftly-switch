package org.de_studio.recentappswitcher.ui.component

import android.content.Context
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import org.de_studio.recentappswitcher.R
import rx.Completable

/**
 * Created by HaiNguyen on 11/24/17.
 */
object Dialog {

    fun notify(context: Context, title: String?, text: String?, doOnOk: () -> Unit = {}) {
        MaterialDialog.Builder(context)
                .apply {
                    if (title != null) title(title)
                    if (text != null) content(text)
                    positiveText(context.getString(R.string.edge_dialog_ok_button))
                    onPositive { _, _ -> doOnOk.invoke() }
                }
                .show()
    }

    fun notify(context: Context, title: Int?, content: Int, positiveAction: DialogAction, negativeAction: DialogAction? = null, neutralAction: DialogAction? = null, doOnDismiss: () -> Unit = {}, cancelable: Boolean = true) {
        MaterialDialog.Builder(context)
                .apply {
                    title?.let { this.title(title) }
                    content(content)
                    positiveAction
                            .let {
                                positiveText(it.text)
                                onPositive { _, _ -> it.action.invoke() }
                            }
                    negativeAction
                            ?.let {
                                negativeText(it.text)
                                onNegative { _, _ -> it.action.invoke() }
                            }
                    neutralAction
                            ?.let {
                                neutralText(it.text)
                                onNeutral { _, _ -> it.action.invoke() }
                            }
                    dismissListener { doOnDismiss.invoke() }
                    cancelable(cancelable)
                }
                .show()
    }

    fun actionOnItemDialog(context: Context, title: String, actions: List<DialogAction>) {
        MaterialDialog.Builder(context)
                .title(title)
                .items(actions.map { it.text })
                .itemsCallback { _, _, position, _ ->
                    actions[position].action.invoke()
                }.show()
    }

    fun confirmDelete(context: Context, onDelete: () -> Unit) {
        MaterialDialog.Builder(context)
                .content(R.string.confirm_before_delete)
                .positiveText(R.string.delete)
                .negativeText(R.string.md_cancel_label)
                .onPositive({ _, _ -> onDelete.invoke() })
                .show()
    }


    fun simpleProgress(context: Context, title: Int?, content: Int, completedContent: Int, finishedEvent: Completable) {
        MaterialDialog
                .Builder(context)
                .apply {
                    if (title != null) {
                        this.title(title)
                    }
                    content(content)
                    cancelable(false)
                    canceledOnTouchOutside(false)
                    progress(true, 0)
                }
                .show()
                .apply {
                    finishedEvent.subscribe {
                        progressBar.visibility = View.GONE
                        setCancelable(true)
                        setCanceledOnTouchOutside(true)
                        setContent(completedContent)
                    }
                }
    }

    fun yesNo(context: Context, content: String, onYes: () -> Unit, onNo: () -> Unit) {
        MaterialDialog.Builder(context)
                .content(content)
                .positiveText(R.string.yes)
                .onPositive { _, _ -> onYes.invoke() }
                .negativeText(R.string.no)
                .onNegative { _, _ -> onNo.invoke() }
                .show()
    }


    fun customView(context: Context, title: String?, layoutId: Int, positiveAction: DialogAction? = null, negativeAction: DialogAction? = null, neutralAction: DialogAction? = null, doOnDismiss: () -> Unit = {}, cancelable: Boolean = true, dismissEvent: Completable? = null): View {
        return MaterialDialog
                .Builder(context)
                .apply {
                    if (title != null) this.title(title)
                    customView(layoutId, false)
                    positiveAction
                            ?.let {
                                positiveText(it.text)
                                onPositive { _, _ -> it.action.invoke() }
                            }
                    negativeAction
                            ?.let {
                                negativeText(it.text)
                                onNegative { _, _ -> it.action.invoke() }
                            }
                    neutralAction
                            ?.let {
                                neutralText(it.text)
                                onNeutral { _, _ -> it.action.invoke() }
                            }
                    dismissListener { doOnDismiss.invoke() }
                    cancelable(cancelable)
                }
                .show()
                .let {
                    dismissEvent?.subscribe { it.dismiss() }
                    it.customView!!
                }
    }

    fun pickColor(context: Context, currentColor: Int, gotColor: (Int) -> Unit) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle(context.getString(R.string.main_set_background_color))
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener { }
                .setPositiveButton("ok") { _, selectedColor, _ -> gotColor.invoke(selectedColor) }
                .setNegativeButton("cancel") { _, _ -> }
                .build()
                .show()
    }
}

open class DialogAction(val text: String, val action: () -> Unit)