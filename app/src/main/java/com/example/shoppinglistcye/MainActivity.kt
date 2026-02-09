package com.example.shoppinglistcye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.shoppinglistcye.ui.theme.ShoppingListCyETheme

//rutas
const val PANTALLA_LISTA = "lista"
const val PANTALLA_ANADIR = "anadir"

//modelo
data class Producto(
    val nombre: String,
    val comprado: Boolean = false
)

//main activity, contiene el tema y la navegación
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListCyETheme { //fuerzo a que se use el tema que yo pongo y no el predeterminado por el dispositivo movil
                Navegacion()
            }
        }
    }
}

//compose de navegación con dos pantallas
@Composable
fun Navegacion() {
    val navController = rememberNavController()

    val productos = remember {
        mutableStateListOf(
            Producto("Leche"),
            Producto("Pan"),
            Producto("Huevos")
        )
    }

    var productoRecienAnadido by remember { mutableStateOf<Producto?>(null) }

    NavHost(
        navController = navController,
        startDestination = PANTALLA_LISTA
    ) {
        composable(PANTALLA_LISTA) {
            PantallaListaProductos(
                navController,
                productos,
                productoRecienAnadido,
                onDeshacer = {
                    productos.remove(it)
                    productoRecienAnadido = null
                },
                onSnackbarMostrada = {
                    productoRecienAnadido = null
                }
            )
        }
        composable(PANTALLA_ANADIR) {
            PantallaAnadirProducto(
                navController,
                productos
            ) {
                productoRecienAnadido = it
            }
        }
    }
}

//composable de la primera pantalla, lista los productos, muestra cunatos hay pendientes, con tres botones para mostrar
//los tres productos, el boton de añadir y de borrar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaProductos(
    navController: NavController,
    productos: MutableList<Producto>,
    productoRecienAnadido: Producto?,
    onDeshacer: (Producto) -> Unit,
    onSnackbarMostrada: () -> Unit
) {
    val verdeApp = colorResource(id = R.color.verde)

    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    // 0 = Todos | 1 = Pendientes | 2 = Comprados
    var opcionSeleccionada by remember { mutableIntStateOf(0) }

    val productosFiltrados = when (opcionSeleccionada) {
        0 -> productos
        1 -> productos.filter { !it.comprado }
        2 -> productos.filter { it.comprado }
        else -> productos
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productoRecienAnadido) {
        if (productoRecienAnadido != null) {
            val resultado = snackbarHostState.showSnackbar(
                message = "Producto nuevo añadido",
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Short
            )

            if (resultado == SnackbarResult.ActionPerformed) {
                onDeshacer(productoRecienAnadido)
            } else {
                onSnackbarMostrada()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        //meto la imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.imagen_fondo),
            contentDescription = "Fondo de pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Estira la imagen
        )

        // scaffold transparente para que se vea el fondo
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = verdeApp
                    ),
                    title = { Text("ShoppingList") },
                    navigationIcon = {
                        // imagen del top bar
                        Box(
                            modifier = Modifier
                                .size(40.dp) // tamaño pequeño
                                .clip(CircleShape) // hago redonda la imagen
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pimiento),
                                contentDescription = "Imagen redonda",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop // recorto la imagen para que se ajuste
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(PANTALLA_ANADIR) },
                    containerColor = verdeApp
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        ) { padding ->

            Column(modifier = Modifier.padding(padding)) {

                // contador de los productos pendientes
                Text(
                    text = "Tienes ${productos.count { !it.comprado }} productos pendientes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                // filtros de los botones
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    listOf("Todos", "Pendientes", "Comprados").forEachIndexed { index, texto ->
                        SegmentedButton(
                            selected = opcionSeleccionada == index,
                            onClick = { opcionSeleccionada = index },
                            shape = SegmentedButtonDefaults.itemShape(index, 3),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = verdeApp,
                                inactiveContainerColor = verdeApp.copy(alpha = 0.6f)
                            ),
                            label = { Text(texto) }
                        )
                    }
                }

                // lista de productos
                LazyColumn {
                    items(productosFiltrados) { producto ->
                        ItemProducto(
                            producto = producto,
                            verdeApp = verdeApp,
                            onCheckedChange = { checked ->
                                val index = productos.indexOf(producto)
                                if (index != -1) {
                                    productos[index] =
                                        producto.copy(comprado = checked)
                                }
                            },
                            onDelete = {
                                productoAEliminar = producto
                                mostrarDialogo = true
                            }
                        )
                    }
                }
            }

            // dialogo de ocnfirmación de eliminación
            if (mostrarDialogo && productoAEliminar != null) {
                CuadroDialogo(
                    icon = Icons.Default.Delete,
                    dialogTitle = "Eliminar producto",
                    dialogText = "¿Seguro que quieres eliminar ${productoAEliminar!!.nombre}?",
                    onDismissRequest = { mostrarDialogo = false },
                    onConfirmation = {
                        productos.remove(productoAEliminar)
                        mostrarDialogo = false
                    }
                )
            }
        }
    }
}

//pantalla de añadir producto y botones de confirmacion y cancelar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAnadirProducto(
    navController: NavController,
    productos: MutableList<Producto>,
    onProductoAnadido: (Producto) -> Unit
) {
    val verdeApp = colorResource(id = R.color.verde)
    var texto by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        //imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.imagen_fondo),
            contentDescription = "Fondo de pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        //de nuevo scaffold transparente
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = verdeApp
                    ),
                    title = { Text("Añadir producto") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                Text(
                    text = "Nombre del producto",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.95f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.90f),
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (texto.isNotBlank()) {
                                val nuevoProducto = Producto(texto)
                                productos.add(nuevoProducto)
                                onProductoAnadido(nuevoProducto)
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar")
                    }

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

//item dle producto, hace que cada producto este dentro de una card
@Composable
fun ItemProducto(
    producto: Producto,
    verdeApp: Color,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // añade espacio entre los elementos
        colors = CardDefaults.cardColors(
            containerColor = verdeApp
        ),
        elevation = CardDefaults.cardElevation(4.dp), // Sombra para la card
        shape = MaterialTheme.shapes.medium, // Bordes redondeados
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Espaciado interno
            verticalAlignment = Alignment.CenterVertically
        ) {
            // chaeck de comprado
            Checkbox(
                checked = producto.comprado,
                onCheckedChange = onCheckedChange,
            )

            // nombre del producto
            Text(
                text = producto.nombre,
                modifier = Modifier.weight(1f), // Rellenar el espacio disponible
                style = MaterialTheme.typography.bodyLarge
            )

            // boton para eliminar el producto
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar producto"
                )
            }
        }
    }
}

//dialogo de confirmación para cuando queremos eliminar un producto de la lista
@Composable
fun CuadroDialogo(
    icon: ImageVector,
    dialogTitle: String,
    dialogText: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(dialogTitle) },
        text = {
            if (cargando) {
                CircularProgressIndicator()
            } else {
                Text(dialogText)
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    cargando = true
                    scope.launch {
                        delay(1000)
                        onConfirmation()
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}
