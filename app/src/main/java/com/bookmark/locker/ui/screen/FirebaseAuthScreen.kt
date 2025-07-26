package com.bookmark.locker.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bookmark.locker.ui.viewmodel.AuthState
import com.bookmark.locker.ui.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseAuthScreen(
    onNavigateBack: () -> Unit,
    onAuthSuccess: () -> Unit,
    viewModel: FirebaseViewModel = hiltViewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.SignedIn) {
            onAuthSuccess()
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("GoogleSignIn", "Activity result received: ${result.resultCode}")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            android.util.Log.d("GoogleSignIn", "Google account obtained: ${account?.email}")
            viewModel.signInWithGoogle(account!!)
        } catch (e: ApiException) {
            android.util.Log.e("GoogleSignIn", "Google Sign-In failed: ${e.statusCode} - ${e.message}")
            viewModel.clearAuthError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSignUp) "Create Account" else "Sign In") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isSignUp) "Create your account" else "Welcome back",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Show current auth state for debugging
                    Text(
                        text = "Auth State: ${when(authState) {
                            is AuthState.SignedOut -> "Signed Out"
                            is AuthState.Loading -> "Loading..."
                            is AuthState.SignedIn -> "Signed In as ${(authState as AuthState.SignedIn).user.email ?: "Anonymous"}"
                            is AuthState.Error -> "Error: ${(authState as AuthState.Error).message}"
                        }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when(authState) {
                            is AuthState.SignedIn -> MaterialTheme.colorScheme.primary
                            is AuthState.Error -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = authState is AuthState.Error
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = authState is AuthState.Error
                    )

                    if (isSignUp) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = authState is AuthState.Error || (isSignUp && password != confirmPassword && confirmPassword.isNotEmpty())
                        )
                    }

                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (isSignUp) {
                                if (password == confirmPassword) {
                                    viewModel.createAccount(email, password)
                                }
                            } else {
                                viewModel.signInWithEmail(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = email.isNotEmpty() && password.isNotEmpty() && 
                                 (!isSignUp || password == confirmPassword) &&
                                 authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (isSignUp) "Create Account" else "Sign In")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val googleSignInClient = viewModel.getGoogleSignInClient()
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authState !is AuthState.Loading
                    ) {
                        Text("Sign in with Google")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.signInAnonymously()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authState !is AuthState.Loading
                    ) {
                        Text("Continue as Guest")
                    }

                    Divider()

                    TextButton(
                        onClick = { 
                            isSignUp = !isSignUp
                            viewModel.clearAuthError()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isSignUp) "Already have an account? Sign In" 
                            else "Don't have an account? Sign Up"
                        )
                    }
                }
            }
        }
    }
}
