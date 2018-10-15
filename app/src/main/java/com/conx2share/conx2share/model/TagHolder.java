package com.conx2share.conx2share.model;

import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.network.models.response.HashTag;
import com.conx2share.conx2share.text.HashTagLinkSpan;
import com.conx2share.conx2share.text.UserTagLinkSpan;
import com.linkedin.android.spyglass.mentions.MentionSpan;
import com.linkedin.android.spyglass.mentions.MentionsEditable;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TagHolder {

    protected String body;

    protected ArrayList<UserTag> userTags;

    protected ArrayList<HashTag> hashtags;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ArrayList<UserTag> getUserTags() {
        return userTags;
    }

    public void setUserTags(ArrayList<UserTag> userTags) {
        this.userTags = userTags;
    }

    public ArrayList<HashTag> getHashtags() {
        return hashtags;
    }

    public String hashTags() {
        String tags = "";
        Iterator<HashTag> iterator = hashtags.iterator();
        while (iterator.hasNext()) {
            tags = tags + " #" + iterator.next().getTitle();
        }
        return tags;
    }

    public void setHashtags(ArrayList<HashTag> hashtags) {
        this.hashtags = hashtags;
    }

    /**
     * For use with RichEditorView.  Returns an array of UserTags for autocompleted @mentioned users in the text.  If the user is not selected from the autocomplete list,
     * that user will not be returned by this method.
     * @param spans         List of MentionSpans from the RichEditorView
     * @param spannedText   Editable text of the RichEditorView
     * @param taggerId      user id of the person doing the tagging, i.e. - current user
     * @return              List of UserTag objects to send to the server
     */
    public static ArrayList<UserTag> getUserTagsFromText(List<MentionSpan> spans, SpannableStringBuilder spannedText, int taggerId) {
        ArrayList<UserTag> userTagsAttributes = new ArrayList<>();
        for (MentionSpan span : spans) {
            int taggedId = span.getMention().getId();

            int spanStart = spannedText.getSpanStart(span);
            int spanEnd = spannedText.getSpanEnd(span);
            String tagText = spannedText.toString().substring(spanStart, spanEnd);

            UserTag tag = new UserTag();
            tag.setUserId(taggedId);
            tag.setTaggerId(taggerId);
            tag.setTag(tagText);
            userTagsAttributes.add(tag);
        }
        return userTagsAttributes;
    }

    /**
     * Should usertag spans be included in the body?
     * @return  true if spans should be included
     */
    public boolean spanUserTags() {
        return getUserTags() != null && getUserTags().size() > 0;
    }

    /**
     * Should hastag spans be included in the body?
     * @return  true if spans should be included
     */
    public boolean spanHashTags() {
        return getHashtags() != null && getHashtags().size() > 0;
    }

    /**
     * Does the body text require any spans to be added?
     * @return  true if any spans need to be added
     */
    public boolean needsSpanning() {
        return spanUserTags() || spanHashTags();
    }

    /**
     * Obtain body text with usertag and hashtag spans included for display.  This method should not be used for editing the body text.
     * @param context   Context to be passed to Span constructors
     * @return  spanned body text if spans are required or plain body text if no spans are required.
     */
    public CharSequence getBodyTextWithSpans(Context context) {
        CharSequence result;
        if (needsSpanning()) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(getBody());

            if (spanUserTags()) {
                for (UserTag tag : getUserTags()) {
                    UserTagLinkSpan span = new UserTagLinkSpan(context, tag);
                    int pos = getBody().indexOf(tag.getTag());
                    if (pos >= 0) {
                        ssb.setSpan(span, pos, pos + tag.getTag().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            if (spanHashTags()) {
                for (HashTag tag : getHashtags()) {
                    HashTagLinkSpan span = new HashTagLinkSpan(context, tag);
                    int pos = getBody().indexOf("#" + tag.getTitle());
                    if (pos >= 0) {
                        ssb.setSpan(span, pos, pos + tag.getTitle().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            result = ssb;
        } else {
            result = getBody();
        }

        return result;
    }

    /**
     * Obtain body text with MentionSpans in place for editing.  This method should not be used for displaying text with user mentions.
     * @param context   Context to be passed to MentionSpan constructor
     * @return  spanned body text if spans are required or plain body text if no spans are required.
     */
    public CharSequence getBodyTextForEditing(Context context) {
        if (spanUserTags()) {
            MentionsEditable editable = new MentionsEditable(getBody());
            int pos;
            for (UserTag userTag : getUserTags()) {
                User user = new User();
                user.setId(userTag.getUserId());
                user.setUsername(userTag.getTag().substring(1));
                MentionSpan span = new MentionSpan(context, user);
                pos = getBody().indexOf(userTag.getTag());
                editable.setSpan(span, pos, pos + userTag.getTag().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return editable;
        } else {
            return getBody();
        }
    }

}
