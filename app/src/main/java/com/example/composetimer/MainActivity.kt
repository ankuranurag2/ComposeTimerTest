package com.example.composetimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composetimer.ui.theme.ComposeTimerTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutineScope = rememberCoroutineScope()

                    var firstPageTime by remember { mutableIntStateOf(0) }
                    var secondPageTime by remember { mutableIntStateOf(0) }
                    var thirdPageTime by remember { mutableIntStateOf(0) }
                    var job: Job? = null

                    var totalTime by remember { mutableIntStateOf(0) }
                    LaunchedEffect(Unit) {
                        while (isActive) {
                            delay(1.seconds)
                            totalTime++

                            Log.d("TIMERRR","Total time : $totalTime, 1st : $firstPageTime, 2nd : $secondPageTime, 3rd $thirdPageTime")
                        }
                    }

                    Greeting(
                        onPageChanged = { pageIndex ->
                            job?.cancel()
                            job = coroutineScope.launch {
                                while (isActive) {
                                    delay(1.seconds)
                                    when (pageIndex+1) {
                                        1 -> {
                                            firstPageTime++
                                        }

                                        2 -> {
                                            secondPageTime++
                                        }

                                        3 -> {
                                            thirdPageTime++
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Greeting(onPageChanged: (page: Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { page ->
            onPageChanged(page)
        }
    }
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray),
            userScrollEnabled = false
        ) { page ->
            // Our page content
            Text(
                text = "Page: ${page + 1}",
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        if (page < pagerState.pageCount)
                            pagerState.animateScrollToPage(page + 1)
                    }
                }
            )
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }
    }
}