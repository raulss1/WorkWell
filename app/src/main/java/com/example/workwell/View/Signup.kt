package com.example.workwell.View

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.workwell.R
import com.example.workwell.ViewModel.AuthState
import com.example.workwell.ViewModel.AuthViewModel
import com.example.workwell.ui.theme.AzulBotonLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun Signup(modifier: Modifier = Modifier, navController: NavHostController, authViewModel: AuthViewModel) {
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        authViewModel.resetErrorFields()
    }
    LaunchedEffect(authState) {
        // Asegúrate de que usas 'Authenticated' aquí si así lo llamaste en el ViewModel
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("signup") { inclusive = true }
            }
            Toast.makeText(context, "SignUp correcto", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val image = painterResource(R.drawable.register_screen)
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5f,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            InitText(stringResource(R.string.signuptitle), stringResource(R.string.signupsubtitle))
            Spacer(modifier = Modifier.height(20.dp))
            AddCreateAccountForm(navController = navController, authViewModel = authViewModel)
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AddCreateAccountForm(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var showDatePicker by remember { mutableStateOf(false) }

    var customBlue = Color(0xFF1F41BB)
    var customBackground = Color(0xFFF1F4FF)
    val unfocusedIconColor = Color(0xFF6A6A6A)
    val unfocusedLabelColor = Color(0xFF626262)
    var customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = customBlue,
        unfocusedContainerColor = customBackground,
        focusedContainerColor = customBackground,

        focusedLeadingIconColor = customBlue,
        unfocusedLeadingIconColor = unfocusedIconColor,

        focusedLabelColor = customBlue,
        unfocusedLabelColor = unfocusedLabelColor
    )

    // Form state lives here (UI)
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var confirmPasswd by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            label = { Text("Nombre",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Introduce tu nombre",
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15),
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                authViewModel.usernameError.value = ""
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            label = { Text("Nombre de usuario",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Introduce el nombre de usuario",
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        AddErrorText(authViewModel.usernameError.value)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.emailError.value = ""
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            label = { Text("Email",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Introduce el email",
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        AddErrorText(authViewModel.emailError.value)
        Spacer(modifier = Modifier.height(16.dp))

        var passwordVisible by remember { mutableStateOf(false) }
        var passwordConfirmVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = passwd,
            onValueChange = { passwd = it },
            label = { Text("Contraseña",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Password,
                    contentDescription = "Introduce la contraseña",
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPasswd,
            onValueChange = {
                confirmPasswd = it
                authViewModel.confirmPasswdError.value = ""
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            label = { Text("Confirma la contraseña",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            ) },
            visualTransformation = if (passwordConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordConfirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordConfirmVisible = !passwordConfirmVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Password,
                    contentDescription = "Confirma la contraseña",
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        AddErrorText(authViewModel.confirmPasswdError.value)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = {
                birthDate = it
                authViewModel.birthDateError.value = ""
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            readOnly = true,
            label = { Text("Fecha de nacimiento",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            ) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Selecciona una fecha",
                    modifier = Modifier.clickable {
                        showDatePicker = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            colors = customTextFieldColors,
            singleLine = true,
            shape = RoundedCornerShape(15)
        )
        AddErrorText(authViewModel.birthDateError.value)

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { selectedDateMillis ->
                    if (selectedDateMillis != null) {
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                            Date(selectedDateMillis)
                        )
                        birthDate = formattedDate
                    }
                },
                onDismiss = {
                    showDatePicker = false
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    AddButtons(
        navController = navController,
        authViewModel = authViewModel,
        name = name,
        username = username,
        email = email,
        passwd = passwd,
        confirmPasswd = confirmPasswd,
        birthDate = birthDate
    )
}

@Composable
fun AddButtons(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    name: String,
    username: String,
    email: String,
    passwd: String,
    confirmPasswd: String,
    birthDate: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val authState by authViewModel.authState.observeAsState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(15),
                    spotColor = Color.Black.copy(alpha = 0.5f),
                    ambientColor = Color.Black.copy(alpha = 0.5f)
                )
                .fillMaxWidth()
                .height(46.dp),
            onClick = {
                authViewModel.signup(
                    name = name,
                    username = username,
                    email = email,
                    password = passwd,
                    confirmPassword = confirmPasswd,
                    birthDate = birthDate
                )
            },
            interactionSource = interactionSource,
            enabled = authState !is AuthState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) Color(0xFF163099) else Color(0xFF1F41BB),
                disabledContainerColor = Color(0xFF1F41BB).copy(alpha = 0.7f),
                disabledContentColor = Color.White.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(15)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (authState is AuthState.Loading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Creando cuenta...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                } else {
                    Text(
                        text = "Crear cuenta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }

        authState?.let {
            if (it is AuthState.Error) {
                Text(
                    text = it.message,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Ya tengo una cuenta",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF494949),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("login")
                }
        )
    }
}

class PastSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    //Validación
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val datePickerState = rememberDatePickerState(
        initialSelectedDate = null,
        yearRange = 1920..currentYear,
        selectableDates = PastSelectableDates()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


/*
@Preview(showBackground = true)
@Composable
fun ScreenPreview2() {
    WorkWellTheme {
        CreateAccountBackground(
            "Create Account",
            "Create an account so you can explore\n" +
                    "all the existing jobs",
        )
    }
}*/