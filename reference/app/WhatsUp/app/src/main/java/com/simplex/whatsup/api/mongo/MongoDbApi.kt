package com.simplex.whatsup.api.mongo

import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.models.User
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface MongoDbApi {
    @Multipart
    @POST("user")
    fun addUser(
        @Part("email") email: RequestBody,
        @Part("username") username: RequestBody
        ): Single<Any>

    @GET("users")
    fun getUser(): Flowable<User>

    @GET("report")
    fun getReports(): Flowable<List<Report>>

    @GET("report")
    fun getEventReports(@Query("event") event: String): Flowable<List<Report>>

    @GET("event")
    fun getEvent(@Query("event_id") event_id: String): Flowable<Event>

    @GET("events")
    fun getEvents(): Flowable<List<Event>>

    @Multipart
    @POST("add_report")
    fun addReport(
        @Part("event") event: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part("category")category: RequestBody,
        @Part report_image: MultipartBody.Part? = null
    ): Single<Report>

    // TODO: make api return posted report
    @Multipart
    @POST("post-event")
    fun addEvent(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("datetime") datetime: RequestBody,
        @Part("location") location: RequestBody,
        @Part("geo_latitude") latitude: RequestBody,
        @Part("geo_longitude")longitude: RequestBody,
        @Part("category")category: RequestBody,
        @Part cover_image: MultipartBody.Part? = null
    ): Single<Event>

    @Multipart
    @POST("subscribe-event/{eventId}/mobile")
    fun subscribeEvent(
        @Path("eventId") eventId: String,
        @Part("subscribe_status") subscribeStatus: RequestBody
    ): Single<User>

    @GET("event-category")
    fun getEventCategory(): Flowable<List<String>>

    @GET("report-category")
    fun getReportCategory(): Flowable<List<String>>

    @GET("event")
    fun getEventImage(@Query("image_id") image_id: String): Flowable<ResponseBody>

    @GET("report")
    fun getReportImage(@Query("image_id") image_id: String): Flowable<ResponseBody>
}