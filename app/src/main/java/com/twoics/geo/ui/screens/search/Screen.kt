package com.twoics.geo.ui.screens.search

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.twoics.geo.data.models.BookMark
import kotlinx.coroutines.*
import org.osmdroid.views.MapView
import java.util.*

class SearchScreen(
    private var viewModel: SearchViewModel,
) {

    private lateinit var sizes: SearchScreenSizes

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Screen() {
        val scaffoldState = rememberScaffoldState()
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        MaterialTheme {
            BoxWithConstraints {
                sizes = SearchScreenSizes(this.maxWidth)
                Scaffold(
                    topBar = {
                        TopBar(interactionSource)
                    },
                    scaffoldState = scaffoldState

                ) { contentPadding ->
                    Box(
                        modifier = Modifier
                            .padding(contentPadding)
                    ) {
                        val sheetState = rememberBottomSheetState(
                            initialValue = BottomSheetValue.Collapsed
                        )
                        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                            bottomSheetState = sheetState
                        )
                        BottomSheetScaffold(
                            scaffoldState = bottomSheetScaffoldState,
                            sheetContent = {
                                SheetContent(interactionSource, isPressed)
                            },
                            sheetPeekHeight = sizes.sheetPeakHeight,
                            sheetShape = RoundedCornerShape(
                                sizes.sheetCorner,
                                sizes.sheetCorner,
                                0.dp,
                                0.dp
                            ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(contentPadding)
                            ) {

                                MapContent(
                                    Modifier.fillMaxSize(),
                                    viewModel.mapView
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBar(interactionSource: MutableInteractionSource) {
        Column {
            val adding = remember { mutableStateOf(false) }
            val adding_confirmed = remember { mutableStateOf(false) }
            var text = remember { mutableStateOf("") }
            Row {
                if (adding.value) {
                    Button(
                        onClick = {
                            adding.value = !adding.value
                            adding_confirmed.value = !adding_confirmed.value

                            updateBookmarkState()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(1f, 1f, 1f)),
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text("Add", fontSize = 30.sp)
                    }
                }
                Button(
                    onClick = {
                        adding.value = !adding.value

                        updateBookmarkState()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(1f, 1f, 1f)),
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(if (!adding.value) "Add bookmark" else "Cancel", fontSize = 30.sp)
                }
            }

            if (adding.value) {
                Row {
                    TextField(
                        value = text.value,
                        onValueChange = { if (adding.value) text.value = it else text.value = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter bookmark name") }
                    )
                }
            }


            if (adding_confirmed.value) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val hours = c.get(Calendar.HOUR_OF_DAY)
                val minutes = c.get(Calendar.MINUTE)

                val values = mutableListOf<String>()

                val coroutine = rememberCoroutineScope()
                val dpd = DatePickerDialog(
                    LocalContext.current,
                    { view, year, monthOfYear, dayOfMonth ->
                        values += year.toString()
                        values += monthOfYear.toString()
                        values += dayOfMonth.toString()

                        val tpd = TimePickerDialog(
                            view.context,
                            { view, hourOfDay, minute ->
                                values += hourOfDay.toString()
                                values += minute.toString()

                                val timeString =
                                    values[0] + "-" + values[1] + "-" + values[2] + "." + values[3] + ":" + values[4]
                                val cords = viewModel.getCurrentCords()

                                val newBookmark = BookMark(
                                    latitude = cords.latitude,
                                    longitude = cords.longitude,
                                    description = if (text.value !== "") text.value else "Null",
                                    time = timeString
                                )

                                viewModel.addBookmark(newBookmark)
                                viewModel.onEvent(SearchEvent.updateBookMarks)
                                adding_confirmed.value = false
                                text.value = ""

                                coroutine.launch {
                                    val press = PressInteraction.Press(Offset.Zero)
                                    interactionSource.emit(press)
                                    interactionSource.emit(PressInteraction.Release(press))
                                }
                            },
                            hours,
                            minutes,
                            true
                        )
                        tpd.show()

                    },
                    year,
                    month,
                    day
                )
                dpd.show()


            }
        }
    }

    private fun updateBookmarkState() {
        viewModel.onEvent(SearchEvent.changeAddingState)
    }

    @Composable
    private fun Slider() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp),
        ) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = "Menu"
            )
        }
    }

    @Composable
    private fun SheetContent(interactionSource: MutableInteractionSource, isPressed: Boolean) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(sizes.sheetMaxHeight + 20)
                .shadow(25.dp),
            backgroundColor = Color.White
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider()
                val items = viewModel.getAll()

                if (isPressed || true) LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(20.dp),
                    content = {
                        itemsIndexed(items) { id, data ->
                            BookMarkItem(data, interactionSource)
                        }
                    },
                )
            }
        }
    }

    @Composable
    private fun BookMarkItem(
        bookMark: BookMark,
        interactionSource: MutableInteractionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row() {
                Text(text = bookMark.description, fontSize = 30.sp, fontWeight = FontWeight(600))
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Button(
                    onClick = { viewModel.deleteBookMark(bookMark) },
                    interactionSource = interactionSource,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(contentColor = Color.Red, backgroundColor = Color.White),
                    modifier = Modifier.size(40.dp, 40.dp)
                ) {
                    Text(text = "X")
                }
            }

            val coroutineScope = rememberCoroutineScope()
            val text = remember { mutableStateOf("") }

            val getLocationOnClick: () -> Unit = {
                coroutineScope.launch {
                    text.value = viewModel.getPlace(bookMark.latitude, bookMark.longitude)
                }
            }
            getLocationOnClick()

            Text(text = text.value, fontSize = 24.sp, fontWeight = FontWeight(500))
            Text(text = timePipe(bookMark.time), fontSize = 24.sp, fontWeight = FontWeight(500))
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(10.dp))

        }
    }

    private fun timePipe(time: String): String {
        val splittedTime = time.split(".")
        var resultString = ""
        resultString += splittedTime[0].split("-")[2]
        resultString += "."
        resultString += splittedTime[0].split("-")[1]
        resultString += "."
        resultString += splittedTime[0].split("-")[0]
        resultString += ", "
        resultString += splittedTime[1]
        return resultString
    }

    @Composable
    private fun MapContent(
        modifier: Modifier,
        mapViewState: MapView,
        onLoad: ((map: MapView) -> Unit)? = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            AndroidView(
                { mapViewState },
                modifier
            ) { mapView -> onLoad?.invoke(mapView) }
        }
    }
}