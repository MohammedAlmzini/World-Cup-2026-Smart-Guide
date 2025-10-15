package com.ahmmedalmzini783.wcguide.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.ahmmedalmzini783.wcguide.data.local.converter.Converters;
import com.ahmmedalmzini783.wcguide.data.local.dao.EventDao;
import com.ahmmedalmzini783.wcguide.data.local.dao.FavoriteDao;
import com.ahmmedalmzini783.wcguide.data.local.dao.PlaceDao;
import com.ahmmedalmzini783.wcguide.data.local.dao.ReviewDao;
import com.ahmmedalmzini783.wcguide.data.local.entity.EventEntity;
import com.ahmmedalmzini783.wcguide.data.local.entity.FavoriteEntity;
import com.ahmmedalmzini783.wcguide.data.local.entity.PlaceEntity;
import com.ahmmedalmzini783.wcguide.data.local.entity.ReviewEntity;

@Database(
        entities = {
                EventEntity.class,
                PlaceEntity.class,
                FavoriteEntity.class,
                ReviewEntity.class
        },
        version = 2,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "wcguide_database";
    private static volatile AppDatabase INSTANCE;

    public abstract EventDao eventDao();
    public abstract PlaceDao placeDao();
    public abstract FavoriteDao favoriteDao();
    public abstract ReviewDao reviewDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .enableMultiInstanceInvalidation()
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration() // For development only
                            .allowMainThreadQueries() // For development only
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Future migrations will be added here
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add isFeatured column to events table
            database.execSQL("ALTER TABLE events ADD COLUMN isFeatured INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static void destroyInstance() {
        if (INSTANCE != null) {
            INSTANCE.close();
        }
        INSTANCE = null;
    }

    public static void clearDatabase(Context context) {
        destroyInstance();
        context.deleteDatabase(DATABASE_NAME);
    }
}