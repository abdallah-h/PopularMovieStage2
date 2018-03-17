package com.abdoh.popularmoviestage2.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdoh.popularmoviestage2.Models.MovieInfo;
import com.abdoh.popularmoviestage2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdoh.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PosterViewHolder> {
    private ArrayList<MovieInfo> mMovieInfos;
    private final onPosterClickHandler mClickHandler;

    public interface onPosterClickHandler{
        void onClick(MovieInfo movieInfo);

    }

    public MovieAdapter(onPosterClickHandler clickHandler){
        mMovieInfos = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public void addMovies(ArrayList<MovieInfo> movieInfos){
        mMovieInfos.addAll(movieInfos);
        notifyDataSetChanged();
    }

    public void clear(){
        mMovieInfos.clear();
        notifyDataSetChanged();
    }

    public void saveInstanceState(Bundle outState){
        outState.putParcelableArrayList("ADAPTER_MOVIES", mMovieInfos);
    }

    public void restoreInstanceState(Bundle savedInstanceState){
        if (savedInstanceState.containsKey("ADAPTER_MOVIES")){
            ArrayList<MovieInfo> savedMovies = savedInstanceState.getParcelableArrayList("ADAPTER_MOVIES");
            mMovieInfos.clear();
            mMovieInfos.addAll(savedMovies);
            notifyDataSetChanged();
        }
    }
    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        int layoutItemId = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutItemId,parent,false);

        return new PosterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        holder.setImage(mMovieInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieInfos.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.item_poster_image)
        ImageView mImageView;
        Context mContext;

        public PosterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void setImage(MovieInfo movieInfo){
            Uri posterUri = movieInfo.getPosterUri(mContext.getString(R.string.poster_size));
            Picasso.with(mContext).load(posterUri).into(mImageView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieInfo selectedMovie = mMovieInfos.get(position);
            mClickHandler.onClick(selectedMovie);
        }
    }
}
