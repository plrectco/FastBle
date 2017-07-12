package com.clj.blesample.tool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.blesample.R;
import com.clj.blesample.tool.scan.AnyScanActivity;


/**
 * 可以作为测试工具
 */
public class MainActivity extends AppCompatActivity {

    private String[] modes = new String[]{
            "Start PEPS scanning",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startActivity(new Intent(MainActivity.this, AnyScanActivity.class));

        ListView mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(new ConnectModeAdapter(this, modes));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, AnyScanActivity.class));
                        break;
                }
            }
        });
    }

    private class ConnectModeAdapter extends BaseAdapter {

        private Context context;
        private String[] modes;

        ConnectModeAdapter(Context context, String[] modes) {
            this.context = context;
            this.modes = modes;
        }

        @Override
        public int getCount() {
            if (modes == null)
                return 0;
            return modes.length;
        }

        @Override
        public String getItem(int position) {
            if (modes == null)
                return null;
            return modes[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_connect_mode, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_mode = (TextView) convertView.findViewById(R.id.txt_mode);
            }
            holder.txt_mode.setText(modes[position]);
            return convertView;
        }

        class ViewHolder {
            TextView txt_mode;
        }
    }
}
