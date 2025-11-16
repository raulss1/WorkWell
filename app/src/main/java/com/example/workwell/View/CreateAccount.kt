package com.example.workwell.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workwell.R
import com.example.workwell.ui.theme.AzulBotonLogin
import com.example.workwell.ui.theme.WorkWellTheme



class CreateAccount: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkWellTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    CreateAccountBackground(
                        "Create Account",
                        "Create an account so you can explore\n" +
                                "all the existing jobs",
                    )
                }
            }
        }
    }
}

@Composable
fun CreateAccountBackground(loginText: String, welcomeText: String, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        val image = painterResource(R.drawable.login_screen)
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            InitText(loginText, welcomeText)
            Spacer(modifier = Modifier.height(50.dp))
            addCreateAccountForm()
            Spacer(modifier = Modifier.height(20.dp))
            addButtons()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Or continue with",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AzulBotonLogin,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            addSignUpButtons()
        }
    }
}


@Composable
fun addSignUpButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { /* login Google */ }) {
            Image(
                painter = painterResource(id = R.drawable.android_neutral_rd_na),
                contentDescription = "Google",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun addCreateAccountForm(modifier: Modifier = Modifier) {
    var usuario: String by remember { mutableStateOf("") }
    var nombreUsuario: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var contrasena: String by remember { mutableStateOf("") }
    var confirmarContrasena: String by remember { mutableStateOf("") }
    var fechaNacimiento: String by remember { mutableStateOf("") }

    var customBlue = Color(0xFF1F41BB)
    var customBackground = Color(0xFFF1F4FF)
    var customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = customBlue,
        unfocusedContainerColor = customBackground,
        focusedContainerColor = customBackground,
        focusedTextColor = customBlue
    )
    val context = LocalContext.current

    Column (
        modifier = Modifier.width(350.dp)
    ) {
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Name",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            )},
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Username",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            )},
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            )},
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Password",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            )},
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confirm Password",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            ) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("DD/MM/YY",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            ) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )

    }
}


@Composable
fun addButtons(modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.width(350.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forgot your password?",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AzulBotonLogin,
            modifier = Modifier.width(165.dp)
                .align(Alignment.End)
                .clickable {
                    /*
                    *val intent = Intent(context, ForgotPassword::class.java)
                    context.startActivity(intent)
                    * */
                },
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.width(350.dp)
                .height(60.dp)
                .shadow(
                    elevation = 15.dp,
                    shape = RoundedCornerShape(15.dp),
                    spotColor = Color(0xFF1F41BB),
                    ambientColor = Color(0xFF1F41BB)
                ),
            onClick = {
                /*TODO*/
            },
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) Color(0xFF163099) else Color(0xFF1F41BB)
            ),
            shape = RoundedCornerShape(15)
        ) {
            Text(text = "Sign up",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Already have an account",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF494949),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(200.dp)
                .clickable {
                    val intent = Intent(context, CreateAccount::class.java)
                    context.startActivity(intent)
                }
        )
    }
}

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
}
