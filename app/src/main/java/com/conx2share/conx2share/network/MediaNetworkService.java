package com.conx2share.conx2share.network;


import com.conx2share.conx2share.model.EventResponse;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.network.models.response.GetUserResponse;
import com.conx2share.conx2share.network.models.response.SendMessageResponse;

import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

public interface MediaNetworkService {

    /* MESSAGES */

    @Multipart
    @POST("/api/messages")
    SendMessageResponse uploadPhotoMessage(
            @Part("message[body]") TypedString body,
            @Part("message[time_to_live") TypedString timeToLive,
            @Part("message[title]") TypedString title,
            @Part("message[chat_id]") TypedString chatIdInner,
            @Part("chat_id") TypedString chatId,
            @Part("message[image]") TypedFile typedFile,
            @Part("auth_token") TypedString auth,
            @Part("message[to_id]") TypedString toId);

    @Multipart
    @POST("/api/messages")
    SendMessageResponse uploadVideoMessage(
            @Part("message[body]") TypedString body,
            @Part("message[time_to_live") TypedString timeToLive,
            @Part("message[title]") TypedString title,
            @Part("message[chat_id]") TypedString chatIdInner,
            @Part("chat_id") TypedString chatId,
            @Part("message[image]") TypedFile thumbFile,
            @Part("auth_token") TypedString auth,
            @Part("message[video]") TypedFile videoFile,
            @Part("message[to_id]") TypedString toId);

    @Multipart
    @POST("/api/messages")
    SendMessageResponse uploadAudioMessage(
            @Part("message[body]") TypedString body,
            @Part("message[time_to_live") TypedString timeToLive,
            @Part("message[title]") TypedString title,
            @Part("message[chat_id]") TypedString chatIdInner,
            @Part("chat_id") TypedString chatId,
            @Part("auth_token") TypedString auth,
            @Part("message[audio]") TypedFile audioFile,
            @Part("message[to_id]") TypedString toId,
            @Part("message[audio_length]") TypedString audioLength);

    @Multipart
    @PUT("/api/messages/{messageId}")
    SendMessageResponse updatePhotoMessage(@Path("messageId") String messageId, @Part("message[image]") TypedFile typedFile);

    @Multipart
    @PUT("/api/messages/{messageId}")
    SendMessageResponse updateVideoMessage(@Path("messageId") String messageId, @Part("message[video]") TypedFile videoTypedFile, @Part("message[image]") TypedFile thumbnailTypedFile);

    /* POSTS */

    @POST("/api/posts")
    GetPostsResponse createPost(@Body MultipartTypedOutput multipartTypedOutput);

    @POST("/api/groups/{id}/posts")
    GetPostsResponse createGroupPost(@Body MultipartTypedOutput multiPartTypedOutput, @Path("id") int groupId);

    @POST("/api/posts")
    GetPostsResponse createBusinessPost(@Body MultipartTypedOutput multiPartTypedOutput);

    @PUT("/api/posts/{id}")
    GetPostsResponse updatePost(@Path("id") String postId, @Body MultipartTypedOutput multipartTypedOutput);

    /* GROUPS */

    @Multipart
    @POST("/api/groups.json")
    GroupResponse createGroupWithAvatar(@Part("group[name]") String groupName,
                                        @Part("group[about]") String groupAbout,
                                        @Part("group[groupavatar]") TypedFile picture,
                                        @Part("group[group_type]") int groupType);

    @Multipart
    @PUT("/api/groups/{id}")
    GroupResponse updateGroupWithAvatar(@Path("id") String id, @Part("group[name]") String groupName, @Part("group[about]") String about, @Part("group[groupavatar]") TypedFile picture);

    @Multipart
    @PUT("/api/groups/{id}")
    GroupResponse updateGroupWithAvatarWithoutName(@Path("id") String id, @Part("group[about]") String about, @Part("group[groupavatar]") TypedFile picture);

    /* USERS */

    @Multipart
    @PUT("/api/users/{id}")
    Observable<GetUserResponse> updateProfileAvatar(@Part("user[avatar]") TypedFile avatar, @Path("id") int id);

    @Multipart
    @PUT("/api/users/{id}")
    Observable<GetUserResponse> updateProfileCover(@Part("user[cover_photo]") TypedFile avatar, @Path("id") int id);

    /* EVENTS */
    @Multipart
    @POST("/api/events")
    Observable<EventResponse> createGroupEventAsObservable(@Part("event[image]") TypedFile picture,
                                            @Part("event[group_id]") int groupId,
                                            @Part("event[name]") String eventName,
                                            @Part("event[description]") String eventDescription,
                                            @Part("event[start_time]") String eventStartTime,
                                            @Part("event[end_time]") String eventEndTime,
                                            @Part("event[location]") String eventLocation);

    @Multipart
    @PUT("/api/events/{id}")
    Observable<EventResponse> updateGroupEventWithPhoto(@Path("id") int id,
                                            @Part("event[image]") TypedFile picture,
                                            @Part("event[group_id]") int groupId,
                                            @Part("event[name]") String eventName,
                                            @Part("event[description]") String eventDescription,
                                            @Part("event[start_time]") String eventStartTime,
                                            @Part("event[end_time]") String eventEndTime,
                                            @Part("event[location]") String eventLocation);

    @Multipart
    @POST("/api/events")
    Observable<EventResponse> createBusinessEventAsObservable(@Part("event[image]") TypedFile picture,
                                               @Part("event[business_id]") int businessId,
                                               @Part("event[name]") String eventName,
                                               @Part("event[description]") String eventDescription,
                                               @Part("event[start_time]") String eventStartTime,
                                               @Part("event[end_time]") String eventEndTime,
                                               @Part("event[location]") String eventLocation);

    @Multipart
    @PUT("/api/events/{id}")
    EventResponse updateBusinessEventWithPhoto(@Path("id") int id,
                                               @Part("event[image]") TypedFile picture,
                                               @Part("event[business_id]") int businessId,
                                               @Part("event[name]") String eventName,
                                               @Part("event[description]") String eventDescription,
                                               @Part("event[start_time]") String eventStartTime,
                                               @Part("event[end_time]") String eventEndTime,
                                               @Part("event[location]") String eventLocation);

}
