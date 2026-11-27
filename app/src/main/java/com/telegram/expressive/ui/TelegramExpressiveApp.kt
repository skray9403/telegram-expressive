package com.telegram.expressive.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowWidthSizeClass
import com.telegram.expressive.model.ChatFolder
import com.telegram.expressive.model.ChatMessage
import com.telegram.expressive.model.MessageReaction
import com.telegram.expressive.model.TelegramSampleData
import com.telegram.expressive.theme.TelegramExpressiveTheme
import com.telegram.expressive.ui.screens.ChatDetailPlaceholder
import com.telegram.expressive.ui.screens.ChatDetailScreen
import com.telegram.expressive.ui.screens.ChatListScreen
import com.telegram.expressive.ui.screens.ProfileScreen
import com.telegram.expressive.ui.screens.SettingsScreen
import com.telegram.expressive.ui.screens.TelegramDrawerContent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object ChatListKey : NavKey

@Serializable
data class ChatDetailKey(val chatId: String) : NavKey

@Serializable
data class ProfileKey(val chatId: String) : NavKey

@Serializable
data object SettingsKey : NavKey

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TelegramExpressiveApp() {
    val systemDark = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemDark) }

    TelegramExpressiveTheme(darkTheme = isDarkMode) {
        val backStack = rememberNavBackStack(ChatListKey)
        val listDetailStrategy = rememberListDetailSceneStrategy<Any>()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isExpandedWidth = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

        var selectedFolder by remember { mutableStateOf(ChatFolder.ALL) }
        var isSyncing by remember { mutableStateOf(false) }
        var selectedNavIndex by remember { mutableIntStateOf(0) }

        val messagesByChat = remember {
            mutableStateMapOf<String, List<ChatMessage>>().apply {
                putAll(TelegramSampleData.initialMessages)
            }
        }

        val currentChatId = (backStack.lastOrNull { it is ChatDetailKey } as? ChatDetailKey)?.chatId

        fun openChat(chatId: String) {
            backStack.removeAll { it is ChatDetailKey || it is ProfileKey || it is SettingsKey }
            backStack.add(ChatDetailKey(chatId))
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                TelegramDrawerContent(
                    currentUser = TelegramSampleData.currentUser,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = it },
                    onOpenSavedMessages = { openChat("saved") },
                    onOpenProfile = {
                        backStack.add(ProfileKey("saved"))
                    },
                    onOpenSettings = {
                        backStack.add(SettingsKey)
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    item(
                        selected = selectedNavIndex == 0,
                        onClick = {
                            selectedNavIndex = 0
                            backStack.removeAll { it is SettingsKey }
                        },
                        icon = { Icon(Icons.Filled.Chat, contentDescription = "Chats") },
                        label = { Text("Chats") }
                    )
                    item(
                        selected = selectedNavIndex == 1,
                        onClick = {
                            selectedNavIndex = 1
                            scope.launch { snackbarHostState.showSnackbar("Contacts skeleton") }
                        },
                        icon = { Icon(Icons.Filled.Contacts, contentDescription = "Contacts") },
                        label = { Text("Contacts") }
                    )
                    item(
                        selected = selectedNavIndex == 2,
                        onClick = {
                            selectedNavIndex = 2
                            scope.launch { snackbarHostState.showSnackbar("Calls skeleton") }
                        },
                        icon = { Icon(Icons.Filled.Call, contentDescription = "Calls") },
                        label = { Text("Calls") }
                    )
                    item(
                        selected = selectedNavIndex == 3,
                        onClick = {
                            selectedNavIndex = 3
                            backStack.removeAll { it is SettingsKey }
                            backStack.add(SettingsKey)
                        },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onBack = { keysToRemove ->
                            repeat(keysToRemove) {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            }
                        },
                        sceneStrategy = listDetailStrategy,
                        entryProvider = entryProvider {
                            entry<ChatListKey>(
                                metadata = ListDetailSceneStrategy.listPane(
                                    detailPlaceholder = { ChatDetailPlaceholder() }
                                )
                            ) {
                                ChatListScreen(
                                    chats = TelegramSampleData.chats,
                                    stories = TelegramSampleData.stories,
                                    selectedChatId = currentChatId,
                                    selectedFolder = selectedFolder,
                                    isSyncing = isSyncing,
                                    onFolderSelected = { selectedFolder = it },
                                    onChatClick = { chat -> openChat(chat.id) },
                                    onStoryClick = { story ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Story: ${story.authorName}")
                                        }
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onSyncToggle = { isSyncing = !isSyncing },
                                    onNewChatAction = { action ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("$action composer")
                                        }
                                    }
                                )
                            }

                            entry<ChatDetailKey>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) { key ->
                                val chat = TelegramSampleData.chats.firstOrNull { it.id == key.chatId }
                                    ?: TelegramSampleData.chats.first()
                                val messages = messagesByChat[chat.id].orEmpty()

                                ChatDetailScreen(
                                    chat = chat,
                                    messages = messages,
                                    showBackButton = !isExpandedWidth,
                                    onBackClick = { backStack.removeLastOrNull() },
                                    onProfileClick = { backStack.add(ProfileKey(chat.id)) },
                                    onSendMessage = { text ->
                                        val next = ChatMessage(
                                            id = "local_${System.currentTimeMillis()}",
                                            senderName = TelegramSampleData.currentUser.name,
                                            text = text,
                                            timestamp = "Now",
                                            isOutgoing = true,
                                            isRead = true
                                        )
                                        messagesByChat[chat.id] = messages + next
                                    },
                                    onToggleReaction = { messageId, emoji ->
                                        messagesByChat[chat.id] = messages.map { msg ->
                                            if (msg.id != messageId) return@map msg
                                            val updated = msg.reactions.map { r ->
                                                if (r.emoji != emoji) r
                                                else if (r.isSelected) r.copy(count = (r.count - 1).coerceAtLeast(0), isSelected = false)
                                                else r.copy(count = r.count + 1, isSelected = true)
                                            }.filter { it.count > 0 }
                                            msg.copy(reactions = updated)
                                        }
                                    }
                                )
                            }

                            entry<ProfileKey>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) { key ->
                                val chat = TelegramSampleData.chats.firstOrNull { it.id == key.chatId }
                                    ?: TelegramSampleData.chats.first()
                                ProfileScreen(
                                    chat = chat,
                                    onBackClick = { backStack.removeLastOrNull() },
                                    onMessageClick = { openChat(chat.id) }
                                )
                            }

                            entry<SettingsKey>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                SettingsScreen(
                                    currentUser = TelegramSampleData.currentUser,
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { isDarkMode = it },
                                    onBackClick = {
                                        selectedNavIndex = 0
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
