package com.example.scoutingplatform;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class RecyclerAdapterdp extends RecyclerView.Adapter<RecyclerAdapterdp.RecyclerViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private boolean isSelectedAll;
    Integer viewposition;
    public CheckBox mycheckBox;
    DatabaseHelper mDatabaseHelper;
    int id;
    public ArrayList<RecyclerViewHolder> holders = new ArrayList<RecyclerViewHolder>();
    public ArrayList<Integer> holderids = new ArrayList<>();

    interface OnItemCheckListener {
        void onItemCheck(ClipData.Item item);

        void onItemUncheck(ClipData.Item item);
    }

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public RecyclerAdapterdp(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView textDataPoint;
        public TextView txtGender;
        public TextView txtSev;
        public TextView txtQuantity;
        public TextView txtPos;
        public TextView txtTime;
        public CheckBox checkBox;
        public int holderid;


        public RecyclerViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDatabaseHelper = new DatabaseHelper(mContext.getApplicationContext());
            textDataPoint = itemView.findViewById(R.id.textDataPoint);
            txtGender = itemView.findViewById(R.id.txtGender);
            txtSev = itemView.findViewById(R.id.txtSev);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPos = itemView.findViewById(R.id.txtPos);
            txtTime = itemView.findViewById(R.id.txtTime);
            checkBox = itemView.findViewById(R.id.cbInteract);
            mycheckBox = checkBox;
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.mydprecycleritem, parent, false);
        return new RecyclerViewHolder(view);
    }

    boolean ch = false;

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        try {
            if (!mCursor.moveToPosition(position)) {
                return;
            }
            viewposition = position;
            String DataPoint = mCursor.getString(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLDESCRIPTION));
            String Gender = mCursor.getString(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLGENDER));
            String Postition = mCursor.getString(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLPOS));
            String Severity = mCursor.getString(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLSEV));
            String Quantity = mCursor.getString(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLQUAN));
            String Timestamp = mCursor.getString(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLTIME));
            int ID = mCursor.getInt(mCursor.getColumnIndex(RecyclerContainerdp.RecyclerEntry.COLID));
            int add = 0;
            holder.holderid = mCursor.getInt(0);
            ch = holder.checkBox.isChecked();
            for(int i=0;i< holders.size();i++) {
                if (holders.get(i).holderid == holder.holderid) {
                    add++;
                }
            }
            if(add == 0)
            {
                holders.add(holder);
            }
            else
            {
                Log.d("DELETE", "onBindViewHolder: cannot insert duplicate");
            }

            Log.d("Position", "onBindViewHolder: pos " + position);
            Log.d("Position", "onBindViewHolder: id " + ID);
            holder.holderid = ID;

            if (!TextUtils.isEmpty(DataPoint)) {
                holder.textDataPoint.setText(DataPoint);
            } else {
                holder.textDataPoint.setText("");
            }

            if (!TextUtils.isEmpty(Gender)) {
                holder.txtGender.setText(Gender);
            } else {
                holder.txtGender.setText("");
            }

            if (!TextUtils.isEmpty(Postition)) {
                holder.txtPos.setText(Postition);
            } else {
                holder.txtPos.setText("");
            }

            if (!TextUtils.isEmpty(Severity)) {
                holder.txtSev.setText(Severity);
            } else {
                holder.txtSev.setText("");
            }

            if (!TextUtils.isEmpty(Quantity)) {
                holder.txtQuantity.setText(Quantity);
            } else {
                holder.txtQuantity.setText("");
            }

            if (!TextUtils.isEmpty(Timestamp)) {
                holder.txtTime.setText(Timestamp);
            } else {
                holder.txtTime.setText("");
            }

            holder.checkBox.setOnClickListener(v -> {
            });

            if (!isSelectedAll) {
                holder.checkBox.setChecked(false);
            } else {
                holder.checkBox.setChecked(true);
            }
            holders.add(holder);
        } catch (Exception errr) {
            Log.d("exeptions", "Recycler Adapter: " + errr);
        }
    }

    public void DeleteEntry() {
        try {
            holderids = new ArrayList<>();
            ArrayList<RecyclerViewHolder> holderstoremove = new ArrayList<>();
            for (RecyclerViewHolder r : holders
            ) {
                if (r.checkBox.isChecked()) {
                    holderids.add(r.holderid);
                    holderstoremove.add(r);
                }
            }
            if(holderstoremove.size() > 0) {
                holders.removeAll(holderstoremove);
                Log.d("DELETE", "DeleteEntry: " + holderids);
                mDatabaseHelper.delentryData(holderids);
                notifyDataSetChanged();
            }
            else
            {
                Log.d("already", "DeleteEntry: no more");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectAll() {
        try {
            isSelectedAll = true;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unselectall() {
        try {
            isSelectedAll = false;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try {
            return mCursor.getCount();
        } catch (Exception exeptionex) {
            Log.d("exeptions", exeptionex.toString());
            return 0;
        }
    }

    public void swapCursor(Cursor newCursor) {
        try {
            if (mCursor != null) {
                mCursor.close();
            }
            mCursor = newCursor;
            if (newCursor != null) {
                notifyDataSetChanged();
            }
        } catch (Exception exeptionex) {
            Log.d("exeptions", exeptionex.toString());
        }
    }
}
