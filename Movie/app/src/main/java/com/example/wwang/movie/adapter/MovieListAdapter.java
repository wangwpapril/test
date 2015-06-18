package com.example.wwang.movie.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wwang.movie.R;
import com.example.wwang.movie.model.MovieItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MovieListAdapter extends BaseAdapter {
	private List<MovieItem> datas;
    protected Activity context;

	public MovieListAdapter(List<MovieItem> datas,
                            Activity activity) {
		this.datas = datas;
		this.context = activity;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return datas.get(position).getmId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item,
					null);

			holder.movieName = (TextView) convertView
					.findViewById(R.id.movie_name);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MovieItem model = datas.get(position);
		holder.movieName.setText(model.getmTitle());
		convertView.setTag(holder);
		return convertView;
	}

    public void setList(List<MovieItem> datas){
        this.datas = datas;
        notifyDataSetChanged();
    }

    public List<MovieItem> getList(){
        return datas;
    }

    private class ViewHolder {
		public TextView movieName;
	}

}