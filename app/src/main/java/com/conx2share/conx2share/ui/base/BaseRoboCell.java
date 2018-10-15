package com.conx2share.conx2share.ui.base;

import android.view.View;

import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

import io.techery.celladapter.Cell;
import roboguice.RoboGuice;
import roboguice.util.RoboContext;

public abstract class BaseRoboCell<T, R extends Cell.Listener<T>> extends Cell<T, R> implements RoboContext {
    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<>();

    public BaseRoboCell(View view) {
        super(view);
        RoboGuice.getInjector(view.getContext())
                .injectMembersWithoutViews(this);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                RoboGuice.destroyInjector(v.getContext());
                view.removeOnAttachStateChangeListener(this);
            }
        });
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}