package com.conx2share.conx2share.network;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.Conx2ShareApplication;
import com.conx2share.conx2share.model.AuthUser;
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
import com.conx2share.conx2share.model.LiveEvent;
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
import com.conx2share.conx2share.network.models.SendMessage;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.network.models.param.CommentWrapper;
import com.conx2share.conx2share.network.models.param.EventParam;
import com.conx2share.conx2share.network.models.param.GetHashTagFeedParams;
import com.conx2share.conx2share.network.models.param.GroupInviteParams;
import com.conx2share.conx2share.network.models.param.GroupLeaveParams;
import com.conx2share.conx2share.network.models.param.GroupParam;
import com.conx2share.conx2share.network.models.param.PostChatParams;
import com.conx2share.conx2share.network.models.param.PostParams;
import com.conx2share.conx2share.network.models.param.RsvpWrapper;
import com.conx2share.conx2share.network.models.param.SearchParams;
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
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.Statics;
import com.conx2share.conx2share.util.TypedUri;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

import static rx.Observable.just;

@Singleton
public class NetworkClient {

    public static final String TAG = NetworkClient.class.getSimpleName();
    public static final long MEDIA_UPLOAD_TIMEOUT_IN_MILLIS = TimeUnit.MINUTES.toMillis(3);
    private static final String ANDROID_DEVICE = "Android";

    @Inject
    PreferencesUtil preferencesUtil;
    private NetworkService service;
    private MediaNetworkService mediaService;

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return builder.create();
    }

    public NetworkService getService() {
        if (service == null) {
            Gson gson = getGson();
            RestAdapter adapter = new RestAdapter.Builder().setEndpoint(getBaseUrl()).setConverter(new GsonConverter
                    (gson)).setRequestInterceptor(new NetworkRequestInterceptor()).build();
            adapter.setLogLevel(LogUtil.getRestAdapterLogLevel());
            service = adapter.create(NetworkService.class);
        }
        return service;
    }

    private MediaNetworkService getMediaService() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(MEDIA_UPLOAD_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setConnectTimeout(MEDIA_UPLOAD_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
        if (mediaService == null) {
            Gson gson = getGson();
            RestAdapter adapter = new RestAdapter.Builder().setEndpoint(getBaseUrl()).setConverter(new GsonConverter
                    (gson)).setRequestInterceptor(new NetworkRequestInterceptor()).setClient(new OkClient
                    (okHttpClient)).build();
            adapter.setLogLevel(LogUtil.getMediaRestAdapterLogLevel());
            mediaService = adapter.create(MediaNetworkService.class);
        }
        return mediaService;
    }

    public String getBaseUrl() {
        return Statics.BASE_URL;
    }

    public Observable<SignUpResponse> signIn(final Creds params) {
        return getService().login(params);
    }

    public Observable<SignUpResponse> signUp(final Creds params) {
        return getService().signUp(params);
    }

    public Result<MessagesResponse> getContextMessages(final int chatId) {
        try {
            return new Result<>(getService().getContextMessages(chatId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Users> getFollowers(Integer id, Integer page) {
        try {
            return new Result<>(getService().getFollowers(id, page));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Users> getFollowingUsers(int id, int page) {
        try {
            return new Result<>(getService().getFollowingUsers(id, page));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetFriendsResponse> getFriends(@Nullable Boolean mutual) {
        return getService().getFriends("1", mutual);
    }

    public Observable<MessagesHolder> getMessageById(int messageId) {
        return getService().getMessageById(messageId);
    }

    public Observable<MessagesResponse> getUnreadMessages() {
        return getService().getUnreadMessages();
    }

    public Result<SendMessageResponse> sendMessage(final MessageWrapper message) {
        try {
            return new Result<>(getService().sendMessage(message));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<SendMessageResponse> sendChatMessage(String message, int chatId,
                                                           TypedFile photo, TypedFile video, TypedFile audio, String audioLength) {
        String chatIdAsString = String.valueOf(chatId);
        return getService().sendMessage(
                new TypedString(message),
                null,
                TextUtils.isEmpty(audioLength) ? null : new TypedString(audioLength),
                null,
                new TypedString(chatIdAsString),
                new TypedString(chatIdAsString),
                photo,
                video,
                audio,
                new TypedString(preferencesUtil.getAuthToken())
        );
    }

    public Observable<MessagesResponse> getMessagesByChatId(int chatId) {
        return getService().getMessagesByChatId(chatId);
    }

    public Observable<Void> switchChatMode(int chatId,
                                           int userId,
                                           boolean isAnonymous) {
        return getService().switchChatMode(chatId, userId, isAnonymous);
    }

    public Result<SendMessageResponse> postPhotoMessage(final MessageWrapper messageWrapper, TypedUri typedUri) {
        try {
            TypedFile typedFile = null;
            if (typedUri != null && typedUri.mimeType() != null && typedUri.getFilePath() != null) {
                typedFile = new TypedFile(typedUri.mimeType(), new File(typedUri.getFilePath()));
            }
            SendMessage message = messageWrapper.getMessage();

            TypedString timeToLive = null;
            if (message.getTimeToLive() != null) {
                timeToLive = new TypedString(String.valueOf(message.getTimeToLive()));
            }

            String chatIdString = String.valueOf(String.valueOf(message.getChatId()));

            return new Result<>(getMediaService().uploadPhotoMessage(new TypedString(message.getBody()), timeToLive,
                    new TypedString(message.getTitle()), new TypedString(chatIdString), new TypedString(chatIdString)
                    , typedFile, new TypedString(preferencesUtil.getAuthToken()), new TypedString(String.valueOf
                            (message.getToId()))));

        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<SendMessageResponse> postVideoMessage(final MessageWrapper messageWrapper, TypedFile typedFile,
                                                        TypedFile thumbnailTypedFile) {
        try {
            SendMessage message = messageWrapper.getMessage();
            TypedString timeToLive = null;
            if (message.getTimeToLive() != null) {
                timeToLive = new TypedString(String.valueOf(message.getTimeToLive()));
            }

            String chatIdString = String.valueOf(String.valueOf(message.getChatId()));

            return new Result<>(getMediaService().uploadVideoMessage(new TypedString(message.getBody()), timeToLive,
                    new TypedString(message.getTitle()), new TypedString(chatIdString), new TypedString(chatIdString)
                    , thumbnailTypedFile, new TypedString(preferencesUtil.getAuthToken()), typedFile, new TypedString
                            (String.valueOf(message.getToId()))));

        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<SendMessageResponse> postAudioMessage(final MessageWrapper messageWrapper, TypedFile typedFile) {

        try {
            SendMessage message = messageWrapper.getMessage();
            TypedString timeToLive = null;
            if (message.getTimeToLive() != null) {
                timeToLive = new TypedString(String.valueOf(message.getTimeToLive()));
            }

            String chatIdString = String.valueOf(String.valueOf(message.getChatId()));

            return new Result<>(getMediaService().uploadAudioMessage(new TypedString(message.getBody()), timeToLive,
                    new TypedString(message.getTitle()), new TypedString(chatIdString), new TypedString(chatIdString)
                    , new TypedString(preferencesUtil.getAuthToken()), typedFile, new TypedString(String.valueOf
                            (message.getToId())), new TypedString(String.valueOf(message.getAudioLength()))));

        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<SendMessageResponse> updateMessage(final String params, ViewMessageWrapper message) {
        try {
            return new Result<>(getService().updateMessage(params, message));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<SendMessageResponse> updatePhotoMessage(final String messageId, TypedUri typedUri) {
        try {
            TypedFile typedFile = null;
            if (typedUri != null && typedUri.mimeType() != null && typedUri.getFilePath() != null) {
                typedFile = new TypedFile(typedUri.mimeType(), new File(typedUri.getFilePath()));
            }

            return new Result<>(getMediaService().updatePhotoMessage(messageId, typedFile));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<SendMessageResponse> updateVideoMessage(final String messageId, TypedFile videoTypedFile, TypedFile
            thumbnailTypedFile) {
        try {
            return new Result<>(getMediaService().updateVideoMessage(messageId, videoTypedFile, thumbnailTypedFile));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<MessagesResponse> getSentMessages(final String user_id) {
        try {
            return new Result<>(getService().getSentMessages(user_id));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<MessagesResponse> getMissedMessages(final String user_id) {
        try {
            return new Result<>(getService().getMissedMessages(user_id));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetPostsResponse> getNewestPosts() {
        return getService().getNewestPosts();
    }

    public Observable<GetPostsResponse> refreshFeed(String direction, String id) {
        return getService().refreshFeed(direction, id);
    }

    public Observable<GetPostsResponse> getFeedPage(int page) {
        return getService().getFeedPage(page);
    }

    public Result<GetPostsResponse> refreshUserFeed(String direction, String id, String userId) {
        try {
            return new Result<>(getService().refreshUserFeed(direction, id, userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<Users> searchUsers(String query, Integer page, String include) {
        return getService().searchUsers(query, page, include);
    }

    public Result<Users> searchFollowUsers(String query, String groupId, String page) {
        try {
            return new Result<>(getService().searchFollowUsers(query, groupId, page));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<User> getUserProfile() {
        try {
            return new Result<>(getService().getUserProfile());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Post> createPost(PostParams postParams) {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        addCommonPostMultipartData(multipartTypedOutput, postParams);

        try {
            return new Result<>(getMediaService().createPost(multipartTypedOutput).getPost());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    private void addCommonPostMultipartData(MultipartTypedOutput multipartTypedOutput, PostParams postParams) {
        TypedString body = new TypedString(postParams.getBody());
        multipartTypedOutput.addPart("post[body]", body);

        ArrayList<UserTag> userTags = null;
        if (postParams.getUserTags() != null && postParams.getUserTags().size() > 0) {
            userTags = postParams.getUserTags();
        }
        if (userTags != null) {
            for (UserTag tag : userTags) {
                multipartTypedOutput.addPart("post[user_tags_attributes][][tag]", new TypedString(tag.getTag()));
                multipartTypedOutput.addPart("post[user_tags_attributes][][tagger_id]", new TypedString(String
                        .valueOf(tag.getTaggerId())));
                multipartTypedOutput.addPart("post[user_tags_attributes][][user_id]", new TypedString(String.valueOf
                        (tag.getUserId())));
            }
        }

        if (postParams.getPicture() != null && postParams.getPicture().mimeType() != null && postParams.getPicture()
                .getFilePath() != null) {
            TypedFile pictureTypedFile = new TypedFile(postParams.getPicture().mimeType(), new File(postParams
                    .getPicture().getFilePath()));
            multipartTypedOutput.addPart("post[picture]", pictureTypedFile);
        }

        if (postParams.getVideo() != null) {
            multipartTypedOutput.addPart("post[video]", postParams.getVideo());
        }

        if (postParams.getPrivate()) {
            multipartTypedOutput.addPart("post[is_private]", new TypedString("1"));
        }
    }

    public Result<Post> createGroupPost(PostParams postParams) {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        addCommonPostMultipartData(multipartTypedOutput, postParams);

        try {
            return new Result<>(getMediaService().createGroupPost(multipartTypedOutput, postParams.getPostReceiver()
                    .getReceiverId()).getPost());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Post> createBusinessPost(PostParams postParams) {

        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        addCommonPostMultipartData(multipartTypedOutput, postParams);
        multipartTypedOutput.addPart("post[business_id]", new TypedString(String.valueOf(postParams.getPostReceiver()
                .getReceiverId())));

        try {
            return new Result<>(getMediaService().createBusinessPost(multipartTypedOutput).getPost());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostsResponse> editPost(PostParams postParams) {

        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        //        multipartTypedOutput.addPart("id", new TypedString(postParams.getId()));
        addCommonPostMultipartData(multipartTypedOutput, postParams);

        try {
            return new Result<>(getMediaService().updatePost(postParams.getId(), multipartTypedOutput));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Like> likePost(Like like) {
        try {
            return new Result<>(getService().likePost(like));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Like> unlikePost(int postId) {
        try {
            return new Result<>(getService().unlikePost(postId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostsResponse> deletePost(String postId) {
        try {
            return new Result<>(getService().deletePost(postId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<CommentHolder> deletePostComment(String postCommentId) {
        try {
            return new Result<>(getService().deletePostComment(postCommentId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostsResponse> getUserFeed(String userId) {
        try {
            return new Result<>(getService().getUserFeed(userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetUserResponse> getUser(String userId) {
        try {
            return new Result<>(getService().getUser(userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetUserResponse> getUserObservable(String userId) {
        return getService().getUserObservable(userId);
    }

    public Observable<GetUserResponse> updateAvatar(TypedFile avatar, int userId) {
        return getMediaService().updateProfileAvatar(avatar, userId);
    }

    public Observable<GetUserResponse> updateCover(TypedFile cover, int userId) {
        return getMediaService().updateProfileCover(cover, userId);
    }

    public Observable<GetUserResponse> updatePassword(UpdatePasswordWrapper user, String userId) {
        return getService().updatePassword(userId, user);
    }

    public Result<GetUserResponse> updateProfileUser(Integer id, User updateUser) {

        try {
            return new Result<>(getService().updateProfileUser(id, updateUser));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostsResponse> getPost(String postId) {
        try {
            return new Result<>(getService().getPost(postId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetPostsResponse> getPostWithLikers(String postId) {
        return getService().getPostWithLikers(postId, true);
    }

    public Result<GetPostCommentsResponse> getPostComments(String postId, String pageNumber) {
        try {
            return new Result<>(getService().getPostComments(postId, pageNumber));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostCommentsResponse> postComment(CommentWrapper wrapper) {
        try {
            return new Result<>(getService().postComment(wrapper));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostCommentsResponse> updateComment(String commentId, CommentWrapper comment) {
        try {
            return new Result<>(getService().updateComment(commentId, comment));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<SuccessResponse> inviteFriends(ContactParams contactParams) {
        try {
            return new Result<>(getService().inviteFriends(contactParams));
        } catch (RetrofitError error) {
            return new Result<>(error);

        }
    }

    public Result<GetGroupListResponse> getGroups(boolean excludeGroups) {
        try {
            return new Result<>(getService().getGroups(excludeGroups));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetGroupListResponse> getGroupsAsObservable() {
        return getService().getGroupsAsObservable();
    }

    public Result<GroupResponse> createGroup(GroupParam param) {
        try {
            TypedFile typedFile = null;
            if (param != null && param.getAttachmentUri() != null && param.getAttachmentUri().mimeType() != null &&
                    param.getAttachmentUri().getFilePath() != null) {
                typedFile = new TypedFile(param.getAttachmentUri().mimeType(), new File(param.getAttachmentUri()
                        .getFilePath()));
            }

            return new Result<>(getMediaService().createGroupWithAvatar(param.getGroupName(), param.getGroupAbout(),
                    typedFile, param.getGroupType().ordinal()));
        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<GroupResponse> updateGroup(Group group, String groupName, String about, TypedUri attachmentUri) {
        try {
            if (attachmentUri != null) {
                TypedFile typedFile = null;
                if (attachmentUri.mimeType() != null && attachmentUri.getFilePath() != null) {
                    typedFile = new TypedFile(attachmentUri.mimeType(), new File(attachmentUri.getFilePath()));
                }

                if (!group.getName().equals(groupName)) {
                    Log.d(TAG, "Updating group with avatar and name has changed");
                    return new Result<>(getMediaService().updateGroupWithAvatar(group.getId().toString(), groupName,
                            about, typedFile));
                } else {
                    Log.d(TAG, "Updating group with avatar and name has not changed");
                    return new Result<>(getMediaService().updateGroupWithAvatarWithoutName(group.getId().toString(),
                            about, typedFile));
                }
            } else {
                if (!group.getName().equals(groupName)) {
                    Log.d(TAG, "Updating group without avatar and name has changed");
                    return new Result<>(getService().updateGroup(group.getId().toString(), groupName, about));
                } else {
                    Log.d(TAG, "Updating group without avatar and name hasn't changed");
                    return new Result<>(getService().updateGroupWithoutName(group.getId().toString(), about));
                }
            }
        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<GroupResponse> getGroup(int groupId) {
        try {
            return new Result<>(getService().getGroup(groupId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GroupInviteResponse> inviteUserToGroup(GroupInviteParams params) {
        return getService().inviteUserToGroup(params);
    }

    public Result<GroupLeaveResponse> leaveGroup(GroupLeaveParams params) {
        try {
            return new Result<>(getService().leaveGroup(params));
        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<GroupResponse> followGroup(int groupId) {
        try {
            return new Result<>(getService().followGroup(groupId));
        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<GroupResponse> unfollowGroup(int groupId) {
        try {
            return new Result<>(getService().unfollowGroup(groupId));
        } catch (RetrofitError e) {
            return new Result<>(e);
        }
    }

    public Result<GroupResponse> getGroupPosts(int groupId) {
        try {
            return new Result<>(getService().getGroupPosts(groupId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<NotificationsUpdateViewedResponse> updateNotificationsAsViewed(ArrayList<String> ids) {
        return getService().updateNotificationsAsViewed(ids);
    }

    public Observable<BadgeCountResponse> getNotificationBadgeCount() {
        return getService().getNotificationBadgeCount();
    }

    public Observable<BadgeCountResponse> getGroupsBadgeCount() {
        return getService().getGroupsBadgeCount();
    }

    public Observable<BadgeCountResponse> getBusinessesBadgeCount() {
        return getService().getBusinessesBadgeCount();
    }

    public Observable<GetNotificationResponse> getNotifications() {
        return getService().getNotifications();
    }

    public Observable<GetNotificationResponse> getNotificationsWithDirection(int id, String direction) {
        return getService().getNotificationsWithDirection(id, direction);
    }

    public Result<GetPostsResponse> refreshGroupFeed(String direction, int id, int groupId) {
        try {
            return new Result<>(getService().refreshGroupFeed(direction, id, groupId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetGroupInviteActionResponse> acceptGroupInvite(GroupInviteActionWrapper param) {
        return getService().acceptGroupInvite(param);
    }

    public Observable<GetGroupInviteActionResponse> declineGroupInvite(GroupInviteActionWrapper param) {
        return getService().declineGroupInvite(param.getGroupId());
    }

    public Result<Users> getCurrentGroupMembers(String groupId) {
        try {
            return new Result<>(getService().getCurrentGroupMembers(groupId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Users> getCurrentGroupFollowers(String groupId) {
        try {
            return new Result<>(getService().getCurrentGroupFollowers(groupId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<ResponseMessage> removeUserFromGroup(int groupId, String userId) {
        try {
            return new Result<>(getService().removeUserFromGroup(groupId, userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<ResponseMessage> blockUserFromGroup(int groupId, UserIdWrapper userId) {
        try {
            return new Result<>(getService().blockUserFromGroup(groupId, userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<ResponseMessage> unblockUserFromGroup(int groupId, String userId) {
        try {
            return new Result<>(getService().unblockUserFromGroup(groupId, userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<ResponseMessage> inviteToFollowGroup(int groupId, UserIdWrapper userId) {
        try {
            return new Result<>(getService().inviteToFollowGroup(groupId, userId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetGroupListResponse> searchGroups(SearchParams params) {
        try {
            return new Result<>(getService().searchGroups(params.getQuery(), params.getPage()));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetGroupListResponse> searchGroupsBy(@NonNull String query,
                                                           int page) {
        return getService().searchGroupsBy(query, page);
    }

    public Observable<List<Group>> getSayNoGroup() {
        return getService().getSayNoGroups()
                .map(GetGroupListResponse::getGroups);
    }

    public Observable<Void> inviteMemberToGroup(int groupId) {
        return getService().sendSayNoGroupInvitation(groupId);
    }

    public Observable<ChatHolder> getChatWith(int friendId) {
        return getService().getChatWith(new UserIdWrapper(friendId));
    }

    public Result<ChatHolder> getChatBetweenUsers(int friendId) {
        try {
            return new Result<>(getService().getChatBetweenUsers(new UserIdWrapper(friendId)));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<ChatsHolder> getIndexOfChats(Integer page) {
        return getService().getIndexOfChats(page);
    }

    public Boolean unfollowUser(final String id) {
        try {
            getService().unfollowUser(id);
            return true;
        } catch (RetrofitError error) {
            return false;
        }
    }

    public Boolean followUser(String id) {
        try {
            getService().followUser(id);
            return true;
        } catch (RetrofitError error) {
            return false;
        }
    }

    public Observable<ResponseMessage> followUserRequest(final String id, final Boolean invite) {
        return getService().followUserRequest(id, invite);
    }

    public Observable<ResponseMessage> acceptFollowInvite(final String id) {
        return getService().acceptFollowInvite(id);
    }

    public Observable<ResponseMessage> declineFollowInvite(final String id) {
        return getService().declineFollowInvite(id);
    }

    public Boolean unfollowMe(final String id) {
        try {
            getService().unfollowMe(id, true);
            return true;
        } catch (RetrofitError error) {
            return false;
        }
    }

    public Result<MessagesResponse> updateExpiration(ExpirationWrapper expirationWrapper) {
        try {
            return new Result<>(getService().updateExpiration(expirationWrapper));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<PostChatResponse> startChat(String userId) {
        try {
            return new Result<>(getService().startChat(new PostChatParams(userId)));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPurchaseResponse> purchasePlan(PurchaseSubscriptionWrapper purchaseWrapper) {
        try {
            return new Result<>(getService().purchasePlan(purchaseWrapper));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetGroupInviteActionResponse> resetPassword(String email) {
        return getService().resetPassword(email);
    }

    public Result<ResponseMessage> flagPost(FlagHolder flagHolder) {
        try {
            return new Result<>(getService().flagPost(flagHolder));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<Void> favoriteUser(String id) {
           return getService().favoriteUser(id);
    }

    public Observable<Void> unfavoriteUser(String id) {
            return getService().unfavoriteUser(id);
    }

    public Observable<GetFriendsResponse> getFavorites() {
        AuthUser authUser = preferencesUtil.getAuthUser();
        return (authUser == null || authUser.getId() == null) ?
                Observable.error(new IllegalStateException("AuthUser in preferences is null")) :
                getService().getFavorites(authUser.getId());
    }

    public Result<ResponseMessage> checkPromoCode(String promoCode) {
        try {
            return new Result<>(getService().checkPromoCode(promoCode));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetUserResponse> updateToPromoUser(int id, PromoCodeWrapper promoCodeWrapper) {
        try {
            return new Result<>(getService().updateToPromoUser(id, promoCodeWrapper));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<BusinessResponse> getBusiness(int id) {
        try {
            return new Result<>(getService().getBusiness(id));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostsResponse> getBusinessFeed(int id, int page) {
        try {
            return new Result<>(getService().getBusinessFeed(id, page));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<BusinessesResponse> getMyBusinessAsObservable() {
        return getService().getMyBusinessesAsObservable();
    }

    public Result<BusinessesResponse> getMyBusinesses() {
        try {
            return new Result<>(getService().getMyBusinesses());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<BusinessesResponse> searchBusinesses(SearchParams params) {
        try {
            return new Result<>(getService().searchBusinesses(params.getQuery(), params.getPage()));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<HashTagsResponse> searchHashTags(String searchTerms, int page) {
        try {
            return new Result<>(getService().searchHashTags(searchTerms, page));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<GetPostsResponse> getHashTagFeed(GetHashTagFeedParams params) {
        try {
            if (params.getPostId() != null && params.getDirection() != null) {
                return new Result<>(getService().getHashTagFeedWithDirection(params.getTitle(), params.getPostId(),
                        params.getDirection()));
            } else {
                return new Result<>(getService().getHashTagFeed(params.getTitle()));
            }
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<BusinessesResponse> getFollowedBusinesses() {
        try {
            return new Result<>(getService().getFollowedBusinesses());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    // NOTE: the server returns a business, but the is_owner and is_following fields are null
    public Result<BusinessResponse> followBusiness(int businessId) {
        try {
            return new Result<>(getService().followBusiness(businessId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    // NOTE: the server returns a business, but the is_owner and is_following fields are null
    public Result<BusinessResponse> unfollowBusiness(int businessId) {
        try {
            return new Result<>(getService().unfollowBusiness(businessId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<NewsSources> getNewsSources() {
        try {
            return new Result<>(getService().getNewsSources());
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<GetEventListResponse> getGroupEventsAsObservable(int groupId, String type) {
        Map<String, Integer> params = new HashMap<>();
        if (type.equals("business")) {
            params.put("business_id", groupId);
        } else {
            params.put("group_id", groupId);
        }
        return getService().getGroupEventsAsObservable(params);
    }

    public Result<GetEventListResponse> getEventsForGroup(int groupId, String type) {
        try {
            Map<String, Integer> params = new HashMap<>();
            if (type.equals("business")) {
                params.put("business_id", groupId);
            } else {
                params.put("group_id", groupId);
            }
            return new Result<>(getService().getEventsForGroup(params));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<EventResponse> getEvent(int eventId) {
        try {
            return new Result<>(getService().getEvent(eventId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<EventResponse> getEventAsObservable(int eventId) {
        return getService().getEventAsObservable(eventId);
    }

    public Result<EventResponse> deleteEvent(int eventId) {
        try {
            return new Result<>(getService().deleteEvent(eventId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<RsvpResponse> registerAttendance(int eventId, int attendanceType) {
        try {
            return new Result<>(getService().registerAttendance(eventId, new RsvpWrapper(attendanceType)));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<EventResponse> createEvent(EventParam param) {
        TypedFile typedFile = null;
        if (param != null && param.getAttachmentUri() != null && param.getAttachmentUri().mimeType() != null &&
                param.getAttachmentUri().getFilePath() != null) {
            typedFile = new TypedFile(param.getAttachmentUri().mimeType(), new File(param.getAttachmentUri()
                    .getFilePath()));
        }

        if (param.getmBusinessId() != 0) {
            return getMediaService().createBusinessEventAsObservable(typedFile, param.getmBusinessId(), param.getEventName(),
                    param.getEventDescription(), param.getEventStartTime(), param.getEventEndTime(), param.getLocation());
        } else {
            return getMediaService().createGroupEventAsObservable(typedFile, param.getGroupId(), param.getEventName(),
                    param.getEventDescription(), param.getEventStartTime(), param.getEventEndTime(), param.getLocation());
        }
    }

    public Observable<EventResponse> updateEvent(EventParam param) {
        TypedUri attachmentUri = param.getAttachmentUri();
        if (attachmentUri != null) {
            TypedFile typedFile = null;
            if (attachmentUri.mimeType() != null && attachmentUri.getFilePath() != null) {
                typedFile = new TypedFile(attachmentUri.mimeType(), new File(attachmentUri.getFilePath()));
            }
            return getMediaService().updateGroupEventWithPhoto(param.getEventId(), typedFile, param.getGroupId(),
                    param.getEventName(), param.getEventDescription(), param.getEventStartTime(), param.getEventEndTime(),
                    param.getLocation());
        } else {
            return getService().updateEventWithoutPhoto(param.getEventId(), param.getEventName(),
                    param.getEventDescription(), param.getEventStartTime(), param.getEventEndTime(), param.getLocation());
        }
    }

    public Result<RegisteredDeviceResponse> sendDeviceToken(String token) {
        try {
            String versionName = "";
            try {
                versionName = Conx2ShareApplication.getInstance().getPackageManager().getPackageInfo(Conx2ShareApplication.getInstance().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            RegisteredDeviceResponse response = getService().registerDevice(token, "android", versionName);
            return new Result<>(response);
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Result<Response> unregisterDevice(int deviceId) {
        try {
            return new Result<>(getService().unregisterDevice(deviceId));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    public Observable<UpdateResponse> getUpdateInfo() {
        return getService().checkShouldUpdate(BuildConfig.VERSION_NAME);
    }

    public Observable<BadgeCount> clearAllNotification() {
        return getService().clearAllNotification();
    }

    public Observable<LiveEventWrapper> getLiveStreamList(String query) {
        // now remove ios events from list and sort
        return getService().getLiveStreamList(query)
                .map(liveEventWrapper -> {
                    for (int i = liveEventWrapper.getLiveEvents().size() - 1; i >= 0; i--) {
                        liveEventWrapper.getLiveEvents().get(i).calculatePriority();
                        if (liveEventWrapper.getLiveEvents().get(i).getDeviceOs().equals("ios")) {
                            liveEventWrapper.getLiveEvents().remove(i);
                        }
                    }
                    Collections.sort(liveEventWrapper.getLiveEvents(), new LiveEvent.LiveEventComparator());
                    return liveEventWrapper;
                });
    }

    public Observable<List<Message>> getChatHistory(int chatId, Integer page) {
        return getService().getChatHistory(chatId, page)
                .map(messagesResponse -> {
                    if (messagesResponse != null && messagesResponse.getMessages() != null) {
                        return messagesResponse.getMessages();
                    } else {
                        return new ArrayList<Message>();
                    }
                });
    }

    private class NetworkRequestInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Accept", "application/json");
            request.addHeader("Platform", ANDROID_DEVICE);
            request.addHeader("App-Version", BuildConfig.VERSION_NAME);
            if (preferencesUtil.getAuthToken() != null) {
                request.addQueryParam("auth_token", preferencesUtil.getAuthToken());
            } else {
                Log.d(TAG, "auth_token is null");
            }
        }
    }
}
