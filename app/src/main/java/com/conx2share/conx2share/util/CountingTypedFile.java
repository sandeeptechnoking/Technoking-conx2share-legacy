package com.conx2share.conx2share.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedFile;

/**
 * Created by heathersnepenger on 4/17/17.
 */

public class CountingTypedFile extends TypedFile {

    private static final int BUFFER_SIZE = 4096;

    private final ProgressListener listener;
    private long totalSize = 0;

    public CountingTypedFile(String mimeType, File file, ProgressListener listener) {
        super(mimeType, file);
        totalSize = file.length();
        Log.d("TAG", "total number: " + totalSize);
        this.listener = listener;
    }

    @Override public void writeTo(OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream in = new FileInputStream(super.file());
        long total = 0;
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                out.write(buffer, 0, read);
                this.listener.transferred((int) ((total / (float) totalSize) * 100));
            }
        } finally {
            in.close();
        }
    }

    public interface ProgressListener {
        void transferred(long num);
    }
}


