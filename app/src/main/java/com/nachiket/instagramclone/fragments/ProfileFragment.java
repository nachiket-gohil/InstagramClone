package com.nachiket.instagramclone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nachiket.instagramclone.EditProfileActivity;
import com.nachiket.instagramclone.LoginActivity;
import com.nachiket.instagramclone.R;
import com.nachiket.instagramclone.adapter.PhotoAdapter;
import com.nachiket.instagramclone.model.Post;
import com.nachiket.instagramclone.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter photoAdapterSaves;
    private List<Post> mySavedPost;

    private CircleImageView profileImage;
    private ImageView ivLogout;
    private ImageView ivPhotos;
    private ImageView ivSaved;
    private TextView username;
    private TextView fullname;
    private TextView bio;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView btnEditProfile;//(AS BUTTON)

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String profileId;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileId = firebaseUser.getUid();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
        }

        profileImage = view.findViewById(R.id.civ_profile_image);
        ivLogout = view.findViewById(R.id.iv_logout);
        ivPhotos = view.findViewById(R.id.iv_grids);
        ivSaved = view.findViewById(R.id.iv_saved);
        username = view.findViewById(R.id.profile_username);
        fullname = view.findViewById(R.id.profile_fullname);
        bio = view.findViewById(R.id.profile_bio);
        posts = view.findViewById(R.id.tv_post_count);
        followers = view.findViewById(R.id.tv_followers_count);
        following = view.findViewById(R.id.tv_following_count);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        recyclerView = view.findViewById(R.id.recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewSaves = view.findViewById(R.id.recycler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPost = new ArrayList<>();
        photoAdapterSaves = new PhotoAdapter(getContext(), mySavedPost);
        recyclerViewSaves.setAdapter(photoAdapterSaves);

        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        getSavedPost();

        if(profileId.equals(firebaseUser.getUid())) {
            btnEditProfile.setText("Edit Profile");
        } else {
            checkFollowingStatus();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = btnEditProfile.getText().toString();
                if (btnText.equals("Edit Profile")) {
                    //Go to edit profile
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
                    if (btnText.equals("follow")) {
                        databaseReference.child("Follow").child(firebaseUser.getUid()).child("following")
                                .child(profileId).setValue(true);
                        databaseReference.child("Follow").child(profileId).child("followers")
                                .child(firebaseUser.getUid()).setValue(true);
                    } else {
                        databaseReference.child("Follow").child(firebaseUser.getUid()).child("following")
                                .child(profileId).removeValue();
                        databaseReference.child("Follow").child(profileId).child("followers")
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        ivPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        ivSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Signing out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                getActivity().finish();
            }
        });

        return view;
    }

    private void getSavedPost() {
        final List<String> savedIds = new ArrayList<>();
        databaseReference.child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    savedIds.add(dataSnapshot.getKey());
                }
                databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        mySavedPost.clear();
                        for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            for (String id : savedIds) {
                                if (post.getPostId().equals(id)) {
                                    mySavedPost.add(post);
                                }
                            }
                        }
                        photoAdapterSaves.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {
        databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        myPhotoList.add(post);
                    }
                }
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void checkFollowingStatus() {
        databaseReference.child("Follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    btnEditProfile.setText("following");
                } else {
                    btnEditProfile.setText("follow");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                        counter++;
                }
                posts.setText(String.valueOf(counter));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference reference = databaseReference.child("Follow").child(profileId);

        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void userInfo() {
        databaseReference.child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(profileImage);
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
