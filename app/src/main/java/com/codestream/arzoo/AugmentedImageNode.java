package com.codestream.arzoo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

    private static final String TAG = "AugmentedImageNode";
    private AugmentedImage image;
    private static CompletableFuture<ModelRenderable> ulCorner;

    public AugmentedImageNode(Context context) {
        // Upon construction, start loading the models for the corners of the frame.
        if (ulCorner == null) {
            ulCorner = new Animal(context).getModel();
        }
    }

    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image. The corners are then positioned based on the
     * extents of the image. There is no need to worry about world coordinates since everything is
     * relative to the center of the image, which is the parent node of the corners.
     */
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void setImage(AugmentedImage image) {
        this.image = image;

        // If any of the models are not loaded, then recurse when all are loaded.
        if (!ulCorner.isDone()) {
            CompletableFuture.allOf(ulCorner)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
        }

        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        // Make the 4 corner nodes.
        Vector3 localPosition = new Vector3();
        Vector3 localScale = new Vector3();
        // Middle is clockwise counter clockwise, last is stick forward stick backward
        Quaternion localRotation = new Quaternion(new Vector3(0,90, -90));
        Node centerNode;

        // Upper left corner.
        localPosition.set(0,0,0);
        localScale.set(0.2f, 0.2f, 0.2f);
        centerNode = new Node();
        centerNode.setParent(this);
        centerNode.setLocalPosition(localPosition);
        centerNode.setLocalScale(localScale);
        centerNode.setLocalRotation(localRotation);
        centerNode.setRenderable(ulCorner.getNow(null));
    }

    public AugmentedImage getImage() {
        return image;
    }
}
