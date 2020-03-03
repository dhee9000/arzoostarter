package com.codestream.arzoo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Reference to AR Fragment in our activity_main.xml
    private ArFragment arFragment;

    // Reference to the guide image in our activity_main.xml
    private ImageView fitToScanView;

    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        fitToScanView = findViewById(R.id.image_view_fit_to_scan);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    }


    // Function that runs every times our AR Scene View Updates the "Frame"
    // We get the "frame time" which is the time since the last frame was updated as frameTime
    private void onUpdateFrame(FrameTime frameTime) {

        // Get the AR Frame in our SceneView
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        // Make a collection of all the trackable augmented images in our scene
        Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        // Loop through all the trackable images we found and see what "tracking state" we are in.
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    String text = "Tracking Paused on Image " + augmentedImage.getName();
                    // SnackbarHelper.getInstance().showMessage(this, text);
                    Log.e("AR STATUS", text);
                    break;

                case TRACKING:

                    // Hide the guiding image if we are actively tracking something
                    fitToScanView.setVisibility(View.GONE);
                    Log.i("AR STATUS", "Tracking Active on Image " + augmentedImage.getName());

                    // Create a new anchor for the images we are tracking (AugmentedAnimalNode)
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        augmentedImage.getName();
                        AugmentedImageNode node = new AugmentedImageNode(this);
                        node.setImage(augmentedImage);
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
                    }
                    break;

                case STOPPED:
                    // If we lost the tracking on an image, then remove this from our collection of detected augmented images
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }
}
