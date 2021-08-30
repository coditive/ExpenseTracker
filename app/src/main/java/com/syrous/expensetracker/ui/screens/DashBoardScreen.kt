package com.syrous.expensetracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syrous.expensetracker.R
import com.syrous.expensetracker.ui.theme.Background
import com.syrous.expensetracker.ui.theme.Surface

@Preview
@Composable
fun DashBoardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TotalBalanceComposable()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement= Arrangement.SpaceBetween
        ) {
            ContentCardComposable()
            ContentCardComposable()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp
            )
        }
    }

}

@Preview
@Composable
fun TotalBalanceComposable() {
    Box(modifier = Modifier
        .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .height(124.dp)
                .fillMaxWidth()
                .background(Surface)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.text_total_balance),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                letterSpacing = (0.16667).sp
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )

            Text(
                text = "$23,104",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                letterSpacing = 0.sp
            )

        }
    }
}

@Preview
@Composable
fun ContentCardComposable() {
    Box(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .background(Surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_income
                    ),
                    contentDescription = ""
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp, start = 12.dp)
            ) {
                Text(
                    text = "TOTAL INCOME",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    fontSize = 10.sp,
                    letterSpacing = (0.16667).sp
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )

                Text(
                    text = "$23,104",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    fontSize = 16.sp,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}


@Preview
@Composable
fun TransactionComposable() {
    Box (
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Surface)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_others),
                    contentDescription = "",
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                )
            }
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 24.dp, bottom = 24.dp)
                    .weight(2f)
            ){
                Text(
                    text = "Brochure Design",
                    fontSize = 16.sp
                )

                Spacer(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(16.dp)
                )

                Text(
                    text = "Business Work",
                    fontSize = 12.sp
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = "$12",
                    fontSize = 20.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}