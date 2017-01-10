package xyz.snsstudio.gpsalarm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static xyz.snsstudio.gpsalarm.Constant.TIME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.ALARM_NAME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.REPEAT_TEXT;

/**
 *
 * @author Paresh N. Mayani
 */
public class listviewAdapter extends BaseAdapter
{
    public ArrayList<HashMap> list;
    Activity activity;

    public listviewAdapter(Activity activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater =  activity.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listlayout, null);
            holder = new ViewHolder();
            holder.txtFirst = (TextView) convertView.findViewById(R.id.timeText);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.nameText);
            holder.txtThird = (TextView) convertView.findViewById(R.id.datetext);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap map = list.get(position);
        holder.txtFirst.setText((CharSequence) map.get(TIME_TEXT));
        holder.txtSecond.setText((CharSequence) map.get(ALARM_NAME_TEXT));
        holder.txtThird.setText((CharSequence) map.get(REPEAT_TEXT));

        return convertView;
    }

}