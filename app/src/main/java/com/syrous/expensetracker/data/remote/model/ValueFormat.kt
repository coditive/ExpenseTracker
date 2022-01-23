package com.syrous.expensetracker.data.remote.model

import java.util.*

sealed class ValueFormat()

data class StringObject(val value: String): ValueFormat()

data class IntegerObject(val value: Int): ValueFormat()