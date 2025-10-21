package com.kotliners.piedrapapeltijera


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kotliners.piedrapapeltijera.ui.theme.PiedraPapelTijeraTheme
import kotlinx.coroutines.delay

/*Rutas*/
sealed class Screen(val route: String, val title: String){
    data object Splash : Screen("splash","Splash")
    data object Home : Screen( "home", "Home")
    data object History : Screen( "history", "Historial")
    data object Ranking : Screen( "ranking", "Ranking")
    data object Setting : Screen("setting", "Ajustes")
    data object Help : Screen("help","Ayuda")

    data object Game : Screen("game","Juego")
}

//Activity principal
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(0xFF000000.toInt()),
            navigationBarStyle = SystemBarStyle.dark(0xFF000000.toInt())
        )
        setContent {
            AppRoot()
        }
    }
}


//Raiz de la app
@Composable
fun AppRoot() {
    PiedraPapelTijeraTheme {
        val nav = rememberNavController()
        MaterialTheme {
            NavHost(
                navController = nav,
                startDestination = Screen.Splash.route
            ){
                composable(Screen.Splash.route) {SplashScreen(nav) }
                composable(Screen.Home.route) { AppScaffold(nav) { HomeScreen(nav)} }
                composable(Screen.History.route) { AppScaffold(nav) { HistoryScreen()} }
                composable(Screen.Ranking.route) { AppScaffold(nav) { RankingScreen()} }
                composable(Screen.Setting.route) { AppScaffold(nav) { SettingScreen()} }
                composable(Screen.Help.route) { AppScaffold(nav) { HelpScreen()} }
                composable(Screen.Game.route) {AppScaffold(nav) { GameScreen()} }
            }
        }
    }
}

//Pantalla de Inicio
@Composable
fun HomeScreen(nav: NavHostController) {
    //Diseño del HOME
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //Boton "Jugar"
        Button(
            onClick = { nav.safeNavigate(Screen.Game.route)},
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(72.dp)
                .shadow(20.dp, RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF),
                contentColor = Color.Black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Jugar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

//Pantalla de Bienvenida
@Composable
fun SplashScreen(nav: NavHostController){
    LaunchedEffect(Unit) {
        delay(5000)
        nav.navigate(Screen.Home.route){
            popUpTo(Screen.Splash.route)  { inclusive = true }
            launchSingleTop = true
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_splash),
            contentDescription = "Bienvenida Kotliners",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

//Pantalla stub Historial
@Composable
fun HistoryScreen() = Center("Historial de Partidas")

//Pantalla stub Ranking
@Composable
fun RankingScreen() = Center ( "Ranking de Jugadores")

//Pantalla stub Ajustes
@Composable
fun SettingScreen() = Center ( "Ajustes")

//Pantalla stub Ayuda
@Composable
fun HelpScreen() = Center ( "Ayuda y Reglas")

//Pantalla Juego
@Composable
fun GameScreen() = Center ( "Juego")

//Scaffold comun para todas las pantallas menos la de Bienvenida
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    nav: NavHostController,
    content: @Composable () -> Unit
) {
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: Screen.Home.route

    val title = when (current) {
        Screen.Home.route -> Screen.Home.title
        Screen.History.route -> Screen.History.title
        Screen.Ranking.route -> Screen.Ranking.title
        Screen.Setting.route -> Screen.Setting.title
        Screen.Help.route -> Screen.Help.title
        Screen.Game.route -> Screen.Game.title
        else -> "Inicio"
    }

    val items = listOf(
        Screen.Home,
        Screen.History,
        Screen.Ranking,
        Screen.Setting,
        Screen.Help
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BrandBar(title)},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = (current == screen.route),
                        onClick = { nav.safeNavigate(screen.route) },
                        icon = {
                            when (screen) {
                                Screen.Home -> Icon(Icons.Default.Home, contentDescription = null)
                                Screen.History -> Icon(
                                    Icons.Default.History,
                                    contentDescription = null
                                )

                                Screen.Ranking -> Icon(
                                    Icons.Default.Leaderboard,
                                    contentDescription = null
                                )

                                Screen.Setting -> Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )

                                Screen.Help -> Icon(
                                    Icons.Default.HelpOutline,
                                    contentDescription = null
                                )

                                else -> {}
                            }
                        },
                        label = { Text(screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor   = Color.White,
                            indicatorColor      = Color(0xFFFFEA00),
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White
                            )
                    )
                }
            }
        }
    ) { inner ->
        Box(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color.Black)

        ) {
            content()
        }
    }
}

//Extesión de NavHostController para navegar sin duplicar
private fun NavHostController.safeNavigate(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

//Helper visual para centrar el texto a pantalla completa
@Composable
private fun Center(text: String) {
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}

//Encabezado de marca
@Composable
fun BrandBar(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = 0.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_prov_kotliners),
            contentDescription = "Logo Kotliners",
            modifier = Modifier.height(64.dp),
            contentScale = ContentScale.Fit
        )
        Text(
         text = title,
            color = Color(0xFFFFEA00),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        )
    }
}


