package com.syrous.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syrous.expensetracker.ui.theme.Surface


@Preview
@Composable
fun TransactionDetailsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Text(
            text = "Title",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 32.dp, start = 16.dp)
        )

        Text(
            text = "Samsung level earphone",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )

        Text(
            text = "Amount",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
        )

        Text(
            text = "Amount",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )

        Text(
            text = "Transaction type",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
        )

        Text(
            text = "Income",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )

        Text(
            text = "Tag",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
        )

        Text(
            text = "Personal Spendings",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )

        Text(
            text = "When",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
        )

        Text(
            text = "Sunday, 18 Dec 2021",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )
        Text(
            text = "Note",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
        )

        Text(
            text = "Note",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )
        Text(
            text = "Created At",
            fontSize = 12.sp,
            letterSpacing = (0.033333).sp,
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
        )

        Text(
            text = "Sunday, 18 Dec 2021",
            fontSize = 16.sp,
            letterSpacing = (0.009375).sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )
    }
}