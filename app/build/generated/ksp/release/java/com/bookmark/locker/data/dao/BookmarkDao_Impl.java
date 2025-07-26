package com.bookmark.locker.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bookmark.locker.data.model.Bookmark;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BookmarkDao_Impl implements BookmarkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Bookmark> __insertionAdapterOfBookmark;

  private final EntityDeletionOrUpdateAdapter<Bookmark> __deletionAdapterOfBookmark;

  private final EntityDeletionOrUpdateAdapter<Bookmark> __updateAdapterOfBookmark;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookmarkById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookmarksByFolder;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReadingListStatus;

  private final SharedSQLiteStatement __preparedStmtOfIncrementVisitCount;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReminderStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastReminderTime;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReminderTime;

  private final SharedSQLiteStatement __preparedStmtOfSoftDeleteBookmark;

  private final SharedSQLiteStatement __preparedStmtOfRestoreBookmark;

  private final SharedSQLiteStatement __preparedStmtOfPermanentlyDeleteOldBookmarks;

  private final SharedSQLiteStatement __preparedStmtOfEmptyTrash;

  public BookmarkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookmark = new EntityInsertionAdapter<Bookmark>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bookmarks` (`id`,`title`,`url`,`faviconUrl`,`folderId`,`isFavorite`,`isInReadingList`,`dateAdded`,`dateModified`,`visitCount`,`description`,`tags`,`reminderTime`,`isReminderActive`,`lastReminderTime`,`isDeleted`,`deletedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bookmark entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getUrl());
        if (entity.getFaviconUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFaviconUrl());
        }
        if (entity.getFolderId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFolderId());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isInReadingList() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getDateAdded());
        statement.bindLong(9, entity.getDateModified());
        statement.bindLong(10, entity.getVisitCount());
        if (entity.getDescription() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getDescription());
        }
        if (entity.getTags() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getTags());
        }
        if (entity.getReminderTime() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getReminderTime());
        }
        final int _tmp_2 = entity.isReminderActive() ? 1 : 0;
        statement.bindLong(14, _tmp_2);
        if (entity.getLastReminderTime() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLastReminderTime());
        }
        final int _tmp_3 = entity.isDeleted() ? 1 : 0;
        statement.bindLong(16, _tmp_3);
        if (entity.getDeletedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getDeletedAt());
        }
      }
    };
    this.__deletionAdapterOfBookmark = new EntityDeletionOrUpdateAdapter<Bookmark>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bookmarks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bookmark entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBookmark = new EntityDeletionOrUpdateAdapter<Bookmark>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bookmarks` SET `id` = ?,`title` = ?,`url` = ?,`faviconUrl` = ?,`folderId` = ?,`isFavorite` = ?,`isInReadingList` = ?,`dateAdded` = ?,`dateModified` = ?,`visitCount` = ?,`description` = ?,`tags` = ?,`reminderTime` = ?,`isReminderActive` = ?,`lastReminderTime` = ?,`isDeleted` = ?,`deletedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bookmark entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getUrl());
        if (entity.getFaviconUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFaviconUrl());
        }
        if (entity.getFolderId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFolderId());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isInReadingList() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getDateAdded());
        statement.bindLong(9, entity.getDateModified());
        statement.bindLong(10, entity.getVisitCount());
        if (entity.getDescription() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getDescription());
        }
        if (entity.getTags() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getTags());
        }
        if (entity.getReminderTime() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getReminderTime());
        }
        final int _tmp_2 = entity.isReminderActive() ? 1 : 0;
        statement.bindLong(14, _tmp_2);
        if (entity.getLastReminderTime() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLastReminderTime());
        }
        final int _tmp_3 = entity.isDeleted() ? 1 : 0;
        statement.bindLong(16, _tmp_3);
        if (entity.getDeletedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getDeletedAt());
        }
        statement.bindLong(18, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBookmarkById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteBookmarksByFolder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE folderId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReadingListStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET isInReadingList = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementVisitCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET visitCount = visitCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReminderStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET isReminderActive = ?, reminderTime = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastReminderTime = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET lastReminderTime = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReminderTime = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET reminderTime = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDeleteBookmark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET isDeleted = 1, deletedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestoreBookmark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bookmarks SET isDeleted = 0, deletedAt = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPermanentlyDeleteOldBookmarks = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE isDeleted = 1 AND deletedAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfEmptyTrash = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE isDeleted = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insertBookmark(final Bookmark bookmark,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBookmark.insertAndReturnId(bookmark);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBookmarks(final List<Bookmark> bookmarks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookmark.insert(bookmarks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmark(final Bookmark bookmark,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBookmark.handle(bookmark);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBookmark(final Bookmark bookmark,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBookmark.handle(bookmark);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmarkById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookmarkById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBookmarkById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmarksByFolder(final long folderId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookmarksByFolder.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, folderId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBookmarksByFolder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFavoriteStatus(final long id, final boolean isFavorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoriteStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateFavoriteStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReadingListStatus(final long id, final boolean isInReadingList,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReadingListStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isInReadingList ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateReadingListStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementVisitCount(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementVisitCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementVisitCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReminderStatus(final long id, final boolean isActive, final Long reminderTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReminderStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isActive ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (reminderTime == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, reminderTime);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateReminderStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastReminderTime(final long id, final Long lastReminderTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastReminderTime.acquire();
        int _argIndex = 1;
        if (lastReminderTime == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, lastReminderTime);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateLastReminderTime.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReminderTime(final long id, final Long reminderTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReminderTime.acquire();
        int _argIndex = 1;
        if (reminderTime == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, reminderTime);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateReminderTime.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object softDeleteBookmark(final long id, final long deletedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDeleteBookmark.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSoftDeleteBookmark.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreBookmark(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestoreBookmark.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRestoreBookmark.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object permanentlyDeleteOldBookmarks(final long cutoffTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPermanentlyDeleteOldBookmarks.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfPermanentlyDeleteOldBookmarks.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object emptyTrash(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEmptyTrash.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfEmptyTrash.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Bookmark>> getAllBookmarks() {
    final String _sql = "SELECT * FROM bookmarks WHERE isDeleted = 0 ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Bookmark>> getFavoriteBookmarks() {
    final String _sql = "SELECT * FROM bookmarks WHERE isFavorite = 1 AND isDeleted = 0 ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Bookmark>> getReadingListBookmarks() {
    final String _sql = "SELECT * FROM bookmarks WHERE isInReadingList = 1 AND isDeleted = 0 ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Bookmark>> getBookmarksByFolder(final long folderId) {
    final String _sql = "SELECT * FROM bookmarks WHERE folderId = ? AND isDeleted = 0 ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, folderId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Bookmark>> getBookmarksWithoutFolder() {
    final String _sql = "SELECT * FROM bookmarks WHERE folderId IS NULL AND isDeleted = 0 ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Bookmark>> searchBookmarks(final String query) {
    final String _sql = "SELECT * FROM bookmarks WHERE (title LIKE '%' || ? || '%' OR url LIKE '%' || ? || '%') AND isDeleted = 0 ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getBookmarkById(final long id, final Continuation<? super Bookmark> $completion) {
    final String _sql = "SELECT * FROM bookmarks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Bookmark>() {
      @Override
      @Nullable
      public Bookmark call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final Bookmark _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _result = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getBookmarkByUrl(final String url,
      final Continuation<? super Bookmark> $completion) {
    final String _sql = "SELECT * FROM bookmarks WHERE url = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, url);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Bookmark>() {
      @Override
      @Nullable
      public Bookmark call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final Bookmark _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _result = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getBookmarkCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM bookmarks";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getFavoriteCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM bookmarks WHERE isFavorite = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getReadingListCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM bookmarks WHERE isInReadingList = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveReminders(final long currentTime,
      final Continuation<? super List<Bookmark>> $completion) {
    final String _sql = "SELECT * FROM bookmarks WHERE isReminderActive = 1 AND reminderTime <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Bookmark>> getAllActiveReminders() {
    final String _sql = "SELECT * FROM bookmarks WHERE isReminderActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Bookmark>> getDeletedBookmarks() {
    final String _sql = "SELECT * FROM bookmarks WHERE isDeleted = 1 ORDER BY deletedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFaviconUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "faviconUrl");
          final int _cursorIndexOfFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "folderId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsInReadingList = CursorUtil.getColumnIndexOrThrow(_cursor, "isInReadingList");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfDateModified = CursorUtil.getColumnIndexOrThrow(_cursor, "dateModified");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsReminderActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isReminderActive");
          final int _cursorIndexOfLastReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReminderTime");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpFaviconUrl;
            if (_cursor.isNull(_cursorIndexOfFaviconUrl)) {
              _tmpFaviconUrl = null;
            } else {
              _tmpFaviconUrl = _cursor.getString(_cursorIndexOfFaviconUrl);
            }
            final Long _tmpFolderId;
            if (_cursor.isNull(_cursorIndexOfFolderId)) {
              _tmpFolderId = null;
            } else {
              _tmpFolderId = _cursor.getLong(_cursorIndexOfFolderId);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsInReadingList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsInReadingList);
            _tmpIsInReadingList = _tmp_1 != 0;
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final long _tmpDateModified;
            _tmpDateModified = _cursor.getLong(_cursorIndexOfDateModified);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsReminderActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsReminderActive);
            _tmpIsReminderActive = _tmp_2 != 0;
            final Long _tmpLastReminderTime;
            if (_cursor.isNull(_cursorIndexOfLastReminderTime)) {
              _tmpLastReminderTime = null;
            } else {
              _tmpLastReminderTime = _cursor.getLong(_cursorIndexOfLastReminderTime);
            }
            final boolean _tmpIsDeleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_3 != 0;
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            _item = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDeletedBookmarkCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM bookmarks WHERE isDeleted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
