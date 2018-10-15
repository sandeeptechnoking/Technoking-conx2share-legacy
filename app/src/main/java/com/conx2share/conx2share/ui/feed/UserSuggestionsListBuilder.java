package com.conx2share.conx2share.ui.feed;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsListBuilder;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class UserSuggestionsListBuilder implements SuggestionsListBuilder {

    public static final String TAG = UserSuggestionsListBuilder.class.getSimpleName();

    public static final String USER_BUCKET = "user_bucket";

    @NonNull
    @Override
    public List<Suggestible> buildSuggestions(@NonNull Map<String, SuggestionsResult> latestResults, @NonNull String currentTokenString) {
        SuggestionsResult suggestionsResult = latestResults.get(USER_BUCKET);
        // noinspection unchecked
        return (List<Suggestible>) suggestionsResult.getSuggestions();
    }

    @NonNull
    @Override
    public View getView(@NonNull Suggestible suggestible, @Nullable View view, ViewGroup viewGroup, @NonNull Context context, @NonNull LayoutInflater layoutInflater, @NonNull Resources resources) {

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_user_autocomplete, viewGroup, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView handle = (TextView) view.findViewById(R.id.handle);
        AvatarImageView avatar = (AvatarImageView) view.findViewById(R.id.avatar);

        User user = (User) suggestible;
        avatar.initView(user);
        name.setText(user.getDisplayName());
        handle.setText(user.getHandleText());

        view.setTag(user);

        return view;
    }
}
