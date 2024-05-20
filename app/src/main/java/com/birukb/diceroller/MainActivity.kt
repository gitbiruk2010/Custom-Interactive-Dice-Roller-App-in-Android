package com.birukb.diceroller

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.birukb.diceroller.ui.theme.DiceRollerTheme
import com.birukb.diceroller.ui.theme.PurpleGrey80

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerTheme {
                DiceRollerApp(this)
            }
        }
    }
}

@Composable
fun DiceRollerApp(activity: MainActivity) {
    var diceType by remember { mutableStateOf(DiceType.D6) }
    var numberOfDice by remember { mutableIntStateOf(1) }
    val rollHistory = remember { mutableStateListOf<List<Int>>() }
    var showHistory by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DiceTypeSelector { diceType = it }
        NumberOfDiceSelector { numberOfDice = it }
        DiceWithButtonAndImage(diceType, numberOfDice, rollHistory, activity)
        Button(onClick = { showHistory = !showHistory }) {
            Text(if (showHistory) "Hide History" else "Show History")
        }
        if (showHistory) {
            RollHistory(rollHistory) {
                // Reset the app by clearing the roll history
                rollHistory.clear()
            }
        }
    }
}

@Composable
fun DiceTypeSelector(onTypeSelected: (DiceType) -> Unit) {
    Row {
        DiceType.entries.forEach { type ->
            Button(
                onClick = {
                    Log.d("DiceTypeSelector", "Selected: $type")
                    onTypeSelected(type)
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = type.name)
            }
        }
    }
}

@Composable
fun NumberOfDiceSelector(onNumberSelected: (Int) -> Unit) {
    var sliderPosition by remember { mutableFloatStateOf(1f) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Number of Dice: ${sliderPosition.toInt()}")
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onNumberSelected(it.toInt())
            },
            valueRange = 1f..10f,
            steps = 8
        )
    }
}

@Composable
fun DiceWithButtonAndImage(diceType: DiceType, numberOfDice: Int, rollHistory: MutableList<List<Int>>, activity: MainActivity, modifier: Modifier = Modifier) {
    var results by remember { mutableStateOf(List(numberOfDice) { 1 }) }
    LaunchedEffect(numberOfDice) {
        results = List(numberOfDice) { 1 }
    }

    val imageResources = results.map { getImageResourceForResult(diceType, it) }

    Log.d("DiceWithButtonAndImage", "Dice Type: $diceType, Results: $results, Image Resources: $imageResources")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            imageResources.forEach { imageResource ->
                Image(
                    painter = painterResource(imageResource),
                    contentDescription = results.toString(),
                    modifier = Modifier.padding(4.dp).size(64.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            results = List(numberOfDice) { (1..diceType.sides).random() }
            rollHistory.add(0, results) // Add to the start of the list
            if (rollHistory.size > 10) {
                rollHistory.removeLast() // Limit the history to 10 entries
            }
            Log.d("DiceWithButtonAndImage", "Rolled: $results")

            // Play sound effect
            val mediaPlayer = MediaPlayer.create(activity, R.raw.audio) // Use your sound file
            mediaPlayer.setOnCompletionListener {
                it.release()
                Log.d("MediaPlayer", "MediaPlayer released")
            }
            mediaPlayer.start()

        }) {
            Text(stringResource(R.string.roll))
        }
    }
}

fun getImageResourceForResult(diceType: DiceType, result: Int): Int {
    return when (diceType) {
        DiceType.D4 -> when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            else -> R.drawable.dice_4
        }
        DiceType.D6 -> when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        DiceType.D8 -> when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            7 -> R.drawable.dice_1 // Reuse images
            else -> R.drawable.dice_2
        }
        DiceType.D10 -> when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            7 -> R.drawable.dice_1 // Reuse images
            8 -> R.drawable.dice_2
            9 -> R.drawable.dice_3
            else -> R.drawable.dice_4
        }
        DiceType.D12 -> when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            7 -> R.drawable.dice_1 // Reuse images
            8 -> R.drawable.dice_2
            9 -> R.drawable.dice_3
            10 -> R.drawable.dice_4
            11 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        DiceType.D20 -> when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            7 -> R.drawable.dice_1 // Reuse images
            8 -> R.drawable.dice_2
            9 -> R.drawable.dice_3
            10 -> R.drawable.dice_4
            11 -> R.drawable.dice_5
            12 -> R.drawable.dice_6
            13 -> R.drawable.dice_1 // Reuse images
            14 -> R.drawable.dice_2
            15 -> R.drawable.dice_3
            16 -> R.drawable.dice_4
            17 -> R.drawable.dice_5
            18 -> R.drawable.dice_6
            19 -> R.drawable.dice_1 // Reuse images
            else -> R.drawable.dice_2
        }
    }
}

@Composable
fun RollHistory(rollHistory: List<List<Int>>, onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurpleGrey80)
            .border(1.dp, Color.Black)
            .padding(8.dp)
    ) {
        Text(
            text = "Roll History",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Divider(color = Color.LightGray, thickness = 1.dp)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Adjust the height as needed
        ) {
            items(rollHistory) { roll ->
                Text(text = "Roll: ${roll.joinToString()}")
                Divider(color = Color.LightGray)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onReset,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            )
        ) {
            Text("Restart")
        }
    }
}


enum class DiceType(val sides: Int) {
    D4(4), D6(6), D8(8), D10(10), D12(12), D20(20)
}

@Preview
@Composable
fun DiceRollerAppPreview() {
    DiceRollerTheme {
        DiceRollerApp(MainActivity())
    }
}
