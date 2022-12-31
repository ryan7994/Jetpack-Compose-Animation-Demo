package com.example.jetpackcomposeanimation

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.rememberMotionLayoutState
import com.example.jetpackcomposeanimation.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue


data class PagerItem(
        val name: String,
        @DrawableRes val drawableId: Int,
        val backgroundColor: Color,
        var isFocused: Boolean,
        var radiusSize: Float,
        val gradient: Brush,
        val accentColor: Color
)

private val items = listOf(
        PagerItem(name = "1", drawableId = R.drawable.sneaker1, backgroundColor = Color1, isFocused = true, 0f, Gradient1, ColorAccent1),
        PagerItem(name = "2", drawableId = R.drawable.sneaker2, backgroundColor = Color2, isFocused = false, 0f, Gradient2, ColorAccent2),
)

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class, ExperimentalMotionApi::class, ExperimentalAnimationApi::class)
@Composable
fun SneakerDetail() {

    val state = rememberPagerState(0)

    var size by remember { mutableStateOf(IntSize.Zero) }

    var isAnimating by remember { mutableStateOf(false) }
    var targetRippleColor by remember { mutableStateOf(Color.White) }
    var targetAccentColor by remember { mutableStateOf(Color.White) }

    val backgroundColor by animateColorAsState(
            targetValue = targetRippleColor,
            animationSpec = tween(durationMillis = 800),
            finishedListener = { isAnimating = false }
    )

    val accentColor by animateColorAsState(
            targetValue = targetAccentColor,
            animationSpec = tween(durationMillis = 800)
    )

    val motionState = rememberMotionLayoutState(initialProgress = 0f)

    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        size = it
                    }
                    .background(backgroundColor)
    ) {

        var detailView by remember { mutableStateOf(false) }
        var motionLayoutHeight by remember { mutableStateOf(0) }
        var pagerHeight by remember { mutableStateOf(0) }


        LaunchedEffect(state) {
            snapshotFlow { state.currentPage }.distinctUntilChanged().collect { currentPage ->
                targetRippleColor = items[currentPage].backgroundColor
                targetAccentColor = items[currentPage].accentColor
                isAnimating = true
            }
        }

        val radiusSize by animateFloatAsState(
                targetValue = if (isAnimating) size.height.toFloat() * 1.2f else 0f,
                animationSpec = tween(durationMillis = if (isAnimating) 500 else 0),
                finishedListener = {
                    isAnimating = false
                }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {

            val height = this.size.height
            val width = this.size.width

            drawCircle(color = targetRippleColor, radius = radiusSize, center = Offset(x = width * 3f / 4f, y = height))
        }



        Column(
                Modifier.fillMaxSize(),
        ) {


            val context = LocalContext.current
            val motionScene = remember {
                context.resources
                        .openRawResource(R.raw.motion_scene)
                        .readBytes()
                        .decodeToString()
            }
            MotionLayout(
                    motionScene = MotionScene(content = motionScene),
                    motionLayoutState = motionState,
                    modifier = Modifier
                            .onSizeChanged {
                                motionLayoutHeight = it.height
                            }) {


                val padding = motionProperties(id = "header1").value.int("padding").dp

                Spacer(modifier = Modifier
                        .size(20.dp)
                        .fillMaxHeight()
                        .layoutId("leftSpacer"))

                Spacer(modifier = Modifier
                        .size(20.dp)
                        .fillMaxWidth()
                        .layoutId("topSpacer"))

                Text(
                        text = "Men's",
                        modifier = Modifier
                                .layoutId("header1")
                                .padding(start = padding, top = padding),
                        style = RubikTypography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Light,
                        fontSize = 20.sp
                )
                Text(
                        text = "BOB MARLEY",
                        modifier = Modifier.layoutId("header_2"),
                        style = RubikTypography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                )
                Text(
                        text = "Sneaker",
                        modifier = Modifier.layoutId("header_3"),
                        style = RubikTypography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Thin,
                        fontSize = 20.sp
                )

                val spacerHeight = with(LocalDensity.current) { ((motionLayoutHeight - pagerHeight) / 2).toDp() }

                Spacer(modifier = Modifier
                        .size(spacerHeight)
                        .layoutId("spacer"))

                HorizontalPager(
                        count = items.count(), state = state,
                        modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .onSizeChanged {
                                    pagerHeight = it.height
                                }
                                .layoutId("pager")
                ) { page ->


                    var pageOffset by remember { mutableStateOf(0f) }

                    val rotationAngle by animateFloatAsState(
                            targetValue = if (detailView) 0f else if (pageOffset < .15f) 20f else 35f,
                            animationSpec = if (detailView) tween(durationMillis = 500) else tween(delayMillis = 400, durationMillis = 500)
                    )


                    Image(painter = painterResource(items[page].drawableId), contentDescription = "",
                            modifier = Modifier
                                    .graphicsLayer {
                                        pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                                        rotationZ = rotationAngle
                                        transformOrigin = TransformOrigin(0.5f, 0.7f)
                                    }
                                    .wrapContentSize(unbounded = true)

                    )
                }


            }

            ProductDetails(visible = detailView,
                    gradientColor = items[state.currentPage].gradient,
                    colorAccent = accentColor)


        }

        BottomRowButtons(
                isDetailView = detailView,
                onClickViewItem = {
                    detailView = !detailView
                    motionState.animateTo(
                            newProgress = if (detailView) 1f else 0f,
                            animationSpec = if (detailView) tween(durationMillis = 500) else tween(delayMillis = 400, durationMillis = 500)
                    )
                },
                onClickBack = {
                    detailView = !detailView
                    motionState.animateTo(
                            newProgress = if (detailView) 1f else 0f,
                            animationSpec = if (detailView) tween(durationMillis = 500) else tween(delayMillis = 400, durationMillis = 500)
                    )
                }
        )
    }

}

@Composable
fun BottomRowButtons(onClickViewItem: () -> Unit, isDetailView: Boolean, onClickBack: () -> Unit) {

    Row(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
    ) {


        AnimatedVisibility(visible = isDetailView,
                exit = fadeOut() + slideOutHorizontally { -it },
                enter = fadeIn(animationSpec = tween(delayMillis = 600)) + slideInHorizontally(animationSpec = tween(delayMillis = 600)) { -it }) {

            Button(
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonColor), shape = RoundedCornerShape(64.dp),
                    onClick = onClickBack
            ) {
                Text(text = "Back",
                        style = RubikTypography.labelLarge,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Black)
            }


        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor), shape = RoundedCornerShape(64.dp),
                onClick = {
                    if (!isDetailView) {
                        onClickViewItem.invoke()
                    }
                }
        ) {
            Text(text = if (isDetailView) "Buy item" else "View item",
                    style = RubikTypography.labelLarge,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black)
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProductDetails(visible: Boolean, gradientColor: Brush, colorAccent: Color) {
    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = visible, exit = fadeOut(), enter = fadeIn(animationSpec = tween(delayMillis = 600))) {
            Column(
                    modifier = Modifier
                            .padding(top = 16.dp)
                            .verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateEnterExit(
                                enter = slideInHorizontally(animationSpec = tween(delayMillis = 600)),
                                exit = slideOutHorizontally(targetOffsetX = { -it / 2 })
                        )
                ) {
                    Text(
                            text = "US 10", modifier = Modifier
                            .background(color = colorAccent, shape = RoundedCornerShape(32.dp))
                            .padding(16.dp),
                            color = Color.White
                    )
                    Spacer(modifier = Modifier.size(4.dp))

                    Text(
                            text = "EU 44", modifier = Modifier
                            .background(color = colorAccent, shape = RoundedCornerShape(32.dp))
                            .padding(16.dp),
                            color = Color.White
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                            text = "Length 27.6 cm",
                            color = Color.White,
                            modifier = Modifier
                                    .background(color = colorAccent, shape = RoundedCornerShape(32.dp))
                                    .padding(16.dp)
                    )
                }

                Text(
                        text = "Looking for Bob Marley shoes? The best gift for couples matching shoes. These bob marley shoes are suitable for women, can be as gifts for couples or lovers. The brand new rubber soles is soft and flexible while the high quality material can be stretched to fit your shoes most comfortable for couples, matching shoes, sneakers, boots for women and men. Explore a wide selection of the best bob marley shoes on AliExpress to find one that fits your need! Besides good quality brands, you’ll also find plenty of discounts when you shop for bob marley shoes during big sales. Don’t forget one crucial step - filter for items that offer bonus perks like free shipping & free return to make the most of your online shopping experience!</string>\n",
                        modifier = Modifier
                                .animateEnterExit(
                                        enter = slideInVertically(animationSpec = tween(delayMillis = 600), initialOffsetY = { it }),
                                        exit = slideOutVertically(targetOffsetY = { it })
                                )
                                .padding(20.dp),
                        color = Color.White,
                        style = RubikTypography.bodyLarge,
                        fontWeight = FontWeight.Normal
                )

            }

        }

        AnimatedVisibility(visible = visible, modifier = Modifier.align(Alignment.BottomCenter),
                exit = fadeOut(), enter = fadeIn(animationSpec = tween(delayMillis = 600))) {
            Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(gradientColor))
        }


    }
}

@Composable
private fun animate(targetValue: Float, finishedListener: (Float) -> Unit): State<Float> {
    return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = 5000),
            finishedListener = finishedListener
    )
}