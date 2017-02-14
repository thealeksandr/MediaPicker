package com.thealeksandr.mediapicker.adapters

/**
 * Created by Aleksandr Nikiforov on 2/14/17.
 */
interface OnItemClickListener<in T> {
    fun onClick(item: T)
}