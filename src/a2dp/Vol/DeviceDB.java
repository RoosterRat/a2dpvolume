/* use this link for help
 * http://www.screaming-penguin.com/node/7742
 */

package a2dp.Vol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DeviceDB {

   private static final String DATABASE_NAME = "btdevices.db";
   private static final int DATABASE_VERSION = 4;
   private static final String TABLE_NAME = "devices";
   private Context context;
   private SQLiteDatabase db;
   private SQLiteStatement insertStmt;
   private static final String INSERT = "insert into "
      + TABLE_NAME + "(desc1, desc2, mac, maxv, setv, getl) values (?, ?, ?, ?, ?, ?)";
  
   public DeviceDB(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT);
   }
   public void update(btDevice bt){
	   ContentValues vals = new ContentValues();
	   vals.put("desc2", bt.getDesc2());
	   vals.put("maxv", (long)bt.getDefVol());
	   vals.put("setv", bt.islSetV());
	   vals.put("getl", (long)bt.islGetLoc());
	   this.db.update(TABLE_NAME, vals, "mac='" + bt.mac + "'", null);
   }
   
   public void delete(btDevice bt){
	   this.db.delete(TABLE_NAME, "mac='" + bt.mac + "'", null);
   }
   
   public long insert(btDevice btd) {
	   this.insertStmt.bindString(1, btd.desc1);
      this.insertStmt.bindString(2, btd.desc2);
      this.insertStmt.bindString(3, btd.mac);
      this.insertStmt.bindLong(4, (long)btd.getDefVol());
      this.insertStmt.bindLong(5, (long)btd.islSetV());
      this.insertStmt.bindLong(6, (long)btd.islGetLoc());
      return this.insertStmt.executeInsert();
   }
   
   public btDevice getBTD(String imac){
	   btDevice bt = new btDevice();
	   Cursor cs = this.db.query(TABLE_NAME, null, "mac = ?", new String[] { imac}, null, null, null, null);
	   if(cs.moveToFirst()){
		   bt.setDesc1(cs.getString(0));
		   bt.setDesc2(cs.getString(1));
		   bt.setMac(cs.getString(2));
		   bt.setDefVol((int)cs.getInt(3));
		   bt.setSetV(cs.getInt(4));
		   bt.setGetLoc(cs.getInt(5));
	   }
	   return bt;
   }
   
   public void deleteAll() {
      this.db.delete(TABLE_NAME, null, null);
   }
   
   public SQLiteDatabase getDb() {
	      return this.db;
	   }

   public int getLength(){
	   return selectAll().size();
   }
   
   public List<String> selectAll() {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "desc1", "desc2" },
        null, null, null, null, "desc1");
      if (cursor.moveToFirst()) {
         do {
        	 String t = cursor.getString(1);
        	 if(t.length() < 2)
        		 list.add(cursor.getString(0));
        	 else
        		 list.add(t);
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }

   public Vector<btDevice> selectAlldb() {
	      Vector<btDevice> list = new Vector<btDevice>();
	      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "desc1", "desc2", "mac", "maxv", "setv", "getl"},
	        null, null, null, null, "desc1");
	      if (cursor.moveToFirst()) {
	         do {
	        	 btDevice bt = new btDevice();
	        	 bt.setDesc1(cursor.getString(0));
	        	 bt.setDesc2(cursor.getString(1));
	        	 bt.setMac(cursor.getString(2));
	        	 bt.setSetV(cursor.getInt(4));
	        	 bt.setDefVol(cursor.getInt(3));
	        	 bt.setGetLoc(cursor.getInt(5));
	        	 list.add(bt);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
   
   private static class OpenHelper extends SQLiteOpenHelper {
      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }
      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + TABLE_NAME + 
        		 "(desc1 TEXT, desc2 TEXT, mac TEXT PRIMARY KEY, maxv INTEGER, setv INTEGER, getl INTEGER)");
      }
      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         if(newVersion < 4){
    	  Log.w("Example", "Upgrading database, this will drop tables and recreate.");
         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         onCreate(db);
         }
         else
         {
       	  Log.w("Update","Update table and default the new column");
          
        	 try {
				db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN getl INTEGER DEFAULT 1");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
      }
   }
   
  

}
