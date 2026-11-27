package com.telegram.expressive.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.telegram.expressive.model.ChatFolder
import com.telegram.expressive.model.ChatSummary
import com.telegram.expressive.model.ChatType
import com.telegram.expressive.model.StoryItem
import com.telegram.expressive.model.TelegramSampleData
import com.telegram.expressive.ui.components.ExpressiveComposeToolbar
import com.telegram.expressive.ui.components.ExpressiveFolderGroup
import com.telegram.expressive.ui.components.ExpressiveStoriesRow
import com.telegram.expressive.ui.components.TelegramAvatar

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChatListScreen(
    chats: List<ChatSummary>,
    stories: List<StoryItem>,
    selectedChatId: String?,
    selectedFolder: ChatFolder,
    isSyncing: Boolean,
    onFolderSelected: (ChatFolder) -> Unit,
    onChatClick: (ChatSummary) -> Unit,
    onStoryClick: (StoryItem) -> Unit,
    onMenuClick: () -> Unit,
    onSyncToggle: () -> Unit,
    onNewChatAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val toolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        exitDirection = FloatingToolbarExitDirection.Bottom
    )

    val filteredChats = remember(chats, selectedFolder, searchQuery) {
        chats.filter { chat ->
            val matchesFolder = when (selectedFolder) {
                ChatFolder.ALL -> true
                ChatFolder.UNREAD -> chat.unreadCount > 0
                ChatFolder.PERSONAL -> chat.type == ChatType.PRIVATE || chat.type == ChatType.SAVED
                ChatFolder.GROUPS -> chat.type == ChatType.GROUP
                ChatFolder.CHANNELS -> chat.type == ChatType.CHANNEL
            }
            val matchesQuery = searchQuery.isBlank() ||
                chat.title.contains(searchQuery, ignoreCase = true) ||
                chat.lastMessage.contains(searchQuery, ignoreCase = true)
            matchesFolder && matchesQuery
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            GoogleMessagesPillSearchHeader(
                query = searchQuery,
                isSearchActive = isSearchActive,
                isSyncing = isSyncing,
                onQueryChange = { searchQuery = it },
                onSearchActiveChange = { active ->
                    isSearchActive = active
                    if (!active) searchQuery = ""
                },
                onMenuClick = onMenuClick,
                onSyncToggle = onSyncToggle
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(toolbarScrollBehavior)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 104.dp)
            ) {
                if (!isSearchActive) {
                    item(key = "stories", contentType = "stories") {
                        ExpressiveStoriesRow(
                            stories = stories,
                            onStoryClick = onStoryClick
                        )
                    }
                }

                item(key = "folders", contentType = "folders") {
                    ExpressiveFolderGroup(
                        selectedFolder = selectedFolder,
                        onFolderSelected = onFolderSelected
                    )
                    Spacer(Modifier.height(4.dp))
                }

                itemsIndexed(
                    items = filteredChats,
                    key = { _, chat -> chat.id },
                    contentType = { _, _ -> "chat_row" }
                ) { index, chat ->
                    val itemShape = groupedCardShape(index = index, total = filteredChats.size)
                    ChatListItemRow(
                        chat = chat,
                        shape = itemShape,
                        isSelected = chat.id == selectedChatId,
                        onClick = { onChatClick(chat) }
                    )
                }
            }

            ExpressiveComposeToolbar(
                expanded = true,
                scrollBehavior = toolbarScrollBehavior,
                onNewChat = { onNewChatAction("Start chat") },
                onNewGroup = { onNewChatAction("New Group") },
                onNewChannel = { onNewChatAction("New Channel") },
                onSavedMessages = {
                    chats.firstOrNull { it.type == ChatType.SAVED }?.let(onChatClick)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoogleMessagesPillSearchHeader(
    query: String,
    isSearchActive: Boolean,
    isSyncing: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onSyncToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            AnimatedContent(
                targetState = isSearchActive,
                label = "searchHeaderState"
            ) { searching ->
                if (searching) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onSearchActiveChange(false) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close search"
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search messages, people...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            BasicTextField(
                                value = query,
                                onValueChange = onQueryChange,
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onSearchActiveChange(true) }
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                        }

                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isSyncing) "Updating..." else "Telegram",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isSyncing) {
                                LoadingIndicator(modifier = Modifier.size(18.dp))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(CircleShape)
                                .clickable { onSyncToggle() }
                        ) {
                            TelegramAvatar(
                                initials = TelegramSampleData.currentUser.initials,
                                containerColor = TelegramSampleData.currentUser.avatarColor,
                                size = 34.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun groupedCardShape(index: Int, total: Int): Shape {
    val outer = 24.dp
    val inner = 6.dp
    return when {
        total <= 1 -> RoundedCornerShape(outer)
        index == 0 -> RoundedCornerShape(
            topStart = outer, topEnd = outer,
            bottomStart = inner, bottomEnd = inner
        )
        index == total - 1 -> RoundedCornerShape(
            topStart = inner, topEnd = inner,
            bottomStart = outer, bottomEnd = outer
        )
        else -> RoundedCornerShape(inner)
    }
}

@Composable
private fun ChatListItemRow(
    chat: ChatSummary,
    shape: Shape,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }

    Surface(
        color = containerColor,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 1.5.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TelegramAvatar(
                initials = chat.initials,
                containerColor = chat.avatarColor,
                size = 52.dp,
                isOnline = chat.isOnline,
                isSavedMessages = chat.type == ChatType.SAVED
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = chat.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (chat.isVerified) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (chat.isMuted) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Muted",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = chat.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (chat.unreadCount > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Spacer(Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val subtitle = when {
                        chat.isTyping -> "typing..."
                        chat.lastMessageSender != null -> "${chat.lastMessageSender}: ${chat.lastMessage}"
                        else -> chat.lastMessage
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        color = when {
                            chat.isTyping -> MaterialTheme.colorScheme.primary
                            chat.unreadCount > 0 -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (chat.isPinned && chat.unreadCount == 0) {
                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = "Pinned",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        if (chat.unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (chat.isMuted) {
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                    .padding(horizontal = 7.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chat.unreadCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chat.isMuted) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
