package com.github.homeant.data.shield.mybatis.domain;

import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;

public abstract class DefaultCursor<E> implements Cursor<E> {

    private final Cursor<E> cursor;

    protected DefaultCursor(Cursor<E> cursor) {
        this.cursor = cursor;
    }

    @Override
    public boolean isOpen() {
        return cursor.isOpen();
    }

    @Override
    public boolean isConsumed() {
        return cursor.isConsumed();
    }

    @Override
    public int getCurrentIndex() {
        return cursor.getCurrentIndex();
    }

    @Override
    public void close() throws IOException {
        cursor.close();
    }
}
