package com.telegram.expressive.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

enum class ChatFolder(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    PERSONAL("Personal"),
    GROUPS("Groups"),
    CHANNELS("Channels")
}

enum class ChatType {
    PRIVATE,
    GROUP,
    CHANNEL,
    BOT,
    SAVED
}

@Immutable
data class StoryItem(
    val id: String,
    val authorName: String,
    val initials: String,
    val avatarColor: Color,
    val hasUnseen: Boolean,
    val isSelf: Boolean = false
)

@Immutable
data class MessageReaction(
    val emoji: String,
    val count: Int,
    val isSelected: Boolean = false
)

@Immutable
data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
    val isRead: Boolean = true,
    val replyToPreview: String? = null,
    val voiceDurationSec: Int? = null,
    val reactions: List<MessageReaction> = emptyList()
)

@Immutable
data class ChatSummary(
    val id: String,
    val title: String,
    val initials: String,
    val avatarColor: Color,
    val lastMessage: String,
    val lastMessageSender: String? = null,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isVerified: Boolean = false,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val type: ChatType = ChatType.PRIVATE,
    val statusText: String = "last seen recently",
    val bio: String = "Material 3 Expressive enthusiast",
    val username: String = "@telegram_user"
)

@Immutable
data class UserProfile(
    val id: String,
    val name: String,
    val phone: String,
    val username: String,
    val bio: String,
    val initials: String,
    val avatarColor: Color
)

object TelegramSampleData {
    val currentUser = UserProfile(
        id = "me",
        name = "Alex Mercer",
        phone = "+1 (415) 890-4312",
        username = "@alexmercer",
        bio = "Building expressive Android apps with Jetpack Compose & Navigation 3.",
        initials = "AM",
        avatarColor = Color(0xFF006494)
    )

    val stories = listOf(
        StoryItem("self", "My Story", "AM", Color(0xFF006494), hasUnseen = false, isSelf = true),
        StoryItem("s1", "Elena", "ER", Color(0xFF6B4EA0), hasUnseen = true),
        StoryItem("s2", "Design", "M3", Color(0xFF00796B), hasUnseen = true),
        StoryItem("s3", "Marcus", "MK", Color(0xFFC25900), hasUnseen = true),
        StoryItem("s4", "Sora", "SK", Color(0xFFAD1457), hasUnseen = false),
        StoryItem("s5", "Kotlin", "KT", Color(0xFF512DA8), hasUnseen = false)
    )

    val chats = listOf(
        ChatSummary(
            id = "saved",
            title = "Saved Messages",
            initials = "SM",
            avatarColor = Color(0xFF1E88E5),
            lastMessage = "Material 3 Expressive MotionScheme.expressive() notes",
            timestamp = "10:42",
            isPinned = true,
            type = ChatType.SAVED,
            statusText = "cloud storage",
            username = "me"
        ),
        ChatSummary(
            id = "elena",
            title = "Elena Rostova",
            initials = "ER",
            avatarColor = Color(0xFF6B4EA0),
            lastMessage = "Just pushed the new HorizontalFloatingToolbar prototype!",
            timestamp = "10:38",
            unreadCount = 3,
            isPinned = true,
            isOnline = true,
            type = ChatType.PRIVATE,
            statusText = "online",
            bio = "Staff UI Engineer • Material Design",
            username = "@elenarostova"
        ),
        ChatSummary(
            id = "compose_group",
            title = "Compose Expressive Lab",
            initials = "CE",
            avatarColor = Color(0xFF00796B),
            lastMessage = "The spring physics on ButtonGroup feel so snappy",
            lastMessageSender = "Marcus",
            timestamp = "09:55",
            unreadCount = 14,
            isTyping = true,
            type = ChatType.GROUP,
            statusText = "428 members, 54 online",
            bio = "Open discussion on Jetpack Compose & Material 3 Expressive.",
            username = "@compose_expressive"
        ),
        ChatSummary(
            id = "telegram_news",
            title = "Telegram News",
            initials = "TN",
            avatarColor = Color(0xFF0288D1),
            lastMessage = "Introducing interactive stories, adaptive two-pane layouts, and expressive reactions.",
            timestamp = "09:12",
            unreadCount = 2,
            isMuted = true,
            isVerified = true,
            type = ChatType.CHANNEL,
            statusText = "4.8M subscribers",
            bio = "Official Telegram announcements and feature drops.",
            username = "@telegram"
        ),
        ChatSummary(
            id = "marcus",
            title = "Marcus Vance",
            initials = "MV",
            avatarColor = Color(0xFFC25900),
            lastMessage = "Voice message (0:18)",
            timestamp = "Yesterday",
            isOnline = false,
            type = ChatType.PRIVATE,
            statusText = "last seen 2 hours ago",
            bio = "Motion Designer",
            username = "@marcusv"
        ),
        ChatSummary(
            id = "botfather",
            title = "BotFather",
            initials = "BF",
            avatarColor = Color(0xFF3949AB),
            lastMessage = "Done! Congratulations on your new bot.",
            timestamp = "Yesterday",
            isVerified = true,
            type = ChatType.BOT,
            statusText = "bot",
            bio = "BotFather is the one bot to rule them all.",
            username = "@BotFather"
        ),
        ChatSummary(
            id = "sora",
            title = "Sora Takahashi",
            initials = "ST",
            avatarColor = Color(0xFFAD1457),
            lastMessage = "Foldable dual-pane scene works seamlessly with Nav 3!",
            timestamp = "Mon",
            type = ChatType.PRIVATE,
            statusText = "last seen yesterday",
            bio = "Android Platform & Foldables",
            username = "@sorat"
        )
    )

    val initialMessages: Map<String, List<ChatMessage>> = mapOf(
        "elena" to listOf(
            ChatMessage(
                id = "m1",
                senderName = "Elena Rostova",
                text = "Hey Alex! Have you tried the new Material 3 Expressive theme yet?",
                timestamp = "10:30",
                isOutgoing = false,
                reactions = listOf(MessageReaction("🔥", 2, isSelected = true))
            ),
            ChatMessage(
                id = "m2",
                senderName = "Alex Mercer",
                text = "Yes! Wrapping the app in MaterialExpressiveTheme with MotionScheme.expressive() makes every spring feel super organic.",
                timestamp = "10:32",
                isOutgoing = true,
                isRead = true
            ),
            ChatMessage(
                id = "m3",
                senderName = "Elena Rostova",
                text = "Right? Also check out this voice note about the Navigation 3 ListDetailSceneStrategy.",
                timestamp = "10:34",
                isOutgoing = false,
                voiceDurationSec = 18,
                reactions = listOf(MessageReaction("❤️", 1), MessageReaction("⚡", 1, isSelected = true))
            ),
            ChatMessage(
                id = "m4",
                senderName = "Alex Mercer",
                text = "Using SceneStrategy instead of legacy scaffolds keeps the backstack super clean on tablets and foldables.",
                timestamp = "10:36",
                isOutgoing = true,
                isRead = true,
                replyToPreview = "Right? Also check out this voice note..."
            ),
            ChatMessage(
                id = "m5",
                senderName = "Elena Rostova",
                text = "Just pushed the new HorizontalFloatingToolbar prototype!",
                timestamp = "10:38",
                isOutgoing = false
            )
        ),
        "saved" to listOf(
            ChatMessage(
                id = "sm1",
                senderName = "Alex Mercer",
                text = "• MaterialExpressiveTheme(motionScheme = MotionScheme.expressive())\n• ButtonGroup + ToggleButton for chat folders\n• HorizontalFloatingToolbar + VibrantFloatingActionButton\n• Navigation 3 ListDetailSceneStrategy",
                timestamp = "10:42",
                isOutgoing = true,
                isRead = true
            )
        ),
        "compose_group" to listOf(
            ChatMessage(
                id = "cg1",
                senderName = "Sora Takahashi",
                text = "Anyone testing adaptive layouts on Pixel Fold?",
                timestamp = "09:40",
                isOutgoing = false
            ),
            ChatMessage(
                id = "cg2",
                senderName = "Marcus Vance",
                text = "The spring physics on ButtonGroup feel so snappy",
                timestamp = "09:55",
                isOutgoing = false,
                reactions = listOf(MessageReaction("👍", 6, isSelected = true), MessageReaction("🎉", 3))
            )
        )
    )
}
