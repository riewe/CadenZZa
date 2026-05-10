package by.riewe.cadence.presentation.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary

@Composable
fun ValidatedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean,
    errorMessage: String,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        isError = isError,
        supportingText = if (isError) {
            { Text(text = errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            focusedLabelColor = OrangePrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ValidatedTextFieldPreview() {
    CadenceTheme {
        ValidatedTextField(
            value = "Input text",
            onValueChange = {},
            label = "Email",
            isError = false,
            errorMessage = ""
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ValidatedTextFieldErrorPreview() {
    CadenceTheme {
        ValidatedTextField(
            value = "Invalid input",
            onValueChange = {},
            label = "Email",
            isError = true,
            errorMessage = "Please enter a valid email address"
        )
    }
}