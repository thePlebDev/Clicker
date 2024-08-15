package com.example.clicker.presentation.authentication

import com.example.clicker.BuildConfig

private val clientId = BuildConfig.CLIENT_ID
private val redirectUrl = BuildConfig.REDIRECT_URL
val twitchAuthorizationScopeURL ="https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit+channel:read:editors+moderator:manage:chat_settings+moderator:read:automod_settings+moderator:manage:chat_messages+moderator:manage:automod_settings+moderator:manage:banned_users+user:read:moderated_channels+channel:manage:broadcast+user:edit:broadcast+moderator:manage:automod+moderator:manage:blocked_terms+user:read:chat+user:bot+channel:bot+moderator:manage:unban_requests+moderator:read:moderators+moderator:read:vips+moderator:manage:warnings+moderator:read:unban_requests+moderator:manage:unban_requests+user:read:subscriptions"