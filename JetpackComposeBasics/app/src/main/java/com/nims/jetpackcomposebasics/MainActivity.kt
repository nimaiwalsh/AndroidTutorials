package com.nims.jetpackcomposebasics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nims.jetpackcomposebasics.ui.theme.JetpackComposeBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeBasicsTheme {
                MyApp()
            }
        }
    }
}


@Composable
private fun MyApp() {
    /** rememberSaveable:
     * This will save each state surviving configuration changes (such as rotations) and process death.
     */
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
    } else {
        Greetings()
    }
}

@Composable
private fun Greetings(names: List<String> = List(1000) { "$it" }) {
    Surface(color = MaterialTheme.colors.background) {

        /** Note: LazyColumn and LazyRow are equivalent to RecyclerView in Android Views.
         *
         * LazyColumn doesn't recycle its children like RecyclerView. It emits new Composables as you scroll
         * through it and is still performant, as emitting Composables is relatively cheap compared to
         * instantiating Android Views.
         * */

        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(items = names) { name ->
                Greeting(name = name)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    // To preserve state across recompositions, remember the mutable state using remember.
    // remember is used to guard against recomposition, so the state is not reset.
    var expanded by remember { mutableStateOf(false) }

    /** ANIMATION
     * Any animation created with animate*AsState is interruptible. This means that if the target value changes in the
     * middle of the animation, animate*AsState restarts the animation and points to the new value.
     * Interruptions look especially natural with spring-based animations
     * */
    val extraPadding by animateDpAsState(
        targetValue = if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        )
    )

    val animateColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colors.secondary else MaterialTheme.colors.primary,
    )

    // A surface container using the 'background' color from the theme
    Surface(
        color = animateColor,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    // ensure padding is never negative to avoid a crash.
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "Hello,")
                Text(
                    text = "$name!",
                    // sometimes you need to deviate slightly from the selection of colors and font styles.
                    // In those situations it's better to base your color or style on an existing one.
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Show less" else "Show more")
            }
        }
    }
}

/* ===== ON-BOARDING SCREEN ====== */

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

/* ===== PREVIEWS ====== */
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    JetpackComposeBasicsTheme {
        Greetings()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    JetpackComposeBasicsTheme {
        OnboardingScreen(onContinueClicked = {}) // Do nothing on click
    }
}
