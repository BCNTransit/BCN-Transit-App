package com.bcntransit.app.api

import com.bcntransit.app.model.FavoriteDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


import retrofit2.http.Query

interface UserApiService {
    @POST("users/register")
    suspend fun registerUser(
        @Body body: Map<String, String>
    ): Boolean

    @POST("users/notifications/toggle/{status}")
    suspend fun toggleUserNotifications(
        @Path("status") status: Boolean
    ): Boolean

    @GET("users/notifications/configuration")
    suspend fun getUserNotificationsConfiguration(): Boolean

    @GET("users/favorites")
    suspend fun getUserFavorites(): List<FavoriteDto>

    @POST("users/favorites")
    suspend fun addUserFavorite(
        @Body favorite: FavoriteDto
    ): Boolean

    @DELETE("users/favorites")
    suspend fun deleteUserFavorite(
        @Query("type") type: String,
        @Query("item_id") itemId: String
    ): Boolean

    @GET("users/favorites/exists")
    suspend fun userHasFavorite(
        @Query("type") type: String,
        @Query("item_id") itemId: String
    ): Boolean
}