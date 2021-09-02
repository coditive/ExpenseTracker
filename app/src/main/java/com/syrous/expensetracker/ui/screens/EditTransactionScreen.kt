package com.syrous.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.syrous.expensetracker.R
import com.syrous.expensetracker.ui.theme.Blue500
import com.syrous.expensetracker.ui.theme.Surface
import com.syrous.expensetracker.ui.theme.White
import org.w3c.dom.Text

@Preview
@Composable
fun EditTransactionScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(scrollState)
    ) {
        AddTransactionLayout()
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            colors = buttonColors(backgroundColor = Blue500, contentColor = White)
        ) {
            Text(
                text = "Save Transaction",
                fontSize = 14.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Preview
@Composable
fun AddTransactionLayout() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Surface)
    ) {
        InputTextFieldComposable()
        InputTextFieldComposable()
        InputTextFieldComposable()
        InputTextFieldComposable()
        InputTextFieldComposable()
        InputTextFieldComposable()
    }
}




@Preview
@Composable
fun InputTextFieldComposable() {
    var text by remember {
        mutableStateOf("")
    }
     TextField(
         value = text,
         onValueChange = {
             text = it
         },
         label = { Text(text = "Title") },
         modifier = Modifier
             .background(Surface)
             .fillMaxWidth()
             .padding(16.dp),
         trailingIcon = { Icon(painter = painterResource(id = R.drawable.ic_baseline_calendar), contentDescription = "")}
     )
}

@Preview
@Composable
fun AutoCompleteTextComposable() {
    var text by remember {
        mutableStateOf("")
    }
    val suggestions = listOf<String>()

    OutlinedTextField(
        value = text,
        onValueChange = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
        ,
        label = { Text(text = "Title") }
    )

    DropdownMenu(
        expanded = suggestions.isNotEmpty(),
        onDismissRequest = { },
        modifier = Modifier.fillMaxWidth(),
        properties = PopupProperties(focusable = false)
    ) {
        suggestions.forEach{
            label ->
            DropdownMenuItem(onClick = {
                onOptionSelected(label)
            }) {
                Text(text = label)
            }
        }
    }
}

fun onOptionSelected(text: String) {

}

