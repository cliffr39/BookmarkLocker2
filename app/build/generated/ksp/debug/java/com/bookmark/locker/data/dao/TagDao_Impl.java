package com.bookmark.locker.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bookmark.locker.data.model.Bookmark;
import com.bookmark.locker.data.model.BookmarkTagCrossRef;
import com.bookmark.locker.data.model.BookmarkWithTags;
import com.bookmark.locker.data.model.Tag;
import com.bookmark.locker.data.model.TagWithBookmarks;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class TagDao_Impl implements TagDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Tag> __insertionAdapterOfTag;

  private final EntityInsertionAdapter<BookmarkTagCrossRef> __insertionAdapterOfBookmarkTagCrossRef;

  private final EntityDeletionOrUpdateAdapter<Tag> __deletionAdapterOfTag;

  private final EntityDeletionOrUpdateAdapter<BookmarkTagCrossRef> __deletionAdapterOfBookmarkTagCrossRef;

  private final EntityDeletionOrUpdateAdapter<Tag> __updateAdapterOfTag;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTagById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTagsForBookmark;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllBookmarksForTag;

  private final SharedSQLiteStatement __preparedStmtOfRemoveTagFromBookmark;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAllTagUsageCounts;

  public TagDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTag = new EntityInsertionAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `tags` (`id`,`name`,`color`,`dateCreated`,`usageCount`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getColor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getColor());
        }
        statement.bindLong(4, entity.getDateCreated());
        statement.bindLong(5, entity.getUsageCount());
      }
    };
    this.__insertionAdapterOfBookmarkTagCrossRef = new EntityInsertionAdapter<BookmarkTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `bookmark_tag_cross_ref` (`bookmarkId`,`tagId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkTagCrossRef entity) {
        statement.bindLong(1, entity.getBookmarkId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__deletionAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tags` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfBookmarkTagCrossRef = new EntityDeletionOrUpdateAdapter<BookmarkTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bookmark_tag_cross_ref` WHERE `bookmarkId` = ? AND `tagId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkTagCrossRef entity) {
        statement.bindLong(1, entity.getBookmarkId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__updateAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tags` SET `id` = ?,`name` = ?,`color` = ?,`dateCreated` = ?,`usageCount` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getColor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getColor());
        }
        statement.bindLong(4, entity.getDateCreated());
        statement.bindLong(5, entity.getUsageCount());
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteTagById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tags WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllTagsForBookmark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark_tag_cross_ref WHERE bookmarkId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllBookmarksForTag = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark_tag_cross_ref WHERE tagId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveTagFromBookmark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark_tag_cross_ref WHERE bookmarkId = ? AND tagId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAllTagUsageCounts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE tags SET usageCount = (\n"
                + "            SELECT COUNT(*) FROM bookmark_tag_cross_ref \n"
                + "            WHERE tagId = tags.id\n"
                + "        )\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insertTag(final Tag tag, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTag.insertAndReturnId(tag);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTags(final List<Tag> tags,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfTag.insertAndReturnIdsList(tags);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBookmarkTagCrossRef(final BookmarkTagCrossRef crossRef,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookmarkTagCrossRef.insert(crossRef);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBookmarkTagCrossRefs(final List<BookmarkTagCrossRef> crossRefs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookmarkTagCrossRef.insert(crossRefs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTag(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTag.handle(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmarkTagCrossRef(final BookmarkTagCrossRef crossRef,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBookmarkTagCrossRef.handle(crossRef);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTag(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTag.handle(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTagById(final long tagId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTagById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, tagId);
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
          __preparedStmtOfDeleteTagById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTagsForBookmark(final long bookmarkId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTagsForBookmark.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, bookmarkId);
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
          __preparedStmtOfDeleteAllTagsForBookmark.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllBookmarksForTag(final long tagId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllBookmarksForTag.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, tagId);
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
          __preparedStmtOfDeleteAllBookmarksForTag.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object removeTagFromBookmark(final long bookmarkId, final long tagId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveTagFromBookmark.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, bookmarkId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, tagId);
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
          __preparedStmtOfRemoveTagFromBookmark.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAllTagUsageCounts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAllTagUsageCounts.acquire();
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
          __preparedStmtOfUpdateAllTagUsageCounts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Tag>> getAllTags() {
    final String _sql = "SELECT * FROM tags ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tags"}, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final long _tmpDateCreated;
            _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            _item = new Tag(_tmpId,_tmpName,_tmpColor,_tmpDateCreated,_tmpUsageCount);
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
  public Object getTagById(final long tagId, final Continuation<? super Tag> $completion) {
    final String _sql = "SELECT * FROM tags WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tagId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Tag>() {
      @Override
      @Nullable
      public Tag call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final Tag _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final long _tmpDateCreated;
            _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            _result = new Tag(_tmpId,_tmpName,_tmpColor,_tmpDateCreated,_tmpUsageCount);
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
  public Object getTagByName(final String name, final Continuation<? super Tag> $completion) {
    final String _sql = "SELECT * FROM tags WHERE name = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Tag>() {
      @Override
      @Nullable
      public Tag call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final Tag _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final long _tmpDateCreated;
            _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            _result = new Tag(_tmpId,_tmpName,_tmpColor,_tmpDateCreated,_tmpUsageCount);
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
  public Object getBookmarkWithTags(final long bookmarkId,
      final Continuation<? super BookmarkWithTags> $completion) {
    final String _sql = "SELECT * FROM bookmarks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookmarkId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<BookmarkWithTags>() {
      @Override
      @Nullable
      public BookmarkWithTags call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
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
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<Tag>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomBookmarkLockerDataModelTag(_collectionTags);
            final BookmarkWithTags _result;
            if (_cursor.moveToFirst()) {
              final Bookmark _tmpBookmark;
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
              _tmpBookmark = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
              final ArrayList<Tag> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _result = new BookmarkWithTags(_tmpBookmark,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BookmarkWithTags>> getAllBookmarksWithTags() {
    final String _sql = "SELECT * FROM bookmarks WHERE isDeleted = 0 ORDER BY dateModified DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"bookmark_tag_cross_ref", "tags",
        "bookmarks"}, new Callable<List<BookmarkWithTags>>() {
      @Override
      @NonNull
      public List<BookmarkWithTags> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
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
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<Tag>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomBookmarkLockerDataModelTag(_collectionTags);
            final List<BookmarkWithTags> _result = new ArrayList<BookmarkWithTags>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final BookmarkWithTags _item;
              final Bookmark _tmpBookmark;
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
              _tmpBookmark = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
              final ArrayList<Tag> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _item = new BookmarkWithTags(_tmpBookmark,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTagWithBookmarks(final long tagId,
      final Continuation<? super TagWithBookmarks> $completion) {
    final String _sql = "SELECT * FROM tags WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tagId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<TagWithBookmarks>() {
      @Override
      @Nullable
      public TagWithBookmarks call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
            final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
            final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
            final LongSparseArray<ArrayList<Bookmark>> _collectionBookmarks = new LongSparseArray<ArrayList<Bookmark>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionBookmarks.containsKey(_tmpKey)) {
                _collectionBookmarks.put(_tmpKey, new ArrayList<Bookmark>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipbookmarksAscomBookmarkLockerDataModelBookmark(_collectionBookmarks);
            final TagWithBookmarks _result;
            if (_cursor.moveToFirst()) {
              final Tag _tmpTag;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpColor;
              if (_cursor.isNull(_cursorIndexOfColor)) {
                _tmpColor = null;
              } else {
                _tmpColor = _cursor.getString(_cursorIndexOfColor);
              }
              final long _tmpDateCreated;
              _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
              final int _tmpUsageCount;
              _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
              _tmpTag = new Tag(_tmpId,_tmpName,_tmpColor,_tmpDateCreated,_tmpUsageCount);
              final ArrayList<Bookmark> _tmpBookmarksCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpBookmarksCollection = _collectionBookmarks.get(_tmpKey_1);
              _result = new TagWithBookmarks(_tmpTag,_tmpBookmarksCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TagWithBookmarks>> getAllTagsWithBookmarks() {
    final String _sql = "SELECT * FROM tags ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"bookmark_tag_cross_ref",
        "bookmarks", "tags"}, new Callable<List<TagWithBookmarks>>() {
      @Override
      @NonNull
      public List<TagWithBookmarks> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
            final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
            final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
            final LongSparseArray<ArrayList<Bookmark>> _collectionBookmarks = new LongSparseArray<ArrayList<Bookmark>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionBookmarks.containsKey(_tmpKey)) {
                _collectionBookmarks.put(_tmpKey, new ArrayList<Bookmark>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipbookmarksAscomBookmarkLockerDataModelBookmark(_collectionBookmarks);
            final List<TagWithBookmarks> _result = new ArrayList<TagWithBookmarks>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final TagWithBookmarks _item;
              final Tag _tmpTag;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpColor;
              if (_cursor.isNull(_cursorIndexOfColor)) {
                _tmpColor = null;
              } else {
                _tmpColor = _cursor.getString(_cursorIndexOfColor);
              }
              final long _tmpDateCreated;
              _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
              final int _tmpUsageCount;
              _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
              _tmpTag = new Tag(_tmpId,_tmpName,_tmpColor,_tmpDateCreated,_tmpUsageCount);
              final ArrayList<Bookmark> _tmpBookmarksCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpBookmarksCollection = _collectionBookmarks.get(_tmpKey_1);
              _item = new TagWithBookmarks(_tmpTag,_tmpBookmarksCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<BookmarkWithTags>> getBookmarksWithTags(final List<Long> tagIds) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT DISTINCT b.* FROM bookmarks b ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN bookmark_tag_cross_ref btcr ON b.id = btcr.bookmarkId ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN tags t ON btcr.tagId = t.id ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE t.id IN (");
    final int _inputSize = tagIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") AND b.isDeleted = 0");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY b.dateModified DESC");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (long _item : tagIds) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    return CoroutinesRoom.createFlow(__db, true, new String[] {"bookmark_tag_cross_ref", "tags",
        "bookmarks"}, new Callable<List<BookmarkWithTags>>() {
      @Override
      @NonNull
      public List<BookmarkWithTags> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
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
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<Tag>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomBookmarkLockerDataModelTag(_collectionTags);
            final List<BookmarkWithTags> _result = new ArrayList<BookmarkWithTags>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final BookmarkWithTags _item_1;
              final Bookmark _tmpBookmark;
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
              _tmpBookmark = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
              final ArrayList<Tag> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _item_1 = new BookmarkWithTags(_tmpBookmark,_tmpTagsCollection);
              _result.add(_item_1);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTagUsageCount(final long tagId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM bookmark_tag_cross_ref \n"
            + "        WHERE tagId = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tagId);
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
  public Flow<List<BookmarkWithTags>> searchBookmarksWithTags(final String query) {
    final String _sql = "\n"
            + "        SELECT DISTINCT b.* FROM bookmarks b \n"
            + "        LEFT JOIN bookmark_tag_cross_ref btcr ON b.id = btcr.bookmarkId \n"
            + "        LEFT JOIN tags t ON btcr.tagId = t.id \n"
            + "        WHERE (b.title LIKE '%' || ? || '%' \n"
            + "        OR b.url LIKE '%' || ? || '%' \n"
            + "        OR b.description LIKE '%' || ? || '%'\n"
            + "        OR t.name LIKE '%' || ? || '%') \n"
            + "        AND b.isDeleted = 0\n"
            + "        ORDER BY b.dateModified DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"bookmark_tag_cross_ref", "tags",
        "bookmarks"}, new Callable<List<BookmarkWithTags>>() {
      @Override
      @NonNull
      public List<BookmarkWithTags> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
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
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<Tag>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomBookmarkLockerDataModelTag(_collectionTags);
            final List<BookmarkWithTags> _result = new ArrayList<BookmarkWithTags>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final BookmarkWithTags _item;
              final Bookmark _tmpBookmark;
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
              _tmpBookmark = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
              final ArrayList<Tag> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _item = new BookmarkWithTags(_tmpBookmark,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshiptagsAscomBookmarkLockerDataModelTag(
      @NonNull final LongSparseArray<ArrayList<Tag>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshiptagsAscomBookmarkLockerDataModelTag(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `tags`.`id` AS `id`,`tags`.`name` AS `name`,`tags`.`color` AS `color`,`tags`.`dateCreated` AS `dateCreated`,`tags`.`usageCount` AS `usageCount`,_junction.`bookmarkId` FROM `bookmark_tag_cross_ref` AS _junction INNER JOIN `tags` ON (_junction.`tagId` = `tags`.`id`) WHERE _junction.`bookmarkId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      // _junction.bookmarkId;
      final int _itemKeyIndex = 5;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      final int _cursorIndexOfColor = 2;
      final int _cursorIndexOfDateCreated = 3;
      final int _cursorIndexOfUsageCount = 4;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Tag> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Tag _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final String _tmpColor;
          if (_cursor.isNull(_cursorIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
          }
          final long _tmpDateCreated;
          _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
          final int _tmpUsageCount;
          _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
          _item_1 = new Tag(_tmpId,_tmpName,_tmpColor,_tmpDateCreated,_tmpUsageCount);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshipbookmarksAscomBookmarkLockerDataModelBookmark(
      @NonNull final LongSparseArray<ArrayList<Bookmark>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipbookmarksAscomBookmarkLockerDataModelBookmark(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `bookmarks`.`id` AS `id`,`bookmarks`.`title` AS `title`,`bookmarks`.`url` AS `url`,`bookmarks`.`faviconUrl` AS `faviconUrl`,`bookmarks`.`folderId` AS `folderId`,`bookmarks`.`isFavorite` AS `isFavorite`,`bookmarks`.`isInReadingList` AS `isInReadingList`,`bookmarks`.`dateAdded` AS `dateAdded`,`bookmarks`.`dateModified` AS `dateModified`,`bookmarks`.`visitCount` AS `visitCount`,`bookmarks`.`description` AS `description`,`bookmarks`.`tags` AS `tags`,`bookmarks`.`reminderTime` AS `reminderTime`,`bookmarks`.`isReminderActive` AS `isReminderActive`,`bookmarks`.`lastReminderTime` AS `lastReminderTime`,`bookmarks`.`isDeleted` AS `isDeleted`,`bookmarks`.`deletedAt` AS `deletedAt`,_junction.`tagId` FROM `bookmark_tag_cross_ref` AS _junction INNER JOIN `bookmarks` ON (_junction.`bookmarkId` = `bookmarks`.`id`) WHERE _junction.`tagId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      // _junction.tagId;
      final int _itemKeyIndex = 17;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfTitle = 1;
      final int _cursorIndexOfUrl = 2;
      final int _cursorIndexOfFaviconUrl = 3;
      final int _cursorIndexOfFolderId = 4;
      final int _cursorIndexOfIsFavorite = 5;
      final int _cursorIndexOfIsInReadingList = 6;
      final int _cursorIndexOfDateAdded = 7;
      final int _cursorIndexOfDateModified = 8;
      final int _cursorIndexOfVisitCount = 9;
      final int _cursorIndexOfDescription = 10;
      final int _cursorIndexOfTags = 11;
      final int _cursorIndexOfReminderTime = 12;
      final int _cursorIndexOfIsReminderActive = 13;
      final int _cursorIndexOfLastReminderTime = 14;
      final int _cursorIndexOfIsDeleted = 15;
      final int _cursorIndexOfDeletedAt = 16;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Bookmark> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Bookmark _item_1;
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
          _item_1 = new Bookmark(_tmpId,_tmpTitle,_tmpUrl,_tmpFaviconUrl,_tmpFolderId,_tmpIsFavorite,_tmpIsInReadingList,_tmpDateAdded,_tmpDateModified,_tmpVisitCount,_tmpDescription,_tmpTags,_tmpReminderTime,_tmpIsReminderActive,_tmpLastReminderTime,_tmpIsDeleted,_tmpDeletedAt);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
