package com.raedev.forms.items

import android.view.View
import com.raedev.forms.FormType
import com.raedev.forms.R
import com.raedev.forms.dialog.FormSelectDialogFragment
import com.raedev.forms.dict.FormDataProvider
import com.raedev.forms.dict.FormSelectItem
import com.raedev.forms.listener.FormSelectChangedListener
import com.raedev.forms.listener.FormViewHolder

/**
 * 选择框表单
 * @author RAE
 * @date 2022/09/15
 * @copyright Copyright (c) https://github.com/raedev All rights reserved.
 */
class SelectFormItem(
    /** 表单数据提供 */
    private val provider: FormDataProvider,
    label: String,
    name: String,
    value: String?,
    required: Boolean = false
) : FormItem(label, name, value), FormSelectChangedListener {

    override val layoutId: Int = R.layout.form_item_select
    override val formType: Int = FormType.Select.ordinal
    override var hint: String? = null
        get() = if (field == null) "请选择" else field
    var dialogTitle: String? = null
        get() = if (field == null) "请选择$label" else field

    /** 字典Key过滤 */
    var dictFilter: String? = null

    /** 是否显示全路径 */
    var enableFullPath: Boolean = true

    init {
        this.required = required
    }

    override fun onBindViewEvent(holder: FormViewHolder) {
        super.onBindViewEvent(holder)
        holder.itemView.setOnClickListener(this::onItemClick)
    }

    override fun onUnBindViewEvent(holder: FormViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    override fun onBindViewHolder(holder: FormViewHolder) {
        this.provider.filter = this.dictFilter
        val label = this.value?.let {
            if (enableFullPath) {
                provider.getItem(it)?.let { item -> provider.getFullLabel(item) }
            } else provider.getItemLabel(it)
        }
        holder.setVisibility(R.id.tv_value, true)
        holder.setText(R.id.tv_value, label)
        holder.setHint(R.id.tv_value, this.hint, this.viewonly)
    }

    private fun onItemClick(view: View) {
        val manager = fragmentManager ?: return
        FormSelectDialogFragment.show(manager, this.value, this.dialogTitle).apply {
            this.provider = this@SelectFormItem.provider
            this.listener = this@SelectFormItem
            this.provider.filter = this@SelectFormItem.dictFilter
        }
    }

    override fun onFormSelectChanged(item: FormSelectItem) {
        val newValue = provider.getItemValue(item.value)
        if (this.value != newValue) {
            onValueChanged(newValue, true)
        }
    }
}