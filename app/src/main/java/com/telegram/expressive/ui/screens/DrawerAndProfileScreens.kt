package com.telegram.expressive.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.telegram.expressive.model.ChatSummary
import com.telegram.expressive.model.UserProfile
import com.telegram.expressive.ui.components.TelegramAvatar

@Composable
fun TelegramDrawerContent(
    currentUser: UserProfile,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onOpenSavedMessages: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(310.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    TelegramAvatar(
                        initials = currentUser.initials,
                        containerColor = currentUser.avatarColor,
                        size = 64.dp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DarkMode,
                            contentDescription = "Night Mode",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = currentUser.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = currentUser.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        val itemPadding = NavigationDrawerItemDefaults.ItemPadding
        NavigationDrawerItem(
            label = { Text("My Profile") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            selected = false,
            onClick = {
                onCloseDrawer()
                onOpenProfile()
            },
            modifier = Modifier.padding(itemPadding)
        )
        NavigationDrawerItem(
            label = { Text("New Group") },
            icon = { Icon(Icons.Filled.Group, contentDescription = null) },
            selected = false,
            onClick = onCloseDrawer,
            modifier = Modifier.padding(itemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Calls") },
            icon = { Icon(Icons.Filled.Call, contentDescription = null) },
            selected = false,
            onClick = onCloseDrawer,
            modifier = Modifier.padding(itemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Saved Messages") },
            icon = { Icon(Icons.Filled.Bookmark, contentDescription = null) },
            selected = false,
            onClick = {
                onCloseDrawer()
                onOpenSavedMessages()
            },
            modifier = Modifier.padding(itemPadding)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

        NavigationDrawerItem(
            label = { Text("Settings") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            selected = false,
            onClick = {
                onCloseDrawer()
                onOpenSettings()
            },
            modifier = Modifier.padding(itemPadding)
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ProfileScreen(
    chat: ChatSummary,
    onBackClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(chat.isMuted) }
    val tabs = listOf("Media", "Files", "Links", "Voice")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Profile") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TelegramAvatar(
                        initials = chat.initials,
                        containerColor = chat.avatarColor,
                        size = 96.dp,
                        isOnline = chat.isOnline
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = chat.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = chat.statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        ToggleButton(
                            checked = false,
                            onCheckedChange = { onMessageClick() },
                            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
                        ) {
                            Icon(Icons.Filled.Chat, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Message")
                        }
                        ToggleButton(
                            checked = isMuted,
                            onCheckedChange = { isMuted = it },
                            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes()
                        ) {
                            Icon(
                                if (isMuted) Icons.Filled.NotificationsOff else Icons.Filled.Notifications,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isMuted) "Muted" else "Mute")
                        }
                        ToggleButton(
                            checked = false,
                            onCheckedChange = { /* Call */ },
                            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes()
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Call")
                        }
                        ToggleButton(
                            checked = false,
                            onCheckedChange = { /* Search */ },
                            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                }
            }

            item(key = "info_card") {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        ListItem(
                            headlineContent = { Text(chat.username) },
                            supportingContent = { Text("Username") }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ListItem(
                            headlineContent = { Text(chat.bio) },
                            supportingContent = { Text("Bio") }
                        )
                    }
                }
            }

            item(key = "shared_tabs") {
                Column {
                    SecondaryTabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Shared ${tabs[selectedTab]}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Skeleton placeholder for ${tabs[selectedTab].lowercase()} grid",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUser: UserProfile,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Settings") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "account_card") {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TelegramAvatar(
                            initials = currentUser.initials,
                            containerColor = currentUser.avatarColor,
                            size = 60.dp
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${currentUser.phone} • ${currentUser.username}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item(key = "appearance_group") {
                SettingsGroupCard(
                    title = "Appearance",
                    content = {
                        ListItem(
                            headlineContent = { Text("Night Mode") },
                            supportingContent = { Text("Material 3 Expressive dark theme") },
                            leadingContent = { Icon(Icons.Filled.Palette, contentDescription = null) },
                            trailingContent = {
                                Switch(checked = isDarkMode, onCheckedChange = onToggleDarkMode)
                            }
                        )
                    }
                )
            }

            item(key = "preferences_group") {
                SettingsGroupCard(
                    title = "Preferences",
                    content = {
                        SettingsRow(Icons.Filled.Notifications, "Notifications and Sounds", "All chats active")
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(Icons.Filled.Lock, "Privacy and Security", "Passcode, 2FA, active sessions")
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(Icons.Filled.DataUsage, "Data and Storage", "Network usage & auto-download")
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, contentDescription = null) }
    )
}
