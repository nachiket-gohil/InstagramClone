package com.nachiket.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.nachiket.instagramclone.CommentActivity;
import com.nachiket.instagramclone.R;
import com.nachiket.instagramclone.fragments.PostDetailFragment;
import com.nachiket.instagramclone.fragments.ProfileFragment;
import com.nachiket.instagramclone.model.Post;
import com.nachiket.instagramclone.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.snippet_post_item, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post post = mPosts.get(position);
        Picasso.get().load(post.getImgUrl()).into(holder.postImg);
        holder.postDescription.setText(post.getDescription());

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    holder.postProfileImage.setImageResource(R.drawable.def_user);
                } else {
                    Picasso.get().load(user.getImageurl()).into(holder.postProfileImage);
                }
                holder.postUsername.setText(user.getUsername());
                holder.postAuthor.setText(user.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isLiked(post.getPostId(), holder.ivLike);
        noOfLikes(post.getPostId(), holder.postLikes);
        getComment(post.getPostId(), holder.postNoOfComments);
        isSaved(post.getPostId(), holder.ivSave);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.ivLike.getTag().equals("like")) {
                    databaseReference.child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    databaseReference.child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ivSave.getTag().equals("save")) {
                    databaseReference.child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostId()).setValue(true);
                } else {
                    databaseReference.child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostId()).removeValue();
                }
            }
        });

        holder.postNoOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.postProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.postUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.postAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        .putString("postId", post.getPostId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView postProfileImage;
        public ImageView ivMore;
        public ImageView postImg;
        public ImageView ivLike;
        public ImageView ivComment;
        public ImageView ivSave;
        public TextView postUsername;
        public TextView postAuthor;
        public TextView postLikes;
        public TextView postNoOfComments;
        public SocialTextView postDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postProfileImage = itemView.findViewById(R.id.post_userimg);
            ivMore = itemView.findViewById(R.id.more);
            postImg = itemView.findViewById(R.id.posted_img);
            ivLike = itemView.findViewById(R.id.like);
            ivComment = itemView.findViewById(R.id.comment);
            ivSave = itemView.findViewById(R.id.save);
            postUsername = itemView.findViewById(R.id.tv_post_username);
            postAuthor = itemView.findViewById(R.id.tv_post_author);
            postLikes = itemView.findViewById(R.id.tv_no_of_likes);
            postNoOfComments = itemView.findViewById(R.id.tv_no_of_comment);
            postDescription = itemView.findViewById(R.id.tv_post_bio);
        }
    }

    private void isLiked(String postId, final ImageView imageView) {
        databaseReference.child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikes(String postId, final TextView text) {
        databaseReference.child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + " Likes");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComment(String postId, final TextView text) {
        databaseReference.child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText("View all " + snapshot.getChildrenCount() + " comments");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(final String postId, final ImageView image) {
        databaseReference.child("Saves").child(firebaseUser.getUid())
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_saved);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_save);
                    image.setTag("save");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
