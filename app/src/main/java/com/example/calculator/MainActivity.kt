    package com.example.calculator

    import android.R.attr.onClick
    import android.media.Image
    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.content.MediaType.Companion.Image
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.interaction.collectIsPressedAsState
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.WindowInsets
    import androidx.compose.foundation.layout.aspectRatio
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.navigationBars
    import androidx.compose.foundation.layout.navigationBarsPadding
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.layout.windowInsetsPadding
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.lightColorScheme
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.calculator.ui.theme.CalculatorTheme
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import org.w3c.dom.Text
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.setValue
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.runtime.*
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material3.OutlinedTextFieldDefaults

    import androidx.compose.ui.composed
    import androidx.compose.ui.semantics.Role.Companion.Image
    import com.example.calculator.R

    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                CalculatorTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        ScreenButton(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ScreenButton(modifier: Modifier = Modifier) {
        var firstNumber by rememberSaveable {mutableStateOf("")}
        var secondNumber by rememberSaveable {mutableStateOf("")}
        var operator by rememberSaveable {mutableStateOf("")}
        var result by rememberSaveable {mutableStateOf("")}
        var activeField by remember { mutableStateOf(1) }

        val firstInteraction = remember { MutableInteractionSource() }
        val firstPressed by firstInteraction.collectIsPressedAsState()

        LaunchedEffect(firstPressed) {
            if (firstPressed) activeField = 1
        }

        val secondInteraction = remember { MutableInteractionSource() }
        val secondPressed by secondInteraction.collectIsPressedAsState()

        LaunchedEffect(secondPressed) {
            if (secondPressed) activeField = 2
        }

        fun enterDigit(value: String) {
            result = ""

            if (activeField == 1) {
                if (value == "." && firstNumber.contains(".")) return
                firstNumber += value
            } else {
                if (value == "." && secondNumber.contains(".")) return
                secondNumber += value
            }
        }

        fun deleteLast() {
            result = ""

            if (activeField == 1) {
                if (firstNumber.isNotEmpty()) {
                    firstNumber = firstNumber.dropLast(1)
                }
            } else {
                if (secondNumber.isNotEmpty()) {
                    secondNumber = secondNumber.dropLast(1)
                }
            }
        }

        fun clearAll() {
            firstNumber = ""
            secondNumber = ""
            operator = ""
            result = ""
            activeField = 1
        }

        fun changeSign() {
            result = ""

            if (activeField == 1) {
                val number = firstNumber.toDoubleOrNull()
                if (number != null) {
                    firstNumber = (-number).toString()
                }
            } else {
                val number = secondNumber.toDoubleOrNull()
                if (number != null) {
                    secondNumber = (-number).toString()
                }
            }
        }


        fun applyPercentage() {
            result = ""

            if (activeField == 1) {
                val number = firstNumber.toDoubleOrNull()
                if (number != null) {
                    firstNumber = (number / 100).toString()
                }
            } else {
                val number = secondNumber.toDoubleOrNull()
                if (number != null) {
                    secondNumber = (number / 100).toString()
                }
            }
        }

        fun calculate() {
            val first = firstNumber.toDoubleOrNull()
            val second = secondNumber.toDoubleOrNull()

            if (first == null || second == null) {
                result = "Enter both numbers"
                return
            }

            val answer = when (operator) {
                "+" -> first + second
                "-" -> first - second
                "×" -> first * second

                "÷" -> {
                    if (second == 0.0) {
                        result = "Cannot divide by zero"
                        return
                    }
                    first / second
                }

                else -> {
                    result = "Select an operator"
                    return
                }
            }

            result = if (answer.isFinite()) {
                java.math.BigDecimal.valueOf(answer)
                    .stripTrailingZeros()
                    .toPlainString()
            } else {
                "Invalid result"
            }
        }

        // 1. ROOT BOX: This holds the Image and the Calculator on top of it
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // 2. BACKGROUND IMAGE (Bottom Layer)
            Image(
                painter = painterResource(id = R.drawable.calculator),
                contentDescription = "Calculator Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 3. MAIN CALCULATOR LAYOUT (Top Layer)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // TOP SECTION: DISPLAY (Takes 35% of the screen height)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.Bottom // Pushes the text down towards the keypad
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { activeField = 1 }
                    ) {
                        OutlinedTextField(
                            value = firstNumber,
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            label = { Text("First number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = if (activeField == 1) Color(0xFF2196F3) else Color.Gray,
                                disabledLabelColor = Color.Black,
                                disabledContainerColor = Color.Unspecified
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { activeField = 2 }
                    ) {
                        OutlinedTextField(
                            value = secondNumber,
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            label = { Text("Second number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = if (activeField == 2) Color(0xFF2196F3) else Color.Gray,
                                disabledLabelColor = Color.Black,
                                disabledContainerColor = Color.Unspecified
                            )
                        )
                    }
                    Text(
                        text = if (operator.isEmpty()) "Operator: " else "Operator: $operator",
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                    )

                    Text(
                        text = if (result.isEmpty()) " " else "Result: $result",
                        fontSize = 28.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    )


                }

                Spacer(Modifier.height(120.dp))

                // BOTTOM SECTION: KEYPAD (Takes 65% of the screen height)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.65f)
                        .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // COLUMN 1
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ColumnButton("AC", Color(0xFFFFCC80), Modifier.weight(1f)) { clearAll() }
                            ColumnButton("7", modifier = Modifier.weight(1f)) { enterDigit("7") }
                            ColumnButton("4", modifier = Modifier.weight(1f)) { enterDigit("4") }
                            ColumnButton("1", modifier = Modifier.weight(1f)) { enterDigit("1") }
                            ColumnButton("%", modifier = Modifier.weight(1f)) { applyPercentage() }
                        }

                        // COLUMN 2
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ColumnButton("\u232B", Color(0xFFFFCC80), Modifier.weight(1f)) { deleteLast() }
                            ColumnButton("8", modifier = Modifier.weight(1f)) { enterDigit("8") }
                            ColumnButton("5", modifier = Modifier.weight(1f)) { enterDigit("5") }
                            ColumnButton("2", modifier = Modifier.weight(1f)) { enterDigit("2") }
                            ColumnButton("0", modifier = Modifier.weight(1f)) { enterDigit("0") }
                        }

                        // COLUMN 3
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ColumnButton("+/-", modifier = Modifier.weight(1f)) { changeSign() }
                            ColumnButton("9", modifier = Modifier.weight(1f)) { enterDigit("9") }
                            ColumnButton("6", modifier = Modifier.weight(1f)) { enterDigit("6") }
                            ColumnButton("3", modifier = Modifier.weight(1f)) { enterDigit("3") }
                            ColumnButton(".", modifier = Modifier.weight(1f)) { enterDigit(".") }
                        }

                        // COLUMN 4
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ColumnButton("\u00D7", modifier = Modifier.weight(1f)) { operator = "\u00D7"; result = "" }
                            ColumnButton("\u00F7", modifier = Modifier.weight(1f)) { operator = "\u00F7"; result = "" }
                            ColumnButton("-", modifier = Modifier.weight(1f)) { operator = "-"; result = "" }
                            ColumnButton("+", modifier = Modifier.weight(1f)) { operator = "+"; result = "" }
                            ColumnButton("=", Color(0xFFFFCC80), Modifier.weight(1f)) { calculate() }
                        }
                    }
                }
            }
        }
    }

    // Ensure your ColumnButton takes the modifier so the weight applies properly
    @Composable
    fun ColumnButton(
        value: String,
        color: Color = Color.LightGray,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {}
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .padding(4.dp) // Increase this to make buttons smaller, decrease to make them bigger
                .aspectRatio(1f), // Forces perfect circles
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp), // Keeps text centered perfectly
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = value,
                fontSize = 25.sp
            )
        }
    }







    @Preview(showBackground = true)
    @Composable
    fun PreviewScreenButton() {
        CalculatorTheme {
            ScreenButton()
        }
    }