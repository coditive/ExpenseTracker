package com.syrous.expensetracker.addusertransaction

import com.syrous.expensetracker.model.SubCategoryItem

interface SubCategoryListProvider {

    fun getSubCategoryList(): List<SubCategoryItem>
}