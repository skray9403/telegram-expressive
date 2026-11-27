package com.telegram.expressive.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telegram.expressive.model.ChatMessage
import com.telegram.expressive.model.ChatSummary
import com.telegram.expressive.model.ChatType
import com.telegram.expressive.ui.components.ExpressiveReactionRow
import com.telegram.expressive.ui.components.ExpressiveVoiceNoteContent
import com.telegram.expressive.ui.components.TelegramAvatar

@Composable
fun ChatDetailPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(84.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Forum,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Select a chat to start messaging",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Adaptive two-pane layout powered by Navigation 3 & Material 3 Expressive.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ChatDetailScreen(
    chat: ChatSummary,
    messages: List<ChatMessage>,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onToggleReaction: (messageId: String, emoji: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var draftText by remember(chat.id) { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    var activeReactionPickerMessageId by remember(chat.id) { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onProfileClick)
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        TelegramAvatar(
                            initials = chat.initials,
                            containerColor = chat.avatarColor,
                            size = 40.dp,
                            isOnline = chat.isOnline,
                            isSavedMessages = chat.type == ChatType.SAVED
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = chat.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (chat.isTyping) "typing..." else chat.statusText,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (chat.isOnline || chat.isTyping) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    Box {
                        SplitButtonLayout(
                            leadingButton = {
                                SplitButtonDefaults.TonalLeadingButton(
                                    onClick = { /* Voice call */ }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Call,
                                        contentDescription = "Voice Call",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            trailingButton = {
                                SplitButtonDefaults.TonalTrailingButton(
                                    checked = menuExpanded,
                                    onCheckedChange = { menuExpanded = it }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "More options",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Video Call") },
                                leadingIcon = { Icon(Icons.Filled.Videocam, contentDescription = null) },
                                onClick = { menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("View Profile") },
                                onClick = {
                                    menuExpanded = false
                                    onProfileClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear History") },
                                onClick = { menuExpanded = false }
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
        },
        bottomBar = {
            GoogleMessagesComposerBar(
                text = draftText,
                onTextChange = { draftText = it },
                onSend = {
                    val trimmed = draftText.trim()
                    if (trimmed.isNotEmpty()) {
                        onSendMessage(trimmed)
                        draftText = ""
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = messages,
                key = { _, msg -> msg.id },
                contentType = { _, _ -> "message_bubble" }
            ) { index, message ->
                val prevSameSender = index > 0 && messages[index - 1].isOutgoing == message.isOutgoing
                val nextSameSender = index < messages.lastIndex && messages[index + 1].isOutgoing == message.isOutgoing

                ExpressiveMessageBubble(
                    message = message,
                    prevSameSender = prevSameSender,
                    nextSameSender = nextSameSender,
                    showSenderName = chat.type == ChatType.GROUP && !message.isOutgoing && !prevSameSender,
                    showReactionPicker = activeReactionPickerMessageId == message.id,
                    onBubbleClick = {
                        activeReactionPickerMessageId =
                            if (activeReactionPickerMessageId == message.id) null else message.id
                    },
                    onReactionClick = { emoji ->
                        onToggleReaction(message.id, emoji)
                        activeReactionPickerMessageId = null
                    },
                    modifier = Modifier.padding(
                        top = if (prevSameSender) 0.dp else 6.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun ExpressiveMessageBubble(
    message: ChatMessage,
    prevSameSender: Boolean,
    nextSameSender: Boolean,
    showSenderName: Boolean,
    showReactionPicker: Boolean,
    onBubbleClick: () -> Unit,
    onReactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val large = 22.dp
    val grouped = 6.dp
    val bubbleShape = if (message.isOutgoing) {
        RoundedCornerShape(
            topStart = large,
            topEnd = if (prevSameSender) grouped else large,
            bottomStart = large,
            bottomEnd = if (nextSameSender) grouped else grouped
        )
    } else {
        RoundedCornerShape(
            topStart = if (prevSameSender) grouped else large,
            topEnd = large,
            bottomStart = if (nextSameSender) grouped else grouped,
            bottomEnd = large
        )
    }

    val containerColor = if (message.isOutgoing) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }

    val contentColor = if (message.isOutgoing) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isOutgoing) Alignment.End else Alignment.Start
    ) {
        AnimatedVisibility(
            visible = showReactionPicker,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                tonalElevation = 6.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("👍", "❤️", "😂", "😮", "😢", "🔥").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onReactionClick(emoji) }
                                .padding(2.dp)
                        )
                    }
                }
            }
        }

        Surface(
            shape = bubbleShape,
            color = containerColor,
            contentColor = contentColor,
            modifier = Modifier
                .widthIn(max = 316.dp)
                .clip(bubbleShape)
                .clickable(onClick = onBubbleClick)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (showSenderName) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(2.dp))
                }

                if (message.replyToPreview != null) {
                    Surface(
                        color = contentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .clip(CircleShape)
                                    .background(contentColor.copy(alpha = 0.7f))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = message.replyToPreview,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (message.voiceDurationSec != null) {
                    ExpressiveVoiceNoteContent(
                        durationSec = message.voiceDurationSec,
                        isOutgoing = message.isOutgoing
                    )
                }

                if (message.text.isNotEmpty()) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                ExpressiveReactionRow(
                    reactions = message.reactions,
                    onReactionClick = onReactionClick
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.72f)
                    )
                    if (message.isOutgoing) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Read",
                            tint = contentColor.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoogleMessagesComposerBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = { /* Attachments */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add attachment"
                )
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Emoji */ }) {
                        Icon(
                            imageVector = Icons.Filled.SentimentSatisfiedAlt,
                            contentDescription = "Emoji",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "Telegram message",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = onTextChange,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    IconButton(onClick = { /* Gallery */ }) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = "Gallery",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            FilledIconButton(
                onClick = onSend,
                modifier = Modifier.size(48.dp)
            ) {
                AnimatedContent(
                    targetState = text.isNotBlank(),
                    label = "sendOrMic"
                ) { hasText ->
                    Icon(
                        imageVector = if (hasText) Icons.AutoMirrored.Filled.Send else Icons.Filled.Mic,
                        contentDescription = if (hasText) "Send" else "Record voice message"
                    )
                }
            }
        }
    }
}
