package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballWidgetFactory(getApplicationContext(), intent);
    }


    class FootballWidgetFactory implements RemoteViewsFactory {
        Cursor c;
        private int mAppWidgetId;
        private Context mContext;

        public FootballWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        }

        @Override
        public void onCreate() {
            updateDateFromDb();
        }

        @Override
        public void onDataSetChanged() {
            updateDateFromDb();
        }

        private void updateDateFromDb() {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            c = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), null,
                    null, new String[]{mformat.format(date)}, null);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return c.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_score_item);
            Intent fillInIntent = new Intent();
            rv.setOnClickFillInIntent(R.id.widget_item_root, fillInIntent);

            if (c.moveToPosition(i)) {
                String strHomeName = c.getString(ScoresAdapter.COL_HOME);
                String strAwayName = c.getString(ScoresAdapter.COL_AWAY);
                String strScore = Utilies.getScores(c.getInt(ScoresAdapter.COL_HOME_GOALS),
                        c.getInt(ScoresAdapter.COL_AWAY_GOALS));
                String strMatchTime = c.getString(ScoresAdapter.COL_MATCHTIME);

                String textGameName = strHomeName + " " + mContext.getString(R.string.vs) + " " + strAwayName;
                rv.setTextViewText(R.id.tv_widget_game, textGameName);
                rv.setContentDescription(R.id.tv_widget_game, textGameName);

                String textGameTime = mContext.getString(R.string.time_short) + " " + strMatchTime;
                rv.setTextViewText(R.id.tv_widget_time, textGameTime);
                rv.setContentDescription(R.id.tv_widget_time, textGameTime);

                String textGameScore = mContext.getString(R.string.score_short) + " " + strScore;
                rv.setTextViewText(R.id.tv_widget_score, textGameScore);
                rv.setContentDescription(R.id.tv_widget_score, textGameScore);
            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            if (c.moveToPosition(i)) {
                return (long) c.getLong(ScoresAdapter.COL_ID);
            }
            return 0;

        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
