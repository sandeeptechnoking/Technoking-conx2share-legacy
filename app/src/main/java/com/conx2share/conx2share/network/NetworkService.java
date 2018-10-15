package com.conx2share.conx2share.network;

import com.conx2share.conx2share.model.ChatHolder;
import com.conx2share.conx2share.model.ChatsHolder;
import com.conx2share.conx2share.model.CommentHolder;
import com.conx2share.conx2share.model.ContactParams;
import com.conx2share.conx2share.model.Creds;
import com.conx2share.conx2share.model.EventResponse;
import com.conx2share.conx2share.model.ExpirationWrapper;
import com.conx2share.conx2share.model.FlagHolder;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupInviteActionWrapper;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.model.LiveEventWrapper;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.MessagesHolder;
import com.conx2share.conx2share.model.NewsSources;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.PromoCodeWrapper;
import com.conx2share.conx2share.model.PurchaseSubscriptionWrapper;
import com.conx2share.conx2share.model.RegisteredDeviceResponse;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.model.UpdatePasswordWrapper;
import com.conx2share.conx2share.model.UpdateResponse;
import com.conx2share.conx2share.model.UserIdWrapper;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.model.ViewMessageWrapper;
import com.conx2share.conx2share.network.models.GetFriendsResponse;
import com.conx2share.conx2share.network.models.Like;
import com.conx2share.conx2share.network.models.MessageWrapper;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.param.CommentWrapper;
import com.conx2share.conx2share.network.models.param.GroupInviteParams;
import com.conx2share.conx2share.network.models.param.GroupLeaveParams;
import com.conx2share.conx2share.network.models.param.PostChatParams;
import com.conx2share.conx2share.network.models.param.RsvpWrapper;
import com.conx2share.conx2share.network.models.param.SayNoChatParams;
import com.conx2share.conx2share.network.models.response.BadgeCount;
import com.conx2share.conx2share.network.models.response.BadgeCountResponse;
import com.conx2share.conx2share.network.models.response.BusinessResponse;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;
import com.conx2share.conx2share.network.models.response.GetEventListResponse;
import com.conx2share.conx2share.network.models.response.GetGroupInviteActionResponse;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.network.models.response.GetNotificationResponse;
import com.conx2share.conx2share.network.models.response.GetPostCommentsResponse;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.network.models.response.GetPurchaseResponse;
import com.conx2share.conx2share.network.models.response.GetUserResponse;
import com.conx2share.conx2share.network.models.response.GroupInviteResponse;
import com.conx2share.conx2share.network.models.response.GroupLeaveResponse;
import com.conx2share.conx2share.network.models.response.HashTagsResponse;
import com.conx2share.conx2share.network.models.response.MessagesResponse;
import com.conx2share.conx2share.network.models.response.NotificationsUpdateViewedResponse;
import com.conx2share.conx2share.network.models.response.PostChatResponse;
import com.conx2share.conx2share.network.models.response.RsvpResponse;
import com.conx2share.conx2share.network.models.response.SendMessageResponse;
import com.conx2share.conx2share.network.models.response.ServerTimeResponse;
import com.conx2share.conx2share.network.models.response.SignUpResponse;
import com.conx2share.conx2share.network.models.response.SuccessResponse;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

public interface NetworkService {

    /*
        USERS
    */

    @POST("/api/users/sign_in")
    Observable<SignUpResponse> login(@Body Creds creds);

    @POST("/api/users")
    Observable<SignUpResponse> signUp(@Body Creds creds);

    @GET("/api/users/get_friends?user_preview=1")
    Observable<GetFriendsResponse> getFriends(@Query("new") String newString, @Query("mutual") Boolean mutual);

    @GET("/api/users/search")
    Observable<Users> searchUsers(@Query("query") String searchTerms,
                                  @Query("page") Integer page,
                                  @Query("include") String include);

    @GET("/api/users/follow_search")
    Users searchFollowUsers(@Query("query") String query,
                            @Query("group_id") String groupId,
                            @Query("page") String page);

    @GET("/api/users/me")
    User getUserProfile();

    @DELETE("/api/users/{id}/unfollow")
    Object unfollowUser(@Path("id") String id);

    @DELETE("/api/users/{id}/unfollow")
    Object unfollowMe(@Path("id") String id, @Query("forced") boolean forced);

    @POST("/api/users/{id}/follow")
    Object followUser(@Path("id") String id);

    @GET("/api/users/{id}")
    GetUserResponse getUser(@Path("id") String id);

    @GET("/api/users/{id}")
    Observable<GetUserResponse> getUserObservable(@Path("id") String id);

    @GET("/api/users/{id}/get_following")
    Users getFollowingUsers(@Path("id") int id, @Query("page") int page);

    @GET("/api/users/{id}/get_followers")
    Users getFollowers(@Path("id") Integer id, @Query("page") Integer page);

    @PUT("/api/users/{id}")
    Observable<GetUserResponse> updatePassword(@Path("id") String id, @Body UpdatePasswordWrapper user);

    @PUT("/api/users/{id}")
    GetUserResponse updateProfileUser(@Path("id") int id, @Body User user);

    @PUT("/api/users/{id}")
    GetUserResponse updateToPromoUser(@Path("id") int id, @Body PromoCodeWrapper promoCodeWrapper);

    @POST("/api/users/invite_friend.json")
    SuccessResponse inviteFriends(@Body ContactParams contactParams);

    @POST("/api/users/{id}/favorite")
    Observable<Void> favoriteUser(@Path("id") String id);

    @DELETE("/api/users/{id}/unfavorite")
    Observable<Void> unfavoriteUser(@Path("id") String id);

    @GET("/api/users/{id}/get_favorites")
    Observable<GetFriendsResponse> getFavorites(@Path("id") int id);

    /*
        MESSAGES
    */

    @GET("/api/messages")
    MessagesResponse getContextMessages(@Query("chat_id") int chatId);

    @GET("/api/messages")
    Observable<MessagesResponse> getMessagesByChatId(@Query("chat_id") int chatId);

    @GET("/api/messages/{messageId}")
    Observable<MessagesHolder> getMessageById(@Path("messageId") int messageId);

    @GET("/api/messages/missed")
    Observable<MessagesResponse> getUnreadMessages();

    @GET("/api/messages/missed")
    MessagesResponse getMissedMessages(@Query("user_id") String userId);

    @POST("/api/messages")
    SendMessageResponse sendMessage(@Body MessageWrapper message);

    @Multipart
    @POST("/api/messages")
    Observable<SendMessageResponse> sendMessage(
            @Part("message[body]") TypedString body,
            @Part("message[time_to_live") TypedString timeToLive,
            @Part("message[audio_length") TypedString audioLength,
            @Part("message[title]") TypedString title,
            @Part("message[chat_id]") TypedString chatIdInner,
            @Part("chat_id") TypedString chatId,
            @Part("message[image]") TypedFile photoFile,
            @Part("message[video]") TypedFile videoFile,
            @Part("message[audio]") TypedFile audioFile,
            @Part("auth_token") TypedString auth);

    @PUT("/api/messages/{id}")
    SendMessageResponse updateMessage(@Path("id") String id, @Body ViewMessageWrapper message);

    @GET("/api/messages/sent_messages")
    MessagesResponse getSentMessages(@Query("user_id") String userId);

    @PUT("/api/messages/update_multiple")
    MessagesResponse updateExpiration(@Body ExpirationWrapper expirationWrapper);

    /*
        POSTS
    */

    @GET("/api/posts/feed")
    Observable<GetPostsResponse> getNewestPosts();

    @GET("/api/posts/feed")
    Observable<GetPostsResponse> refreshFeed(@Query("direction") String direction, @Query("id") String id);

    @GET("/api/posts/feed")
    Observable<GetPostsResponse> getFeedPage(@Query("page") int page);

    @POST("/api/likes")
    Like likePost(@Body Like like);

    @DELETE("/api/posts/{postId}/unlike.json")
    Like unlikePost(@Path("postId") int postId);

    @GET("/api/users/get_feed")
    GetPostsResponse getUserFeed(@Query("id") String id);

    @GET("/api/users/get_feed")
    GetPostsResponse refreshUserFeed(@Query("direction") String direction, @Query("post_id") String id, @Query("id")
            String userId);

    @GET("/api/posts/{id}")
    GetPostsResponse getPost(@Path("id") String postId);

    @POST("/api/flags")
    ResponseMessage flagPost(@Body FlagHolder flagHolder);

    @DELETE("/api/posts/{id}")
    GetPostsResponse deletePost(@Path("id") String postId);

    @Multipart
    @PUT("/api/posts/{id}")
    Post updatePostWithJustBody(@Path("id") String id, @Part("post[body]") TypedString body, @Part("post[picture]")
            String picture, @Part("post[video]") String video);

    @GET("/api/posts/{id}")
    Observable<GetPostsResponse> getPostWithLikers(@Path("id") String postId,
                                                   @Query("include_liker") boolean includeLiker);

    /*
        POST COMMENTS
     */

    @GET("/api/posts/{id}/comments")
    GetPostCommentsResponse getPostComments(@Path("id") String postId, @Query("page") String pageNumber);

    @POST("/api/comments")
    GetPostCommentsResponse postComment(@Body CommentWrapper comment);

    @PUT("/api/comments/{id}")
    GetPostCommentsResponse updateComment(@Path("id") String postCommentId, @Body CommentWrapper comment);

    @DELETE("/api/comments/{id}")
    CommentHolder deletePostComment(@Path("id") String postCommentId);

    /*
        GROUPS
    */

    @GET("/api/groups/my_groups")
    GetGroupListResponse getGroups(@Query("exclude_events") boolean excludeGroups);

    @GET("/api/groups/my_groups")
    Observable<GetGroupListResponse> getGroupsAsObservable();

    @POST("/api/groups")
    GroupResponse createGroup(@Body Group group);

    @POST("/api/groups/{id}/follow")
    GroupResponse followGroup(@Path("id") int groupId);

    @DELETE("/api/groups/{id}/unfollow")
    GroupResponse unfollowGroup(@Path("id") int groupId);

    @PUT("/api/groups/{id}")
    GroupResponse updateGroup(@Path("id") String id, @Query("group[name]") String groupName, @Query("group[about]")
            String about);

    @PUT("/api/groups/{id}")
    GroupResponse updateGroupWithoutName(@Path("id") String id, @Query("group[about]") String about);

    @GET("/api/groups/{id}")
    GroupResponse getGroup(@Path("id") int groupId);

    @GET("/api/groups/{id}/get_feed")
    GroupResponse getGroupPosts(@Path("id") int groupId);

    @GET("/api/groups/my_say_no_groups")
    Observable<GetGroupListResponse> getSayNoGroups();

    @GET("/api/groups/{id}/get_feed")
    GetPostsResponse refreshGroupFeed(@Query("direction") String direction, @Query("post_id") int id, @Path("id") int
            groupId);

    @POST("/api/groups/invite")
    Observable<GroupInviteResponse> inviteUserToGroup(@Body GroupInviteParams params);

    @POST("/api/groups/accept")
    Observable<GetGroupInviteActionResponse> acceptGroupInvite(@Body GroupInviteActionWrapper param);

    @POST("/api/groups/leave")
    GroupLeaveResponse leaveGroup(@Body GroupLeaveParams params);

    @DELETE("/api/groups/decline")
    Observable<GetGroupInviteActionResponse> declineGroupInvite(@Query("group_id") String param);

    @GET("/api/groups/{id}/get_members")
    Users getCurrentGroupMembers(@Path("id") String groupId);

    @GET("/api/groups/{id}/get_followers")
    Users getCurrentGroupFollowers(@Path("id") String groupId);

    @GET("/api/groups/get_badge_count")
    Observable<BadgeCountResponse> getGroupsBadgeCount();

    @DELETE("/api/groups/{id}/remove_user_from_group")
    ResponseMessage removeUserFromGroup(@Path("id") int groupId, @Query("user_id") String userId);

    @POST("/api/groups/{id}/block")
    ResponseMessage blockUserFromGroup(@Path("id") int groupId, @Body UserIdWrapper userId);

    @DELETE("/api/groups/{id}/unblock")
    ResponseMessage unblockUserFromGroup(@Path("id") int groupId, @Query("user_id") String userId);

    @POST("/api/groups/{id}/invite_to_follow")
    ResponseMessage inviteToFollowGroup(@Path("id") int groupId, @Body UserIdWrapper userIdWrapper);

    @GET("/api/groups/search")
    GetGroupListResponse searchGroups(@Query("query") String searchTerms, @Query("page") int page);

    @GET("/api/groups/search")
    Observable<GetGroupListResponse> searchGroupsBy(@Query("query") String searchTerms,
                                                    @Query("page") int page);

    @POST("/api/groups/{group_id}/say_no_invitations")
    Observable<Void> sendSayNoGroupInvitation(@Path("group_id") int groupId);

    /*
        NOTIFICATIONS
    */

    @GET("/api/notifications?except=Message")
    Observable<GetNotificationResponse> getNotifications();

    @GET("/api/notifications")
    Observable<GetNotificationResponse> getNotificationsWithDirection(@Query("id") int id, @Query("direction") String direction);

    @GET("/api/notifications/badge_count")
    Observable<BadgeCountResponse> getNotificationBadgeCount();

    @GET("/api/notifications/update_viewed")
    Observable<NotificationsUpdateViewedResponse> updateNotificationsAsViewed(@Query("ids") ArrayList<String> ids);

    /*
        CHATS
     */

    @POST("/api/chats")
    PostChatResponse startChat(@Body PostChatParams params);

    @PUT("/api/chats/{id}")
    Observable<Void> changeChatState(@Path("id") int chatId,
                                     @Body JsonObject body);

    @POST("/api/chats")
    Observable<PostChatResponse> startSayNoChat(@Body SayNoChatParams params);

    @GET("/api/chats?user_preview=true")
    Observable<ChatsHolder> getIndexOfChats(@Query("page") Integer page);

    @POST("/api/chats?user_preview=true")
    ChatHolder getChatBetweenUsers(@Body UserIdWrapper id);

    @POST("/api/chats?user_preview=true")
    Observable<ChatHolder> getChatWith(@Body UserIdWrapper id);

    @PUT("/api/chat_users")
    Observable<Void> switchChatMode(@Query("chat_id") int chatId,
                                    @Query("user_id") int userId,
                                    @Query("chat_user[is_anonymous]") boolean isAnonymous);

    /*
        PURCHASES
     */

    @POST("/api/purchases")
    GetPurchaseResponse purchasePlan(@Body PurchaseSubscriptionWrapper purchase);

    /*
        RESET PASSWORD
     */
    @GET("/api/users/forgot_password")
    Observable<GetGroupInviteActionResponse> resetPassword(@Query("email") String email);

    /*
        PROMO CODES
     */

    @GET("/api/promo_codes/valid")
    ResponseMessage checkPromoCode(@Query("promo_code") String promoCode);


    /*
        BUSINESSES
     */

    @GET("/api/businesses/{id}")
    BusinessResponse getBusiness(@Path("id") int id);

    @GET("/api/businesses/{id}/get_feed")
    GetPostsResponse getBusinessFeed(@Path("id") int id, @Query("page") int page);

    @GET("/api/businesses/my_businesses")
    BusinessesResponse getMyBusinesses();

    @GET("/api/businesses/my_businesses")
    Observable<BusinessesResponse> getMyBusinessesAsObservable();

    @GET("/api/businesses/search")
    BusinessesResponse searchBusinesses(@Query("query") String searchTerms, @Query("page") int page);

    @GET("/api/businesses/followed_businesses")
    BusinessesResponse getFollowedBusinesses();

    @POST("/api/businesses/{id}/follow")
    BusinessResponse followBusiness(@Path("id") int businessId);

    @DELETE("/api/businesses/{id}/unfollow")
    BusinessResponse unfollowBusiness(@Path("id") int businessId);

    @GET("/api/businesses/get_badge_count")
    Observable<BadgeCountResponse> getBusinessesBadgeCount();

    /*
        HASH TAGS
     */

    @GET("/api/hashtags/search")
    HashTagsResponse searchHashTags(@Query("query") String searchTerms, @Query("page") int page);

    @GET("/api/hashtags/get_feed")
    GetPostsResponse getHashTagFeedWithDirection(@Query("hashtag") String hashTag, @Query("post_id") int postId,
                                                 @Query("direction") String direction);

    @GET("/api/hashtags/get_feed")
    GetPostsResponse getHashTagFeed(@Query("hashtag") String hashTag);

    /*
        NEWS
     */

    @GET("/api/news_feeds")
    NewsSources getNewsSources();

    @GET("/api/live_streams/search")
    Observable<LiveEventWrapper> getLiveStreamList(@Query("query") String query);

    /*
        EVENTS
     */
    @GET("/api/events")
    GetEventListResponse getEventsForGroup(@QueryMap Map<String, Integer> params);

    @GET("/api/events")
    Observable<GetEventListResponse> getGroupEventsAsObservable(@QueryMap Map<String, Integer> params);

    @GET("/api/events/{id}")
    EventResponse getEvent(@Path("id") int eventId);

    @GET("/api/events/{id}")
    Observable<EventResponse> getEventAsObservable(@Path("id") int eventId);

    @DELETE("/api/events/{id}")
    EventResponse deleteEvent(@Path("id") int eventId);

    @PUT("/api/events/{id}/rsvp")
    RsvpResponse registerAttendance(@Path("id") int id, @Body RsvpWrapper rsvp);

    @PUT("/api/events/{id}")
    Observable<EventResponse> updateEventWithoutPhoto(@Path("id") int eventId,
                                                      @Query("event[name]") String eventName,
                                                      @Query("event[description]") String eventDescription,
                                                      @Query("event[start_time]") String eventStartTime,
                                                      @Query("event[end_time]") String eventEndTime,
                                                      @Query("event[location]") String eventLocation);


    @POST("/api/devices")
    RegisteredDeviceResponse registerDevice(@Query("device[uid]") String token,
                                            @Query("device[os]") String os,
                                            @Query("device[app_version]") String appVersion);

    @DELETE("/api/devices/{id}")
    Response unregisterDevice(@Path("id") int deviceId);

    @GET("/api/app_versions/check_for_updates?os=android")
    Observable<UpdateResponse> checkShouldUpdate(@Query("version") String appVersion);

    @GET("/api/notifications/update_viewed?all=1")
    Observable<BadgeCount> clearAllNotification();

    @POST("/api/users/{id}/follow")
    Observable<ResponseMessage> followUserRequest(@Path("id") String id, @Query("invite") Boolean invite);

    @POST("/api/users/{id}/accept")
    Observable<ResponseMessage> acceptFollowInvite(@Path("id") String id);

    @DELETE("/api/users/{id}/decline")
    Observable<ResponseMessage> declineFollowInvite(@Path("id") String id);

    @GET("/api/messages")
    Observable<MessagesResponse> getChatHistory(@Query("chat_id") int chatId, @Query("message_id") Integer page);

}
