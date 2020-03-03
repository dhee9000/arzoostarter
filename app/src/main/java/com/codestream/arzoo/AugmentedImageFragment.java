package com.codestream.arzoo;

import android.app.ActivityManager;

import android.content.Context;
import android.content.res.AssetManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extend the ArFragment to customize the ARCore session configuration to include Augmented Images.
 */
public class AugmentedImageFragment extends ArFragment {
    private static final String TAG = "AugmentedImageFragment";

    // This is the name of the images in the sample database.  A copy of the image is in the assets
    // directory.  Opening this image on your computer is a good quick way to test the augmented image
    // matching.
    private static final String[] DEFAULT_IMAGES = {"default.jpg", "Giraffe.jpg", "Zebra.jpg", "Tortoise.jpg"};//, "Cheetah.jpg", "Giraffe.png", "Gorilla.png", "Penguin.png", "Tiger.png", "Tortoise.png", "Turtle.png", "Zebra.png"};
    private static final boolean USE_SINGLE_IMAGE = true;

    // This is a pre-created database containing the sample image.
    private static final String SAMPLE_IMAGE_DATABASE = "sample_database.imgdb";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Turn off the plane discovery since we're only looking for images
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);
        getArSceneView().getPlaneRenderer().setEnabled(false);
        return view;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        if (!setupAugmentedImageDatabase(config, session)) {
            Log.e(TAG, "Couldn't setup augmented image database!");
        }
        return config;
    }

    private boolean setupAugmentedImageDatabase(Config config, Session session) {
        AugmentedImageDatabase augmentedImageDatabase;

        AssetManager assetManager = getContext() != null ? getContext().getAssets() : null;
        if (assetManager == null) {
            Log.e(TAG, "Context is null, cannot initialize image database.");
            return false;
        }

        // There are two ways to configure an AugmentedImageDatabase:
        // 1. Add Bitmap to DB directly
        // 2. Load a pre-built AugmentedImageDatabase
        // Option 2) has
        // * shorter setup time
        // * doesn't require images to be packaged in apk.
        if (USE_SINGLE_IMAGE) {

            augmentedImageDatabase = new AugmentedImageDatabase(session);
            for(String IMAGE_NAME : DEFAULT_IMAGES){
                Bitmap augmentedImageBitmap = loadAugmentedImageBitmap(assetManager, IMAGE_NAME);
                if (augmentedImageBitmap == null) {
                    return false;
                }
                augmentedImageDatabase.addImage(IMAGE_NAME, augmentedImageBitmap);
                Log.i(TAG, "Added Image To Database");
            }
            // If the physical size of the image is known, you can instead use:
            //     augmentedImageDatabase.addImage("image_name", augmentedImageBitmap, widthInMeters);
            // This will improve the initial detection speed. ARCore will still actively estimate the
            // physical size of the image as it is viewed from multiple viewpoints.
        }
        /// ################## THE FOLLOWING SECTION IS FOR A PRECOMPUTED DATABASE, NOT REQUIRED #######################
//        else {
//            // This is an alternative way to initialize an AugmentedImageDatabase instance,
//            // load a pre-existing augmented image database.
//            try (InputStream is = getContext().getAssets().open(SAMPLE_IMAGE_DATABASE)) {
//                augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
//            } catch (IOException e) {
//                Log.e(TAG, "IO exception loading augmented image database.", e);
//                return false;
//            }
//        }

        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

    private Bitmap loadAugmentedImageBitmap(AssetManager assetManager, String IMAGE_NAME) {
        try (InputStream is = assetManager.open(IMAGE_NAME)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e);
        }
        return null;
    }
}
