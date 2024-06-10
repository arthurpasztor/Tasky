package com.example.tasky.agenda.presentation

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.domain.DetailItemType
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.greenSaveButtonStyle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.parcelize.Parcelize

@Destination
@Composable
fun TextEditorRoot(
    resultNavigator: ResultBackNavigator<TextEditorResponse>,
    text: String,
    type: DetailItemType
) {
    TextEditorScreen(
        text = text,
        type = type,
        onBackNavigation = {
            resultNavigator.navigateBack()
        },
        onSave = { newText ->
            resultNavigator.navigateBack(result = TextEditorResponse(newText, type))
        }
    )
}

@Preview
@Composable
private fun TextEditorScreen(
    text: String = "Title",
    type: DetailItemType = DetailItemType.TITLE,
    onBackNavigation: () -> Unit = {},
    onSave: (newText: String) -> Unit = {}
) {
    val headerPadding = dimensionResource(R.dimen.padding_20)

    val headerText = when (type) {
        DetailItemType.TITLE -> stringResource(id = R.string.edit_title)
        DetailItemType.DESCRIPTION -> stringResource(id = R.string.edit_description)
    }

    val fontSize = when (type) {
        DetailItemType.TITLE -> 26.sp
        DetailItemType.DESCRIPTION -> 16.sp
    }

    var mutableText by remember {
        mutableStateOf(text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Row {
            ArrowBackButton { onBackNavigation.invoke() }
            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = headerPadding),
                text = headerText.uppercase(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(Modifier.weight(1f))
            ClickableText(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = headerPadding),
                text = AnnotatedString(stringResource(id = R.string.save)),
                style = greenSaveButtonStyle,
                onClick = {
                    onSave.invoke(mutableText)
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

        TextField(
            modifier = Modifier
                .fillMaxSize(),
            value = mutableText,
            textStyle = TextStyle(
                fontSize = fontSize,
                color = Color.Black
            ),
            onValueChange = { mutableText = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

@Parcelize
data class TextEditorResponse(val newText: String, val type: DetailItemType) : Parcelable
