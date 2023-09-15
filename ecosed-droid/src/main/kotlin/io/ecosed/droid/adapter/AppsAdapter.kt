package io.ecosed.droid.adapter

import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.google.android.material.snackbar.Snackbar
import com.kongzue.dialogx.dialogs.PopTip
import io.ecosed.droid.R
import io.ecosed.droid.holder.AppsViewHolder
import io.ecosed.droid.model.AppsModel


internal class AppsAdapter constructor(
    applicationList: List<AppsModel>,
    current: (Int) -> Unit
) : RecyclerView.Adapter<AppsViewHolder>() {

    private val mApplicationList: List<AppsModel>
    private val mCurrent: (Int) -> Unit

    init {
        mApplicationList = applicationList
        mCurrent = current
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {

        val appIcon = AppCompatImageView(parent.context)
        val appTitle = AppCompatTextView(parent.context)

        val itemView: LinearLayoutCompat = LinearLayoutCompat(
            parent.context
        ).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                parent.context.resources.getDimension(R.dimen.app_width).toInt(),
                parent.context.resources.getDimension(R.dimen.app_height).toInt()
            )
            setPadding(
                0,
                parent.context.resources.getDimension(
                    R.dimen.icon_padding
                ).toInt(),
                0,
                parent.context.resources.getDimension(
                    R.dimen.icon_padding
                ).toInt()
            )
            orientation = LinearLayoutCompat.VERTICAL
            addView(
                appIcon.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                },
                LinearLayoutCompat.LayoutParams(
                    parent.context.resources.getDimension(
                        R.dimen.icon_size
                    ).toInt(),
                    parent.context.resources.getDimension(
                        R.dimen.icon_size
                    ).toInt()
                )
            )
            addView(
                appTitle.apply {
                    gravity = Gravity.CENTER
                },
                LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
            )
        }

        return AppsViewHolder(
            item = itemView,
            icon = appIcon,
            title = appTitle
        )
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        // 设置应用图标
        holder.mAppIconView.apply {
            setImageDrawable(
                AppUtils.getAppIcon(
                    mApplicationList[
                        holder.absoluteAdapterPosition
                    ].pkgName
                )
            )
        }




        // 设置应用标题
        holder.mTextView.apply {
            // 最多一行
            maxLines = 1
            // 末尾添加省略号
            ellipsize = TextUtils.TruncateAt.END
            // 设置文本
            text = AppUtils.getAppName(
                mApplicationList[
                    holder.absoluteAdapterPosition
                ].pkgName
            )
        }

        holder.itemView.setOnLongClickListener { view ->
//            MessageDialog.build()
//                .setTitle(
//                    AppUtils.getAppName(
//                        mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName
//                    )
//                )
//                .setMessage(R.string.lib_name)
//                .setOkButton(
//                    if (
//                        mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        R.string.menu_delete
//                    } else {
//                        R.string.menu_settings
//                    }
//                )
//                .setOkButton { _, _ ->
//                    if (mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        deleteApp(
//                            view = view,
//                            position = position
//                        )
//                    } else {
//                        navToSettings()
//                    }
//                    false
//                }
//                .setCancelButton(
//                    if (
//                        mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        R.string.menu_open
//                    } else {
//                        R.string.menu_apps
//                    }
//                )
//                .setCancelButton { _, _ ->
//                    if (mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        openApp(
//                            view = view,
//                            position = position
//                        )
//                    } else {
//                        navToLauncher()
//                    }
//                    false
//                }
//                .setOtherButton(
//                    if (
//                        mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        R.string.menu_info
//                    } else {
//                        R.string.menu_flutter
//                    }
//                )
//                .setOtherButton { _, _ ->
//                    if (mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        infoApp(
//                            position = position
//                        )
//                    } else {
//                        navToHome()
//                    }
//                    false
//                }
//                .setOkTextInfo(
//                    if (
//                        mApplicationList[
//                            holder.absoluteAdapterPosition
//                        ].pkgName != BuildConfig.APPLICATION_ID
//                    ) {
//                        TextInfo().setFontColor(Color.RED).setBold(true)
//                    } else {
//                        TextInfo().setBold(true)
//                    }
//                )
//                .setButtonOrientation(
//                    LinearLayout.VERTICAL
//                )
//                .show()
            true
        }

        holder.itemView.setOnClickListener { view ->
            if (mApplicationList[
                    holder.absoluteAdapterPosition
                ].pkgName != AppUtils.getAppPackageName()
            ) {
                openApp(view = view, position = position)
            } else {
                navToHome()
            }
        }
    }

    override fun getItemCount(): Int {
        return mApplicationList.size
    }

    private fun openApp(view: View, position: Int) {
        if (
            view.context.packageManager.getLaunchIntentForPackage(
                mApplicationList[position].pkgName
            ) != null
        ) AppUtils.launchApp(mApplicationList[position].pkgName) else {
            val error = view.context.getString(R.string.error_could_not_start)
            try {
                PopTip.show(error)
            } catch (e: Exception) {
                val errors = error + Log.getStackTraceString(e)
                Snackbar.make(view.context, view, errors, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun infoApp(position: Int) {
        AppUtils.launchAppDetailsSettings(mApplicationList[position].pkgName)
    }

    private fun deleteApp(view: View, position: Int) {
        if (!AppUtils.isAppSystem(mApplicationList[position].pkgName)) {
            AppUtils.uninstallApp(mApplicationList[position].pkgName)
        } else {
            val error = view.context.getString(R.string.uninstall_error)
            try {
                PopTip.show(error)
            } catch (e: Exception) {
                val errors = error + Log.getStackTraceString(e)
                Snackbar.make(view.context, view, errors, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun navToLauncher() {
        //mCurrent(ContentAdapter.launcher)
    }

    private fun navToHome() {
        //mCurrent(ContentAdapter.home)
    }

    private fun navToSettings() {
        //mCurrent(ContentAdapter.settings)
    }

    companion object {

        fun newInstance(
            applicationList: List<AppsModel>,
            current: (Int) -> Unit
        ): AppsAdapter {
            return AppsAdapter(
                applicationList = applicationList,
                current = current
            )
        }

        const val open: Int = 0
        const val info: Int = 1
        const val delete: Int = 2
    }
}