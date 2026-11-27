package com.telegram.expressive.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telegram.expressive.model.ChatFolder
import com.telegram.expressive.model.MessageReaction
import com.telegram.expressive.model.StoryItem

@Composable
fun TelegramAvatar(
    initials: String,
    containerColor: Color,
    size: Dp = 52.dp,
    isOnline: Boolean = false,
    hasStoryRing: Boolean = false,
    isSavedMessages: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val ringModifier = if (hasStoryRing) {
            Modifier
                .size(size)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    shape = CircleShape
                )
                .padding(3.5.dp)
        } else {
            Modifier.size(size)
        }

        Box(
            modifier = ringModifier
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            if (isSavedMessages) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = "Saved Messages",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.45f)
                )
            } else {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.34f).sp
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2ECC71))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFolderGroup(
    selectedFolder: ChatFolder,
    onFolderSelected: (ChatFolder) -> Unit,
    modifier: Modifier = Modifier
) {
    val folders = ChatFolder.entries
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        ButtonGroup(
            overflowIndicator = { menuState ->
                ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
            }
        ) {
            folders.forEachIndexed { index, folder ->
                val shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    folders.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
                ToggleButton(
                    checked = selectedFolder == folder,
                    onCheckedChange = { checked ->
                        if (checked) onFolderSelected(folder)
                    },
                    shapes = shapes,
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = folder.label,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ExpressiveStoriesRow(
    stories: List<StoryItem>,
    onStoryClick: (StoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(
            items = stories,
            key = { it.id },
            contentType = { "story" }
        ) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onStoryClick(story) }
                    .padding(vertical = 4.dp)
            ) {
                Box {
                    TelegramAvatar(
                        initials = story.initials,
                        containerColor = story.avatarColor,
                        size = 56.dp,
                        hasStoryRing = story.hasUnseen
                    )
                    if (story.isSelf) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add story",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = story.authorName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveComposeToolbar(
    expanded: Boolean,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    onNewChat: () -> Unit,
    onNewGroup: () -> Unit,
    onNewChannel: () -> Unit,
    onSavedMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        scrollBehavior = scrollBehavior,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(
                onClick = onNewChat
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "New Message"
                )
            }
        },
        modifier = modifier
    ) {
        IconButton(onClick = onNewGroup) {
            Icon(Icons.Filled.GroupAdd, contentDescription = "New Group")
        }
        IconButton(onClick = onNewChannel) {
            Icon(Icons.Filled.Campaign, contentDescription = "New Channel")
        }
        IconButton(onClick = onSavedMessages) {
            Icon(Icons.Filled.Bookmark, contentDescription = "Saved Messages")
        }
    }
}

@Composable
fun ExpressiveVoiceNoteContent(
    durationSec: Int,
    isOutgoing: Boolean,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    val activeColor = if (isOutgoing) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val trackColor = activeColor.copy(alpha = 0.28f)
    val bars = remember { listOf(0.3f, 0.7f, 0.5f, 0.9f, 1.0f, 0.6f, 0.8f, 0.4f, 0.75f, 0.5f, 0.9f, 0.65f, 0.35f, 0.8f, 0.5f) }

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledIconToggleButton(
            checked = isPlaying,
            onCheckedChange = { isPlaying = it },
            modifier = Modifier.size(38.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause voice note" else "Play voice note"
            )
        }

        Column(modifier = Modifier.weight(1f, fill = false)) {
            Canvas(
                modifier = Modifier
                    .width(136.dp)
                    .height(22.dp)
            ) {
                val barWidth = size.width / (bars.size * 1.6f)
                val gap = barWidth * 0.6f
                bars.forEachIndexed { idx, amplitude ->
                    val barHeight = size.height * amplitude
                    val x = idx * (barWidth + gap)
                    val y = (size.height - barHeight) / 2f
                    val barColor = if (isPlaying && idx < 6) activeColor else trackColor
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "0:${durationSec.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.labelSmall,
                color = activeColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ExpressiveReactionRow(
    reactions: List<MessageReaction>,
    onReactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reactions.isEmpty()) return
    Row(
        modifier = modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        reactions.forEach { reaction ->
            val containerColor by animateColorAsState(
                targetValue = if (reaction.isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f)
                },
                label = "reactionBg"
            )
            val contentColor by animateColorAsState(
                targetValue = if (reaction.isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                label = "reactionFg"
            )
            Surface(
                shape = RoundedCornerShape(50),
                color = containerColor,
                contentColor = contentColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onReactionClick(reaction.emoji) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = reaction.emoji, fontSize = 13.sp)
                    Text(
                        text = reaction.count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
