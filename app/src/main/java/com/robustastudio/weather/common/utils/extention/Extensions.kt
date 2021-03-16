package com.robustastudio.weather.common.utils.extention

import android.content.Context
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.robustastudio.weather.R
import com.robustastudio.weather.common.base.BaseActivity
import es.dmoral.toasty.Toasty
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable =
    apply { compositeDisposable.add(this) }

fun <T> applySchedulers(): SingleTransformer<T, T> {
    return SingleTransformer<T, T> { observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * context extension functions
 * */
fun Context.showErrorMessage(message: String = getString(R.string.someting_wrong)) =
    Toasty.error(this, message, Toasty.LENGTH_LONG).show()


fun Context.showInfo(message: String) =
    Toasty.info(this, message, Toasty.LENGTH_LONG).show()


fun Context.showWarningMessage(message: String) =
    Toasty.warning(this, message, Toasty.LENGTH_LONG).show()


fun Context.showNormalMessage(context: Context, message: String) =
    Toasty.normal(context, message, Toasty.LENGTH_LONG).show()


fun Context.showSuccessMessage(message: String) =
    Toasty.success(this, message, Toasty.LENGTH_LONG).show()

fun FragmentActivity.hideKeypad(baseActivity: BaseActivity<*>) {
    val imm: InputMethodManager =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(baseActivity.currentFocus?.windowToken, 0)

}

fun Fragment.showSuccessMessage(msg: String) {
    Toasty.success(this.requireContext(), msg, Toasty.LENGTH_SHORT).show()
}
fun Fragment.showErrorMessage(msg: String) {
    Toasty.error(this.requireContext(), msg, Toasty.LENGTH_SHORT).show()
}

fun ImageView.loadFrom(url: String) {
    Glide.with(this).load(url).into(this)
}

